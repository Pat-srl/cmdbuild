package org.cmdbuild.auth.login.cas;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apereo.cas.client.util.CommonUtils;
import org.apereo.cas.client.validation.Assertion;
import org.apereo.cas.client.validation.Cas20ServiceTicketValidator;
import org.apereo.cas.client.validation.TicketValidationException;
import org.apereo.cas.client.validation.TicketValidator;
import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.LoginModuleClientRequestAuthenticator;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.login;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.redirect;
import static org.cmdbuild.auth.login.cas.CasAuthenticatorConfiguration.CAS_LOGIN_MODULE_TYPE;
import org.cmdbuild.auth.user.GenericUserScriptService;
import static org.cmdbuild.auth.utils.RequestAuthUtils.shouldRedirectToLoginPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CasAuthenticator implements LoginModuleClientRequestAuthenticator<CasAuthenticatorConfiguration> {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final GenericUserScriptService userScriptService;

    public CasAuthenticator(GenericUserScriptService userScriptService) {
        this.userScriptService = checkNotNull(userScriptService);
    }

    @Override
    public String getType() {
        return CAS_LOGIN_MODULE_TYPE;
    }

    @Override
    @Nullable
    public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthRequest(AuthRequestInfo request, CasAuthenticatorConfiguration conf) {
        return new CasAuthenticatorHelper(conf).handleAuthRequest(request);
    }

    @Override
    public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthResponse(AuthRequestInfo request, CasAuthenticatorConfiguration conf) {
        return new CasAuthenticatorHelper(conf).handleAuthResponse(request);
    }

    private class CasAuthenticatorHelper {

        private final CasAuthenticatorConfiguration config;
//        private final String stateParam;

        public CasAuthenticatorHelper(CasAuthenticatorConfiguration config) {
            this.config = checkNotNull(config);
//            this.stateParam = format("cm_cas_%s", config.getCode().replaceAll("[^a-zA-Z0-9]", ""));
        }

        @Nullable
        public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthRequest(AuthRequestInfo request) {
            if (shouldRedirectToLoginPage(request)) {
                String casServerLoginUrl = config.getCasServerUrl() + config.getCasLoginPage(),
                        //                        tempId = temp.putTempData(buildRequestInfo(request)),
                        serviceUrl = request.getRequestUrl(),
                        //                        serviceUrl = format("%s?%s=%s", request.getRequestUrl(), stateParam, tempId),// does not work, param stripped by CAS service
                        //                    serviceUrl = request.getRequestUrlWithFragment(), // does not work, fragment stripped by CAS service
                        // NOTE: it is not possible to add a state/tracking param to cas request, so it is not possibile to recover fragment information !!
                        redirectUrl = CommonUtils.constructRedirectUrl(casServerLoginUrl, config.getCasServiceParam(), serviceUrl, false, false);
                logger.debug("redirect to cas url =< {} > (service url =< {} >)", redirectUrl, serviceUrl);
                return redirect(redirectUrl);
            } else {
                return null;
            }
        }

        public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthResponse(AuthRequestInfo request) {
            String userFromTicket = getValidatedUsernameFromTicketOrNull(request);
            if (userFromTicket != null) {
                LoginUserIdentity login;
                if (isBlank(config.getLoginHandlerScript())) {
                    login = LoginUserIdentity.build(userFromTicket);
                } else {
                    login = userScriptService.getLoginFromScript(config, LoggerFactory.getLogger(format("%s.HANDLER", getClass().getName())), userFromTicket);
                }
//                String redirectUrl = getRedirectUrlFromRequestInfo(temp.getTempDataAsString(request.getParameter(stateParam))); //            String redirectUrl = request.getRequestUrl();
                // NOTE: it is not possible to add a state/tracking param to cas request, so it is not possibile to recover fragment information !!
                String redirectUrl = request.getRequestUrl();
                logger.debug("cas returned login identity =< {} >, redirect to initial landing page =< {} >", login.getValue(), redirectUrl);
                return login(login).withRedirect(redirectUrl);
            } else {
                return null;
            }
        }

        @Nullable
        private String getValidatedUsernameFromTicketOrNull(AuthRequestInfo request) {
            try {
                String ticket = request.getParameter(config.getCasTicketParam());
                if (isBlank(ticket)) {
                    return null;
                } else {
                    logger.debug("processing cas ticket =< {} >", ticket);
                    String service = request.getRequestUrl();
                    TicketValidator ticketValidator = new Cas20ServiceTicketValidator(config.getCasServerUrl());
                    Assertion assertion = ticketValidator.validate(ticket, service);
                    String username = assertion.getPrincipal().getName();
                    logger.debug("validated cas ticket, got username =< {} >", username);
                    return username;
                }
            } catch (TicketValidationException ex) {
                logger.warn("ticket validation exception", ex);
                return null;
            }
        }
    }
}
