/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils.Cm3EasyCryptoHelper;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;

/**
 *
 * @author afelice
 */
public class TokenEncrypter {

    public static String encrypt(InputStream inputStream) throws IOException {
        return encrypt(IOUtils.toString(inputStream, Charset.defaultCharset()));
    }

    public static String encrypt(String content) {

        String encryptedContent = content;
        if (!Cm3EasyCryptoUtils.isEncrypted(content)) {
            encryptedContent = getCustomCm3EasyCryptoUtils().encryptValue(content);
        }

        return encryptedContent;
    }

    public static Cm3EasyCryptoHelper getCustomCm3EasyCryptoUtils() {
        return Cm3EasyCryptoUtils.customUtils(getCustomPrivateKey());
    }

    public static Cm3EasyCryptoHelper getCustomCm3EasyCryptoUtils(String customKey) {
        return Cm3EasyCryptoUtils.customUtils(toByteArray(new File(customKey)));
    }

    private static byte[] getCustomPrivateKey() {
        return toByteArray(new File("/usr/local/bin/key_cm3easy"));
    }
}
