/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import static org.apache.commons.codec.binary.Hex.encodeHexString;
import static org.apache.commons.codec.digest.DigestUtils.sha256;
import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;
import org.cmdbuild.utils.crypto.MagicUtils.MagicUtilsHelper;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmDataCryptoUtils {

    private final static int BUFFER_SIZE = 1024 * 1024 * 4;

    private final static MagicUtilsHelper MAGIC_HELPER = MagicUtils.helper(encodeHexString(sha256("CMDBUILD_ENCRYPTED_DATA_V1")).substring(0, 6), 7, 13, 19, 21, 24, 27);

    public static boolean isEncrypted(InputStream in) {
        return MAGIC_HELPER.hasMagic(in);
    }

    public static boolean isEncrypted(byte[] data) {
        return MAGIC_HELPER.hasMagic(data);
    }

    public static CmDataCryptoHelper withPassword(String password) {
        return new CmDataCryptoHelper(checkNotBlank(password).getBytes(UTF_8));
    }

    public static class CmDataCryptoHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Key key;
        private final IvParameterSpec iv;
        private final Cipher cipher;
        private final String keyId;

        private CmDataCryptoHelper(byte[] source) {
            try {
                logger.debug("creating crypto utils from source = {}", abbreviate(encodeHexString(source)), 20);
                source = source.length == 32 ? source : sha256(source);
                byte[] keyBytes = Arrays.copyOfRange(source, 1, 17),
                        ivBytes = Arrays.copyOfRange(source, 15, 31);
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

        public void encrypt(InputStream in, OutputStream out) {
            try {
                out = encrypt(out);
                copy(in, out);
                out.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        public OutputStream encrypt(OutputStream out) {
            try {
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
                return new FilterOutputStream(MAGIC_HELPER.addMagicOnClose(out)) {
                    @Override
                    public void close() throws IOException {
                        try {
                            Optional.ofNullable(cipher.doFinal()).ifPresent(rethrowConsumer(out::write));
                            super.close();
                        } catch (IllegalBlockSizeException | BadPaddingException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    @Override
                    public void write(byte[] b, int off, int len) throws IOException {
                        Optional.ofNullable(cipher.update(b, off, len)).ifPresent(rethrowConsumer(out::write));
                    }

                    @Override
                    public void write(byte[] b) throws IOException {
                        Optional.ofNullable(cipher.update(b)).ifPresent(rethrowConsumer(out::write));
                    }

                    @Override
                    public void write(int b) throws IOException {
                        write(new byte[]{(byte) b});
                    }

                };
            } catch (InvalidKeyException | InvalidAlgorithmParameterException ex) {
                throw new RuntimeException(ex);
            }
        }

        public void decrypt(InputStream in, OutputStream out) {
            try {
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
                byte[] buffer = new byte[BUFFER_SIZE];
                int count = in.read(buffer);
                byte[] header = Arrays.copyOfRange(buffer, 0, count);
                checkArgument(MAGIC_HELPER.hasMagic(header), "invalid data: failed to detect magic header");
                Optional.ofNullable(cipher.update(MAGIC_HELPER.stripMagic(header))).ifPresent(rethrowConsumer(out::write));
                while ((count = in.read(buffer)) != -1) {
                    Optional.ofNullable(cipher.update(buffer, 0, count)).ifPresent(rethrowConsumer(out::write));
                }
                Optional.ofNullable(cipher.doFinal()).ifPresent(rethrowConsumer(out::write));
                out.flush();
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        public byte[] decryptBytes(InputStream in) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            decrypt(in, out);
            return out.toByteArray();
        }

        public byte[] decryptBytes(byte[] data) {
            return decryptBytes(new ByteArrayInputStream(data));
        }

        public byte[] encryptBytes(InputStream in) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encrypt(in, out);
            return out.toByteArray();
        }

        public byte[] encryptBytes(byte[] data) {
            return encryptBytes(new ByteArrayInputStream(data));
        }

    }

}
