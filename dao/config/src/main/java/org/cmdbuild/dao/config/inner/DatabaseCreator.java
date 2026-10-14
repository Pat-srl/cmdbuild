package org.cmdbuild.dao.config.inner;

import jakarta.annotation.Nullable;
import java.io.File;
import static java.lang.String.format;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;
import javax.sql.DataSource;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FilenameUtils.getBaseName;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.api.ConfigCategory;
import static org.cmdbuild.config.api.ConfigCategory.CC_DEFAULT;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigDefinition;
import static org.cmdbuild.config.utils.ConfigDefinitionUtils.getAllConfigDefinitionsFromClasspath;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.config.utils.PatchManagerUtils;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmExecutorUtils.runSafe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.postgres.PgVersionUtils.getPostgresServerVersionNum;
import org.cmdbuild.utils.postgres.PostgresHelperConfigImpl.PostgresHelperBuilder;
import org.cmdbuild.utils.postgres.PostgresUtils;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public final class DatabaseCreator {

    public final static String EXISTING_DATABASE = "existing",
            EMPTY_DUMP = "empty.dump.xz",
            DEMO_DUMP = "demo.dump.xz",
            R2U_DUMP = "ready2use_demo.dump.xz";

    public static final Set<String> EMBEDDED_DATABASES = Set.of(EXISTING_DATABASE, EMPTY_DUMP, DEMO_DUMP, R2U_DUMP);

    public static final Set<String> ALLOWED_POSTGRES_EXTENSIONS = Set.of("tablefunc");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String POSTGRES_SUPER_DATABASE = "postgres", GIS_SCHEMA = "gis", PUBLIC_SCHEMA = "public";

    private final DatabaseCreatorConfig config;

    public DatabaseCreator(DatabaseCreatorConfig config) {
        this.config = checkNotNull(config);
    }

    public String getDatabaseUrl() {
        return config.getDatabaseUrl();
    }

    public DatabaseCreatorConfig getConfig() {
        return config;
    }

    private boolean hasDumpDir() {
        return isNotBlank(config.getSqlPath()) && getDumpDir().isDirectory();
    }

    public File getDumpDir() {
        return checkNotNull(getDumpDirOrNull());
    }

    @Nullable
    private File getDumpDirOrNull() {
        if (isBlank(config.getSqlPath())) {
            return null;
        } else {
            return new File(config.getSqlPath(), "dump");
        }
    }

    public DataSource getAdminDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{config.getHost()});
        dataSource.setPortNumbers(new int[]{config.getPort()});
        dataSource.setUser(config.getAdminUser());
        dataSource.setPassword(config.getAdminPassword());
        dataSource.setDatabaseName(POSTGRES_SUPER_DATABASE);
        return dataSource;
    }

    public DataSource getCmdbuildDataSource() {
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setServerNames(new String[]{config.getHost()});
        dataSource.setPortNumbers(new int[]{config.getPort()});
        dataSource.setUser(config.getCmdbuildUser());
        dataSource.setPassword(config.getCmdbuildPassword());
        dataSource.setDatabaseName(config.getDatabaseName());
        return dataSource;
    }

    public JdbcTemplate getAdminJdbcTemplate() {
        return new JdbcTemplate(getAdminDataSource());
    }

    public JdbcTemplate getCmdbuildJdbcTemplate() {
        return new JdbcTemplate(getCmdbuildDataSource());
    }

    public boolean cmdbuildDatabaseExists() { //TODO check with a query ?
        logger.info("checking database");
        DataSource dataSource = getCmdbuildDataSource();
        try {
            try (Connection connection = dataSource.getConnection()) {
                checkNotNull(connection);
                String versionString = getPostgresServerVersionNum(connection).getCanonical();
                logger.info("database found, postgres server version = {}", versionString);
                return true;
            }
        } catch (SQLException ex) {
            logger.debug("database not found", ex);
            logger.info("database not found: {}", ex.toString());
            return false;
        }
    }

    public boolean useExistingDatabase() {
        return config.useExistingDatabase();
    }

    public void configureDatabase() {
        try {
            if (!useExistingDatabase()) {
                logger.info("create database = {} from source = {}", config.getDatabaseUrl(), config.getSource());

                if (config.createDatabase()) {
                    checkArgument(!cmdbuildDatabaseExists(), "database %s already exists; if you really want to trash it, drop it manually before running this procedure", config.getDatabaseName());
                    createDatabase();
                }

                if (config.useLimitedUser() && config.isLimitedUserNotEqualToAdminUser()) {
                    createUserIfNotExists(config.getLimitedUser(), config.getLimitedPassword());
                    getAdminJdbcTemplate().execute(format("ALTER DATABASE \"%s\" OWNER TO \"%s\"", config.getDatabaseName(), config.getCmdbuildUser()));
                }

                try (SuperUserHelper sudo = new SuperUserHelper()) {
                    checkArgument(cmdbuildDatabaseExists(), "database not found = %s", config.getDatabaseName());

                    restoreDump();

                    //TODO move this to custom sql function (?)
                    sudo.getLimitedAsSudoJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS \"_DatabaseImportLog\" (\"Source\" varchar not null,\"ImportTime\" timestamp not null default now())");
                    sudo.getLimitedAsSudoJdbcTemplate().update("INSERT INTO \"_DatabaseImportLog\" (\"Source\") VALUES (?)", config.getSource());
                } catch (Exception ex) {
                    logger.warn("failed to aquire or release superuser access", ex);
                }
            }
        } catch (Exception e) {
            throw new DaoException(e, "Error while configuring the database");
        }
    }

    public void applyPatchesOrSkip() {
        if (!config.skipPatches()) {
            applyPatches();
        }
    }

    public void applyPatches() {
        getPatchManager().applyPendingPatchesAndFunctions();
    }

    public void applyPatchesUpTo(String lastPatch) {
        getPatchManager().applyPendingPatchesAndFunctionsUpTo(lastPatch);
    }

    public PatchService getPatchManager() {
        return PatchManagerUtils.buildAndInitPatchManager(this, new File(config.getSqlPath()));
    }

    public void adjustConfigs() {
        adjustConfigs(null);
    }

    public void adjustConfigs(@Nullable Map<String, String> previousCmdbuildConfigs) {
        JdbcTemplate jdbcTemplate = getCmdbuildJdbcTemplate();
        Set<ConfigCategory> categoriesToSkip = switch (config.getConfigImportStrategy()) {
            case CIS_RESTORE_BACKUP -> {
                logger.debug("backup restore mode, import all configs");
                yield emptySet();
            }
            case CIS_DEFAULT -> {
                logger.debug("default restore mode, do not import env configs");
                yield EnumSet.of(CC_ENV);
            }
            case CIS_DATA_ONLY -> {
                logger.debug("data only restore mode, import only data configs");
                yield EnumSet.of(CC_ENV, CC_DEFAULT);
            }
            default ->
                throw new DaoException("unsupported config import strategy = %s", config.getConfigImportStrategy());
        };
        Set<String> configsToSkipFromImport = getAllConfigDefinitionsFromClasspath().stream().filter(ConfigDefinition::isLocationDefault).filter(d -> categoriesToSkip.contains(d.getCategory())).map(ConfigDefinition::getKey).collect(toSet());
        configsToSkipFromImport.forEach(c -> jdbcTemplate.execute(format("DO $$ BEGIN IF to_regclass('\"_SystemConfig\"') IS NOT NULL THEN UPDATE \"_SystemConfig\" SET \"Status\" = 'N', \"Notes\" = 'disabled after import' WHERE \"Status\" = 'A' AND \"Code\" = %s; END IF; END $$ LANGUAGE PLPGSQL;", systemToSqlExpr(c))));

        if (!firstNotNull(previousCmdbuildConfigs, emptyMap()).isEmpty() && config.keepLocalConfig()) {
            logger.debug("keep local configs (from previous db)");
            setConfigs(map(previousCmdbuildConfigs).filterKeys(configsToSkipFromImport::contains));
        }
    }

    public void setConfigs(Map<String, String> configs) {
        JdbcTemplate jdbcTemplate = getCmdbuildJdbcTemplate();
        configs.forEach((k, v) -> {
            jdbcTemplate.execute(format("DO $$ BEGIN IF to_regprocedure('_cm3_system_config_set(varchar,varchar)') IS NOT NULL THEN PERFORM _cm3_system_config_set(%s, %s); END IF; END $$ LANGUAGE PLPGSQL;", systemToSqlExpr(k), systemToSqlExpr(v)));
        });
    }

    public Map<String, String> getSystemConfigsFromDbSafe() {
        try {
            if (cmdbuildDatabaseExists()) {
                return getSystemConfigsFromDb();
            } else {
                return emptyMap();
            }
        } catch (Exception ex) {
            logger.warn("error retrieving currend system config from db", ex);
            return emptyMap();
        }
    }

    public Map<String, String> getSystemConfigsFromDb() {
        try {
            DataSource dataSource = getCmdbuildDataSource();
            try (Connection connection = dataSource.getConnection()) {
                connection.createStatement().executeUpdate("CREATE OR REPLACE FUNCTION pg_temp._cm3_query_aux_configs_as_map_safe() RETURNS jsonb AS $$ BEGIN IF to_regclass('\"_SystemConfig\"') IS NOT NULL THEN RETURN (SELECT COALESCE(jsonb_object_agg(\"Code\", \"Value\"), '{}'::jsonb) FROM \"_SystemConfig\" WHERE \"Status\" = 'A'); ELSE RETURN '{}'::jsonb; END IF; END $$ LANGUAGE PLPGSQL;");
                try (ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM pg_temp._cm3_query_aux_configs_as_map_safe() _config;")) {
                    checkArgument(resultSet.next());
                    return fromJson(resultSet.getString("_config"), MAP_OF_STRINGS);
                } finally {
                    connection.createStatement().executeUpdate("DROP FUNCTION pg_temp._cm3_query_aux_configs_as_map_safe()");
                }
            }
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
    }

    public void compactDb() {
        logger.info("compact db");
        long sizeBefore = getCmdbuildJdbcTemplate().queryForObject("SELECT SUM(total_size) FROM _cm3_utils_disk_usage_detailed()", Long.class);
        logger.info("raw db size (before) = {}", byteCountToDisplaySize(sizeBefore));
        DataSource dataSource = getCmdbuildDataSource();
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute(readToString(getClass().getResourceAsStream("/org/cmdbuild/dao/config/temp_cleanup.sql")));
            statement.execute(readToString(getClass().getResourceAsStream("/org/cmdbuild/dao/config/history_cleanup.sql")));
            statement.execute("VACUUM ANALYZE");
        } catch (SQLException ex) {
            throw new DaoException(ex);
        }
        long sizeAfter = getCmdbuildJdbcTemplate().queryForObject("SELECT SUM(total_size) FROM _cm3_utils_disk_usage_detailed()", Long.class);
        logger.info("raw db size (after) = {}", byteCountToDisplaySize(sizeAfter));
        logger.info("db size reduced by {}", format("%.2f%%", (sizeBefore - sizeAfter) * 100d / sizeAfter));
    }

    private void createDatabase() {
        logger.info("Creating database {}", config.getDatabaseName());
        String query = format("CREATE DATABASE \"%s\" ENCODING 'UTF8'", checkValidName(config.getDatabaseName()));
        if (isNotBlank(config.getTablespace())) {
            query += format(" TABLESPACE %s", config.getTablespace());
        }
        getAdminJdbcTemplate().execute(query);
    }

    public File getDumpFile(String dumpName) {
        if (dumpName.startsWith("classpath:")) {
            File file = tempFile();
            toDelete.add(file);
            copy(getClass().getClassLoader().getResourceAsStream(dumpName.replaceFirst("classpath:/?", "")), file);
            return file;
        } else if (dumpName.startsWith("localdump:")) {
            checkNotNull(System.getenv("CMDBUILD_LOCAL_DUMPS"), "Dump folder not found, set the env variable to CMDBUILD_LOCAL_DUMPS");
            return new File(System.getenv("CMDBUILD_LOCAL_DUMPS") + "/" + dumpName.replaceAll("localdump:", ""));
        } else if (new File(dumpName).isFile()) {
            return new File(dumpName);
        } else {
            return listOf(File.class).accept(l -> {
                if (hasDumpDir()) {
                    list(getDumpDir().listFiles()).stream().filter(equal(File::getName, dumpName)).forEach(l::add);
                    list(getDumpDir().listFiles()).stream().filter(f -> Objects.equals(getBaseName(getBaseName(f.getName())), getBaseName(getBaseName(dumpName)))).forEach(l::add);
                }
            }).stream().filter(File::exists).limit(1).collect(onlyElement("dump file not found for name =< %s >", dumpName));
        }
    }

    private final List<File> toDelete = list();

    private void restoreDump() {
        File dumpFile;
        try {
            String dumpName = checkNotBlank(config.getSource(), "database source is null");
            dumpFile = getDumpFile(dumpName);
            dumpFile = PostgresUtils.getDumpFromFileExtractIfNecessary(dumpFile);
            logger.info("restoring database from dump = {}", dumpFile.getAbsolutePath());
            checkArgument(dumpFile.isFile() && dumpFile.length() > 0, "invalid dump file = %s", dumpFile);
            boolean hasGisSchema = PostgresUtils.dumpContainsSchema(dumpFile, GIS_SCHEMA);
            if (hasGisSchema) {
                logger.info("creating gis schema");
                createSchema(GIS_SCHEMA);
                JdbcTemplate jdbcTemplate = new JdbcTemplate(getCmdbuildDataSource());
                String currentPostgisVersion = jdbcTemplate.queryForObject("SELECT COALESCE((SELECT extversion FROM pg_extension WHERE extname = 'postgis'),'')", String.class);//TODO duplicate code
                if (isBlank(currentPostgisVersion)) {
                    logger.info("creating postgis extension");
                    jdbcTemplate.execute("CREATE EXTENSION postgis SCHEMA gis");
                    jdbcTemplate.execute("DO $$ BEGIN EXECUTE format('ALTER DATABASE %I SET search_path = \"$user\", public, gis', current_database()); END $$;");
                    currentPostgisVersion = jdbcTemplate.queryForObject("SELECT extversion FROM pg_extension WHERE extname = 'postgis'", String.class);//TODO duplicate code
                } else {
                    logger.debug("found existing postgis extension");
                    String gisExtensionTable = jdbcTemplate.queryForObject("SELECT n.nspname AS \"Schema\" FROM pg_catalog.pg_extension e LEFT JOIN pg_catalog.pg_namespace n ON n.oid = e.extnamespace WHERE e.extname = 'postgis'", String.class);//TODO duplicate code
                    if (!Objects.equals(gisExtensionTable, "gis")) {
                        logger.debug("postgis extension is installed in the wrong schema, moving to schema gis");
                        jdbcTemplate.execute("UPDATE pg_extension SET extrelocatable = TRUE WHERE extname = 'postgis'");
                        jdbcTemplate.execute("ALTER EXTENSION postgis SET SCHEMA gis;");
                        jdbcTemplate.execute("UPDATE pg_extension SET extrelocatable = FALSE WHERE extname = 'postgis'");
                        jdbcTemplate.execute("DO $$ BEGIN EXECUTE format('ALTER DATABASE %I SET search_path = \"$user\", public, gis', current_database()); END $$;");
                    }
                }
                logger.info("gis schema ready with postgis version = {}", currentPostgisVersion);//TODO also check version
            } else {
                logger.info("gis schema not present in dump, skipping");
            }
            restoreExtensions(dumpFile);
            logger.info("restore public schema from dump");
            postgresHelper()
                    .withSchemas(list(PUBLIC_SCHEMA))
                    .buildHelper()
                    .restoreDumpFromFile(dumpFile);
            if (hasGisSchema) {
                logger.info("restore gis schema from dump");
                postgresHelper()
                        .withSchema(GIS_SCHEMA)
                        .buildHelper()
                        .restoreDumpFromFile(dumpFile, (s) -> {
                            return s.matches(".* (TABLE|COMMENT|TRIGGER|INDEX|CONSTRAINT) .*") && !s.matches(".*(spatial_ref|geometry_columns|geography_columns).*");
                        });
            }
            List<String> otherSchemas = list(PostgresUtils.getSchemasInDump(dumpFile)).without(PUBLIC_SCHEMA, GIS_SCHEMA).sorted();
            if (!otherSchemas.isEmpty()) {
                logger.info("restore other schemas = {}", otherSchemas);
                postgresHelper()
                        .withSchemas(otherSchemas)
                        .buildHelper()
                        .restoreDumpFromFile(dumpFile);
            }
            if (config.vacuumAnalyzeAfterImport()) {
                logger.info("run vacuum analyze");
                PostgresUtils.newHelper(config.getHost(), config.getPort(), config.getCmdbuildUser(), config.getCmdbuildPassword())
                        .withDatabase(config.getDatabaseName())
                        .buildHelper()
                        .executeQuery("VACUUM ANALYZE");
            }
        } catch (Exception ex) {
            throw new DaoException(ex);
        } finally {
            toDelete.forEach(FileUtils::deleteQuietly);
            toDelete.clear();
        }
    }

    private void restoreExtensions(File dumpFile) {
        runSafe(() -> {
            Set<String> extensions = set(toListOfStrings(list(postgresHelper().buildHelper().readTableFromDump(dumpFile, "_SystemConfig")).filter(r -> Objects.equals(r.get("Status"), "A")).collect(toMap(r -> r.get("Code"), identity())).mapValues(r -> r.get("Value")).get("org.cmdbuild.database.ext")));
            logger.debug("found extensions =< {} >", extensions);
            set(extensions).filter(ALLOWED_POSTGRES_EXTENSIONS).forEach(e -> postgresHelper().buildHelper().executeQuery("CREATE EXTENSION %s".formatted(e)));
        });
    }

    private PostgresHelperBuilder postgresHelper() {
        return PostgresUtils.newHelper(config.getHost(), config.getPort(), config.getCmdbuildUser(), config.getCmdbuildPassword())
                .withDatabase(config.getDatabaseName())
                .withSingleCore(config.isSingleCore());
    }

    private void createSchema(String schema) {
        logger.info("create schema = {}", schema);
        getCmdbuildJdbcTemplate().execute(format("CREATE SCHEMA IF NOT EXISTS \"%s\"", checkValidName(schema)));
    }

    private void grantSchemaPrivileges(String schema, String role) {
        logger.info("Granting schema privileges");
        new JdbcTemplate(getCmdbuildDataSource()).execute(format("GRANT ALL ON SCHEMA \"%s\" TO \"%s\"", checkValidName(schema), checkValidName(role)));
    }

    private void createUserIfNotExists(String roleName, String rolePassword) {
        logger.info("Creating role = {}", roleName);
        checkValidName(roleName);
        checkArgument(!rolePassword.contains("'"), "invalid password syntax");
        getAdminJdbcTemplate().execute(format("DO $$ BEGIN IF NOT EXISTS (SELECT * FROM pg_roles WHERE rolname = '%s') THEN CREATE USER \"%s\" PASSWORD '%s'; END IF; END $$ LANGUAGE PLPGSQL", roleName, roleName, rolePassword));
    }

    private String checkValidName(String name) {
        checkArgument(!name.contains("\""), "invalid name = %s", name);
        return name;
    }

    public void dropDatabase() {
        logger.info("drop database = {}", config.getDatabaseUrl());
        String dbName = checkValidName(config.getDatabaseName()); //TODO make connection termination configurable (?)
        getAdminJdbcTemplate().execute(format("SELECT pg_terminate_backend(pid) from pg_stat_activity WHERE pg_stat_activity.datname = '%s' AND pid <> pg_backend_pid();", dbName));
        getAdminJdbcTemplate().execute(format("DROP DATABASE IF EXISTS \"%s\"", dbName));
    }

    public boolean isRunning() {
        try {
            getAdminJdbcTemplate().execute("SELECT 1");
            return true;
        } catch (Exception ex) { //TODO improve this
            return false;
        }
    }

    public void cleanup() {
        if (!config.useExistingDatabase() && config.createDatabase()) {
            dropDatabase();
        }
    }

    public void freezeSessions() {
        logger.info("freeze sessions on db (set expiration strategy to 'never')");
        getCmdbuildJdbcTemplate().execute("UPDATE \"_Session\" SET \"ExpirationStrategy\" = 'never'");
    }

    public void anonymize() {
        logger.info("starting anonymize database");
        try (SuperUserHelper sudo = new SuperUserHelper()) {
            sudo.getLimitedAsSudoJdbcTemplate().execute("SELECT _cm3_system_anonymize();");
        } catch (Exception ex) {
            logger.warn("failed to aquire or release superuser access", ex);
        }
        logger.info("done!");
    }

    public void checkConfig() {
        if (config.useExistingDatabase()) {
            getCmdbuildJdbcTemplate().execute("SELECT 1");
        } else {
            getAdminJdbcTemplate().execute("SELECT 1");
        }
    }

    private class SuperUserHelper implements AutoCloseable {

        private final boolean isLimited = config.useLimitedUser() && config.isLimitedUserNotEqualToAdminUser();

        private SuperUserHelper() {
            if (isLimited) {
                getAdminJdbcTemplate().update(format("ALTER USER \"%s\" SUPERUSER", config.getLimitedUser()));
            }
        }

        private JdbcTemplate getLimitedAsSudoJdbcTemplate() {
            return getCmdbuildJdbcTemplate();
        }

        @Override
        public void close() {
            if (isLimited) {
                getAdminJdbcTemplate().update(format("ALTER USER \"%s\" NOSUPERUSER", config.getLimitedUser()));
            }
        }
    }
}
