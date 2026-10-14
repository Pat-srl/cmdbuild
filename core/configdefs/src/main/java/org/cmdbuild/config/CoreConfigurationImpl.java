package org.cmdbuild.config;

import jakarta.annotation.Nullable;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.cmdbuild.chat.ChatMultitenantMode;
import static org.cmdbuild.common.http.HttpConst.MAINTENANCE_MODE_PASSTOKEN_DEFAULT;
import static org.cmdbuild.config.CoreConfiguration.CORE_NAMESPACE;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.entrytype.TextContentSecurity;
import org.cmdbuild.fault.FaultLevel;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("coreConfig")
@ConfigComponent(CORE_NAMESPACE)
public class CoreConfigurationImpl implements CoreConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String STARTING_CLASS = "startingclass";
    private static final String RELATION_LIMIT = "relationlimit";
    private static final String LANGUAGE = "language";
    private static final String LANGUAGE_PROMPT = "languageprompt";
    private static final String SESSION_TIMEOUT = "session.timeout";
    private static final String INSTANCE_NAME = "instance_name";
    private static final String TABS_POSITION = "card_tab_position";
    private static final String LOCK_CARD = "lockcardenabled";
    private static final String LOCKER_CARD_USER_VISIBLE = "lockcarduservisible";
    private static final String LOCK_CARD_TIME_OUT = "lockcardtimeout";
    private static final String ENABLED_LANGUAGES = "enabled_languages";
    private static final String LOGIN_LANGUAGES = "login_languages";
    private static final String IMPORT_CSV_ONE_BY_ONE = "import_csv.one_by_one";
    private static final String DEMO_MODE_ADMIN = "demomode";

    @ConfigValue(key = "housekeeping.at_startup.enabled", description = "enable housekeeping run at startup", defaultValue = FALSE)//TODO improve this
    private boolean runDatabaseHousekeepingFunctionAtStartup;

    @ConfigValue(key = "housekeeping.daily.enabled", description = "enable daily housekeeping", defaultValue = TRUE)
    private boolean runDatabaseHousekeepingFunctionDaily;

    @ConfigValue(key = "maintenanceMode.enabled", description = "enable maintenance mode (redirect all requests to maintenance mode landing page unless the request attach valid 'CMDbuild-MMpasstoken', as header or cookie)", defaultValue = FALSE)
    private boolean maintenanceModeEnabled;

    @ConfigValue(key = "maintenanceMode.passtoken", description = "maintenance mode pass token, to be used when in maintenance mode; YOU SHOULD CHANGE THIS VALUE while running maintenance mode on a production node", defaultValue = MAINTENANCE_MODE_PASSTOKEN_DEFAULT)
    private String maintenanceModePasstoken;

    @ConfigValue(key = STARTING_CLASS, description = "", defaultValue = "")
    private String startingClass;

    @ConfigValue(key = RELATION_LIMIT, description = "", defaultValue = "20")
    private int relationLimit;

    @ConfigValue(key = LANGUAGE, description = "", defaultValue = "en")
    private String language;

    @ConfigValue(key = INSTANCE_NAME, description = "", defaultValue = "")
    private String instanceName;

    @ConfigValue(key = "timezone", description = "preferred timezone; if not set, will fall back to java system timezone", defaultValue = "")
    private String timezone;

    @ConfigValue(key = TABS_POSITION, description = "", defaultValue = "bottom")
    private String tabsPosition;

    @ConfigValue(key = DEMO_MODE_ADMIN, description = "", defaultValue = "")
    private String demoModeAdmin;

    @ConfigValue(key = LANGUAGE_PROMPT, description = "", defaultValue = TRUE)
    private boolean languagePropmpt;

    @ConfigValue(key = LOCK_CARD, description = "", defaultValue = FALSE)
    private boolean lockCard;

    @ConfigValue(key = LOCKER_CARD_USER_VISIBLE, description = "", defaultValue = TRUE)
    private boolean lockerCardUserVisible;

    @ConfigValue(key = IMPORT_CSV_ONE_BY_ONE, description = "", defaultValue = FALSE)
    private boolean importCsvOneByOne;

    @ConfigValue(key = SESSION_TIMEOUT, description = "session timeout in seconds", defaultValue = "3600")
    private int sessionTimeout;

    @ConfigValue(key = "session.persist.delay", description = "max delay from last activity before updating session last active date on db (note: this apply only when no session data was modified, but only lastActive date needs to be updated)", defaultValue = "600")
    private int sessionPersistDelay;

    @ConfigValue(key = "session.activeSessionPeriodForStatistics", description = "amount of time in seconds for which a session is considered 'active' (only for the purpose of statistics and error collection); useful if session.timeout is a big value (days or more)", defaultValue = "1200")
    private int sessionActivePeriodForStatistics;

    @ConfigValue(key = LOCK_CARD_TIME_OUT, description = "", defaultValue = "300")
    private int lockCardTimeout;

    @ConfigValue(key = "lock.acquireTimeout", description = "timeout on lock acquisition (ISO 8601 Duration format)", defaultValue = "PT30S")
    private Duration lockAcquireTimeout;

    @ConfigValue(key = "enableConfigUpdate", description = "enable system config update via rest ws and interface (config update will still be possible on db or via config files)", defaultValue = TRUE)
    private boolean enableConfigUpdate;

    @ConfigValue(key = "trustedKeys", description = "trusted RSA public keys (list of keys in openssl format); these keys may be used to perform login with any username", defaultValue = "")
    private List<String> trustedKeys;

    @ConfigValue(key = ENABLED_LANGUAGES, description = "languages enabled for CM objects translation", defaultValue = "")
    private Set<String> enabledLanguages;

    @ConfigValue(key = LOGIN_LANGUAGES, description = "languages enabled for user login", defaultValue = "")
    private Set<String> loginLanguages;

    @ConfigValue(key = "enableMultigroupByDefault", description = "enable user multigroup by default", defaultValue = FALSE)
    private boolean enableMultigroupByDefault;

    @ConfigValue(key = "notificationMessagesLevelThreshold", description = "notification messages level threshold (one of INFO, WARNING, ERROR, NEVER)", defaultValue = "WARNING")
    private FaultLevel notificationMessagesLevelThreshold;

    @ConfigValue(key = "companyLogo", description = "company logo (uploads id)", defaultValue = "")
    private Long companyLogo;

    @ConfigValue(key = "cardlock.enabled", description = "", defaultValue = FALSE)
    private Boolean cardlockEnabled;

    @ConfigValue(key = "cardlock.showuser", description = "", defaultValue = FALSE)
    private Boolean cardlockShowUser;

    @ConfigValue(key = "syslog.gc.heap.perc.trigger", description = "max heap percentage util GC is called", defaultValue = "90")
    private int triggerGCHeapPercentage;

    @ConfigValue(key = "syslog.gc.metaspace.perc.trigger", description = "max metaspace percentage util GC is called", defaultValue = "90")
    private int triggerGCMetaspacePercentage;

    @ConfigValue(key = "syslog.gc.sysmem.perc.trigger", description = "max system memory percentage util GC is called", defaultValue = "85")
    private int triggerGCSystemMemoryPercentage;

    @ConfigValue(key = "syslog.test.warning.enabled", description = "enable syslog test warning", defaultValue = FALSE)
    private Boolean triggerSystemWarningForTest;

    @ConfigValue(key = "syslog.test.critical.enabled", description = "enable syslog test critical", defaultValue = FALSE)
    private Boolean triggerSystemCriticalForTest;

    @ConfigValue(key = "sysmonitor.mode", description = "administration dashboard, `last` or `recent`", defaultValue = "recent")
    private SystemMonitorMode sysMonitorMode;

    @ConfigValue(key = "sysmonitor.autocalculate.interval.enabled", description = "administration dashboard, enable autocalculate interval between first and second record", defaultValue = FALSE)
    private Boolean sysMonitorAutocalculateInterval;

    @ConfigValue(key = "session.keepalive.enabled", description = "enable session keepalive", defaultValue = TRUE)
    private Boolean sessionKeepaliveEnabled;

    @ConfigValue(key = CORE_LOGGER_CONFIG, description = "logger config (packed)", category = CC_ENV)
    private String loggerConfig;

    @ConfigValue(key = CORE_LOGGER_AUTOCONFIGURE, description = "enable logger auto configuration", defaultValue = TRUE, category = CC_ENV)
    private Boolean loggerAutoconfigure;

    @ConfigValue(key = CORE_LOGGER_TYPE, description = "logger type, `default` or `stdout`", defaultValue = "default", category = CC_ENV)
    private LoggerType loggerType;

    @ConfigValue(key = CORE_CONFIG_READY, description = "config has been loaded on db and it is ready", defaultValue = FALSE)
    private boolean configReady;

    @ConfigValue(key = "user.changePasswordRequiredForNewUser", description = "require password change for new user created from admin", defaultValue = FALSE)
    private Boolean changePasswordRequiredForNewUser;

    @ConfigValue(key = "cardcache.enabled", description = "enable card cache (improve performance, but uses more memory)", defaultValue = FALSE)
    private Boolean cardCacheEnabled;

    @ConfigValue(key = "cardcache.maxRecordsPerClass", description = "max records per class to cache", defaultValue = "1000")
    private Integer cardCacheMaxRecordsPerClass;

    @ConfigValue(key = "card.bulk.update.enabled.default", description = "bulk card update default enabled", defaultValue = FALSE)
    private Boolean cardBulkUpdateEnabledDefault;

    @ConfigValue(key = "card.bulk.delete.enabled.default", description = "bulk card delete default enabled", defaultValue = FALSE)
    private Boolean cardBulkDeleteEnabledDefault;

    @ConfigValue(key = "chat.enabled", description = "enable chat", defaultValue = FALSE)
    private Boolean chatEnabled;

    @ConfigValue(key = "chat.multitenantMode", description = "multitenant chat mode: if global, all users can see each other; if tenant, users can see only other users from the same tenant", defaultValue = "global")
    private ChatMultitenantMode chatMultitenantMode;

    @ConfigValue(key = "defaultTextContentSecurity", description = "default text content security (for card notes, etc), one of `html_all`, `html_safe`", defaultValue = "html_all")
    private TextContentSecurity defaultTextContentSecurity;

    @ConfigValue(key = "preload.enabled", description = "enable cache preload at startup", defaultValue = TRUE)
    private Boolean preloadEnabled;

    @ConfigValue(key = "preload.rest", description = "list of rest methods for startup preload", defaultValue = "translations/loadTranslations?lang=default;it;en,classes?detailed=true,processes?detailed=true,domains?ext=true,dashboards?detailed=true")
    private List<String> preloadRestUrls;

    @ConfigValue(key = "view.joinViewPrivilegeModeDefault", description = "default join view privilegeMode", defaultValue = "ignore")
    private String joinViewPrivilegeModeDefault;

    @ConfigValue(key = "cqlSecurity", description = "cql security, one of allow,restrict,deny", defaultValue = "restrict")
    private CqlSecurity cqlSecurity;

    @ConfigValue(key = "openapi.enabled", description = "enable openapi", defaultValue = FALSE)
    private Boolean openApiEnabled;

    @Override
    public CqlSecurity getCqlSecurity() {
        return cqlSecurity;
    }

    @Override
    public String getJoinViewPrivilegeModeDefault() {
        return joinViewPrivilegeModeDefault;
    }

    @Override
    public Duration getLockAcquireTimeout() {
        return lockAcquireTimeout;
    }

    @Override
    public boolean isPreloadEnabled() {
        return preloadEnabled;
    }

    @Override
    public List<String> getPreloadRestUrls() {
        return preloadRestUrls;
    }

    @Override
    public boolean isChatEnabled() {
        return chatEnabled;
    }

    @Override
    public ChatMultitenantMode getChatMultitenantMode() {
        return chatMultitenantMode;
    }

    @Override
    @Nullable
    public String getTimezone() {
        return timezone;
    }

    @Override
    public boolean isBulkUpdateEnabledDefault() {
        return cardBulkUpdateEnabledDefault;
    }

    @Override
    public boolean isBulkDeleteEnabledDefault() {
        return cardBulkDeleteEnabledDefault;
    }

    @Override
    public boolean isChangePasswordRequiredForNewUser() {
        return changePasswordRequiredForNewUser;
    }

    @Override
    public boolean isCardCacheEnabled() {
        return cardCacheEnabled;
    }

    @Override
    public int getCardCacheMaxRecordsPerClass() {
        return cardCacheMaxRecordsPerClass;
    }

    @Override
    public boolean isLogbackAutoconfigurationEnabled() {
        return loggerAutoconfigure;
    }

    @Override
    public LoggerType getLoggerType() {
        return loggerType;
    }

    @Override
    public boolean sessionKeepaliveEnabled() {
        return sessionKeepaliveEnabled;
    }

    @Override
    public String getLoggerConfig() {
        return loggerConfig;
    }

    @Override
    public int getGCHeapPercentage() {
        return triggerGCHeapPercentage;
    }

    @Override
    public int getGCMetaspacePercentage() {
        return triggerGCMetaspacePercentage;
    }

    @Override
    public int getGCSystemMemoryPercentage() {
        return triggerGCSystemMemoryPercentage;
    }

    @Override
    public boolean triggerSystemWarningForTest() {
        return triggerSystemWarningForTest;
    }

    @Override
    public boolean triggerSystemCriticalForTest() {
        return triggerSystemCriticalForTest;
    }

    @Override
    public boolean isSystemMonitorAutoCalculateInterval() {
        return sysMonitorAutocalculateInterval;
    }

    @Override
    public SystemMonitorMode getSystemMonitorMode() {
        return sysMonitorMode;
    }

    @Override
    public int getRelationLimit() {
        return relationLimit;
    }

    @Override
    public boolean getCardlockEnabled() {
        return cardlockEnabled;
    }

    @Override
    public boolean getCardlockShowUser() {
        return cardlockShowUser;
    }

//TODO value validation
//		checkArgument(getSessionTimeoutOrDefault() > getSessionPersistDelay() * 2, "session persist delay should be (quite) less than session timeout");
//		checkArgument(getLockCardTimeOut() > getLockCardPersistDelay() * 2, "lock card persist delay should be (quite) less than lock timeout");
    @Override
    public boolean runDatabaseHousekeepingFunctionAtStartup() {
        return runDatabaseHousekeepingFunctionAtStartup;
    }

    @Override
    public boolean runDatabaseHousekeepingFunctionDaily() {
        return runDatabaseHousekeepingFunctionDaily;
    }

    @Override
    public FaultLevel getNotificationMessagesLevelThreshold() {
        return notificationMessagesLevelThreshold;
    }

    @Override
    public int getSessionPersistDelay() {
        return sessionPersistDelay;
    }

    @Override
    public boolean enableMultigroupByDefault() {
        return enableMultigroupByDefault;
    }

    @Override
    public boolean isMaintenanceModeEnabled() {
        return maintenanceModeEnabled;
    }

    @Override
    public String getMaintenanceModePasstoken() {
        return maintenanceModePasstoken;
    }

    @Override
    public boolean allowConfigUpdateViaWs() {
        return enableConfigUpdate;
    }

    @Override
    public String getDefaultLanguage() {
        return language;
    }

    @Override
    public boolean useLanguagePrompt() {
        return languagePropmpt;
    }

    @Override
    public String getStartingClassName() {
        return startingClass;
    }

    @Override
    public String getDemoModeAdmin() {
        return demoModeAdmin;
    }

    @Override
    public String getInstanceName() {
        return instanceName;
    }

    @Override
    public String getTabsPosition() {
        return tabsPosition;
    }

    @Override
    public int getSessionTimeoutOrDefault() {
        return sessionTimeout;
    }

    @Override
    public boolean getLockCard() {
        return lockCard;
    }

    @Override
    public boolean getLockCardUserVisible() {
        return lockerCardUserVisible;
    }

    @Override
    public int getLockCardTimeOut() {
        return lockCardTimeout;
    }

    @Override
    public Set<String> getEnabledLanguages() {
        return enabledLanguages;
    }

    @Override
    public Set<String> getLoginLanguages() {
        return loginLanguages;
    }

    @Override
    public boolean isImportCsvOneByOne() {
        return importCsvOneByOne;
    }

    @Override
    public int getSessionActivePeriodForStatistics() {
        return sessionActivePeriodForStatistics;
    }

    @Override
    public List<String> getTrustedKeys() {
        return trustedKeys;
    }

    @Override
    @Nullable
    public Long getCompanyLogoUploadsId() {
        return ltEqZeroToNull(companyLogo);
    }

    @Override
    public TextContentSecurity getDefaultTextContentSecurity() {
        return defaultTextContentSecurity;
    }

    @Override
    public boolean isOpenApiEnabled() {
        return openApiEnabled;
    }
}
