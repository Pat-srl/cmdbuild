package org.cmdbuild.dao.config;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.cmdbuild.cleanup.DatabaseCleanupRulesConfig;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface DatabaseConfiguration extends DatabaseCleanupRulesConfig {

    static final String DATABASE_CONFIG_NAMESPACE = "org.cmdbuild.database",
            DATABASE_CONFIG_NAMESPACE_PREFIX = DATABASE_CONFIG_NAMESPACE + ".",
            DATABASE_CONFIG_URL = "db.url",
            DATABASE_CONFIG_TABLESPACE = "db.tablespace",
            DATABASE_CONFIG_USERNAME = "db.username",
            DATABASE_CONFIG_PASSWORD = "db.password",
            DATABASE_CONFIG_ADMIN_USERNAME = "db.admin.username",
            DATABASE_CONFIG_ADMIN_PASSWORD = "db.admin.password",
            DATABASE_CONFIG_AUTOPATCH = "db.autopatch.enable",
            DEFAULT_DB_DRIVER_CLASS_NAME = "org.postgresql.Driver";

    static final ComparableVersion MIN_PSQL_VERSION = new ComparableVersion("12"),
            MAX_PSQL_VERSION = new ComparableVersion("17.3");

    String getDatabaseUrl();

    String getDatabaseUser();

    String getDatabasePassword();

    @Nullable
    String getDatabaseAdminUsernameOrNull();

    @Nullable
    String getDatabaseAdminPasswordOrNull();

    String getDriverClassName();

    boolean enableAutoPatch();

    @Nullable
    String getVertVersionNumber();

    @Nullable
    String getVertName();

    default String getHost() {
        return getDatabaseUrl().replaceFirst(".*//([^:/]+)([:/].*)?", "$1"); //TODO test this
    }

    default int getPort() {
        return Integer.parseInt(getDatabaseUrl().replaceFirst(".*//([^:]+):([0-9]+).*", "$2"));//TODO test this
    }

    default String getDatabase() {
        return getDatabaseUrl().replaceFirst(".*//[^/]+/([^?]+).*", "$1"); //TODO test this
    }

    default String getDatabaseAdminUsername() {
        return checkNotBlank(getDatabaseAdminUsernameOrNull(), "postgres admin username not configured");
    }

    default String getDatabaseAdminPassword() {
        return checkNotBlank(getDatabaseAdminPasswordOrNull(), "postgres admin password not configured");
    }

    default boolean hasConfig() {
        return isNotBlank(getDatabaseUrl()) && isNotBlank(getDatabaseUser()) && isNotBlank(getDatabasePassword());
    }

    default ComparableVersion getMinSupportedPgVersion() {
        return MIN_PSQL_VERSION;
    }

    default ComparableVersion getMaxSupportedPgVersion() {
        return MAX_PSQL_VERSION;
    }

    boolean enableDatabaseConnectionEagerCheck();

    default boolean hasAdminAccount() {
        return isNotBlank(getDatabaseAdminUsernameOrNull());
    }

}
