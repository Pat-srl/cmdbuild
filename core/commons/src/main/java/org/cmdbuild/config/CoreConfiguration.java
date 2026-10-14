package org.cmdbuild.config;

import jakarta.annotation.Nullable;
import java.time.Duration;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Set;
import org.cmdbuild.chat.ChatMultitenantMode;
import org.cmdbuild.common.localization.LanguageConfiguration;
import org.cmdbuild.dao.entrytype.TextContentSecurity;
import org.cmdbuild.fault.FaultLevel;

public interface CoreConfiguration extends LanguageConfiguration {

    public static final String CORE_NAMESPACE = "org.cmdbuild.core",
            CORE_LOGGER_CONFIG = "logger.config",
            CORE_LOGGER_CONFIG_PROPERTY = CORE_NAMESPACE + "." + CORE_LOGGER_CONFIG,
            CORE_LOGGER_AUTOCONFIGURE = "logger.autoconfigure",
            CORE_LOGGER_AUTOCONFIGURE_PROPERTY = CORE_NAMESPACE + "." + CORE_LOGGER_AUTOCONFIGURE,
            CORE_LOGGER_TYPE = "logger.type",
            CORE_LOGGER_TYPE_PROPERTY = CORE_NAMESPACE + "." + CORE_LOGGER_TYPE,
            CORE_CONFIG_READY = "config.ready",
            CORE_CONFIG_READY_PROPERTY = CORE_NAMESPACE + "." + CORE_CONFIG_READY,
            CORE_LOGGER_STATIC_LOGBACK_CONFIG_LOCATION = CORE_NAMESPACE + "." + "logger.static.location";

    String getJoinViewPrivilegeModeDefault();

    Duration getLockAcquireTimeout();

    boolean isPreloadEnabled();

    public List<String> getPreloadRestUrls();

    boolean runDatabaseHousekeepingFunctionAtStartup();

    boolean runDatabaseHousekeepingFunctionDaily();

    boolean isMaintenanceModeEnabled();

    String getMaintenanceModePasstoken();

    boolean allowConfigUpdateViaWs();

    int getSessionPersistDelay();

    boolean useLanguagePrompt();

    String getStartingClassName();

    String getDemoModeAdmin();

    @Nullable
    String getInstanceName();

    String getTabsPosition();

    int getSessionTimeoutOrDefault();

    boolean getLockCard();

    boolean getLockCardUserVisible();

    boolean enableMultigroupByDefault();

    int getLockCardTimeOut();

    int getRelationLimit();

    Set<String> getEnabledLanguages();

    Set<String> getLoginLanguages();

    boolean isImportCsvOneByOne();

    int getSessionActivePeriodForStatistics();

    List<String> getTrustedKeys();

    FaultLevel getNotificationMessagesLevelThreshold();

    @Nullable
    Long getCompanyLogoUploadsId();

    boolean getCardlockEnabled();

    boolean sessionKeepaliveEnabled();

    boolean getCardlockShowUser();

    int getGCHeapPercentage();

    int getGCMetaspacePercentage();

    int getGCSystemMemoryPercentage();

    boolean triggerSystemWarningForTest();

    boolean triggerSystemCriticalForTest();

    boolean isSystemMonitorAutoCalculateInterval();

    SystemMonitorMode getSystemMonitorMode();

    String getLoggerConfig();

    boolean isLogbackAutoconfigurationEnabled();

    LoggerType getLoggerType();

    boolean isChangePasswordRequiredForNewUser();

    boolean isBulkUpdateEnabledDefault();

    boolean isBulkDeleteEnabledDefault();

    boolean isCardCacheEnabled();

    int getCardCacheMaxRecordsPerClass();

    boolean isChatEnabled();

    ChatMultitenantMode getChatMultitenantMode();

    @Nullable
    String getTimezone();

    TextContentSecurity getDefaultTextContentSecurity();

    CqlSecurity getCqlSecurity();

    boolean isOpenApiEnabled();

    /**
     * persist only locks with last persist date older than this (unless lock
     * data has changed). This is to avoid multiple write on db for repeated
     * lock acquisitions within the same method or method sequence; only the
     * first write will be committed, others will be discarded for
     * {@link #getLockCardPersistDelay()} seconds.
     *
     * @return
     */
    default int getLockCardPersistDelay() {
        return 10; //seconds
    }

    default boolean hasDefaultTextContentSecurity(TextContentSecurity... values) {
        return asList(values).contains(getDefaultTextContentSecurity());
    }

    enum CqlSecurity {
        CS_ALLOW, CS_RESTRICT, CS_DENY
    }

    enum SystemMonitorMode {
        SMM_RECENT, SMM_LAST
    }

    enum LoggerType {
        LT_DEFAULT, LT_STDOUT
    }
}
