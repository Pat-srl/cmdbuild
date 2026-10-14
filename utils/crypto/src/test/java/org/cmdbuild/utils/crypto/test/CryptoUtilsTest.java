package org.cmdbuild.utils.crypto.test;

import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testKeyId() throws Exception {
        logger.info("testKeyId BEGIN");
        assertEquals("e47f9eda18a855f9", Cm3EasyCryptoUtils.defaultUtils().getKeyId());
        logger.info("testKeyId END");
    }

    @Test
    public void testDecryption() throws Exception {
        assertEquals("passwordOne", Cm3EasyCryptoUtils.decryptValue("31b57b1d45d78df54981f7c05593094486e64d"));
    }

    @Test
    public void testEncryptionAndDecryption() throws Exception {
        logger.info("testEncryptionAndDecryption BEGIN");
        String value = "this is a param value to be encrypted";

        String encryptedValue = Cm3EasyCryptoUtils.encryptValue(value);
        assertNotEquals(encryptedValue, value);

        String decryptedValue = Cm3EasyCryptoUtils.decryptValue(encryptedValue);
        assertEquals(decryptedValue, value);
        logger.info("testEncryptionAndDecryption END");
    }

    @Test
    public void testEncryptionAndDecryption1() throws Exception {
        logger.info("testEncryptionAndDecryption1 BEGIN");
        String value = "p";

        String encryptedValue = Cm3EasyCryptoUtils.encryptValue(value);
        assertNotEquals(encryptedValue, value);

        String decryptedValue = Cm3EasyCryptoUtils.decryptValue(encryptedValue);
        assertEquals(decryptedValue, value);
        logger.info("testEncryptionAndDecryption1 END");
    }

    @Test
    public void testEncryptionAndDecryption3() throws Exception {
        logger.info("testEncryptionAndDecryption3 BEGIN");
        {
            String value = "passwordOne";
            String encryptedValue = Cm3EasyCryptoUtils.encryptValue(value);
            assertNotEquals(encryptedValue, value);

            String decryptedValue = Cm3EasyCryptoUtils.decryptValue(encryptedValue);
            assertEquals(decryptedValue, value);
        }
        {
            String value = "passwordTwo";
            String encryptedValue = Cm3EasyCryptoUtils.encryptValue(value);
            assertNotEquals(encryptedValue, value);

            String decryptedValue = Cm3EasyCryptoUtils.decryptValue(encryptedValue);
            assertEquals(decryptedValue, value);
        }
        logger.info("testEncryptionAndDecryption3 END");
    }

    @Test
    public void testEncryptionAndDecryption4() throws Exception {
        assertFalse(Cm3EasyCryptoUtils.isEncrypted("e26289aa314f1cd45e669db58e2393f9"));
    }

    @Test
    public void testCustomKey1() throws Exception {
        logger.info("testCustomKey1 BEGIN");

        String value = "passwordOne";
        String encryptedValue = Cm3EasyCryptoUtils.customUtils("custom".getBytes()).encryptValue(value);
        assertNotEquals(encryptedValue, value);

        try {
            String decryptedValue = Cm3EasyCryptoUtils.decryptValue(encryptedValue);
            fail();
        } catch (RuntimeException ex) {
            //expected
        }

        String decryptedValue = Cm3EasyCryptoUtils.customUtils("custom".getBytes()).decryptValue(encryptedValue);
        assertEquals(decryptedValue, value);

        logger.info("testCustomKey1 END");
    }

    @Test
    public void testEncryptionAndDecryption2() throws Exception {
        logger.info("testEncryptionAndDecryption2 BEGIN");
        String value = "this is a param value to be encrypted";

        String encryptedValue = Cm3EasyCryptoUtils.encryptValue(value);
        assertNotEquals(encryptedValue, value);

        String decryptedValue = Cm3EasyCryptoUtils.decryptValue(encryptedValue);
        assertEquals(decryptedValue, value);
        logger.info("testEncryptionAndDecryption2 END");
    }

    @Test
    public void testHandleEmptyValues1() throws Exception {
        logger.info("testHandleEmptyValues1 BEGIN");
        String value = "  ";

        String encryptedValue = Cm3EasyCryptoUtils.encryptValue(value);
        assertEquals(encryptedValue, value);

        String decryptedValue = Cm3EasyCryptoUtils.decryptValue(encryptedValue);
        assertEquals(decryptedValue, value);
        logger.info("testHandleEmptyValues1 END");
    }

    @Test
    public void testHandleEmptyValues2() throws Exception {
        logger.info("testHandleEmptyValues2 BEGIN");
        String value = "";

        String encryptedValue = Cm3EasyCryptoUtils.encryptValue(value);
        assertEquals(encryptedValue, value);

        String decryptedValue = Cm3EasyCryptoUtils.decryptValue(encryptedValue);
        assertEquals(decryptedValue, value);
        logger.info("testHandleEmptyValues2 END");
    }

    @Test
    public void testHandleEmptyValues3() throws Exception {
        logger.info("testHandleEmptyValues3 BEGIN");
        String value = null;

        String encryptedValue = Cm3EasyCryptoUtils.encryptValue(value);
        assertEquals(encryptedValue, value);

        String decryptedValue = Cm3EasyCryptoUtils.decryptValue(encryptedValue);
        assertEquals(decryptedValue, value);
        logger.info("testHandleEmptyValues3 END");
    }

}
