package org.cmdbuild.auth.login.saml;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.onelogin.saml2.Auth;
import com.onelogin.saml2.logout.LogoutRequestParams;
import com.onelogin.saml2.settings.Saml2Settings;
import com.onelogin.saml2.settings.SettingsBuilder;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import static java.lang.String.format;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.AuthenticationException;
import org.cmdbuild.auth.login.LoginModuleClientRequestAuthenticator;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.login;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.redirect;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.response;
import org.cmdbuild.auth.login.SessionDataSupplier;
import static org.cmdbuild.auth.login.saml.SamlAuthenticatorConfiguration.SAML_LOGIN_MODULE_TYPE;
import static org.cmdbuild.auth.utils.RequestAuthUtils.shouldRedirectToLoginPage;
import org.cmdbuild.ui.UiBaseUrlService;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.xml.CmXmlUtils.prettifyXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SamlAuthenticator implements LoginModuleClientRequestAuthenticator<SamlAuthenticatorConfiguration> {

    private final static String SAML_NAME_ID = "org.cmdbuild.auth.login.saml.SAML_NAME_ID", SAML_SESSION_INDEX = "org.cmdbuild.auth.login.saml.SAML_SESSION_INDEX";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UiBaseUrlService baseUrlService;
    private final SamlUserScriptService userScriptService;
    private final SessionDataSupplier sessionDataService;

    public SamlAuthenticator(UiBaseUrlService baseUrlService, SamlUserScriptService userScriptService, SessionDataSupplier sessionDataService) {
        this.baseUrlService = checkNotNull(baseUrlService);
        this.userScriptService = checkNotNull(userScriptService);
        this.sessionDataService = checkNotNull(sessionDataService);
    }

    @Override
    public String getType() {
        return SAML_LOGIN_MODULE_TYPE;
    }

    @Override
    public RequestAuthenticatorResponse handleAuthRequest(AuthRequestInfo request, SamlAuthenticatorConfiguration loginModuleConfiguration) {
        return new SamlAuthenticatorHelper(loginModuleConfiguration).handleAuthRequest(request);
    }

    @Override
    public RequestAuthenticatorResponse handleAuthResponse(AuthRequestInfo request, SamlAuthenticatorConfiguration loginModuleConfiguration) {
        return new SamlAuthenticatorHelper(loginModuleConfiguration).handleAuthResponse(request);
    }

    @Override
    public RequestAuthenticatorResponse logout(Object request, SamlAuthenticatorConfiguration loginModuleConfiguration) {
        return new SamlAuthenticatorHelper(loginModuleConfiguration).logout(request);
    }

    private class SamlAuthenticatorHelper {

        private final SamlAuthenticatorConfiguration config;

        public SamlAuthenticatorHelper(SamlAuthenticatorConfiguration config) {
            this.config = checkNotNull(config);
        }

        @Nullable
        public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthRequest(AuthRequestInfo request) {
            if (shouldRedirectToLoginPage(request)) {
                Saml2Settings samlSettings = getSamlSettings(request.getInner());
                String redirect = request.getRequestUrl();
                return response((response) -> {
                    try {
                        logger.debug("redirect request to saml idp =< {} > url =< {} >", config.getSamlIdpEntityId(), config.getSamlIdpLoginUrl());
                        Auth auth = new Auth(samlSettings, request.getInner(), response);
                        auth.login(redirect);
                    } catch (Exception ex) {
                        throw runtime(ex);
                    }
                });
            } else {
                return null;
            }
        }

        @Nullable
        public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthResponse(AuthRequestInfo request) {
            Auth auth = null;
            if ("POST".equalsIgnoreCase(request.getMethod()) && request.getRequestPath().matches("(?i)/services/saml/(SSO|login)/?")) {
                try {
                    logger.debug("processing saml login request (response from idp)");
                    auth = new Auth(getSamlSettings(request.getInner()), request.getInner(), null);
                    auth.processResponse();
                    List<String> errors = auth.getErrors();
                    checkArgument(errors.isEmpty(), "saml error: %s", Joiner.on(", ").join(errors));

                    Map<String, List<String>> attributes = auth.getAttributes();

                    String nameId = auth.getNameId(), sessionIndex = auth.getSessionIndex();

                    logger.debug("received saml user with nameId =< {} > attrs = \n\n{}\n", nameId, mapToLoggableStringLazy(attributes));

                    Map<String, String> customSessionAttributes = map(
                            SAML_NAME_ID, nameId,
                            SAML_SESSION_INDEX, sessionIndex
                    );

                    LoginUserIdentity login;
                    if (isBlank(config.getLoginHandlerScript())) {
                        login = LoginUserIdentity.build(nameId);
                    } else {
                        AuthResponse api = new AuthResponse() {
                            @Override
                            public String getAttribute(String name) {
                                return getOnlyElement(firstNotNull(attributes.get(name), emptyList()), null);
                            }

                            @Override
                            public String getNameId() {
                                return nameId;
                            }
                        };
                        login = userScriptService.getLoginFromScript(config, LoggerFactory.getLogger(format("%s.HANDLER", getClass().getName())), api);
                    }
                    logger.debug("saml returned login identity =< {} >", login.getValue());

                    RequestAuthenticatorResponseImpl<LoginUserIdentity> response = login(login);

                    String relayState = request.getParameter("RelayState");
                    if (isNotBlank(relayState)) {
                        response = response.withRedirect(relayState);
                    } else {
                        response = response.withRedirect(getBaseUrl(request.getInner()));
                    }

                    logger.debug("custom session attrs = \n\n{}\n", mapToLoggableStringLazy(customSessionAttributes));

                    return response.withCustomAttributes(customSessionAttributes);
                } catch (Exception ex) {
                    logMessagesSafeOnError(auth);
                    throw new AuthenticationException(ex, "unable to authenticate saml request");
                }
            } else if (nullToEmpty(request.getMethod()).matches("(?i)GET|POST") && request.getRequestPath().matches("(?i)/services/saml/(SLO|logout|SingleLogout)/?")) {
                try {
                    logger.debug("processing saml logout request (response from idp)");
                    auth = new Auth(getSamlSettings(request.getInner()), request.getInner(), null);
                    auth.processSLO();
                    List<String> errors = auth.getErrors();
                    checkArgument(errors.isEmpty(), "saml error: %s", Joiner.on(", ").join(errors));
                    return redirect(getBaseUrl(request.getInner()));
                } catch (Exception ex) {
                    logMessagesSafeOnError(auth);
                    throw new AuthenticationException(ex, "unable to process saml logout response");
                }
            } else {
                return null;
            }
        }

        @Nullable
        public RequestAuthenticatorResponse<Void> logout(@Nullable Object request) {
            if (config.isSamlLogoutEnabled()) {
                if (request == null) {
//                logger.warn(marker(), "skip saml logout: missing request bean");
                    logger.debug("skip saml logout: missing request bean");//TODO improve this
                    return null;
                } else {
                    try {
                        Auth auth = new Auth(getSamlSettings(request), (HttpServletRequest) request, null);
                        String samlNameId = sessionDataService.getCurrentSessionData(SAML_NAME_ID), samlSessionIndex = sessionDataService.getCurrentSessionData(SAML_SESSION_INDEX);
                        if (isBlank(samlNameId) || isBlank(samlSessionIndex)) {
                            logger.warn("unable to execute saml logout: missing session data");//TODO improve this, check that user is saml-authenticated!
                            return null;
                        } else {
                            String logoutUrl = auth.logout(null, new LogoutRequestParams(samlSessionIndex, samlNameId), true);
                            logger.debug("saml logout url =< {} >", logoutUrl);
//                   logger.debug("oauth logout url =< {} >, post logout redirect url =< {} >", logoutUrl, postLogoutRedirectUri);
                            return redirect(logoutUrl);
                        }
                    } catch (Exception ex) {
                        throw runtime(ex);
                    }
                }
            } else {
                return null;
            }
        }

        public Saml2Settings getSamlSettings(Object servletRequest) {
            try {
                String baseUrl = getBaseUrl(servletRequest);

                try {
                    X509Certificate idpCertificate = com.onelogin.saml2.util.Util.loadCert(checkNotBlank(config.getSamlIdpCertificate(), "missing saml identity provider certificate"));
                    logger.debug("idp certificate OK: {}", certificateInfo(idpCertificate));
                } catch (Exception ex) {
                    throw new AuthenticationException(ex, "invalid saml idp certificate config =< %s >", abbreviate(config.getSamlIdpCertificate()));
                }
                /*
                saml onelogin confg example here: https://github.com/onelogin/java-saml/blob/master/samples/java-saml-tookit-jspsample/src/main/resources/onelogin.saml.properties
                 */

                FluentMap<String, Object> map = map(
                        "onelogin.saml2.unique_id_prefix", "CMDBUILD_",
                        "onelogin.saml2.debug", logger.isDebugEnabled(),
                        "onelogin.saml2.strict", config.isSamlValidationStrict(),
                        "onelogin.saml2.sp.entityid", config.getSamlServiceProviderEntityId(),
                        "onelogin.saml2.sp.assertion_consumer_service.url", baseUrl + "/services/saml/SSO",
                        "onelogin.saml2.sp.single_logout_service.url", baseUrl + "/services/saml/SingleLogout",
                        "onelogin.saml2.security.want_xml_validation", config.isSamlXmlValidation(),
                        "onelogin.saml2.idp.entityid", checkNotBlank(config.getSamlIdpEntityId(), "missing saml identity provider id"),
                        "onelogin.saml2.idp.single_sign_on_service.url", checkNotBlank(config.getSamlIdpLoginUrl(), "missing saml login url"),
                        "onelogin.saml2.idp.single_logout_service.url", config.getSamlIdpLogoutUrl(),
                        "onelogin.saml2.idp.x509cert", checkNotBlank(config.getSamlIdpCertificate()),
                        "onelogin.saml2.security.want_messages_signed", config.getSamlRequireSignedMessages(),
                        "onelogin.saml2.security.want_assertions_signed", config.getSamlRequireSignedAssertions(),
                        "onelogin.saml2.security.signature_algorithm", config.getSamlSignatureAlgorithm()
                );

                if (isNotBlank(config.getSamlServiceProviderCertificate()) && isNotBlank(config.getSamlServiceProviderKey())) {

                    try {
                        X509Certificate spCertificate = com.onelogin.saml2.util.Util.loadCert(config.getSamlServiceProviderCertificate());
                        logger.debug("sp certificate OK: {}", certificateInfo(spCertificate));
                    } catch (Exception ex) {
                        throw new AuthenticationException(ex, "invalid saml sp certificate config =< %s >", abbreviate(config.getSamlServiceProviderCertificate()));
                    }

                    try {
                        PrivateKey privateKey = com.onelogin.saml2.util.Util.loadPrivateKey(config.getSamlServiceProviderKey());
                        logger.debug("sp key OK: format =< {} > algo =< {} >", privateKey.getFormat(), privateKey.getAlgorithm());
                    } catch (Exception ex) {
                        throw new AuthenticationException(ex, "invalid saml sp key config =< %s >", abbreviate(config.getSamlServiceProviderKey()));
                    }

                    map.with(
                            "onelogin.saml2.sp.x509cert", config.getSamlServiceProviderCertificate(),
                            "onelogin.saml2.sp.privatekey", config.getSamlServiceProviderKey(),
                            "onelogin.saml2.security.authnrequest_signed", true,
                            "onelogin.saml2.security.logoutrequest_signed", true,
                            "onelogin.saml2.security.logoutresponse_signed", true,
                            "onelogin.saml2.security.sign_metadata", true);
                }

                logger.debug("saml authenticator config = \n\n{}\n", mapToLoggableStringLazy(map));
                Saml2Settings settings = new SettingsBuilder().fromValues(map).build();
                List<String> errors = settings.checkSettings();
                checkArgument(errors.isEmpty(), "invalid saml configuration: %s", Joiner.on(", ").join(errors));
                logger.debug("saml metadata = \n\n{}\b", prettifyXml(settings.getSPMetadata()));
                return settings;
            } catch (CertificateException ex) {
                throw new AuthenticationException(ex);
            }
        }

        private String getBaseUrl(Object servletRequest) {
            return firstNotBlank(config.getCmdbuildBaseUrlForSaml(), baseUrlService.getBaseUrl(servletRequest));
        }

    }

    private void logMessagesSafeOnError(@Nullable Auth auth) {
        if (auth != null) {
            safe(() -> logger.warn("processing error, last request message was = \n\n{}\n", prettifyXml(auth.getLastRequestXML())));
            safe(() -> logger.warn("processing error, last response message was = \n\n{}\n", prettifyXml(auth.getLastResponseXML())));
        }
    }

    private static String certificateInfo(X509Certificate certificate) {
        return format("format =< %s > signature =< %s > principal =< %s > issuer=< %s > date =< %s >", certificate.getType(), certificate.getSigAlgName(), certificate.getSubjectX500Principal().getName(), certificate.getIssuerX500Principal().getName(), toIsoDateTimeLocal(certificate.getNotBefore()));
    }

    public interface AuthResponse {

        @Nullable
        String getAttribute(String name);

        @Nullable
        String getNameId();
    }
}
