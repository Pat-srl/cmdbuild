/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ui;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.UiFilterConfiguration;
import static org.cmdbuild.config.UiFilterConfiguration.AUTO_URL;
import static org.cmdbuild.config.UiFilterConfiguration.RULES_URL;
import org.cmdbuild.config.UiFilterConfiguration.UiBaseUrlRule;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UiBaseUrlServiceImpl implements UiBaseUrlService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UiFilterConfiguration config;

    public UiBaseUrlServiceImpl(UiFilterConfiguration config) {
        this.config = checkNotNull(config);
    }

    @Override
    public String getBaseUrl(@Nullable Object request) {
        if (request == null) {
            return getBaseUrl();
        } else {
            return switch (nullToEmpty(config.getBaseUrl()).toLowerCase()) {
                case AUTO_URL ->
                    buildAutoUrl(request);
                case RULES_URL ->
                    buildUrlWithRules(request);
                default ->
                    getStaticUrl();
            };
        }
    }

    @Override
    public String getBaseUrl() {
        return switch (nullToEmpty(config.getBaseUrl()).toLowerCase()) {
            case AUTO_URL ->
                "/";
            default ->
                getStaticUrl();
        };
    }

    private String getStaticUrl() {
        return normalizeUrl(config.getBaseUrl());
    }

    private String buildUrlWithRules(Object request) {
        String autoUrl = buildAutoUrl(request),
                remoteAddr = ((HttpServletRequest) request).getRemoteAddr();
        logger.debug("processing url rules, auto url =< {} >, remote addr =< {} >", autoUrl, remoteAddr);
        for (UiBaseUrlRule rule : config.getBaseUrlRules()) {
            if ((isNotBlank(rule.getTarget()) && Pattern.compile(rule.getTarget()).matcher(autoUrl).find())
                    || (isNotBlank(rule.getSource()) && Pattern.compile(rule.getSource()).matcher(remoteAddr).find())) {
                return switch (rule.getUrl().toLowerCase()) {
                    case AUTO_URL ->
                        autoUrl;
                    default ->
                        normalizeUrl(rule.getUrl());
                };
            }
        }
        return autoUrl;
    }

    private String buildAutoUrl(Object request) {
        try {
            String requestUrl = ((HttpServletRequest) request).getRequestURL().toString(),
                    contextPath = ((HttpServletRequest) request).getContextPath(),
                    baseUrl = new URL(new URL(requestUrl), firstNotBlank(contextPath, "/")).toString();
            return normalizeUrl(baseUrl);
        } catch (MalformedURLException ex) {
            throw runtime(ex);
        }
    }

    private String normalizeUrl(String url) {
        return checkNotBlank(url).replaceFirst("/$", "");
    }

}
