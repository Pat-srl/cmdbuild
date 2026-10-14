/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.ws3.utils.WsCookieUtils.SameSiteMode.SS_LAX;
import static org.cmdbuild.utils.ws3.utils.WsCookieUtils.SameSiteMode.SS_NONE;
import static org.cmdbuild.utils.ws3.utils.WsCookieUtils.SameSiteMode.SS_STRICT;

public class WsCookieUtils {

    private static final DateTimeFormatter HTTP_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O").localizedBy(Locale.US);

    private static final Map<SameSiteMode, String> SAME_SITE_EXPR_MAP = new EnumMap<SameSiteMode, String>(map(SS_LAX, "Lax", SS_STRICT, "Strict", SS_NONE, "None"));

    public static String buildSetCookieHeader(String key, String value, @Nullable Number maxAgeSeconds, @Nullable String path, boolean secure, boolean httpOnly, @Nullable SameSiteMode sameSite) {
        return doBuildSetCookieHeader(key, value, isNotNullAndGtZero(maxAgeSeconds) ? maxAgeSeconds : null, path, secure, httpOnly, sameSite);
    }

    public static String buildDeleteCookieHeader(String key, @Nullable String path, boolean secure, boolean httpOnly, @Nullable SameSiteMode sameSite) {
        return doBuildSetCookieHeader(key, "0", 0, path, secure, httpOnly, sameSite);
    }

    private static String doBuildSetCookieHeader(String key, @Nullable String value, @Nullable Number maxAgeSeconds, @Nullable String path, boolean secure, boolean httpOnly, @Nullable SameSiteMode sameSite) {
        String cookie = format("%s=%s", checkNotBlank(key), checkNotBlank(value));
        if (maxAgeSeconds != null && maxAgeSeconds.longValue() > 0) {
            cookie += format("; Max-Age=%s; Expires=%s", maxAgeSeconds.longValue(), HTTP_DATE_FORMATTER.format(CmDateUtils.now().plusSeconds(maxAgeSeconds.longValue())));
        } else if (maxAgeSeconds != null && maxAgeSeconds.longValue() <= 0) {
            cookie += format("; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT");
        }
        if (isNotBlank(path)) {
            cookie += format("; Path=%s", path);
        }
        if (httpOnly) {
            cookie += "; HttpOnly";
        }
        if (secure) {
            cookie += "; Secure";
        }
        if (sameSite != null) {
            cookie += format("; SameSite=%s", checkNotNull(SAME_SITE_EXPR_MAP.get(sameSite)));
        }
        return cookie;
    }

    public enum SameSiteMode {
        SS_LAX, SS_STRICT, SS_NONE
    }

}
