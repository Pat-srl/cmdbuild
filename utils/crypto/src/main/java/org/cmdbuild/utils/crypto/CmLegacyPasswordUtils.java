package org.cmdbuild.utils.crypto;

import com.google.common.base.Splitter;
import static java.lang.Integer.parseInt;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;

/**
 * @deprecated do not use this, except where backward compatibility is required
 */
@Deprecated
public class CmLegacyPasswordUtils {

    public static String encrypt(String value) {
        return new CmLegacyHelper().encrypt(value);
    }

    public static String decrypt(String value) {
        return new CmLegacyHelper().decrypt(value);
    }

    public static boolean isEncrypted(String value) {
        CmLegacyHelper helper = new CmLegacyHelper();
        try {
            helper.decrypt(value);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private static class CmLegacyHelper {

        private static final String DEFAULT_PARAMS = readToString(CmLegacyHelper.class.getClassLoader().getResourceAsStream("org/cmdbuild/utils/crypto/legacy_default.txt"));

        private final Cipher ecipher;
        private final Cipher dcipher;

        private CmLegacyHelper() {
            try {
                List<String> params = Splitter.on(":").splitToList(DEFAULT_PARAMS);

                String pPh = new String(Hex.decodeHex(params.get(0)), StandardCharsets.UTF_8);
                byte[] salt = Hex.decodeHex(params.get(1));
                int iterationCount = parseInt(params.get(2));

                KeySpec keySpec = new PBEKeySpec(pPh.toCharArray(), salt, iterationCount);
                SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
                AlgorithmParameterSpec cypherParameters = new PBEParameterSpec(salt, iterationCount);

                ecipher = Cipher.getInstance(key.getAlgorithm());
                ecipher.init(Cipher.ENCRYPT_MODE, key, cypherParameters);

                dcipher = Cipher.getInstance(key.getAlgorithm());
                dcipher.init(Cipher.DECRYPT_MODE, key, cypherParameters);
            } catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException | NoSuchPaddingException | InvalidKeySpecException | DecoderException ex) {
                throw new RuntimeException(ex);
            }
        }

        private byte[] encrypt(byte[] val) throws IllegalBlockSizeException, BadPaddingException {
            return ecipher.doFinal(val);
        }

        private byte[] decrypt(byte[] val) throws IllegalBlockSizeException, BadPaddingException {
            return dcipher.doFinal(val);
        }

        private String encrypt(String val) {
            try {
                byte[] passwordBytesAsUTF8Encoding = val.getBytes("UTF8");
                byte[] encryptedPasswordBytes = encrypt(passwordBytesAsUTF8Encoding);
                return java.util.Base64.getEncoder().encodeToString(encryptedPasswordBytes);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private String decrypt(String val) {
            try {
                byte[] encryptedPasswordBytes = java.util.Base64.getDecoder().decode(val);
                byte[] passwordBytesAsUTF8Encoding = decrypt(encryptedPasswordBytes);
                return new String(passwordBytesAsUTF8Encoding, "UTF8");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
