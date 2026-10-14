/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import java.util.Map;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface RequestAuthenticatorResponse<T> {

    RequesthAuthenticatorResponseType getType();

    @Nullable
    T getLogin();

    @Nullable
    String getRedirectUrl();

    @Nullable
    Consumer<HttpServletResponse> getResponse();

    Map<String, String> getCustomAttributes();

    default boolean hasLogin() {
        return getLogin() != null;
    }

    default boolean hasRedirectUrl() {
        return isNotBlank(getRedirectUrl());
    }

    default boolean hasResponseHandler() {
        return getResponse() != null;
    }

}
