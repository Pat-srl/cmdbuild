package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.builder.Builder;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_ADMIN_PASSWORD;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_ADMIN_USERNAME;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_NAMESPACE_PREFIX;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_PASSWORD;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_TABLESPACE;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_URL;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_USERNAME;
import static org.cmdbuild.dao.config.inner.ConfigImportStrategy.CIS_DEFAULT;
import org.cmdbuild.dao.config.utils.PostgresUrl;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import static org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils.decryptValue;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public final class DatabaseCreatorConfigImpl implements DatabaseCreatorConfig {

    private final Map<String, String> config;

    private final String adminUser, adminPassword, limitedUser, limitedPassword, sqlPath, postgresLocation, postgresVersion;
    private final String source, tablespace;
    private final boolean useLimited, createDatabase, keepLocalConfig, installPostgres, skipPatches, singleCore, vacuumAnalyzeAfterImport;
    private final ConfigImportStrategy configImportStrategy;
    private final PostgresUrl postgresUrl;

    private DatabaseCreatorConfigImpl(Map<String, String> config) {
        this.config = map(checkNotNull(config)).immutable();

        String defaultUser = emptyToNull(config.get(DATABASE_CONFIG_USERNAME)),
                defaultPsw = emptyToNull(config.get(DATABASE_CONFIG_PASSWORD));
        adminUser = firstNotBlank(config.get(DATABASE_CONFIG_ADMIN_USERNAME), defaultUser, "postgres");
        adminPassword = decryptValue(firstNotBlank(config.get(DATABASE_CONFIG_ADMIN_PASSWORD), defaultPsw, "postgres"));
        limitedUser = firstNotBlank(config.get(DATABASE_CONFIG_LIMITED_USERNAME), defaultUser, "cmdbuild");
        limitedPassword = decryptValue(firstNotBlank(config.get(DATABASE_CONFIG_LIMITED_PASSWORD), defaultPsw, "cmdbuild"));

        useLimited = !equal(adminUser, limitedUser);

        tablespace = config.get(DATABASE_CONFIG_TABLESPACE);

        source = config.get(DATABASE_CONFIG_SOURCE);

        postgresUrl = PostgresUrl.parse(firstNotBlank(config.get(DATABASE_CONFIG_URL), new PostgresUrl(null, null, "cmdbuild").toJdbcUrl()));

        this.configImportStrategy = parseEnumOrDefault(config.get(DATABASE_CONFIG_IMPORT_CONF_STRATEGY), CIS_DEFAULT);
        this.createDatabase = toBooleanOrDefault(config.get(DATABASE_CONFIG_CREATE_DB), true);
        this.keepLocalConfig = toBooleanOrDefault(config.get(DATABASE_CONFIG_KEEP_LOCAL_CONF), false);
        this.skipPatches = toBooleanOrDefault(config.get(DATABASE_CONFIG_SKIP_PATCHES), false);
        this.singleCore = toBooleanOrDefault(config.get(DATABASE_CONFIG_SINGLE_CORE), false);
        this.vacuumAnalyzeAfterImport = toBooleanOrDefault(config.get(DATABASE_CONFIG_VACUUM_ANALYZE_AFTER_IMPORT), true);

        this.sqlPath = config.get(DATABASE_CONFIG_SQL_PATH);
        this.postgresLocation = config.get(DATABASE_CONFIG_POSTGRES_LOCATION);
        this.postgresVersion = config.get(DATABASE_CONFIG_POSTGRES_VERSION);
        this.installPostgres = toBooleanOrDefault(config.get(DATABASE_CONFIG_INSTALL_POSTGRES), isNotBlank(postgresLocation));
    }

    @Override
    public boolean vacuumAnalyzeAfterImport() {
        return vacuumAnalyzeAfterImport;
    }

    @Override
    @Nullable
    public String getPostgresLocation() {
        return postgresLocation;
    }

    @Override
    @Nullable
    public String getPostgresVersion() {
        return postgresVersion;
    }

    @Override
    @Nullable
    public String getTablespace() {
        return tablespace;
    }

    @Override
    public boolean skipPatches() {
        return skipPatches;
    }

    @Override
    public boolean isSingleCore() {
        return singleCore;
    }

    @Override
    public boolean createDatabase() {
        return createDatabase;
    }

    @Override
    public Map<String, String> getConfig() {
        return config;
    }

    @Override
    public boolean installPostgres() {
        return installPostgres;
    }

    @Override
    public boolean keepLocalConfig() {
        return keepLocalConfig;
    }

    @Override
    public ConfigImportStrategy getConfigImportStrategy() {
        return configImportStrategy;
    }

    @Override
    public boolean useLimitedUser() {
        return useLimited;
    }

    @Override
    public String getCmdbuildUser() {
        if (useLimitedUser()) {
            return checkNotBlank(limitedUser, "limited user enabled but limited username not set!");
        } else {
            return getAdminUser();
        }
    }

    @Override
    public String getCmdbuildPassword() {
        if (useLimitedUser()) {
            return checkNotBlank(limitedPassword, "limited user enabled but limited password not set!");
        } else {
            return getAdminPassword();
        }
    }

    @Override
    public String getDatabaseUrl() {
        return postgresUrl.toJdbcUrl();
    }

    @Override
    public String getHost() {
        return postgresUrl.getHost();
    }

    @Override
    public int getPort() {
        return postgresUrl.getPort();
    }

    @Override
    public String getDatabaseName() {
        return postgresUrl.getDatabase();
    }

    @Override
    public String getAdminUser() {
        return adminUser;
    }

    @Override
    public String getAdminPassword() {
        return adminPassword;
    }

    @Override
    public String getLimitedUser() {
        return limitedUser;
    }

    @Override
    public String getLimitedPassword() {
        return limitedPassword;
    }

    @Override
    @Nullable
    public String getSource() {
        return source;
    }

    @Override
    public String getSqlPath() {
        return sqlPath;
    }

    public static DatabaseCreatorConfigImplBuilder builder() {
        return new DatabaseCreatorConfigImplBuilder();
    }

    public static DatabaseCreatorConfig fromFile(InputStream source) {
        return builder().withConfig(source).build();
    }

    public static DatabaseCreatorConfig build(String configFileContent) {
        return builder().withConfig(loadProperties(configFileContent)).build();
    }

    public static DatabaseCreatorConfigImplBuilder copyOf(DatabaseCreatorConfig source) {
        return new DatabaseCreatorConfigImplBuilder().withConfig(source.getConfig());
    }

    public static class DatabaseCreatorConfigImplBuilder implements Builder<DatabaseCreatorConfig> {

        private final Map<String, String> config = map();

        private DatabaseCreatorConfigImplBuilder() {
        }

        public DatabaseCreatorConfigImplBuilder clear() {
            config.clear();
            return this;
        }

        public DatabaseCreatorConfigImplBuilder keepOnlyCmdbuildConfig() {
            Map<String, String> cmdbuildDbConfig = build().getCmdbuildDbConfig();
            return this.clear().withConfig(cmdbuildDbConfig);
        }

        public DatabaseCreatorConfigImplBuilder withConfig(Map<String, String> config) {
            this.config.putAll(map(config).mapKeys(k -> k.replaceFirst(DATABASE_CONFIG_NAMESPACE_PREFIX, "")));
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withInstallPostgres(boolean installPostgres) {
            config.put(DATABASE_CONFIG_INSTALL_POSTGRES, toStringNotBlank(installPostgres));
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withSkipPatches(boolean skipPatches) {
            config.put(DATABASE_CONFIG_SKIP_PATCHES, toStringNotBlank(skipPatches));
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withSingleCore(boolean singleCore) {
            config.put(DATABASE_CONFIG_SINGLE_CORE, toStringNotBlank(singleCore));
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withTablespace(String tablespace) {
            config.put(DATABASE_CONFIG_TABLESPACE, tablespace);
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withUser(String username, String password) {
            config.put(DATABASE_CONFIG_USERNAME, username);
            config.put(DATABASE_CONFIG_PASSWORD, password);
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withAdminUser(String username, String password) {
            config.put(DATABASE_CONFIG_ADMIN_USERNAME, username);
            config.put(DATABASE_CONFIG_ADMIN_PASSWORD, password);
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withLimitedUser(String username, String password) {
            config.put(DATABASE_CONFIG_LIMITED_USERNAME, username);
            config.put(DATABASE_CONFIG_LIMITED_PASSWORD, password);
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withDatabaseUrl(String cmdbuildDatabaseUrl) {
            config.put(DATABASE_CONFIG_URL, cmdbuildDatabaseUrl);
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withPostgresLocation(String location) {
            config.put(DATABASE_CONFIG_POSTGRES_LOCATION, location);
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withCreateDatabase(Boolean createDatabase) {
            config.put(DATABASE_CONFIG_CREATE_DB, toStringOrNull(createDatabase));
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withKeepLocalConfig(Boolean keepLocalConfig) {
            config.put(DATABASE_CONFIG_KEEP_LOCAL_CONF, toStringOrNull(keepLocalConfig));
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withConfigImportStrategy(ConfigImportStrategy configImportStrategy) {
            config.put(DATABASE_CONFIG_IMPORT_CONF_STRATEGY, serializeEnum(configImportStrategy));
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withDatabaseUrl(@Nullable String host, @Nullable Integer port, String databaseName) {
            return this.withDatabaseUrl(new PostgresUrl(host, port, databaseName).toJdbcUrl());

        }

        public DatabaseCreatorConfigImplBuilder withDatabaseName(String databaseName) {
            if (isBlank(config.get(DATABASE_CONFIG_URL))) {
                return this.withDatabaseUrl(null, null, databaseName);
            } else {
                return this.withDatabaseUrl(PostgresUrl.parse(config.get(DATABASE_CONFIG_URL)).withDatabase(databaseName).toJdbcUrl());
            }
        }

        public DatabaseCreatorConfigImplBuilder withSource(String databaseType) {
            config.put(DATABASE_CONFIG_SOURCE, databaseType);
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withSqlPath(String sqlPath) {
            config.put(DATABASE_CONFIG_SQL_PATH, sqlPath);
            return this;
        }

        public DatabaseCreatorConfigImplBuilder withConfig(InputStream inputStream) {
            return this.withConfig(map(loadProperties(inputStream)).mapValues(Cm3EasyCryptoUtils::decryptValue));
        }

        public DatabaseCreatorConfigImplBuilder withConfig(File configFile) {
            return this.withConfig(map(loadProperties(configFile)).mapValues(Cm3EasyCryptoUtils::decryptValue));
        }

        public DatabaseCreatorConfigImplBuilder accept(Consumer<DatabaseCreatorConfigImplBuilder> visitor) {
            visitor.accept(this);
            return this;
        }

        @Override
        public DatabaseCreatorConfig build() {
            return new DatabaseCreatorConfigImpl(config);
        }

    }

}
