/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import static java.lang.String.format;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * IMPORTANT NOTE: interface used in custom login script: do not change this interface!!
 */
public interface AuthRequestInfo {

    final String AUTH_REQUEST_LOGIN_MODULE_PARAM = "cm_login_module", AUTH_REQUEST_LOGIN_REFERER_FRAGMENT_PARAM = "cm_login_referer_fragment";

    String getRequestUrl();

    String getRequestPath();

    String getMethod();

    @Nullable
    String getHeader(String name);

    @Nullable
    String getParameter(String name);

    @Nullable
    byte[] getMultipartParameter(String key);

    <T> T getInner();

    boolean hasParameter(String key);

    @Nullable
    default String getMultipartParameterAsString(String key) {
        byte[] data = getMultipartParameter(key);
        return data == null ? null : new String(data);//TODO charset
    }

    @Nullable
    default String getLoginRequestRefererFragmentParam() {
        return getParameter(AUTH_REQUEST_LOGIN_REFERER_FRAGMENT_PARAM);
    }

    default boolean hasRefererFragmentParam() {
        return isNotBlank(getLoginRequestRefererFragmentParam());
    }

    default String getRequestUrlWithFragment() {
        return hasRefererFragmentParam() ? format("%s#%s", getRequestUrl(), getLoginRequestRefererFragmentParam()) : getRequestUrl();
    }

}
