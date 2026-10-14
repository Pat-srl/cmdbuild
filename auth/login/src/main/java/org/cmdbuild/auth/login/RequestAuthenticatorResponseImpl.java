/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import static org.cmdbuild.auth.login.RequesthAuthenticatorResponseType.RT_LOGIN;
import static org.cmdbuild.auth.login.RequesthAuthenticatorResponseType.RT_NULL;
import static org.cmdbuild.auth.login.RequesthAuthenticatorResponseType.RT_REDIRECT;
import static org.cmdbuild.auth.login.RequesthAuthenticatorResponseType.RT_RESPONSE;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class RequestAuthenticatorResponseImpl<T> implements RequestAuthenticatorResponse {

    private final RequesthAuthenticatorResponseType type;
    private final T login;
    private final String redirectUrl;
    private final Consumer<HttpServletResponse> response;
    private final Map<String, String> customAttributes;

    private RequestAuthenticatorResponseImpl(RequesthAuthenticatorResponseType type, @Nullable T login, @Nullable String redirectUrl, @Nullable Consumer<HttpServletResponse> response, Map<String, String> customAttributes) {
        this.type = checkNotNull(type);
        this.login = login;
        this.redirectUrl = redirectUrl;
        this.response = response;
        this.customAttributes = map(customAttributes).immutable();
        switch (type) {
            case RT_LOGIN ->
                checkNotNull(this.login);
            case RT_REDIRECT ->
                checkNotBlank(this.redirectUrl);
            case RT_RESPONSE ->
                checkNotNull(this.response);
        }
    }

    @Override
    @Nullable
    public T getLogin() {
        return login;
    }

    @Override
    @Nullable
    public String getRedirectUrl() {
        return redirectUrl;
    }

    @Override
    public RequesthAuthenticatorResponseType getType() {
        return type;
    }

    @Override
    public Consumer<HttpServletResponse> getResponse() {
        return response;
    }

    @Override
    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public RequestAuthenticatorResponseImpl<T> withRedirect(String redirectUrl) {
        return new RequestAuthenticatorResponseImpl<>(type, login, redirectUrl, response, customAttributes);
    }

    public RequestAuthenticatorResponseImpl withCustomAttributes(Map<String, String> customAttributes) {
        return new RequestAuthenticatorResponseImpl<>(type, login, redirectUrl, response, map(this.customAttributes).with(customAttributes));
    }

    public RequestAuthenticatorResponseImpl withCustomAttribute(String key, String value) {
        return new RequestAuthenticatorResponseImpl<>(type, login, redirectUrl, response, map(this.customAttributes).with(key, value));
    }

    public static RequestAuthenticatorResponseImpl emptyResponse() {
        return new RequestAuthenticatorResponseImpl(RT_NULL, null, null, null, emptyMap());
    }

    public static <T> RequestAuthenticatorResponseImpl<T> login(T login) {
        return new RequestAuthenticatorResponseImpl(RT_LOGIN, login, null, null, emptyMap());
    }

    public static RequestAuthenticatorResponseImpl redirect(String redirectUrl) {
        return new RequestAuthenticatorResponseImpl(RT_REDIRECT, null, redirectUrl, null, emptyMap());
    }

    public static RequestAuthenticatorResponseImpl response(Consumer<HttpServletResponse> response) {
        return new RequestAuthenticatorResponseImpl(RT_RESPONSE, null, null, response, emptyMap());
    }

}
