package org.cmdbuild.auth.login.rsa;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.security.PublicKey;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cmdbuild.auth.config.AuthenticationServiceConfiguration;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.PasswordAuthenticator;
import org.cmdbuild.auth.login.PasswordCheckStatus;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_ACCESS_DENIED;
import static org.cmdbuild.auth.login.PasswordCheckStatus.PCR_HAS_VALID_PASSWORD;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.utils.crypto.CmRsaUtils.parsePublicKey;
import static org.cmdbuild.utils.crypto.CmRsaUtils.verifyToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class RsaAuthenticator implements PasswordAuthenticator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration coreConfiguration;
    private final AuthenticationServiceConfiguration configuration;

    public RsaAuthenticator(CoreConfiguration coreConfiguration, AuthenticationServiceConfiguration configuration) {
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.configuration = checkNotNull(configuration);
    }

    @Override
    public boolean isEnabled() {
        return configuration.isRsaEnabled();
    }

    @Override
    public PasswordCheckStatus verifyPassword(LoginUserIdentity login, String password) {
        Matcher matcher = Pattern.compile("rsa:(.+)").matcher(nullToEmpty(password));
        if (matcher.matches()) {
            String token = matcher.group(1);
            return validateToken(token, emptyList()) ? PCR_HAS_VALID_PASSWORD : PCR_ACCESS_DENIED;//TODO get user keys
        } else {
            return PCR_ACCESS_DENIED;
        }
    }

    private boolean validateToken(String token, List<String> userKeys) {
        for (String keyStr : list(userKeys).with(coreConfiguration.getTrustedKeys())) {
            logger.debug("attempt to validate token = {} with key = {}", token, keyStr);
            PublicKey key;
            try {
                key = parsePublicKey(keyStr);
            } catch (Exception ex) {
                logger.error("unable to parse public key from value = {}", keyStr, ex);
                continue;
            }
            try {
                if (verifyToken(token, key)) {
                    logger.debug("successfully validatet token = {} with key = {}", token, keyStr);
                    return true;
                }
            } catch (Exception ex) {
                logger.error("error verifying challenge response", ex);
            }
        }
        return false;
    }

}
