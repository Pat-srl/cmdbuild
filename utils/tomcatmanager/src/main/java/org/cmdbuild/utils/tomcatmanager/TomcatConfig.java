/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.tomcatmanager;

import com.google.common.base.Joiner;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Iterables.all;
import static com.google.common.collect.Iterables.concat;
import com.google.common.collect.Lists;
import static com.google.common.collect.Sets.newLinkedHashSet;
import jakarta.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmNetUtils.isPortAvailable;
import static org.cmdbuild.utils.io.CmNetUtils.scanPortOffset;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the full, immutable configuration of a tomcat instance.<br>
 * The {@link TomcatConfig.Builder} contains method to build a
 * {@link TomcatConfig} instance.
 *
 * @author davide
 */
public class TomcatConfig {

    public final static int TOMCAT_DEFAULT_SHUTDOWN_PORT = 8005,
            TOMCAT_DEFAULT_HTTP_PORT = 8080,
            TOMCAT_DEFAULT_DEBUG_PORT = 8000;
    public static final String TOMCAT_INSTALL_DIR = "tomcat_install_dir",
            TOMCAT_HTTP_PORT = "tomcat_http_port",
            TOMCAT_SHUTDOWN_PORT = "tomcat_shutdown_port",
            TOMCAT_DEBUG_PORT = "tomcat_debug_port",
            TOMCAT_PORT_OFFSET = "tomcat_port_offset",
            AUTO = "AUTO",
            SKIP_PORT_TEST = "skip_port_test";

    private final File installDir;
    private final Map<String, String> config;
    private final int shutdownPort, httpPort, debugPort;

    private TomcatConfig(Map<String, String> config) {
        this.config = ImmutableMap.copyOf(checkNotNull(config));
        this.installDir = new File(checkNotBlank(config.get(TOMCAT_INSTALL_DIR)));
        this.shutdownPort = Integer.parseInt(checkNotBlank(config.get(TOMCAT_SHUTDOWN_PORT)));
        this.httpPort = Integer.parseInt(checkNotBlank(config.get(TOMCAT_HTTP_PORT)));
        this.debugPort = Integer.parseInt(checkNotBlank(config.get(TOMCAT_DEBUG_PORT)));
    }

    public File getInstallDir() {
        return installDir;
    }

    public int getShutodownPort() {
        return shutdownPort;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public Integer getDebugPort() {
        return debugPort;
    }

    public File getCatalinaPidFile() {
        return new File(installDir, "bin/catalina.pid");
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder copyOf(TomcatConfig config) {
        return builder().withProperties(config.config);
    }

    public String getProperty(String key) {
        String value = config.get(key);
        checkNotNull(value, "config not found for key = %s", key);
        return value;
    }

    public String getProperty(String key, @Nullable String defaultValue) {
        return Objects.toString(config.get(key), defaultValue);
    }

    public List<String> getPropertyAsList(String key) {
        return Lists.newArrayList(Splitter.on(",").trimResults().omitEmptyStrings().split(getProperty(key, "")));
    }

    public static class Builder {

        private final Logger logger = LoggerFactory.getLogger(getClass());
        private final Properties config = new Properties();

        private Builder() {
            try {
                config.load(getClass().getResourceAsStream("/tomcat-manager-default-config.properties"));
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        /**
         * note: requires both keys and values of supplied map to be String (not
         * enforced for backward compatibility with {@link Properties})
         *
         * @param config a configuration map;
         * @return
         */
        public Builder withProperties(Map config) {
            checkNotNull(config);
            checkArgument(all(concat(config.keySet(), config.values()), instanceOf(String.class)), "found unsupported non-string key or value in supplied config map");
            this.config.putAll(config);
            return this;
        }

        public Builder withProperties(InputStream config) throws IOException {
            checkNotNull(config);
            this.config.load(config);
            return this;
        }

        public Builder withProperty(String key, Object value) {
            this.config.put(checkNotBlank(key), checkNotNull(toStringOrNull(value)));
            return this;
        }

        public Builder withTomcatInstallDir(String path) {
            return this.withProperty(TOMCAT_INSTALL_DIR, path);
        }

        public Builder skipPortCheck() {
            return skipPortCheck(true);
        }

        public Builder skipPortCheck(boolean skip) {
            return this.withProperty(SKIP_PORT_TEST, String.valueOf(skip));
        }

        /**
         * add/update overlay file (ie an overlay content that will overwrite
         * specified path; most commonly this will be a config file)
         *
         * @param key overlay key
         * @param path path of file (can be null, in which case we expect a
         * config already present from which to inherit file name)
         * @param content file content (will be serialized as .properties)
         * @return
         */
        public Builder withOverlay(String key, @Nullable String path, Map content) {
            Properties properties = new Properties();
            properties.putAll(content);
            StringWriter stringWriter = new StringWriter();
            try {
                properties.store(stringWriter, null);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return this.withOverlay(key, path, stringWriter.toString());
        }

        /**
         * add/update overlay file
         *
         * @param key overlay key
         * @param content file content (will be serialized as .properties)
         * @return
         */
        public Builder withOverlay(String key, Map content) {
            return this.withOverlay(key, null, content);
        }

        /**
         * add/update overlay file (ie an overlay content that will overwrite
         * specified path; most commonly this will be a config file)
         *
         * @param key overlay key
         * @param path path of file (can be null, in which case we expect a
         * config already present from which to inherit file name)
         * @param content file content
         * @return
         */
        public Builder withOverlay(String key, @Nullable String path, String content) {
            if (path == null) {
                path = config.getProperty("tomcat_config_overlay." + key + ".file");
                checkNotNull(path);
            }
            config.setProperty("tomcat_config_overlay", Joiner.on(",").join(newLinkedHashSet(concat(Splitter.on(",").trimResults().omitEmptyStrings().split(config.getProperty("tomcat_config_overlay", "")), singletonList(key)))));
            config.setProperty("tomcat_config_overlay." + key + ".file", path);
            config.setProperty("tomcat_config_overlay." + key + ".content", content);
            return this;
        }

        /**
         * add/update overlay file
         *
         * @param key overlay key
         * @param content file content
         * @return
         */
        public Builder withOverlay(String key, String content) {
            return this.withOverlay(key, null, content);
        }

        public TomcatConfig build() {
            String installDirName = config.getProperty(TOMCAT_INSTALL_DIR, AUTO);
            File installDirFile;
            if (equal(installDirName, AUTO) || isBlank(installDirName)) {
                installDirFile = tempDir("tomcat_dir_");
            } else {
                installDirFile = new File(installDirName);
            }
            if (installDirFile.exists()) {
                checkArgument(installDirFile.isDirectory() && installDirFile.canWrite());
            }
            int httpPort = Integer.parseInt(firstNonNull(trimToNull(config.getProperty(TOMCAT_HTTP_PORT)), Integer.toString(TOMCAT_DEFAULT_HTTP_PORT))),
                    shutdownPort = Integer.parseInt(firstNonNull(trimToNull(config.getProperty(TOMCAT_SHUTDOWN_PORT)), Integer.toString(TOMCAT_DEFAULT_SHUTDOWN_PORT))),
                    debugPort = Integer.parseInt(firstNonNull(trimToNull(config.getProperty(TOMCAT_DEBUG_PORT)), Integer.toString(TOMCAT_DEFAULT_DEBUG_PORT)));
            List<Integer> defaultPorts = ImmutableList.of(httpPort, shutdownPort, debugPort);

            String portOffsetValue = config.getProperty(TOMCAT_PORT_OFFSET, AUTO);
            int portOffset;
            if (equal(portOffsetValue, AUTO) || isBlank(portOffsetValue)) {
                portOffset = scanPortOffset(131, defaultPorts);
            } else {
                portOffset = Integer.parseInt(portOffsetValue);
            }
            logger.info("port offset = {}", portOffset);
            if (!toBooleanOrDefault(config.getProperty(SKIP_PORT_TEST), false)) {
                checkArgument(all(defaultPorts, (Integer port) -> isPortAvailable(port + portOffset)), "tomcat ports are not available, with offset = %s", portOffset);
            }
            Properties thisConfig = new Properties();
            thisConfig.putAll(config);
            return new TomcatConfig(map(config).with(
                    TOMCAT_PORT_OFFSET, String.valueOf(0),
                    TOMCAT_HTTP_PORT, String.valueOf(httpPort + portOffset),
                    TOMCAT_SHUTDOWN_PORT, String.valueOf(shutdownPort + portOffset),
                    TOMCAT_DEBUG_PORT, String.valueOf(debugPort + portOffset),
                    TOMCAT_INSTALL_DIR, installDirFile.getAbsolutePath()
            ));
        }

    }

    public static int getValidPortOffsetForDefaultTomcatPorts() {
        return scanPortOffset(0, list(TOMCAT_DEFAULT_HTTP_PORT, TOMCAT_DEFAULT_SHUTDOWN_PORT, TOMCAT_DEFAULT_DEBUG_PORT));
    }
}
