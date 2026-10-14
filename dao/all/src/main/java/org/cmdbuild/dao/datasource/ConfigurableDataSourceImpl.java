package org.cmdbuild.dao.datasource;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.eventbus.EventBus;
import com.p6spy.engine.common.P6Util;
import com.p6spy.engine.common.StatementInformation;
import com.p6spy.engine.event.CompoundJdbcEventListener;
import com.p6spy.engine.event.DefaultEventListener;
import com.p6spy.engine.event.JdbcEventListener;
import com.p6spy.engine.event.SimpleJdbcEventListener;
import com.p6spy.engine.spy.JdbcEventListenerFactory;
import com.p6spy.engine.spy.P6DataSource;
import jakarta.annotation.PreDestroy;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.BasicDataSource;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.cmdbuild.common.java.sql.ForwardingDataSource;
import org.cmdbuild.config.api.ConfigListener;
import org.cmdbuild.dao.ConfigurableDataSource;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.DatasourceConfiguredEvent;
import org.cmdbuild.dao.MyPooledDataSource;
import org.cmdbuild.dao.config.DatabaseConfiguration;
import org.cmdbuild.dao.config.SqlConfiguration;
import org.cmdbuild.dao.datasource.utils.MyPooledDataSourceImpl;
import org.cmdbuild.minions.PostStartup;
import static org.cmdbuild.minions.SystemStatus.SYST_LOADING_CONFIG_FILES;
import static org.cmdbuild.minions.SystemStatus.SYST_NOT_RUNNING;
import static org.cmdbuild.minions.SystemStatus.SYST_WAITING_FOR_DATABASE_CONFIGURATION;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.RAW_DATA_SOURCE;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmReflectionUtils.existsOnClasspath;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import static org.cmdbuild.utils.log.LogUtils.printWriterFromLogger;
import org.cmdbuild.utils.postgres.PgVersionUtils.PostgresInfo;
import static org.cmdbuild.utils.postgres.PgVersionUtils.getPostgresInfo;
import static org.cmdbuild.utils.postgres.PgVersionUtils.getPostgresServerVersionNum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component(RAW_DATA_SOURCE)
@Qualifier(SYSTEM_LEVEL_ONE)
public class ConfigurableDataSourceImpl extends ForwardingDataSource implements ConfigurableDataSource {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final DatabaseConfiguration databaseConfiguration;
    private final SqlConfiguration sqlConfiguration;

    private final EventBus eventBus = new EventBus(logExceptions(logger));

    private MyPooledDataSourceImpl innerDataSource;
    private P6DataSource loggerDataSource;
    private DataSource delegateDataSource;
    private boolean ready = false;

    public ConfigurableDataSourceImpl(DatabaseConfiguration configuration, SqlConfiguration sqlConfiguration) {
        this.databaseConfiguration = checkNotNull(configuration);
        this.sqlConfiguration = checkNotNull(sqlConfiguration);

        if (databaseConfiguration.hasConfig()) {
            configureDatasource();
        }
    }

    @Override
    public String getDatabaseUrl() {
        return databaseConfiguration.getDatabaseUrl();
    }

    @Override
    public MyPooledDataSource getInner() {
        return checkNotNull((MyPooledDataSource) innerDataSource, "inner data source is null (not configured or already closed)");
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public void closeInner() {
        cleanupSafe();
    }

    @Override
    public void reloadInner() {
        cleanupSafe();
        doConfigureDatasource();
    }

    @Override
    public String getDatabaseUser() {
        return databaseConfiguration.getDatabaseUser();
    }

    @Override
    public boolean hasAdminDataSource() {
        return isReady() && databaseConfiguration.hasAdminAccount();
    }

    @Override
    public void withAdminDataSource(Consumer<DataSource> consumer) {
        BasicDataSource adminDataSource = createAdminDataSource();
        try {
            consumer.accept(adminDataSource);
        } finally {
            try {
                adminDataSource.close();
            } catch (SQLException ex) {
                logger.error("error closing admin data source", ex);
            }
        }
    }

    @PreDestroy
    public void cleanupSafe() {
        try {
            if (innerDataSource != null && !innerDataSource.isClosed()) {
                logger.info("close inner data source");
                innerDataSource.hardReset();
            }
        } catch (Exception ex) {
            logger.error("error closing inner data source", ex);
        }
        innerDataSource = null;
        delegateDataSource = null;
        loggerDataSource = null;
        ready = false;
    }

    @ConfigListener(value = DatabaseConfiguration.class, requireSystemStatus = {SYST_NOT_RUNNING, SYST_LOADING_CONFIG_FILES, SYST_WAITING_FOR_DATABASE_CONFIGURATION})
    public final void configureDatasource() {
        cleanupSafe();
        if (databaseConfiguration.hasConfig()) {
            checkPostgresDriver();
            doConfigureDatasource();
        } else {
            logger.warn("cannot configure data source: missing database configuration!");
            ready = false;
        }
    }

    @ConfigListener(SqlConfiguration.class)
    @PostStartup
    public void checkAndConfigureSqlLogger() {
        if (isReady()) {
            configureSqlLogger();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        checkDatasource();
        return super.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        checkDatasource();
        return super.getConnection(username, password);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        checkNotNull(iface, "Interface argument must not be null");
        if (!DataSource.class.equals(iface)) {
            throw new SQLException(format("data source of type '%s' can only be unwrapped as '%s', not as '%s'", getClass().getName(), DataSource.class.getName(), iface.getName()));
        }
        return (T) this;
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return DataSource.class.equals(iface);
    }

    @Override
    protected DataSource delegate() {
        return checkNotNull(delegateDataSource, "delegate data source is null (not configured or already closed)");
    }

    private BasicDataSource createAdminDataSource() {
        checkArgument(hasAdminDataSource(), "unable to get admin data source: admin account not configured");
        BasicDataSource adminDataSource = new BasicDataSource();
        setDatasourceParams(adminDataSource);
        adminDataSource.setUsername(databaseConfiguration.getDatabaseAdminUsername());
        adminDataSource.setPassword(databaseConfiguration.getDatabaseAdminPassword());
        return adminDataSource;
    }

    private void doConfigureDatasource() {
        ready = false;
        logger.info("configure datasource with url = {} with user = {}", databaseConfiguration.getDatabaseUrl(), databaseConfiguration.getDatabaseUser());

        innerDataSource = new MyPooledDataSourceImpl();
        setDatasourceParams(innerDataSource);
        if (sqlConfiguration.enablePoolDebug()) {
            innerDataSource.setDurationBetweenEvictionRuns(Duration.ofMillis(2000));
        }
        innerDataSource.setUsername(databaseConfiguration.getDatabaseUser());
        innerDataSource.setPassword(databaseConfiguration.getDatabasePassword());

        delegateDataSource = innerDataSource;
        if (databaseConfiguration.enableDatabaseConnectionEagerCheck()) {
            checkDatabaseConnectionAndStuff();
        }
        ready = true;
        eventBus.post(DatasourceConfiguredEvent.INSTANCE);
        configureSqlLogger();
    }

    @Override
    public boolean isSuperuser() {
        return new JdbcTemplate(innerDataSource).queryForObject("SELECT usesuper FROM pg_user WHERE usename = CURRENT_USER", Boolean.class);
    }

    private void setDatasourceParams(BasicDataSource dataSource) {
        dataSource.addConnectionProperty("autosave", "conservative");
        dataSource.setDriverClassName(databaseConfiguration.getDriverClassName());
        dataSource.setUrl(databaseConfiguration.getDatabaseUrl());

        dataSource.setInitialSize(sqlConfiguration.getMaxIdle());
        dataSource.setMaxIdle(sqlConfiguration.getMaxIdle());
        dataSource.setMaxTotal(checkNotNullAndGtZero(sqlConfiguration.getMaxActive(), "invalid max active connection param"));
        dataSource.setMaxWait(Duration.ofMillis(sqlConfiguration.getConnectionTimeout()));
        dataSource.setMaxConn(Duration.ofMillis(sqlConfiguration.getConnectionLifetime()));
        dataSource.setLogExpiredConnections(false);

        if (sqlConfiguration.enablePoolDebug()) {
            dataSource.setRemoveAbandonedOnMaintenance(true);
            dataSource.setRemoveAbandonedOnBorrow(true);
            dataSource.setRemoveAbandonedTimeout(Duration.ofMillis(sqlConfiguration.getPoolDebugRemoveAbandonedTimeoutSeconds()));
            dataSource.setLogAbandoned(true);
            dataSource.setAbandonedUsageTracking(true);
            dataSource.setAbandonedLogWriter(printWriterFromLogger(logger::warn));
        }
    }

    private void configureSqlLogger() {
        logger.debug("configureSqlLogger");
        checkArgument(isReady());
        if (sqlConfiguration.enableSqlOrDdlLogging() && !datasourceLoggingEnabled()) {
            wrapDatasourceWithLogger();
        } else if (!sqlConfiguration.enableSqlOrDdlLogging() && datasourceLoggingEnabled()) {
            unwrapDatasourceLogger();
        }
    }

    private boolean datasourceLoggingEnabled() {
        return loggerDataSource != null;
    }

    private void wrapDatasourceWithLogger() {
        logger.info("enable sql logger");
        checkArgument(!datasourceLoggingEnabled());
        loggerDataSource = new P6DataSource(getInner());
        loggerDataSource.setJdbcEventListenerFactory(new MyJdbcEventListenerFactory());
        delegateDataSource = loggerDataSource;
    }

    private void unwrapDatasourceLogger() {
        logger.info("disable datasource logger");
        checkArgument(datasourceLoggingEnabled());
        delegateDataSource = innerDataSource;
        loggerDataSource = null;
    }

    private void checkDatabaseConnectionAndStuff() {
        try (Connection connection = innerDataSource.getConnection()) {
            ComparableVersion installedVersion = getPostgresServerVersionNum(connection);

            ComparableVersion minVersion = databaseConfiguration.getMinSupportedPgVersion();
            ComparableVersion maxVersion = databaseConfiguration.getMaxSupportedPgVersion();

            PostgresInfo latestVersion = getPostgresInfo(maxVersion.getCanonical());
            ComparableVersion calculatedMaxVersion = applyOrDefault(latestVersion, PostgresInfo::getCurrentMinor, maxVersion);

            String recommendedVersion = format("recommended version is %s to %s", minVersion.getCanonical(), maxVersion.getCanonical());
            logger.info("postgres server version = {}", installedVersion.getCanonical());
            logger.debug("postgres server {}", recommendedVersion);

            if (installedVersion.compareTo(minVersion) < 0 || installedVersion.compareTo(calculatedMaxVersion) > 0) {
                logger.warn(marker(), "CM: using unsupported postgres version = {} ({})", installedVersion.getCanonical(), recommendedVersion);
            }
            PostgresInfo installedVersionEol = getPostgresInfo(installedVersion.getCanonical());
            if (installedVersionEol != null && !installedVersionEol.isSupported()) {
                logger.warn(marker(), "CM: using unsupported (end-of-life) postgres version = {} (eol: {})", installedVersion.getCanonical(), installedVersionEol.getEol());
            }
            if (isSuperuser()) {
                logger.warn(marker(), "CM: configured jdbc account =< {} > has `superuser` privileges: this is not recommended, and incompatible with multi tenant and other features. You should use a non-superuser jdbc account.", databaseConfiguration.getDatabaseUser());
            }
        } catch (Exception ex) {
            logger.error(marker(), "error checking database connection", ex);
        }
    }

    private void checkDatasource() {
        checkArgument(ready, "the datasource is not configured!");
    }

    private void checkPostgresDriver() {
        checkPostgresDriverVersion();
    }

    private boolean postgresDriverExistsOnClasspath() {
        return existsOnClasspath(databaseConfiguration.getDriverClassName());
    }

    private void checkPostgresDriverVersion() {
        try {
            Class pgdriver = Class.forName(databaseConfiguration.getDriverClassName());
            logger.info("postgres driver = {}", pgdriver.getName());
            String pgDriverVersion = (String) pgdriver.getMethod("getVersion").invoke(null);
            logger.info("postgres driver version = {}", pgDriverVersion);
            String normalizedPgDriverVersion = pgDriverVersion.replaceAll("[^0-9.]*", "");
            if (!normalizedPgDriverVersion.matches("42[.]7[.]3")) {
                logger.warn(marker(), "unsupported postgres jdbc driver: recommended postgres driver version is 42.7.3");
            }
        } catch (Exception ex) {
            throw new DaoException(ex, "error checking postgres driver version");
        }
    }

    private class MyJdbcEventListenerFactory extends SimpleJdbcEventListener implements JdbcEventListenerFactory {

        private final Logger sqlLogger = LoggerFactory.getLogger("org.cmdbuild.sql");
        private final Logger ddlLogger = LoggerFactory.getLogger("org.cmdbuild.sql_ddl");

        private final JdbcEventListener listener;

        public MyJdbcEventListenerFactory() {
            CompoundJdbcEventListener compoundEventListener = new CompoundJdbcEventListener();
            compoundEventListener.addListender(DefaultEventListener.INSTANCE);
            compoundEventListener.addListender(this);
            listener = compoundEventListener;
        }

        @Override
        public JdbcEventListener createJdbcEventListener() {
            return listener;
        }

        @Override
        public void onAfterAnyExecute(StatementInformation statementInformation, long timeElapsedNanos, SQLException e) {
            String sql = statementInformation.getSql();
            boolean logToSql = logToSql(sql);
            boolean logToDdl = logToDdl(sql);
            if (e != null || logToSql || logToDdl) {
                sql = normalizeSql(statementInformation.getSqlWithValues());
                if (e == null) {
                    if (logToSql) {
                        sqlLogger.info(sql);
                    } else {
                        sqlLogger.trace(sql);
                    }
                    if (logToDdl) {
                        ddlLogger.info(sql);
                    }
                } else {
                    sqlLogger.error("sql exception = {} on query = {}", e.toString(), sql);
                }
                if (sqlConfiguration.enableSqlLoggingTimeTracking()) {
                    String message = format(" -- elapsed time = %sms", timeElapsedNanos / 1000000);
                    if (logToSql) {
                        sqlLogger.info(message);
                    } else {
                        sqlLogger.trace(message);
                    }
                }
            }
        }

        private String normalizeSql(String sql) {
            sql = format("%s;", P6Util.singleLine(sql));
            if (sqlConfiguration.enableSqlPrettyPrint()) {
                try {
                    sql = SqlFormatter.format(sql);
                } catch (Exception ex) {
                    logger.warn("error pretty printing sql", ex);
                }
            }
            return sql;
        }

        private boolean logToSql(String statement) {
            return sqlConfiguration.enableSqlLogging()
                    && (isBlank(sqlConfiguration.includeSqlRegex()) || Pattern.compile(sqlConfiguration.includeSqlRegex(), Pattern.DOTALL).matcher(statement).find())
                    && (isBlank(sqlConfiguration.excludeSqlRegex()) || !Pattern.compile(sqlConfiguration.excludeSqlRegex(), Pattern.DOTALL).matcher(statement).find());
        }

        private boolean logToDdl(String statement) {
            return sqlConfiguration.enableDdlLogging()
                    && (isBlank(sqlConfiguration.includeDdlRegex()) || Pattern.compile(sqlConfiguration.includeDdlRegex(), Pattern.DOTALL).matcher(statement).find())
                    && (isBlank(sqlConfiguration.excludeDdlRegex()) || !Pattern.compile(sqlConfiguration.excludeDdlRegex(), Pattern.DOTALL).matcher(statement).find());
        }

    }

}
