package org.cmdbuild.auth.login.file;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import org.cmdbuild.auth.config.AuthenticationServiceConfiguration;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.PasswordAuthenticator;
import org.cmdbuild.auth.login.PasswordCheckStatus;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_ACCESS_DENIED;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_HAS_VALID_PASSWORD;
import static org.cmdbuild.auth.login.file.FileAuthUtils.isAuthFilePassword;
import org.springframework.stereotype.Component;
import static org.cmdbuild.auth.login.file.FileAuthUtils.isValidAuthFilePassword;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class FileAuthenticator implements PasswordAuthenticator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthenticationServiceConfiguration configuration;

    public FileAuthenticator(AuthenticationServiceConfiguration configuration) {
        this.configuration = checkNotNull(configuration);
    }

    @Override
    public PasswordCheckStatus verifyPassword(LoginUserIdentity login, String password) {
        File authDir = new File(System.getProperty("java.io.tmpdir"));
        logger.debug("check file password with authDir =< {} >", authDir.getAbsolutePath());
        return isAuthFilePassword(password) && isValidAuthFilePassword(authDir, password) ? PCR_HAS_VALID_PASSWORD : PCR_ACCESS_DENIED;
    }

    @Override
    public boolean isEnabled() {
        return configuration.isFileEnabled();
    }

}
