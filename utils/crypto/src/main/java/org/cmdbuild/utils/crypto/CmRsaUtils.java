/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Math.abs;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.regex.Pattern.DOTALL;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import static org.apache.commons.lang3.StringUtils.trim;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeBytes;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmRsaUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static String SIGN_PREFIX = "signed:v1:",
            TOKEN_PREFIX = "token:v1:";

    public static String createChallengeResponse(String challenge, PrivateKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(decodeBytes(challenge));
            byte[] signature = ArrayUtils.addAll(SIGN_PREFIX.getBytes(), encrypted);
            return encodeBytes(signature);
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static String createToken(PrivateKey key) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(key);

            long timestamp = now().toEpochSecond();
            String salt = randomId();
            String payload = TOKEN_PREFIX + salt + ":" + timestamp;
            signature.update(payload.getBytes());
            byte[] signatureData = signature.sign();
            
            return encodeString(format("%s:%s", payload, encodeBytes(signatureData)));
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static boolean verifyToken(String encodedData, PublicKey key) {
        return verifyToken(encodedData, key, false);
    }

    public static boolean verifyToken(String encodedData, PublicKey key, boolean ignoreTimestamp) {
        try {
            LOGGER.trace("verifying token = {} with key = {}", encodedData, key);

//            Signature signature = Signature.getInstance("SHA256withRSA/PSS");
            Signature signature = Signature.getInstance("SHA256withRSA");
//            Signature signature = Signature.getInstance("SHA512withRSA");
            signature.initVerify(key);

//            Cipher cipher = Cipher.getInstance("RSA");
//            cipher.init(Cipher.DECRYPT_MODE, key);
            String payloadWithSignature = decodeString(encodedData);
            LOGGER.trace("decoded data = {}", payloadWithSignature);

            Matcher matcher = Pattern.compile("(" + Pattern.quote(TOKEN_PREFIX) + "[^:]+:([0-9]+)):(.+)").matcher(payloadWithSignature);
            checkArgument(matcher.matches(), "invalid syntax for payload = %s ", payloadWithSignature);
            String payload = checkNotBlank(matcher.group(1)),
                    signatureStr = checkNotBlank(matcher.group(3));
            long timestamp = toLong(matcher.group(2));
            LOGGER.trace("verified payload =< {} > timestamp = {}", payload, timestamp);
            checkArgument(ignoreTimestamp || abs(timestamp - now().toEpochSecond()) < 600, "invalid timestamp (token expired or too ahead in the future)");

            signature.update(payload.getBytes());
            return signature.verify(decodeBytes(signatureStr));
//            ) {
//                return false;
//            }

//            byte[] decryptedData;
//            try {
//                decryptedData = cipher.doFinal(encryptedData);
//            } catch (Exception ex) {
//                LOGGER.debug("signed token decrypt failed", ex);
//                return false;
//            }
//            LOGGER.trace("decrypted data (hex) = {}", Hex.encodeHexString(decryptedData));
//            String payloadStr = new String(decryptedData, StandardCharsets.UTF_8);
//            Matcher matcher = Pattern.compile(Pattern.quote(TOKEN_PREFIX) + "[^:]+:([0-9]+)").matcher(payloadStr);
//            checkArgument(matcher.matches(), "invalid syntax for payload =< %s > ( hex = %s )", payloadStr, Hex.encodeHexString(decryptedData));
//            long timestamp = toLong(matcher.group(1));
//            LOGGER.trace("decrypted payload =< {} > timestamp = {}", payloadStr, timestamp);
//            checkArgument(abs(timestamp - now().toEpochSecond()) < 600, "invalid timestamp (token expired or too ahead in the future)");
//            return true;
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static boolean verifySignedChallenge(String challenge, String response, PublicKey key) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] challengeData = decodeBytes(challenge);
            byte[] responseData = decodeBytes(response);
            responseData = Arrays.copyOfRange(responseData, SIGN_PREFIX.getBytes().length, responseData.length);

            try {
                byte[] decryptedResponse = cipher.doFinal(responseData);
                return Arrays.equals(decryptedResponse, challengeData);
            } catch (Exception ex) {
                LOGGER.debug("signed challenge verification failed", ex);
                return false;
            }

        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static PrivateKey parsePrivateKey(String data) {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            data = data.replaceAll("\n|\r", "");
            Matcher matcher = Pattern.compile("-----BEGIN RSA PRIVATE KEY-----(.+)-----END RSA PRIVATE KEY-----", DOTALL).matcher(data);
            checkArgument(matcher.find(), "invalid private key format");
            byte[] bytes = Base64.decodeBase64(matcher.group(1));
            PKCS8EncodedKeySpec privKeySpec = new PKCS8EncodedKeySpec(convertPKCS1ToPKCS8(bytes));
            return factory.generatePrivate(privKeySpec);
        } catch (Exception ex) {
            throw runtime(ex, "error parsing private key from data = %s", abbreviate(data));
        }
    }

    public static PublicKey parsePublicKey(String data) {
        try {
            KeyFactory factory = KeyFactory.getInstance("RSA");
            data = data.replaceAll("\n|\r", "");
            Matcher matcher = Pattern.compile("(ssh-rsa +)?([^ ]+)( .*)?").matcher(trim(data));
            checkArgument(matcher.find(), "invalid public key format");
            ByteBuffer bytes = ByteBuffer.wrap(Base64.decodeBase64(matcher.group(2)));
            byte[] header = new byte[11];
            bytes.get(header);
            checkArgument(Arrays.equals(header, new byte[]{0x00, 0x00, 0x00, 0x07, 0x73, 0x73, 0x68, 0x2d, 0x72, 0x73, 0x61}), "invalid public key format");
            int expSize = bytes.getInt();
            byte[] expBytes = new byte[expSize];
            bytes.get(expBytes);
            BigInteger exp = new BigInteger(expBytes);
            int modSize = bytes.getInt();
            byte[] modBytes = new byte[modSize];
            bytes.get(modBytes);
            BigInteger mod = new BigInteger(modBytes);
            RSAPublicKeySpec pubKeySpec = new RSAPublicKeySpec(mod, exp);
            return factory.generatePublic(pubKeySpec);
        } catch (Exception ex) {
            throw runtime(ex, "error parsing public key from data = %s", abbreviate(data));
        }
    }

    private static byte[] convertPKCS1ToPKCS8(byte[] innerKey) {
        final byte[] result = new byte[innerKey.length + 26];
        System.arraycopy(Base64.decodeBase64("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKY="), 0, result, 0, 26);
        System.arraycopy(BigInteger.valueOf(result.length - 4).toByteArray(), 0, result, 2, 2);
        System.arraycopy(BigInteger.valueOf(innerKey.length).toByteArray(), 0, result, 24, 2);
        System.arraycopy(innerKey, 0, result, 26, innerKey.length);
        return result;
    }
}
