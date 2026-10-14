/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.localization;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.isNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.eventbus.Subscribe;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.session.SessionService.CurrentSessionSetOrUpdateEvent;
import org.cmdbuild.common.localization.LanguageService;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.session.inner.SessionDataService;
import org.cmdbuild.common.localization.ContextLanguageHolder;
import org.cmdbuild.common.localization.LanguageInfo;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.translation.RequestLanguageHolder;
import org.cmdbuild.translation.RequestLanguageHolder.RequestLanguageSetEvent;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_LANGUAGE;
import org.cmdbuild.userconfig.UserConfigService;
import org.cmdbuild.userconfig.UserConfigService.UserConfigChangedEvent;
import static org.cmdbuild.utils.json.CmJsonUtils.collectionType;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LanguageServiceImpl implements LanguageService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<LanguageInfo> languages;
    private final List<String> allLanguages;

    private final RequestLanguageHolder requestLanguage;
    private final ContextLanguageHolder contextLanguage;
    private final SessionDataService sessionDataService;
    private final UserConfigService userConfigService;
    private final CoreConfiguration configuration;

    public LanguageServiceImpl(UserConfigService userConfigService, SessionDataService sessionDataService, CoreConfiguration configuration, RequestLanguageHolder requestLanguage, ContextLanguageHolder contextLanguage, EventBusService eventBusService) {
        this.sessionDataService = checkNotNull(sessionDataService);
        this.userConfigService = checkNotNull(userConfigService);
        this.configuration = checkNotNull(configuration);
        this.requestLanguage = checkNotNull(requestLanguage);
        this.contextLanguage = checkNotNull(contextLanguage);
        languages = readLanguages();
        allLanguages = languages.stream().map(LanguageInfo::getCode).collect(toImmutableList());
        eventBusService.getContextEventBus().register(new Object() {

            @Subscribe
            public void handleRequestLanguageSetEvent(RequestLanguageSetEvent event) {
                if (!contextLanguage.hasContextLanguage()) {
                    contextLanguage.setContextLanguage(requestLanguage.getRequestLanguage());
                } else {
                    buildAndSetContextLanguage();
                    updateUserLanguageFromRequest();
                }
            }

            @Subscribe
            public void handleCurrentSessionSetOrUpdateEvent(CurrentSessionSetOrUpdateEvent event) {
                buildAndSetContextLanguage();
                updateUserLanguageFromRequest();
            }

            @Subscribe
            public void handleUserConfigChangedEvent(UserConfigChangedEvent event) {
                buildAndSetContextLanguage();
                updateUserLanguageFromRequest();
            }
        });
    }

    @Override
    public String getDefaultLanguage() {
        return configuration.getDefaultLanguage();
    }

    @Override
    public String getContextLanguage() {
        if (!contextLanguage.hasContextLanguage()) {
            buildAndSetContextLanguage();
        }
        return contextLanguage.getContextLanguage();
    }

    @Override
    public Collection<String> getEnabledLanguages() {
        return firstNonEmpty(configuration.getEnabledLanguages(), allLanguages);
    }

    @Override
    public Collection<String> getLoginLanguages() {
        return firstNonEmpty(configuration.getLoginLanguages(), allLanguages);
    }

    @Override
    public List<LanguageInfo> getAllLanguages() {
        return languages;
    }

    @Override
    public void setContextLanguage(@Nullable String language) {
        buildAndSetContextLanguage(language);
    }

    @Override
    public void resetContextLanguage() {
        buildAndSetContextLanguage();
    }

    private static <E> Collection<E> firstNonEmpty(Collection<E> a, Collection<E> b) {
        return a.isEmpty() ? b : a;
    }

    private void updateUserLanguageFromRequest() {
        if (requestLanguage.hasRequestLanguage() && isNotBlank(userConfigService.getForCurrentUsernameOrNull(USER_CONFIG_LANGUAGE)) && !equal(requestLanguage.getRequestLanguage(), userConfigService.getForCurrentUsernameOrNull(USER_CONFIG_LANGUAGE))) {
            userConfigService.setForCurrent(USER_CONFIG_LANGUAGE, requestLanguage.getRequestLanguage());
        }
    }

    private void buildAndSetContextLanguage() {
        buildAndSetContextLanguage(null);
    }

    private void buildAndSetContextLanguage(@Nullable String langOverride) {
        String lang = list(
                langOverride,
                requestLanguage.getRequestLanguageOrNull(),
                sessionDataService.getCurrentSessionDataSafe().<String>get(USER_CONFIG_LANGUAGE),
                userConfigService.getForCurrentUsernameOrNull(USER_CONFIG_LANGUAGE),
                configuration.getDefaultLanguage(),
                Locale.getDefault().toString(),
                "en"
        ).stream().filter(StringUtils::isNotBlank).findFirst().get();
        logger.debug("set current context language =< {} >", lang);
        contextLanguage.setContextLanguage(lang);
    }

    private List<LanguageInfo> readLanguages() {
        List<LanguageInfo> list = fromJson(getClass().getResourceAsStream("/org/cmdbuild/translation/languages.json"), collectionType(LanguageInfoImpl.class));
        checkArgument(!isNullOrEmpty(list) && !list.stream().anyMatch(isNull()));
        list = list.stream().sorted(Ordering.natural().onResultOf(LanguageInfo::getCode)).collect(toImmutableList());
        return list;
    }

    private static class LanguageInfoImpl implements LanguageInfo {

        private final String code, description;

        @JsonCreator
        public LanguageInfoImpl(@JsonProperty("code") String code, @JsonProperty("description") String description) {
            this.code = checkNotBlank(code);
            this.description = checkNotBlank(description);
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDescription() {
            return description;
        }

    }
}
