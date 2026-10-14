/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import static org.cmdbuild.email.mta.TokenEncrypter.getCustomCm3EasyCryptoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;

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
        try (InputStream inputStream = loadCredentials(tokenAbsolutePath)) {
            return getCustomCm3EasyCryptoUtils().decryptValue(readToString(inputStream));
        } catch (IOException ex) {
            System.err.println(String.format("error in test %s - file =< %s > not found", testClassName, tokenAbsolutePath));
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
        return in;
    }
}
