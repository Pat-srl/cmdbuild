package org.cmdbuild.auth.login.header;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.AuthenticationConfiguration;
import org.cmdbuild.auth.login.ClientRequestAuthenticator;
import org.cmdbuild.auth.login.LoginModuleConfiguration;
import static org.cmdbuild.auth.login.LoginModuleConfiguration.DEFAULT_LOGIN_MODULE_TYPE;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.login;
import org.cmdbuild.auth.user.GenericUserScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Authenticates a user based on the presence of a header parameter. It can be
 * used when a Single Sign-On proxy adds the header.
 */
@Component
public class HeaderAuthenticator implements ClientRequestAuthenticator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final HeaderAuthenticatorConfiguration conf;
    private final AuthenticationConfiguration authConf;
    private final GenericUserScriptService userScriptService;

    public HeaderAuthenticator(HeaderAuthenticatorConfiguration conf, AuthenticationConfiguration authConf, GenericUserScriptService userScriptService) {
        this.conf = checkNotNull(conf);
        this.authConf = checkNotNull(authConf);
        this.userScriptService = checkNotNull(userScriptService);
    }

    @Override
    public boolean isEnabled() {
        return conf.isHeaderEnabled();
    }

    @Override
    @Nullable
    public RequestAuthenticatorResponse<LoginUserIdentity> authenticate(AuthRequestInfo request) {
        String headerAttr = conf.getHeaderAttributeName(),
                loginString = request.getHeader(headerAttr);
        logger.trace("using header attr = {}", headerAttr);
        if ("GET".equalsIgnoreCase(request.getMethod()) && request.getRequestPath().matches("/services/rest/v[34]/sessions/current/?") && isNotBlank(loginString)) {
            LoginUserIdentity login;
            if (authConf.isDefaultLoginModuleEnabled()) {
                LoginModuleConfiguration loginModule = authConf.getLoginModuleByCode(DEFAULT_LOGIN_MODULE_TYPE);
                if (isBlank(loginModule.getLoginHandlerScript())) {
                    login = LoginUserIdentity.build(loginString);
                } else {
                    login = userScriptService.getLoginFromScript(loginModule, LoggerFactory.getLogger(format("%s.HANDLER", getClass().getName())), loginString);
                }
            } else {
                login = LoginUserIdentity.build(loginString);
            }
            logger.debug("authenticating user for login string =< {} >", loginString);
            return login(login);
        } else {
            return null;
        }
    }

}
