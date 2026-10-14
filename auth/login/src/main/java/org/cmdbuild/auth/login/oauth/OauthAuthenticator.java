package org.cmdbuild.auth.login.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.net.UrlEscapers;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.regex.Pattern;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.cmdbuild.auth.login.AuthRequestInfo;
import org.cmdbuild.auth.login.AuthenticationException;
import org.cmdbuild.auth.login.LoginModuleClientRequestAuthenticator;
import org.cmdbuild.auth.login.LoginType;
import static org.cmdbuild.auth.login.LoginType.LT_AUTO;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.login;
import static org.cmdbuild.auth.login.RequestAuthenticatorResponseImpl.redirect;
import static org.cmdbuild.auth.login.oauth.OauthAuthenticatorConfiguration.OAUTH_LOGIN_MODULE_TYPE;
import static org.cmdbuild.auth.login.oauth.OauthProtocol.OP_GOOGLE;
import static org.cmdbuild.auth.login.oauth.OauthProtocol.OP_KEYCLOAK;
import static org.cmdbuild.auth.login.oauth.OauthProtocol.OP_MSAZUREOAUTH2;
import org.cmdbuild.auth.user.GenericUserScriptService;
import static org.cmdbuild.auth.utils.RequestAuthUtils.buildRequestInfo;
import static org.cmdbuild.auth.utils.RequestAuthUtils.getRedirectUrlFromRequestInfo;
import static org.cmdbuild.auth.utils.RequestAuthUtils.shouldRedirectToLoginPage;
import org.cmdbuild.temp.TempService;
import org.cmdbuild.ui.UiBaseUrlService;
import static org.cmdbuild.utils.http.HttpClientUtils.checkStatusAndReadResponse;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OauthAuthenticator implements LoginModuleClientRequestAuthenticator<OauthAuthenticatorConfiguration> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TempService temp;
    private final UiBaseUrlService baseUrlService;
    private final GenericUserScriptService userScriptService;

    public OauthAuthenticator(TempService temp, UiBaseUrlService baseUrlService, GenericUserScriptService userScriptService) {
        this.temp = checkNotNull(temp);
        this.baseUrlService = checkNotNull(baseUrlService);
        this.userScriptService = checkNotNull(userScriptService);
    }

    @Override
    public String getType() {
        return OAUTH_LOGIN_MODULE_TYPE;
    }

    @Override
    @Nullable
    public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthRequest(AuthRequestInfo request, OauthAuthenticatorConfiguration config) {
        return new OauthAuthenticatorHelper(config).handleAuthRequest(request);
    }

    @Override
    @Nullable
    public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthResponse(AuthRequestInfo request, OauthAuthenticatorConfiguration config) {
        return new OauthAuthenticatorHelper(config).handleAuthResponse(request);
    }

    @Override
    @Nullable
    public RequestAuthenticatorResponse<Void> logout(@Nullable Object request, OauthAuthenticatorConfiguration config) {
        return new OauthAuthenticatorHelper(config).logout(request);
    }

    private class OauthAuthenticatorHelper {

        private final OauthAuthenticatorConfiguration config;
        private final String stateParamPrefix;

        public OauthAuthenticatorHelper(OauthAuthenticatorConfiguration config) {
            this.config = checkNotNull(config);
            this.stateParamPrefix = format("cm_oauth_%s", config.getCode().replaceAll("[^a-zA-Z0-9]", ""));
        }

        @Nullable
        public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthRequest(AuthRequestInfo request) {
            if (shouldRedirectToLoginPage(request)) {
                String redirectUri = firstNotBlank(config.getOauthRedirectUrl(), request.getRequestUrl());
                String tempId = temp.putTempData(buildRequestInfo(request));
                String state = format("%s%s", stateParamPrefix, tempId);
                String authUrl = format("%s?response_type=code&response_mode=query&scope=%s&client_id=%s&redirect_uri=%s&state=%s",
                        getAuthUrl(),
                        UrlEscapers.urlFormParameterEscaper().escape(getScope()),
                        UrlEscapers.urlFormParameterEscaper().escape(getClientId()),
                        UrlEscapers.urlFormParameterEscaper().escape(redirectUri),
                        state);
                logger.debug("oauth login url =< {} >", authUrl);
                return redirect(authUrl);
            } else {
                return null;
            }
        }

        @Nullable
        public RequestAuthenticatorResponse<LoginUserIdentity> handleAuthResponse(AuthRequestInfo request) {
            if (nullToEmpty(request.getParameter("state")).matches(format("^%s.*", Pattern.quote(stateParamPrefix)))) {
                String error = request.getParameter("error"),
                        errorDescription = request.getParameter("error_description"),
                        errorUri = request.getParameter("error_uri"),
                        errorMessage = null;
                if (isNotBlank(error) || isNotBlank(errorDescription)) {
                    errorMessage = format("%s: %s %s", firstNotBlank(error, "error"), nullToEmpty(errorDescription), nullToEmpty(errorUri)).trim();
                    logger.warn(marker(), "oauth endpoint returned error: {}", errorMessage);
                }
                try {
                    String code = checkNotBlank(request.getParameter("code"), "missing oauth response code"),
                            state = checkNotBlank(request.getParameter("state"), "missing oauth response state");
                    logger.debug("oauth response code =< {} > state =< {} >", code, state);
                    String redirectUrl = getRedirectUrlFromRequestInfo(temp.getTempDataAsString(state.replaceFirst(format("^%s", stateParamPrefix), "")));
                    try (CloseableHttpClient client = HttpClients.createDefault()) {
                        HttpPost tokenRequest = new HttpPost(getTokenUrl());
                        FluentMap<String, Object> tokenRequestPayload = map(
                                "client_id", getClientId(),
                                "scope", getScope(),
                                "code", code,
                                "redirect_uri", firstNotBlank(config.getOauthRedirectUrl(), request.getRequestUrl()),
                                "grant_type", "authorization_code",
                                "client_secret", getClientSecret());
                        tokenRequest.setEntity(new UrlEncodedFormEntity(tokenRequestPayload.toList((k, v) -> (NameValuePair) new BasicNameValuePair((String) k, (String) v)), StandardCharsets.UTF_8.name()));
                        logger.debug("execute oauth token request =< {} > with payload =\n\n{}\n", tokenRequest, mapToLoggableStringLazy(tokenRequestPayload));
                        Map<String, String> tokenResponse = fromJson(checkStatusAndReadResponse(client.execute(tokenRequest)), MAP_OF_STRINGS);
                        logger.debug("received oauth token response = \n\n{}\n", mapToLoggableStringLazy(tokenResponse));

                        Map<String, Object> info;
                        if (isNotBlank(tokenResponse.get("id_token"))) {//note: this may be an alternative way to get user info, adding from jwt additional claims; requires `openid` scope in request above; currently incomplete
                            String idToken = checkNotBlank(tokenResponse.get("id_token"), "missing id token");
                            DecodedJWT jwt = JWT.decode(idToken);
                            info = map(jwt.getClaims()).mapValues(Claim::asString);
                            logger.debug("received oauth jwt id token info = \n\n{}\n", mapToLoggableStringLazy(info));
                        } else {
                            logger.debug("no oauth jwt id token found in response");
                            info = emptyMap();
                        }

                        switch (config.getOauthProtocol()) {
                            case OP_MSAZUREOAUTH2 -> {
                                String accessToken = checkNotBlank(tokenResponse.get("access_token"), "missing access_token in response payload");
                                HttpGet userInfoRequest = new HttpGet("https://graph.microsoft.com/v1.0/me");
                                userInfoRequest.setHeader("Authorization", format("Bearer %s", accessToken));
                                logger.debug("execute user info request =< {} >", userInfoRequest);
                                Map<String, Object> userInfo = fromJson(checkStatusAndReadResponse(client.execute(userInfoRequest)), MAP_OF_OBJECTS);
                                logger.debug("received user info response = \n\n{}\n", mapToLoggableStringLazy(userInfo));
                                info = map(info).with(userInfo);
                            }
                        }

                        String loginValue = checkNotBlank(toStringOrNull(info.get(getLoginAttr())), "missing oauth login value for attr =< %s >", getLoginAttr());
                        LoginUserIdentity login;
                        if (isBlank(config.getLoginHandlerScript())) {
                            login = LoginUserIdentity.build(loginValue);
                        } else {
                            login = userScriptService.getLoginFromScript(config, LoggerFactory.getLogger(format("%s.HANDLER", getClass().getName())), loginValue);
                        }
                        logger.debug("oauth returned login identity =< {} >, redirect to initial landing page =< {} >", login.getValue(), redirectUrl);
                        return login(LoginUserIdentity.builder().withType(getLoginType()).withValue(loginValue).build()).withRedirect(redirectUrl);
                    }
                } catch (Exception ex) {
                    throw new AuthenticationException(ex, "unable to authenticate oauth request" + (isBlank(errorMessage) ? "" : (": " + errorMessage)));
                }
            } else {
                return null;
            }
        }

        @Nullable
        public RequestAuthenticatorResponse<Void> logout(@Nullable Object request) {
            if (config.isOauthLogoutEnabled()) {
                String postLogoutRedirectUri = firstNotBlank(config.getOauthLogoutRedirectUrl(), baseUrlService.getBaseUrl(request)), logoutUrl;
                logoutUrl = switch (config.getOauthProtocol()) {
                    case OP_MSAZUREOAUTH2 ->
                        format("%s?post_logout_redirect_uri=%s", getLogoutUrl(), UrlEscapers.urlFormParameterEscaper().escape(postLogoutRedirectUri));
                    case OP_KEYCLOAK ->
                        format("%s?post_logout_redirect_uri=%s&client_id=%s", getLogoutUrl(), UrlEscapers.urlFormParameterEscaper().escape(postLogoutRedirectUri), UrlEscapers.urlFormParameterEscaper().escape(config.getOauthClientId()));
                    default ->
                        format("%s?redirect_uri=%s", getLogoutUrl(), UrlEscapers.urlFormParameterEscaper().escape(postLogoutRedirectUri));
                };
                logger.debug("oauth logout url =< {} >, post logout redirect url =< {} >", logoutUrl, postLogoutRedirectUri);
                return redirect(logoutUrl);
            } else {
                return null;
            }
        }

        private String getServiceUrl() {
            String defaultValue = switch (config.getOauthProtocol()) {
                case OP_MSAZUREOAUTH2 ->
                    "https://login.microsoftonline.com";
                case OP_GOOGLE ->
                    "https://accounts.google.com";
                default ->
                    null;
            };
            return checkNotBlank(firstNotBlankOrNull(config.getOauthServiceUrl(), defaultValue), "missing oauth service url").trim().replaceAll("/+$", "");
        }

        private String getTokenServiceUrl() {
            String defaultValue = switch (config.getOauthProtocol()) {
                case OP_GOOGLE ->
                    "https://oauth2.googleapis.com";
                default ->
                    null;
            };
            return checkNotBlank(firstNotBlankOrNull(config.getOauthServiceUrl(), defaultValue), "missing oauth token service url").trim().replaceAll("/+$", "");
        }

        private String getAuthUrl() {
            return switch (config.getOauthProtocol()) {
                case OP_MSAZUREOAUTH2 ->
                    format("%s/%s/oauth2/v2.0/authorize", getServiceUrl(), getTenantId());
                case OP_GOOGLE ->
                    format("%s/o/oauth2/auth", getServiceUrl());
                default ->
                    format("%s/auth", getServiceUrl());
            };
        }

        private String getLogoutUrl() {
            return switch (config.getOauthProtocol()) {
                case OP_MSAZUREOAUTH2 ->
                    format("%s/%s/oauth2/v2.0/logout", getServiceUrl(), getTenantId());
                default ->
                    format("%s/logout", getServiceUrl());
            };
        }

        private String getTokenUrl() {
            return switch (config.getOauthProtocol()) {
                case OP_MSAZUREOAUTH2 ->
                    format("%s/%s/oauth2/v2.0/token", getTokenServiceUrl(), getTenantId());
                default ->
                    format("%s/token", getTokenServiceUrl());
            };
        }

        private String getTenantId() {
            return firstNotBlank(config.getOauthTenantId(), "common");
        }

        private String getClientId() {
            return checkNotBlank(config.getOauthClientId(), "missing oauth client id");
        }

        private String getClientSecret() {
            return checkNotBlank(config.getOauthClientSecret(), "missing oauth client secret");
        }

        private String getScope() {
            return checkNotBlank(config.getOauthScope(), "missing ouauth scope param");
        }

        private String getLoginAttr() {
            return checkNotBlank(config.getOauthLoginAttr(), "missing oauth login attr config");
        }

        private LoginType getLoginType() {
            return parseEnumOrDefault(config.getOauthLoginType(), LT_AUTO);
        }
    }
}
