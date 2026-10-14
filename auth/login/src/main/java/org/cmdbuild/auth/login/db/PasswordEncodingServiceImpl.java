/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login.db;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.login.PasswordEncodingService;
import org.cmdbuild.auth.utils.CmPasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class PasswordEncodingServiceImpl implements PasswordEncodingService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationConfiguration config;

    public PasswordEncodingServiceImpl(AuthenticationConfiguration config) {
        this.config = checkNotNull(config);
    }

    @Override
    @Nullable
    public String encryptPassword(@Nullable String plaintextPassword) {
        return CmPasswordUtils.encryptPassword(plaintextPassword, config.getPreferredPasswordAlgorythm());
    }

    @Override
    public boolean verifyPassword(@Nullable String plaintextPassword, @Nullable String storedEncryptedPassword) {
        return CmPasswordUtils.verifyPassword(plaintextPassword, storedEncryptedPassword);
    }

    @Override
    @Nullable
    public String decryptPasswordIfPossible(@Nullable String password) {
        return CmPasswordUtils.decryptPasswordIfPossible(password);
    }

}
