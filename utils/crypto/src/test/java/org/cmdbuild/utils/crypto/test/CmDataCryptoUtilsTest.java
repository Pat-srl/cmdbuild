/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.crypto.test;

import java.io.ByteArrayInputStream;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Random;
import java.util.stream.IntStream;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.cmdbuild.utils.crypto.CmDataCryptoUtils;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmDataCryptoUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testDataEncryption1() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, DecoderException {
        byte[] data = "ciao come va?".getBytes(UTF_8);
        byte[] encrypted = CmDataCryptoUtils.withPassword("myPassword").encryptBytes(data);

        logger.info("data = {} encrypted = {}", Hex.encodeHexString(data), Hex.encodeHexString(encrypted));

        assertThat(data, not(equalTo(encrypted)));

        assertFalse(CmDataCryptoUtils.isEncrypted(data));
        assertFalse(CmDataCryptoUtils.isEncrypted(new ByteArrayInputStream(data)));

        assertTrue(CmDataCryptoUtils.isEncrypted(encrypted));
        assertTrue(CmDataCryptoUtils.isEncrypted(new ByteArrayInputStream(encrypted)));

        assertEquals("ciao come va?", new String(CmDataCryptoUtils.withPassword("myPassword").decryptBytes(encrypted), UTF_8));
    }

    @Test
    public void testDataEncryption1a() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, DecoderException {
        byte[] data = Hex.decodeHex("56fac2b4d4625232449f0b7e5c64fde90c3a61666530");
        assertTrue(CmDataCryptoUtils.isEncrypted(data));
        assertEquals("ciao come va?", new String(CmDataCryptoUtils.withPassword("myPassword").decryptBytes(data), UTF_8));
    }

    @Test
    public void testDataEncryption2() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, DecoderException {
        String string = "aasasdasdasdqweqweqwewq1435234t25tf45t3f45tv435tv45tv45";
        IntStream.rangeClosed(0, string.length()).mapToObj(i -> string.substring(0, i).getBytes(UTF_8)).forEach(rethrowConsumer(data -> {
            byte[] encrypted = CmDataCryptoUtils.withPassword("myPassword").encryptBytes(data);

            logger.info("data = {} encrypted = {}", Hex.encodeHexString(data), Hex.encodeHexString(encrypted));

            assertThat(data, not(equalTo(encrypted)));

            assertFalse(CmDataCryptoUtils.isEncrypted(data));
            assertFalse(CmDataCryptoUtils.isEncrypted(new ByteArrayInputStream(data)));

            assertTrue(CmDataCryptoUtils.isEncrypted(encrypted));
            assertTrue(CmDataCryptoUtils.isEncrypted(new ByteArrayInputStream(encrypted)));

            assertArrayEquals(data, CmDataCryptoUtils.withPassword("myPassword").decryptBytes(encrypted));
        }));
    }

    @Test
    public void testDataEncryption3() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, DecoderException {
        byte[] data = new byte[10 * 1024 * 1024];
        new Random().nextBytes(data);

        byte[] encrypted = CmDataCryptoUtils.withPassword("myPassword").encryptBytes(data);

        assertThat(data, not(equalTo(encrypted)));

        assertFalse(CmDataCryptoUtils.isEncrypted(data));
        assertFalse(CmDataCryptoUtils.isEncrypted(new ByteArrayInputStream(data)));

        assertTrue(CmDataCryptoUtils.isEncrypted(encrypted));
        assertTrue(CmDataCryptoUtils.isEncrypted(new ByteArrayInputStream(encrypted)));

        assertArrayEquals(data, CmDataCryptoUtils.withPassword("myPassword").decryptBytes(encrypted));
    }

    @Test(expected = RuntimeException.class)
    public void testDataEncryption4a() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, DecoderException {
        byte[] encrypted = CmDataCryptoUtils.withPassword("myPassword").encryptBytes("ciao come va?".getBytes(UTF_8));
        assertEquals("ciao come va?", new String(CmDataCryptoUtils.withPassword("myPassword_").decryptBytes(encrypted), UTF_8));
    }

    @Test
    public void testDataEncryption4b() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, DecoderException {
        byte[] encrypted = CmDataCryptoUtils.withPassword("myPassword").encryptBytes("ciao come va?".getBytes(UTF_8));
        assertEquals("ciao come va?", new String(CmDataCryptoUtils.withPassword("myPassword").decryptBytes(encrypted), UTF_8));
    }
}
