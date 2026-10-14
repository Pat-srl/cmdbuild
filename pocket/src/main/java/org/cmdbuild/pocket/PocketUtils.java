/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.pocket;

import ch.qos.logback.core.joran.util.ConfigurationWatchListUtil;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toSet;
import jakarta.annotation.Nullable;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import static org.apache.catalina.LifecycleState.DESTROYED;
import static org.apache.catalina.LifecycleState.STARTED;
import static org.apache.catalina.LifecycleState.STOPPED;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.cmdbuild.auth.login.file.FileAuthUtils.buildAuthFile;
import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.client.rest.RestClientImpl;
import static org.cmdbuild.config.CoreConfiguration.CORE_LOGGER_AUTOCONFIGURE_PROPERTY;
import static org.cmdbuild.config.CoreConfiguration.CORE_LOGGER_STATIC_LOGBACK_CONFIG_LOCATION;
import org.cmdbuild.config.api.ConfigDefinition;
import static org.cmdbuild.config.utils.ConfigDefinitionUtils.getAllConfigDefinitionsFromClasspath;
import static org.cmdbuild.config.utils.ConfigUtils.addNamespaceToKey;
import static org.cmdbuild.config.utils.ConfigUtils.getFilenameFromNamespace;
import static org.cmdbuild.config.utils.ConfigUtils.getNamespace;
import static org.cmdbuild.config.utils.ConfigUtils.hasNamespace;
import static org.cmdbuild.config.utils.ConfigUtils.stripNamespaceFromKey;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_AUTOPATCH;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_NAMESPACE;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_NAMESPACE_PREFIX;
import org.cmdbuild.dao.config.inner.DatabaseCreator;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl.DatabaseCreatorConfigImplBuilder;
import org.cmdbuild.minions.SystemStatus;
import static org.cmdbuild.minions.SystemStatus.SYST_READY;
import static org.cmdbuild.utils.classpath.ClasspathUtils.buildClassloaderWithoutClass;
import static org.cmdbuild.utils.io.CmIoUtils.tempDirNoCreate;
import static org.cmdbuild.utils.io.CmNetUtils.getAvailablePort;
import static org.cmdbuild.utils.io.CmNetUtils.isPortAvailable;
import static org.cmdbuild.utils.io.CmPropertyUtils.writeProperties;
import org.cmdbuild.utils.io.CmZipUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.waitUntil;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.codehaus.plexus.util.StringUtils;
import static org.codehaus.plexus.util.StringUtils.isBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import static org.cmdbuild.auth.AuthConst.SYSTEM_USER;

public class PocketUtils {

    public static final String POCKET_CONFIG_TOMCAT_PORT = "org.cmdbuild.pocket.port",
            POCKET_CONFIG_WARFILE = "org.cmdbuild.pocket.warFile",
            POCKET_CONFIG_BASE_DIR = "org.cmdbuild.pocket.baseDir";

    static {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    public static PocketHelper pocket(File warFile) {
        return PocketUtils.pocket(warFile, emptyMap());
    }

    public static PocketHelper pocket(File warFile, @Nullable Integer port) {
        return PocketUtils.pocket(warFile, port, emptyMap());
    }

    public static PocketHelper pocket(File warFile, @Nullable Integer port, Map<String, String> config) {
        return pocket(warFile, map(config).with(POCKET_CONFIG_TOMCAT_PORT, toStringOrNull(port)));
    }

    public static PocketHelper pocket(File warFile, Map<String, String> config) {
        return pocket(map(config).with(POCKET_CONFIG_WARFILE, warFile.getAbsolutePath()));
    }

    public static PocketHelper pocket(Map<String, String> config) {
        return new PocketHelperImpl(config);
    }

    private static class PocketHelperImpl implements PocketHelper {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final Map<String, String> pocketConfig, cmdbuildConfig;

        private final int tomcatPort;
        private final File warFile, baseDir, tomcatDir, configDir, logsDir;

        private boolean isInitialized = false, isRunning = false;

        private final Tomcat tomcat;
        private final DatabaseCreator database;

        public PocketHelperImpl(Map<String, String> config) {
            config = (Map) map(
                    POCKET_CONFIG_TOMCAT_PORT, toStringNotBlank(getAvailablePort(8080)),
                    POCKET_CONFIG_BASE_DIR, tempDirNoCreate().getAbsolutePath()
            ).with(map(config).withoutValues(StringUtils::isBlank));

            this.warFile = new File(checkNotBlank(config.get(POCKET_CONFIG_WARFILE), "missing war file config param"));
            this.tomcatPort = toInt(config.get(POCKET_CONFIG_TOMCAT_PORT));
            this.baseDir = new File(checkNotBlank(config.get(POCKET_CONFIG_BASE_DIR)));
            this.tomcatDir = new File(baseDir, "tomcat");
            this.configDir = new File(baseDir, "conf");
            this.logsDir = new File(tomcatDir, "logs");
            this.cmdbuildConfig = map(config).filterKeys(k -> !k.startsWith("org.cmdbuild.pocket") && !k.startsWith(DATABASE_CONFIG_NAMESPACE_PREFIX)).immutable();

            this.tomcat = new Tomcat();

            Map<String, String> dbConfigMap = pocketConfigToDbConfigMap(config);

            DatabaseCreatorConfig dbConfig;

            if (dbConfigMap.isEmpty()) {
                dbConfig = DatabaseCreatorConfigImpl.builder()
                        .withDatabaseUrl("localhost", getAvailablePort(15432), "cmdbuild")
                        .withAdminUser("postgres", randomId())
                        .withLimitedUser("cmdbuild", randomId())
                        .withPostgresLocation(new File(baseDir, "postgres").getAbsolutePath())
                        .build();
            } else {
                dbConfig = DatabaseCreatorConfigImpl.builder().withConfig(dbConfigMap).build();
            }

            if (dbConfig.hasSource() && !dbConfig.useExistingDatabase() && !dbConfig.getSourceFile().isFile() && isBlank(dbConfig.getSourceFile().getParent())) {
                File dumpFile = new File(baseDir, dbConfig.getSource());
                dbConfig = DatabaseCreatorConfigImpl.copyOf(dbConfig).withSource(dumpFile.getAbsolutePath()).build();
            }

            database = new DatabaseCreator(dbConfig);

            pocketConfig = map(config).with(dbConfigToPocketConfig(database.getConfig())).immutable();
        }

        @Override
        public Map<String, String> getConfig() {
            return pocketConfig;
        }

        @Override
        public int getTomcatPort() {
            return tomcatPort;
        }

        @Override
        public void reconfigureDatabase(DatabaseCreatorConfig dbConfig) {
            logger.info("reconfigure database to = {} {}/{}", dbConfig.getDatabaseUrl(), dbConfig.getCmdbuildUser(), dbConfig.getCmdbuildPassword());
            getRestClient().system().reconfigureDatabase(dbConfig.getCmdbuildDbConfig());
        }

        @Override
        public PocketHelper withConfig(Map<String, String> config) {
            return new PocketHelperImpl(map(pocketConfig).with(config));
        }

        @Override
        public PocketHelper withDbConfig(Consumer<DatabaseCreatorConfigImplBuilder> config) {
            return withDbConfig(DatabaseCreatorConfigImpl.builder().accept(config).build());
        }

        @Override
        public PocketHelper withDbConfig(Map<String, String> dbconfig) {
            return withDbConfig(DatabaseCreatorConfigImpl.builder().withConfig(dbconfig).build());
        }

        @Override
        public PocketHelper withDbConfig(DatabaseCreatorConfig config) {
            return new PocketHelperImpl(map(pocketConfig).filterKeys(k -> !k.startsWith(DATABASE_CONFIG_NAMESPACE_PREFIX)).with(dbConfigToPocketConfig(config)));
        }

        @Override
        public DatabaseCreatorConfig getDbConfig() {
            return DatabaseCreatorConfigImpl.builder().withConfig(pocketConfigToDbConfigMap(pocketConfig)).build();
        }

        private synchronized void init() {
            if (!isInitialized) {
                logger.info("prepare pocket cmdbuild with config = \n\n{}\n", mapToLoggableString(pocketConfig));
                try {
                    checkArgument(warFile.exists(), "invalid war file = %s", warFile);
                    checkArgument(isPortAvailable(tomcatPort), "invalid tomcat port = %s : port is not available", tomcatPort);

                    tomcatDir.mkdirs();
                    configDir.mkdirs();

                    new File(tomcatDir, "webapps").mkdirs();
                    new File(tomcatDir, "temp").mkdirs();

                    File webappDir = new File(tomcatDir, "webapps/ROOT");
                    webappDir.mkdirs();

                    logger.info("copy/extract war content");
                    if (warFile.isDirectory()) {
                        FileUtils.copyDirectory(warFile, webappDir);
                    } else {
                        CmZipUtils.unzipToDir(warFile, webappDir);
                    }

                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                    classLoader = buildClassloaderWithoutClass(classLoader, "org.slf4j.impl.StaticLoggerBinder"); //TODO improve this  

                    tomcat.setPort(tomcatPort);
                    tomcat.getConnector();// create default connector
                    tomcat.setBaseDir(tomcatDir.getAbsolutePath());

                    tomcat.getServer().setParentClassLoader(classLoader);

                    Context context = tomcat.addWebapp("", webappDir.getAbsolutePath());
                    context.addParameter("org.cmdbuild.config.location", configDir.getAbsolutePath());
                    context.addParameter("org.cmdbuild.logs.location", logsDir.getAbsolutePath());

                    if (toBooleanOrDefault(cmdbuildConfig.get(CORE_LOGGER_AUTOCONFIGURE_PROPERTY), true) == false) {
                        context.addParameter(CORE_LOGGER_AUTOCONFIGURE_PROPERTY, Boolean.FALSE.toString());
                        context.addParameter(CORE_LOGGER_STATIC_LOGBACK_CONFIG_LOCATION, getCurrentLogbackConfigLocationOrFallback());
                    }

                    if (!database.useExistingDatabase()) {
                        File dumpFile = checkNotNull(database.getConfig().getSourceFile());
                        if (!dumpFile.exists()) {
                            File found = Files.walk(webappDir.toPath()).map(Path::toFile).filter(f -> equal(f.getName(), dumpFile.getName())).collect(onlyElement("dump not found for name =< %s >", dumpFile.getName()));
                            FileUtils.copyFile(found, dumpFile);//TODO improve performance
                        }
                    }

                    database.configureDatabase();

                    Map<String, String> cmdbuildConfigsToSet = map(database.getConfig().getCmdbuildDbConfig()).accept(m -> {
                        if (!database.getConfig().skipPatches()) {
                            m.put(DATABASE_CONFIG_AUTOPATCH, "true");
                        }
                    }).mapKeys(k -> addNamespaceToKey(DATABASE_CONFIG_NAMESPACE, k)).with(cmdbuildConfig);

                    Set<String> fileOnlyConfigKeys = getAllConfigDefinitionsFromClasspath().stream().filter(ConfigDefinition::isLocationFileOnly).map(ConfigDefinition::getKey).collect(toSet());

                    database.setConfigs(map(cmdbuildConfigsToSet).withoutKeys(fileOnlyConfigKeys));

                    Map<String, String> fileOnlyConfigs = map(cmdbuildConfigsToSet).filterKeys(fileOnlyConfigKeys::contains);

                    fileOnlyConfigs.keySet().stream().map(k -> getNamespace(k)).distinct().sorted().forEach(namespace -> {
                        File file = new File(configDir, getFilenameFromNamespace(namespace));
                        Map<String, String> config = map(fileOnlyConfigs).filterKeys(k -> hasNamespace(namespace, k)).mapKeys(k -> stripNamespaceFromKey(namespace, k));
                        logger.info("prepare config file = {} with content = \n\n{}\n", file.getAbsolutePath(), mapToLoggableString(config));
                        writeProperties(file, config);
                    });

                    isInitialized = true;
                } catch (IOException ex) {
                    throw runtime(ex);
                }
            }
        }

        @Override
        public synchronized PocketHelper start() {
            init();
            try {
                checkArgument(!isRunning(), "tomcat is already running");
//                if (database.hasPostgres()) {
//                    database.getPostgres().startPostgres();
//                }
                logger.info("starting tomcat");
                tomcat.start();
                waitUntil(() -> equal(tomcat.getServer().getState(), STARTED));
                isRunning = true;
            } catch (LifecycleException ex) {
                throw runtime(ex);
            }
            return this;
        }

        @Override
        public synchronized PocketHelper stop() {
            if (isRunning()) {
                try {
                    logger.info("stopping tomcat");
                    tomcat.stop();
                    waitUntil(() -> equal(tomcat.getServer().getState(), STOPPED));//TODO check shutdown/destroy sequence
                    tomcat.destroy();
                    waitUntil(() -> equal(tomcat.getServer().getState(), DESTROYED));
                    isRunning = false;
//                    if (database.hasPostgres()) {
//                        database.getPostgres().stopPostgres();
//                    }
                } catch (LifecycleException ex) {
                    throw runtime(ex);
                }
            }
            return this;
        }

        @Override
        public synchronized PocketHelper stopSafe() {
            try {
                stop();
            } catch (Exception ex) {
                logger.warn("error on tomcat stop", ex);
            }
            return this;
        }

        @Override
        public boolean isRunning() {
            return isRunning;
        }

        @Override
        public boolean isReady() {
            if (!isRunning()) {
                return false;
            } else {
                SystemStatus status = getRestClient().system().getStatus();
                return equal(status, SYST_READY);
            }
        }

        @Override
        public RestClient getRestClient() {
            File authDir = new File(System.getProperty("java.io.tmpdir"));
            return RestClientImpl
                    .build("localhost", tomcatPort)//TODO
                    .doLoginWithAnyGroup(SYSTEM_USER, buildAuthFile(authDir).getPassword());
        }

        @Override
        public String getBaseUrl() {
            return getRestClient().getBaseUrl();
        }

        @Override
        public synchronized PocketHelper cleanup() {
            stopSafe();
            logger.info("cleanup");
            try {
                database.cleanup();
            } catch (Exception ex) {
                logger.warn("error on postgres cleanup", ex);
            }
            deleteQuietly(baseDir);
            return this;
        }

        private String getCurrentLogbackConfigLocationOrFallback() {
            String fallbackLocation = checkNotNull(getClass().getClassLoader().getResource("pocket_logback_fallback.xml")).toString();
            try {
                String logbackLocation = checkNotBlank(ConfigurationWatchListUtil.getMainWatchURL(((ch.qos.logback.classic.Logger) logger).getLoggerContext()).toString());
                logger.info("logback location = {}", logbackLocation);
                return logbackLocation;
            } catch (Exception ex) {
                logger.warn("unable to get current logback config, using fallback config", ex);
                return fallbackLocation;
            }
        }

    }

    private static Map<String, String> dbConfigToPocketConfig(DatabaseCreatorConfig dbConfig) {
        return map(dbConfig.getConfig()).mapKeys(k -> DATABASE_CONFIG_NAMESPACE_PREFIX + k);
    }

    private static Map<String, String> pocketConfigToDbConfigMap(Map<String, String> config) {
        return map(config).filterKeys(k -> k.startsWith(DATABASE_CONFIG_NAMESPACE_PREFIX)).mapKeys(k -> k.replaceFirst(DATABASE_CONFIG_NAMESPACE_PREFIX, ""));
    }

}
