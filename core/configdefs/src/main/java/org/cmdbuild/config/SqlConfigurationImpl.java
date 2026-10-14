package org.cmdbuild.config;

import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigValue;
import org.cmdbuild.config.api.ConfigComponent;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.config.SqlConfiguration;
import static org.cmdbuild.config.SqlConfigurationImpl.SQL_CONFIGURATION;
import org.cmdbuild.dao.postgres.q3.QueryBuilderConfiguration;

@Component(SQL_CONFIGURATION)
@ConfigComponent("org.cmdbuild.sql")
public class SqlConfigurationImpl implements SqlConfiguration, QueryBuilderConfiguration {

    public static final String SQL_CONFIGURATION = "sqlConfiguration",
            DEFAULT_EXCLUDE_REGEXP = "(SET SESSION|RESET) cmdbuild[.].*| quartz.qrtz_|(INSERT INTO|UPDATE) \"(_Request|_SystemStatusLog|_Temp|_Uploads)\"|SELECT COUNT.*FROM \"_Session\"|SELECT _cm3_temp_put",
            DEFAULT_DDL_INCLUDE_REGEXP = "pax0i6poprc0gggg0f49s4dpgs1sjj0gsom3ck5r50r579umg2qhqdq0atalto0l9l9q18o24oe1k54spgvlqoksksp6f5rpfvsbkrr07q18uu63i66md5l4a3c5jsmt7v01mef7nki54mle468d78phjm6tg62smpqm2db1dtrqcr6ptncro7fn9lobmu2m3rrhr5pchsciqguvgrrim6uub951hndf68hqu6390533fndli76m68v7e6451vp6kl5e41ptamsfilnf3c132vocdbg3m63mkeok32upgknh90u1oi8daap2f1pf94grmu00b28fj5ue8hjcmql0b8m2nba7i0pmat7d3j81jhmt1053rg70tkll7afsp9ha69kohluce0gevgb373v0c7bddxap",
            SQL_LOG_ENABLED_KEY = "log.enabled";

    @ConfigValue(key = SQL_LOG_ENABLED_KEY, description = "enable logging of all sql queries (on logback category org.cmdbuild.sql)", defaultValue = FALSE)
    private Boolean sqlLoggingEnabled;

    @ConfigValue(key = "log.pretty", description = "enable pretty printing of sql/ddl log statements", defaultValue = FALSE)
    private Boolean sqlPrettyPrintEnabled;

    @ConfigValue(key = "log.include", description = "include in sql log queries matching this regex")
    private String sqlLoggingInclude;

    @ConfigValue(key = "log.exclude", description = "exclude from logs sql queries matching this regex", defaultValue = DEFAULT_EXCLUDE_REGEXP)
    private String sqlLoggingExclude;

    @ConfigValue(key = "log.trackTimes", description = "track and log running time of all queries", defaultValue = FALSE)
    private Boolean sqlLoggingTrackTimes;

    @ConfigValue(key = "ddl_log.enabled", description = "enable logging of ddl queries (on logback category org.cmdbuild.sql_ddl)", defaultValue = FALSE)
    private Boolean ddlLoggingEnabled;

    @ConfigValue(key = "ddl_log.include", description = "include in ddl log only queries matching this regex (default should be good for most applications", defaultValue = DEFAULT_DDL_INCLUDE_REGEXP)
    private String ddlLoggingInclude;

    @ConfigValue(key = "ddl_log.exclude", description = "exclude from ddl log queries matching this regex", defaultValue = DEFAULT_EXCLUDE_REGEXP)
    private String ddlLoggingExclude;

    @ConfigValue(key = "pool.debug.enabled", description = "enable connection pool debug", defaultValue = FALSE)
    private Boolean poolDebugEnabled;

    @ConfigValue(key = "pool.debug.removeAbandonedTimeout", description = "when connection pool debug is enabled, mark connections as abandoned when they are not returned after timeout seconds", defaultValue = "120")
    private Integer poolDebugRemoveAbandonedTimeoutSeconds;

    @ConfigValue(key = "pool.maxIdle", description = "max idle connection", defaultValue = "20")
    private Integer maxIdle;

    @ConfigValue(key = "pool.maxActive", description = "max active connection", defaultValue = "50")
    private Integer maxActive;

    @ConfigValue(key = "pool.connectionTimeout", description = "timeout on connection acquisition from pool (millis)", defaultValue = "30000")
    private Integer connectionTimeoutMillis;

    @ConfigValue(key = "pool.connectionLifetime", description = "max connection lifetime within pool (millis)", defaultValue = "0")
    private Integer getConnectionLifetimeMillis;

    @ConfigValue(key = "query.referenceProcessingStrategy", description = "reference processing strategy, one of `default`, `ignoretenant` (ignore tenant is required for cross-tenant references)", defaultValue = "default", experimental = true)
    private SqlQueryReferenceProcessingStrategy referenceProcessingStrategy;

    @Override
    public SqlQueryReferenceProcessingStrategy getReferenceProcessingStrategy() {
        return referenceProcessingStrategy;
    }

    @Override
    public boolean enablePoolDebug() {
        return poolDebugEnabled;
    }

    @Override
    public int getPoolDebugRemoveAbandonedTimeoutSeconds() {
        return poolDebugRemoveAbandonedTimeoutSeconds;
    }

    @Override
    public int getMaxIdle() {
        return maxIdle;
    }

    @Override
    public int getMaxActive() {
        return maxActive;
    }

    @Override
    public int getConnectionTimeout() {
        return connectionTimeoutMillis;
    }

    @Override
    public int getConnectionLifetime() {
        return getConnectionLifetimeMillis;
    }

    @Override
    public boolean enableSqlLogging() {
        return sqlLoggingEnabled;
    }

    @Override
    public boolean enableSqlPrettyPrint() {
        return sqlPrettyPrintEnabled;
    }

    @Override
    @Nullable
    public String excludeSqlRegex() {
        return sqlLoggingExclude;
    }

    @Override
    @Nullable
    public String includeSqlRegex() {
        return sqlLoggingInclude;
    }

    @Override
    public boolean enableSqlLoggingTimeTracking() {
        return sqlLoggingTrackTimes;
    }

    @Override
    public boolean enableDdlLogging() {
        return ddlLoggingEnabled;
    }

    @Override
    @Nullable
    public String includeDdlRegex() {
        return ddlLoggingInclude;
    }

    @Override
    @Nullable
    public String excludeDdlRegex() {
        return ddlLoggingExclude;
    }

}
