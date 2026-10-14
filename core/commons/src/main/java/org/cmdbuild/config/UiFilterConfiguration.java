/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import java.util.List;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import org.cmdbuild.utils.ws3.utils.WsCookieUtils.SameSiteMode;

public interface UiFilterConfiguration {

    final static String AUTO_URL = "auto", RULES_URL = "rules";

    String getBaseUrl();

    List<UiBaseUrlRule> getBaseUrlRules();

    String getUiManifest();

    CookieSecureMode getCookieSecureMode();

    @Nullable
    Integer getCookieMaxAgeSeconds();

    @Nullable
    SameSiteMode getCookieSameSiteMode();

    boolean isResourceCachingEnabled();

    default boolean hasCookieMaxAge() {
        return isNotNullAndGtZero(getCookieMaxAgeSeconds());
    }

    default boolean enableCookieSecure(boolean requestIsSecure) {
        return switch (getCookieSecureMode()) {
            case CSM_NEVER ->
                false;
            case CSM_ALWAYS ->
                true;
            case CSM_AUTO ->
                requestIsSecure;
            default ->
                throw new UnsupportedOperationException("unsupported cookie secure mode = " + getCookieSecureMode());
        };
    }

    public interface UiBaseUrlRule {

        String getSource();

        String getTarget();

        String getUrl();

    }

}
