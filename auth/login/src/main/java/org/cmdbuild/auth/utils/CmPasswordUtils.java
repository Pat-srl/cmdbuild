/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.utils;

import static com.google.common.base.Objects.equal;
import java.lang.invoke.MethodHandles;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.login.PasswordAlgo;
import static org.cmdbuild.auth.login.PasswordAlgo.PA_CM3;
import static org.cmdbuild.auth.login.PasswordAlgo.PA_CM3EASY;
import static org.cmdbuild.auth.login.PasswordAlgo.PA_LEGACY;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.cmdbuild.utils.crypto.Cm3PasswordUtils;
import org.cmdbuild.utils.crypto.CmLegacyPasswordUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmPasswordUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Nullable
    public static String encryptPassword(@Nullable String plaintextPassword, PasswordAlgo algo) {
        if (isBlank(plaintextPassword)) {
            return null;
        } else {
            return switch (algo) {
                case PA_CM3 ->
                    Cm3PasswordUtils.hash(plaintextPassword);
                case PA_CM3EASY ->
                    Cm3EasyCryptoUtils.encryptValue(plaintextPassword);
                case PA_LEGACY ->
                    CmLegacyPasswordUtils.encrypt(plaintextPassword);
                default ->
                    throw new UnsupportedOperationException("unsupported password algorythm = " + algo);
            };
        }
    }

    @Nullable
    public static String decryptPasswordIfPossible(@Nullable String password) {
        if (isBlank(password)) {
            return null;
        } else if (Cm3PasswordUtils.isEncrypted(password)) {
            LOGGER.trace("password encrypted with CM3, unable to decrypt");
            return null;
        } else if (Cm3EasyCryptoUtils.isEncrypted(password)) {
            LOGGER.trace("password encrypted with CM3EASY");
            return Cm3EasyCryptoUtils.decryptValue(password);
        } else {
            LOGGER.trace("password encrypted with CMLEGACY");
            return CmLegacyPasswordUtils.decrypt(password);
        }
    }

    public static boolean verifyPassword(@Nullable String plaintextPassword, @Nullable String storedEncryptedPassword) {
        try {
            if (isBlank(plaintextPassword) || isBlank(storedEncryptedPassword)) {
                return false;
            } else if (Cm3PasswordUtils.isEncrypted(storedEncryptedPassword)) {
                LOGGER.trace("password encrypted with CM3");
                return Cm3PasswordUtils.isValid(plaintextPassword, storedEncryptedPassword);
            } else if (Cm3EasyCryptoUtils.isEncrypted(storedEncryptedPassword)) {
                LOGGER.trace("password encrypted with CM3EASY");
                return equal(plaintextPassword, Cm3EasyCryptoUtils.decryptValue(storedEncryptedPassword));
            } else if (CmLegacyPasswordUtils.isEncrypted(storedEncryptedPassword)) {
                LOGGER.trace("password encrypted with CMLEGACY");
                return equal(plaintextPassword, CmLegacyPasswordUtils.decrypt(storedEncryptedPassword));
            } else {
                LOGGER.warn("unknown password format, password verification denied");
                return false;
            }
        } catch (Exception ex) {
            LOGGER.error("error checking password", ex);
            return false;
        }
    }

    public static PasswordAlgo detectPasswordAlgo(String password) {
        checkNotBlank(password);
        if (Cm3PasswordUtils.isEncrypted(password)) {
            return PA_CM3;
        } else if (Cm3EasyCryptoUtils.isEncrypted(password)) {
            return PA_CM3EASY;
        } else {
            return PA_LEGACY;
        }
    }
}
