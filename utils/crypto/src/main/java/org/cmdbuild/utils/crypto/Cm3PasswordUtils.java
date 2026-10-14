/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Stopwatch;
import com.google.common.base.Suppliers;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import static java.lang.Math.round;
import static java.lang.Math.toIntExact;
import java.lang.invoke.MethodHandles;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import static org.apache.commons.codec.digest.DigestUtils.sha256;
import static org.apache.commons.io.IOUtils.toByteArray;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeBytes;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cm3PasswordUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final MagicUtils.MagicUtilsHelper PBKDF2_MAGIC = MagicUtils.helper(Arrays.copyOfRange(sha256("CMDBUILD_ENCRYPTED_VALUE_PBKDF2"), 0, 4), 7, 11, 13, 21);

    private static final Supplier<Cm3APasswordHelper> HELPER = Suppliers.memoize(Cm3PasswordUtils::createPasswordHelper);

    private static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA512";
    private static final byte HASH_BYTE_SIZE = 24, SALT_BYTE_SIZE = 24;
    private static final int DEFAULT_PBKDF2_ITERATIONS = 64000;
    private static final long TARGET_DURATION_MILLIS = 300;

    private static Cm3APasswordHelper createPasswordHelper() {
        LOGGER.debug("selecting pbfk2 iteration count, target duration = {}ms", TARGET_DURATION_MILLIS);
        int pbfk2Iterations = DEFAULT_PBKDF2_ITERATIONS;
        while (true) {
            LOGGER.debug("selecting pbfk2 iteration count, test iteration count = {}", pbfk2Iterations);
            int sampleSize = 10;
            Cm3APasswordHelper helper = new Cm3APasswordHelper(pbfk2Iterations);
            List<String> data = IntStream.range(0, sampleSize).mapToObj(i -> randomId()).collect(toImmutableList());
            Stopwatch stopwatch = Stopwatch.createStarted();
            data.forEach(helper::hash);
            long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS), expected = TARGET_DURATION_MILLIS * sampleSize;
            if (elapsed > expected) {
                LOGGER.info("selected pbfk2 iteration count = {}, average duration = {}ms", pbfk2Iterations, elapsed / sampleSize);
                return helper;
            } else {
                pbfk2Iterations = toIntExact(round(pbfk2Iterations * expected * 1.1 / elapsed));
            }
        }
    }

    public static String hash(String password) {
        return HELPER.get().hash(password);
    }

    public static boolean isEncrypted(String value) {
        try {
            byte[] data = decodeBytes(value);
            return PBKDF2_MAGIC.hasMagic(data);
        } catch (Exception ex) {
            LOGGER.trace("hasmagic check failed for has =< %s > with exception", value, ex);
            return false;
        }
    }

    public static void verify(String passwordToValidate, String hashStr) {
        checkArgument(isValid(passwordToValidate, hashStr), "password hash does not match");
    }

    public static boolean isValid(String passwordToValidate, String hashStr) {
        try {
            checkNotBlank(passwordToValidate, "password to validate is blank");
            checkNotBlank(hashStr, "hash is blank");
            byte[] data = decodeBytes(hashStr);
            checkArgument(PBKDF2_MAGIC.hasMagic(data));
            data = PBKDF2_MAGIC.stripMagic(data);
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
            byte saltSize = in.readByte();
            byte[] salt = new byte[saltSize];
            checkArgument(saltSize > 0 && in.read(salt) == saltSize, "invalid salt size");
            byte hashSize = in.readByte();
            byte[] validHash = new byte[hashSize];
            checkArgument(hashSize > 0 && in.read(validHash) == hashSize, "invalid hash size");
            int iterations = in.readInt();
            checkArgument(iterations > 0, "invalid iterations");
            checkArgument(toByteArray(in).length == 0);
            byte[] hashToValidate = pbkdf2(passwordToValidate, salt, iterations, hashSize);
            return Arrays.equals(validHash, hashToValidate);
        } catch (Exception ex) {
            throw runtime(ex, "password validation error");
        }
    }

    private static byte[] pbkdf2(String password, byte[] salt, int iterations, int bytes) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, bytes * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
        byte[] hash = skf.generateSecret(spec).getEncoded();
        checkArgument(hash.length == bytes);
        return hash;
    }

    private static class Cm3APasswordHelper {

        private final int pbfk2Iterations;

        public Cm3APasswordHelper(int pbfk2Iterations) {
            this.pbfk2Iterations = checkNotNullAndGtZero(pbfk2Iterations);
            checkArgument(pbfk2Iterations >= DEFAULT_PBKDF2_ITERATIONS, "invalid pbkf2 interation count = {}, less than default (min) interation count = {}", pbfk2Iterations, DEFAULT_PBKDF2_ITERATIONS);
        }

        public String hash(String password) {
            try {
                checkNotBlank(password, "password is blank");
                SecureRandom random = new SecureRandom();
                byte[] salt = new byte[SALT_BYTE_SIZE];
                random.nextBytes(salt);
                byte[] hash = pbkdf2(password, salt, pbfk2Iterations, HASH_BYTE_SIZE);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (DataOutputStream out = new DataOutputStream(baos)) {
                    out.write(SALT_BYTE_SIZE);
                    out.write(salt);
                    out.write(HASH_BYTE_SIZE);
                    out.write(hash);
                    out.writeInt(pbfk2Iterations);
                }
                byte[] data = baos.toByteArray();
                data = PBKDF2_MAGIC.embedMagic(data);
                return encodeBytes(data);
            } catch (Exception ex) {
                throw runtime(ex, "password hashing error");
            }
        }

    }
}
