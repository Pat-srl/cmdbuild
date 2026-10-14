/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.utils;

import static com.google.common.base.Strings.nullToEmpty;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import org.cmdbuild.auth.login.AuthRequestInfo;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestAuthUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String INFO_PARAM_REQUEST_URL = "requestUrl", INFO_PARAM_REQUEST_URL_WITH_FRAGRMENT = "requestUrlWithFragment", INFO_PARAM_REFERER_FRAGMENT = "refererFragment";

    public static boolean shouldSkipSso(AuthRequestInfo request) {
        return request.hasParameter("skipsso");
    }

    public static boolean isUiRequest(AuthRequestInfo request) {
        return "GET".equalsIgnoreCase(request.getMethod()) && request.getRequestPath().matches("/ui(_dev)?/?");
    }

    public static boolean isMobileLoginRequest(AuthRequestInfo request) {
        return "GET".equalsIgnoreCase(request.getMethod()) && request.getRequestPath().matches("/ui/mobile/login/?");//TODO check if this is actually required
    }

    public static boolean shouldRedirectToLoginPage(AuthRequestInfo request) {
        return (isUiRequest(request) && !shouldSkipSso(request)) || isMobileLoginRequest(request);
    }

    public static String buildRequestInfo(AuthRequestInfo request) {
        Map<String, String> requestInfo = map(INFO_PARAM_REQUEST_URL, checkNotBlank(request.getRequestUrl()), INFO_PARAM_REFERER_FRAGMENT, nullToEmpty(request.getLoginRequestRefererFragmentParam()), INFO_PARAM_REQUEST_URL_WITH_FRAGRMENT, checkNotBlank(request.getRequestUrlWithFragment()));
        LOGGER.debug("request info = \n\n{}\n", mapToLoggableStringLazy(requestInfo));
        return toJson(requestInfo);
    }

    public static String getRedirectUrlFromRequestInfo(String requestInfo) {
        Map<String, String> stateInfo = fromJson(requestInfo, MAP_OF_STRINGS);
        LOGGER.debug("state info = \n\n{}\n", mapToLoggableStringLazy(stateInfo));
        return checkNotBlank(stateInfo.get(INFO_PARAM_REQUEST_URL_WITH_FRAGRMENT), "missing `requestUrl` in state info");
    }
}
