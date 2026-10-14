/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.mobile.notification;

import java.io.IOException;
import java.io.InputStream;
import static org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils.encryptValue;
import static org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils.isEncrypted;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;

/**
 *
 * @author afelice
 */
class TokenEncrypter {

    static String encrypt(InputStream inputStream) throws IOException {
        return encrypt(readToString(inputStream));
    }

    static String encrypt(String content) {
        String encryptedContent = content;
        setJavaEncryptionKey();
        if (!isEncrypted(content)) {
            encryptedContent = encryptValue(content);
        }

        return encryptedContent;
    }

    static void setJavaEncryptionKey() {
        System.setProperty("org.cmdbuild.cm3easy.key", "/usr/local/bin/key_cm3easy");
    }

    static void clearJavaEncryptionKey() {
        System.clearProperty("org.cmdbuild.cm3easy.key");
    }
}
