/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import static java.util.Collections.emptyList;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.json.CmJsonUtils.collectionType;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.ws3.utils.WsCookieUtils.SameSiteMode;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.ui")
public class UiConfigurationImpl implements UiFilterConfiguration, UiConfiguration {

    @ConfigValue(key = "uiServiceBaseUrl", description = "ui service base url (valid values are `<some url>`, `auto` or `rules`)", defaultValue = AUTO_URL, category = CC_ENV)
    private String uiServiceBaseUrl;

    @ConfigValue(key = "uiServiceBaseUrl.rules", description = "ui service base url rules (json structure), example: `[{\"target\":\"^http://127.0.0.1\",\"url\":\"auto\"},{\"source\":\"127.0.0.1\",\"url\":\"auto\"},{\"target\":\".*\",\"url\":\"http://my.proxy.url:123/something/\"}]` (default to auto)", defaultValue = "[]", category = CC_ENV)
    private String uiServiceBaseUrlRules;

    @ConfigValue(key = "manifest", description = "cmdbuild ui manifest", defaultValue = "cmdbuild")
    private String uiManifest;

    @ConfigValue(key = "timeout", description = "ui ws call timeout in seconds", defaultValue = "60")
    private int timeout;

    @ConfigValue(key = "detailWindow.width", description = "", defaultValue = "75")
    private Integer detailWindowWidth;

    @ConfigValue(key = "inlineCard.height", description = "", defaultValue = "80")
    private Integer inlineCardHeight;

    @ConfigValue(key = "detailWindow.height", description = "", defaultValue = "95")
    private Integer detailWindowHeight;

    @ConfigValue(key = "popupWindow.width", description = "", defaultValue = "80")
    private Integer popupWindowWidth;

    @ConfigValue(key = "popupWindow.height", description = "", defaultValue = "80")
    private Integer popupWindowHeight;

    @ConfigValue(key = "startDay", description = "", defaultValue = "0")
    private Integer startDay;

    @ConfigValue(key = "referencecombolimit", description = "", defaultValue = "500")
    private Integer referencecombolimit;

    @ConfigValue(key = "dms.previewlimit", description = "set the maximum previews to generate, if the card exceeds the attachments limit do not request previews; 0 equals unlimited", defaultValue = "0")
    private Integer dmsMaxPreviewLimit;

    @ConfigValue(key = "keepFilterOnUpdatedCard", description = "", defaultValue = TRUE)
    private Boolean keepFilterOnUpdatedCard;

    @ConfigValue(key = "decimalsSeparator", description = "", defaultValue = ".")
    private String decimalsSeparator;

    @ConfigValue(key = "thousandsSeparator", description = "", defaultValue = " ")
    private String thousandsSeparator;

    @ConfigValue(key = "dateFormat", description = "", defaultValue = "Y-m-d")
    private String dateFormat;

    @ConfigValue(key = "timeFormat", description = "", defaultValue = "H:i:s")
    private String timeFormat;

    @ConfigValue(key = "cookie.secure", description = "enable cookie secure flag (to be used with https); valid values are never, always, auto", defaultValue = "auto")
    private CookieSecureMode cookieSecure;

    @ConfigValue(key = "cookie.expiration", description = "set cookie max age in seconds (if not set, or lteq 0, will not set cookie expiration)", defaultValue = "63072000")
    private Integer cookieMaxAgeSeconds;

    @ConfigValue(key = "cookie.sameSite", description = "set cookie sameSite policy, valid values are `lax`, `strict` or `none` (or `` if you don't want a samesite policy at all)", defaultValue = "lax")
    private SameSiteMode cookieSameSite;

    @ConfigValue(key = "cors.allowedOrigins", description = "", defaultValue = "auto")
    private List<String> corsAllowedOrigins;

    @ConfigValue(key = "cors.enabled", description = "", defaultValue = TRUE)
    private Boolean corsEnabled;

    @ConfigValue(key = "resources.cache.enabled", description = "", defaultValue = TRUE)
    private Boolean resourcesCacheEnabled;

    @ConfigValue(key = "email.groupByStatus", description = "group email by status in ui", defaultValue = TRUE)
    private Boolean emailGroupByStatus;

    @ConfigValue(key = "email.defaultDelay", description = "default delay for email sent by ui, seconds (if positive, will also allow abort of email sent from ui)", defaultValue = "0")
    private Long emailDefaultDelay;

    @ConfigValue(key = "fulltextsearch.enabled", description = "enable full text search for all classes and processes, default is true", defaultValue = TRUE)
    private Boolean fullTextSearchEnabled;

    @ConfigValue(key = "redirectTo.subclass.enabled", description = "redirect to superclass, use the subclass directly", defaultValue = FALSE)
    private Boolean redirectToSubclassEnabled;

    @ConfigValue(key = "redirectTo.view.enabled", description = "open /view directly", defaultValue = FALSE)
    private Boolean redirectToViewEnabled;

    @ConfigValue(key = "notifications.sound.enabled", description = "enable / disable notifications sound", defaultValue = TRUE)
    private Boolean notificationsSoundEnabled;

    @Override
    public boolean getEmailGroupByStatus() {
        return emailGroupByStatus;
    }

    @Nullable
    @Override
    public Long getEmailDefaultDelay() {
        return emailDefaultDelay;
    }

    @Override
    public boolean isFullTextSearchEnabled() {
        return fullTextSearchEnabled;
    }

    @Override
    public boolean isResourceCachingEnabled() {
        return resourcesCacheEnabled;
    }

    @Override
    public SameSiteMode getCookieSameSiteMode() {
        return cookieSameSite;
    }

    @Override
    @Nullable
    public Integer getCookieMaxAgeSeconds() {
        return cookieMaxAgeSeconds;
    }

    @Override
    public int getPopupWindowWidth() {
        return popupWindowWidth;
    }

    @Override
    public int getPopupWindowHeight() {
        return popupWindowHeight;
    }

    @Override
    public boolean isCorsEnabled() {
        return corsEnabled;
    }

    @Override
    public List<String> getCorsAllowedOrigins() {
        return corsAllowedOrigins;
    }

    @Override
    public CookieSecureMode getCookieSecureMode() {
        return cookieSecure;
    }

    @Override
    public String getDecimalsSeparator() {
        return decimalsSeparator;
    }

    @Override
    public String getThousandsSeparator() {
        return thousandsSeparator;
    }

    @Override
    public String getDateFormat() {
        return dateFormat;
    }

    @Override
    public String getTimeFormat() {
        return timeFormat;
    }

    @Override
    public String getBaseUrl() {
        return uiServiceBaseUrl;
    }

    @Override
    public List<UiBaseUrlRule> getBaseUrlRules() {
        return isBlank(uiServiceBaseUrlRules) ? emptyList() : fromJson(uiServiceBaseUrlRules, collectionType(UiBaseUrlRuleImpl.class));
    }

    @Override
    public String getUiManifest() {
        return uiManifest;
    }

    @Override
    public int getUiTimeout() {
        return timeout;
    }

    @Override
    public int getDetailWindowWidth() {
        return detailWindowWidth;
    }

    @Override
    public int getInlineCardHeight() {
        return inlineCardHeight;
    }

    @Override
    public int getDetailWindowHeight() {
        return detailWindowHeight;
    }

    @Override
    public int getReferenceComboLimit() {
        return referencecombolimit;
    }

    @Override
    public int getDmsMaxPreviewLimit() {
        return dmsMaxPreviewLimit;
    }

    @Override
    public boolean getKeepFilterOnUpdatedCard() {
        return keepFilterOnUpdatedCard;
    }

    @Override
    public int getStartDay() {
        return startDay;
    }

    @Override
    public boolean isRedirectToSubclassEnabled() {
        return redirectToSubclassEnabled;
    }

    @Override
    public boolean isRedirectToViewEnabled() {
        return redirectToViewEnabled;
    }

    @Override
    public boolean isNotificationsSoundEnabled() {
        return notificationsSoundEnabled;
    }

    private static class UiBaseUrlRuleImpl implements UiBaseUrlRule {

        private final String target, source, url;

        public UiBaseUrlRuleImpl(@JsonProperty("source") String source, @JsonProperty("target") String target, @JsonProperty("url") String url) {
            this.source = source;
            this.target = target;
            this.url = checkNotBlank(url);
        }

        @Override
        public String getTarget() {
            return target;
        }

        @Override
        public String getSource() {
            return source;
        }

        @Override
        public String getUrl() {
            return url;
        }

    }

}
