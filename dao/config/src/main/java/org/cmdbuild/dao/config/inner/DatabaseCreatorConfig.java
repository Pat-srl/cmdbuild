/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Strings.emptyToNull;
import java.io.File;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_ADMIN_PASSWORD;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_ADMIN_USERNAME;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_PASSWORD;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_TABLESPACE;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_URL;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_USERNAME;
import static org.cmdbuild.dao.config.inner.DatabaseCreator.EXISTING_DATABASE;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface DatabaseCreatorConfig {

    static final String DATABASE_CONFIG_LIMITED_USERNAME = "db.user.username",
            DATABASE_CONFIG_LIMITED_PASSWORD = "db.user.password",
            DATABASE_CONFIG_SOURCE = "db.source",
            DATABASE_CONFIG_POSTGRES_LOCATION = "postgres.location",
            DATABASE_CONFIG_POSTGRES_VERSION = "postgres.version",
            DATABASE_CONFIG_IMPORT_CONF_STRATEGY = "import.configStrategy",
            DATABASE_CONFIG_CREATE_DB = "import.createDb",
            DATABASE_CONFIG_KEEP_LOCAL_CONF = "import.keepLocalConfig",
            DATABASE_CONFIG_INSTALL_POSTGRES = "postgres.install",
            DATABASE_CONFIG_SKIP_PATCHES = "import.skipPatches",
            DATABASE_CONFIG_SINGLE_CORE = "import.singleCore",
            DATABASE_CONFIG_VACUUM_ANALYZE_AFTER_IMPORT = "import.vacuumAnalyze",
            DATABASE_CONFIG_SQL_PATH = "import.sqlPath";

    boolean installPostgres();

    @Nullable
    String getPostgresLocation();

    @Nullable
    String getPostgresVersion();

    boolean createDatabase();

    boolean useLimitedUser();

    boolean keepLocalConfig();

    boolean skipPatches();

    boolean isSingleCore();

    boolean vacuumAnalyzeAfterImport();

    ConfigImportStrategy getConfigImportStrategy();

    String getCmdbuildUser();

    String getCmdbuildPassword();

    String getLimitedUser();

    String getLimitedPassword();

    String getDatabaseUrl();

    String getHost();

    int getPort();

    String getDatabaseName();

    String getAdminUser();

    String getAdminPassword();

    @Nullable
    String getSource();

    @Nullable
    String getSqlPath();

    Map<String, String> getConfig();

    @Nullable
    String getTablespace();

    @Nullable
    default File getSourceFile() {
        return isBlank(getSource()) ? null : new File(getSource());
    }

    default Map<String, String> getCmdbuildDbConfig() {
        return (Map) map(
                DATABASE_CONFIG_URL, getDatabaseUrl(),
                DATABASE_CONFIG_USERNAME, getCmdbuildUser(),
                DATABASE_CONFIG_PASSWORD, getCmdbuildPassword())
                .skipNullValues()
                .with(
                        DATABASE_CONFIG_ADMIN_USERNAME, emptyToNull(getAdminUser()),
                        DATABASE_CONFIG_ADMIN_PASSWORD, emptyToNull(getAdminPassword()),
                        //DATABASE_CONFIG_SKIP_PATCHES, skipPatches() ? "true" : "false",
                        DATABASE_CONFIG_TABLESPACE, emptyToNull(getTablespace()));
    }

    default boolean hasAdminUser() {
        return isNotBlank(getAdminUser());
    }

    default boolean isLimitedUserNotEqualToAdminUser() {
        return !equal(getLimitedUser(), getAdminUser());
    }

    default boolean hasSource() {
        return isNotBlank(getSource());
    }

    default boolean useExistingDatabase() {
        return !hasSource() || equal(EXISTING_DATABASE, getSource());
    }

}
