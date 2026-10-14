/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.mobile.notification;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import static org.cmdbuild.mobile.notification.TokenEncrypter.setJavaEncryptionKey;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;

/**
 *
 * @author afelice
 */
class TokenLoaderHelper {

    /**
     * (Eventually) decrypt token using Cm3Easy crypt algorithm
     *
     * @param tokenAbsolutePath
     * @return
     * @throws IOException
     */
    static String loadCredentialsStr(String tokenAbsolutePath, String testClassName) {
        setJavaEncryptionKey();
        try (InputStream inputStream = loadCredentials(tokenAbsolutePath)) {
            return Cm3EasyCryptoUtils.decryptValue(IOUtils.toString(inputStream, Charset.defaultCharset()));
        } catch (IOException ex) {
            System.err.println(format("error in test %s - file =< %s > not found", testClassName, tokenAbsolutePath));
            return "";
        }
    }

    /**
     *
     * @return input stream; <b>remember to close the stream when done</b>
     * @throws IOException
     */
    static InputStream loadCredentials(String tokenAbsolutePath) throws IOException {
        InputStream in = TokenLoaderHelper.class.getResourceAsStream(tokenAbsolutePath);
        if (in == null) {
            throw new FileNotFoundException(format("Resource not found: %s", tokenAbsolutePath));
        }
        // Decomment to encrypt file content: see Output in wrapped text mode
//        System.out.println(
//                String.format("Encrypt of %s:%n%s", tokenAbsolutePath,
//                              TokenEncrypter.encrypt(
//                                      TokenLoaderHelper.class.getResourceAsStream(
//                                              tokenAbsolutePath))));
        return in;
    }
}
