/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import jakarta.annotation.Nullable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.DecoderException;
import static org.apache.commons.codec.binary.Hex.decodeHex;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.apache.commons.codec.digest.DigestUtils.sha256;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.abbreviate;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trim;
import org.cmdbuild.utils.crypto.MagicUtils.MagicUtilsHelper;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackBytes;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cm3EasyCryptoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static int SALT_LEN = 4; // a short salt will strongly enhance password security, while maintaining the final string length quite short

    private final static MagicUtilsHelper MAGIC_HELPER = MagicUtils.helper(encodeHexString(sha256("CMDBUILD_ENCRYPTED_VALUE_V1")).substring(0, 6), 7, 13, 19, 21, 24, 27);
    private final static Cm3EasyCryptoHelper DEFAULT_HELPER = doCreateDefaultHelper();

    @Nullable
    public static String encryptValue(@Nullable String value) {
        return defaultUtils().encryptValue(value);
    }

    @Nullable
    public static String encryptValueIfNotEncrypted(@Nullable String value) {
        return isEncrypted(value) ? value : encryptValue(value);
    }

    @Nullable
    public static String decryptValue(@Nullable String value) {
        return defaultUtils().decryptValue(value);
    }

    public static String decryptValueOrFail(String encrypted) {
        return defaultUtils().decryptValueOrFail(encrypted);
    }

    public static boolean isEncrypted(@Nullable String str) {
        return MAGIC_HELPER.hasMagic(str);
    }

    public static Cm3EasyCryptoHelper defaultUtils() {
        return DEFAULT_HELPER.newHelper();
    }

    public static Cm3EasyCryptoHelper customUtils(byte[] keySource) {
        return new Cm3EasyCryptoHelper(keySource);
    }

    private static Cm3EasyCryptoHelper doCreateDefaultHelper() {
        String keyFile = firstNotBlankOrNull(System.getenv("CMDBUILD_CM3EASY_KEY"), System.getProperty("org.cmdbuild.cm3easy.key"));
        try {
            if (isNotBlank(keyFile)) {
                LOGGER.info("loading cm3easy key from file =< {} >", keyFile);
                return new Cm3EasyCryptoHelper(toByteArray(new File(keyFile)));
            }
        } catch (Exception ex) {
            LOGGER.error("error loading cm3easy crypto key from file =< {} >", keyFile, ex);
        }
        LOGGER.info("using default cm3easy key ( if this is a production environment you should change this to a secure key source by setting either env param `CMDBUILD_CM3EASY_KEY` or java system property `org.cmdbuild.cm3easy.key` to a valid key file )");
        return new Cm3EasyCryptoHelper(unpackBytes(readToString(Cm3EasyCryptoUtils.class.getClassLoader().getResourceAsStream("org/cmdbuild/utils/crypto/cm3easy_default.txt"))));
    }

    public static class Cm3EasyCryptoHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final byte[] source;
        private final Key key;
        private final IvParameterSpec iv;
        private final Cipher cipher;
        private final String keyId;

        private Cm3EasyCryptoHelper(byte[] source) {
            try {
                checkArgument(source.length > 4, "invalid key source, not enough bytes");
                this.source = source.length == 32 ? source : sha256(source);
                logger.debug("creating crypto utils from source = {}", abbreviate(encodeHexString(this.source), 10));
                byte[] keyBytes = Arrays.copyOfRange(this.source, 1, 17),
                        ivBytes = Arrays.copyOfRange(this.source, 15, 31);
                keyId = sha256Hex(new SequenceInputStream(new ByteArrayInputStream(keyBytes), new ByteArrayInputStream(ivBytes))).substring(0, 16);
                logger.debug("crypto utils key id = {}", keyId);
                key = new SecretKeySpec(keyBytes, "AES");
                iv = new IvParameterSpec(ivBytes);
                cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                logger.debug("crypto utils ready");
            } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException ex) {
                throw new RuntimeException(ex);
            }
        }

        public Cm3EasyCryptoHelper newHelper() {
            return new Cm3EasyCryptoHelper(this.source);
        }

        @Nullable
        public String encryptValue(@Nullable String value) {
            logger.trace("encrypting value = {}", value);
            String encryptedValue;
            if (isBlank(value)) {
                encryptedValue = value;
            } else {
                byte[] data = value.getBytes(StandardCharsets.UTF_8);
                data = ArrayUtils.addAll(createSalt(), data);
                data = encrypt(data);
                encryptedValue = MAGIC_HELPER.embedMagic(encodeHexString(data));
            }
            logger.trace("encrypted value =< {} > with result =< {} >", value, encryptedValue);
            return encryptedValue;
        }

        public String decryptValueOrFail(String encrypted) {
            return decryptValue(encrypted, true);
        }

        @Nullable
        public String decryptValue(@Nullable String value) {
            return decryptValue(value, false);
        }

        @Nullable
        public String decryptValue(@Nullable String value, boolean failIfInvalid) {
            logger.trace("decrypting value = {}", value);
            if (isBlank(value)) {
                if (failIfInvalid) {
                    throw new RuntimeException("invalid encrypted value: value is blank");
                } else {
                    logger.trace("value is blank, no decryption necessary");
                    return value;
                }
            } else if (!MAGIC_HELPER.hasMagic(trim(value))) {
                if (failIfInvalid) {
                    throw new RuntimeException("invalid encrypted value: value does not match cm3easy format");
                } else {
                    logger.trace("value is cleartext, no decryption necessary");
                    return value;
                }
            } else {
                try {
                    while (MAGIC_HELPER.hasMagic(trim(value))) {// we handle recursive encryption (ie we handle values that have been encrypted multiple times)
                        value = decryptRawValue(MAGIC_HELPER.stripMagic(trim(value)));
                    }
                    return value;
                } catch (Exception ex) {
                    throw new RuntimeException("error processing encrypted value", ex);
                }
            }
        }

        private String decryptRawValue(String value) throws DecoderException {
            byte[] data = decrypt(decodeHex(value));
            data = Arrays.copyOfRange(data, SALT_LEN, data.length);
            String decryptedValue = new String(data, Charsets.UTF_8);
            logger.trace("decrypted value = {} with result = {}", value, decryptedValue);
            return decryptedValue;
        }

        public byte[] encrypt(byte[] data) {
            try {
                checkNotNull(data);
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                return cipher.doFinal(data);
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
                throw new RuntimeException(ex);
            }
        }

        public byte[] decrypt(byte[] data) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
                return cipher.doFinal(data);
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
                throw new RuntimeException(ex);
            }
        }

        private byte[] createSalt() {
            byte[] data = new byte[SALT_LEN];
            new SecureRandom().nextBytes(data);
            return data;
        }

        public String getKeyId() {
            return keyId;
        }

    }

}
