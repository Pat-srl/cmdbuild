/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import org.cmdbuild.config.api.ConfigEntryImpl;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigUpdate;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import com.google.common.collect.Multimap;
import static com.google.common.collect.Multimaps.index;
import static com.google.common.io.Files.copy;
import java.io.File;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Files;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import org.apache.commons.configuration.ConfigurationConverter;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.FilenameUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.config.api.DirectoryService;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.ConfigDefinitionRepository;
import static org.cmdbuild.config.service.ConfigRepository.DATABASE_EXT_CONFIG;
import static org.cmdbuild.config.utils.ConfigUtils.addNamespaceToKey;
import static org.cmdbuild.config.utils.ConfigUtils.stripNamespaceFromKey;
import static org.cmdbuild.config.utils.LegacyConfigUtils.translateLegacyConfigNames;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.config.utils.ConfigUtils.hasNamespace;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.minions.PostStartup;

@Component
public class FilesystemConfigRepositoryImpl implements ConfigRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;
    private final ConfigDefinitionRepository configDefinitionRepository;

    public FilesystemConfigRepositoryImpl(DirectoryService directoryService, ConfigDefinitionRepository configDefinitionRepository) {
        this.directoryService = checkNotNull(directoryService);
        this.configDefinitionRepository = checkNotNull(configDefinitionRepository);
    }

    @PostStartup
    public void copyConfigReadmeToConfigDirectory() {
        try {
            if (directoryService.hasConfigDirectory()) {
                Files.writeString(directoryService.getConfigDirectory().toPath().resolve("README.txt"), readToString(getClass().getResourceAsStream("/org/cmdbuild/config/config_README.txt")));
            }
        } catch (IOException ex) {
            logger.error("error processing config dir readme.txt", ex);
        }
    }

    @Override
    public List<ConfigEntry> getConfigEntries() {
        if (!directoryService.hasConfigDirectory()) {
            logger.warn("config directory is not available, skip config file load");
            return emptyList();
        } else {
            try {
                return getCandidateConfigFiles().stream().flatMap(f -> new ConfigFileProcessor(f).getConfigsFromFileSafe().stream()).collect(toImmutableList());
            } catch (Exception ex) {
                logger.error(marker(), "error loading config files", ex);
                return emptyList();
            }
        }
    }

    @Override
    public void updateConfigs(List<? extends ConfigUpdate> configs) {
        if (!configs.isEmpty()) {
            Multimap<String, ConfigUpdate> configsByNamespaces = index((List<ConfigUpdate>) configs, ConfigUpdate::getNamespace);
            configsByNamespaces.asMap().forEach((namespace, entries) -> {
                try {
                    File file = getConfigFileForNamespace(namespace);
                    new ConfigFileProcessor(file).saveConfigsToFile(entries);
                } catch (Exception ex) {
                    throw runtime(ex, "error saving config to file for namespace = %s", namespace);
                }
            });
        }
    }

    private List<File> getCandidateConfigFiles() {
        File configDirectory = directoryService.getConfigDirectory();
        return list(configDirectory.listFiles((File file, String name) -> name.toLowerCase().matches(".*[.]conf$")));
    }

    private File getConfigFileForNamespace(String namespace) throws IOException {
        String basename = Splitter.on(".").splitToList(namespace).stream().skip(2).collect(onlyElement());
        File configFile = getCandidateConfigFiles().stream().filter(f -> f.getName().toLowerCase().matches(format("^%s[.]conf$", basename))).findFirst().orElse(null);
        if (configFile == null) {
            String fileName = format("%s.conf", basename);
            configFile = new File(directoryService.getConfigDirectory(), fileName);
        }
        return configFile;
    }

    private class ConfigFileProcessor {

        private final File configFile;
        private final String namespace;

        private List<ConfigEntry> configs = emptyList();

        public ConfigFileProcessor(File configFile) {
            this.configFile = checkNotNull(configFile);
            namespace = format("org.cmdbuild.%s", FilenameUtils.getBaseName(configFile.getName()));
        }

        public List<ConfigEntry> getConfigsFromFileSafe() {
            logger.debug("load config from file = {}", configFile.getAbsolutePath());
            try {
                loadFileContent();
                return configs;
            } catch (Exception ex) {
                logger.error(marker(), "error loading config files from file = {}", configFile.getAbsolutePath(), ex);
                return emptyList();
            }
        }

        private void saveConfigsToFile(Collection<ConfigUpdate> configUpdate) throws ConfigurationException, IOException {
            if (configFile.exists()) {
                loadFileContent();
            }
            Map<String, String> configMerge = map();
            configs.forEach(e -> configMerge.put(e.getKey(), e.getValue()));
            configUpdate.stream().filter(ConfigUpdate::isUpdate).map(ConfigEntry.class::cast).forEach(e -> configMerge.put(e.getKey(), e.getValue()));
            configUpdate.stream().filter(ConfigUpdate::isDelete).map(ConfigUpdate::getKey).forEach(configMerge::remove);
            configs = configMerge.entrySet().stream().map(e -> new ConfigEntryImpl(e.getKey(), e.getValue())).collect(toImmutableList());
            if (configFile.exists()) {
                backupConfigFile();
            }
            doUpdateFileContent();
        }

        private void backupConfigFile() {
            try {
                File backupFile = new File(new File(directoryService.getConfigDirectory(), "backup"), format("%s_%s.conf", FilenameUtils.getBaseName(configFile.getName()), CmDateUtils.dateTimeFileSuffix()));
                backupFile.getParentFile().mkdirs();
                logger.debug("backup config file = {} to file = {}", configFile.getAbsolutePath(), backupFile.getAbsolutePath());
                copy(configFile, backupFile);
            } catch (Exception ex) {
                logger.warn("config files backup error", ex);
            }
        }

        private void loadFileContent() {
            logger.debug("load config file content from file = {}", configFile.getAbsolutePath());
            Map<String, String> rawConfig = loadProperties(configFile);
            logger.trace("loaded raw config from file = {} config = \n\n{}\n", configFile.getAbsolutePath(), mapToLoggableStringLazy(rawConfig));
            List<ConfigEntry> processedConfigs = list();
            rawConfig.forEach((key, storedValue) -> {
                key = addNamespaceToKey(namespace, key);
                boolean isEncrypted = Cm3EasyCryptoUtils.isEncrypted(storedValue);
                String value = Cm3EasyCryptoUtils.decryptValue(storedValue);
                processedConfigs.add(new ConfigEntryImpl(key, value, storedValue, isEncrypted));
            });
            List<ConfigEntry> translatedConfig = translateLegacyConfigNames(processedConfigs);
            logger.trace("loaded processed config from file = {} config = \n\n{}\n", configFile.getAbsolutePath(), mapToLoggableStringLazy(uniqueIndex(translatedConfig, ConfigEntry::getKey)));
            translatedConfig.forEach(e -> {
                if (!configDefinitionRepository.containsConfigOrModuleConfig(e.getKey()) && !e.getKey().equals(DATABASE_EXT_CONFIG)) {
                    logger.warn("found unknown config key = {} in file = {}", e.getKey(), configFile.getAbsolutePath());
                }
            });
            configs = ImmutableList.copyOf(translatedConfig);
        }

        private void doUpdateFileContent() throws ConfigurationException, IOException {
            Map<String, String> newConfig = configs.stream().collect(toMap(e -> {
                String key = e.getKey();
                if (hasNamespace(namespace, key)) {
                    key = stripNamespaceFromKey(namespace, key);
                }
                return key;
            }, ConfigEntry::getValue));
            logger.trace("update config file = {} with config = \n\n{}\n", configFile.getAbsolutePath(), mapToLoggableStringLazy(newConfig));
            if (newConfig.isEmpty()) {
                logger.debug("no config data to save, delete config file = {}", configFile.getAbsolutePath());
                deleteQuietly(configFile);
            } else {
                logger.debug("update config file = {}", configFile.getAbsolutePath());
                configFile.createNewFile();
                PropertiesConfiguration properties = new PropertiesConfiguration();
                properties.setDelimiterParsingDisabled(true);
                properties.load(configFile);
                Map<String, String> currentConfigOnFile = (Map) ConfigurationConverter.getMap(properties);
                currentConfigOnFile.keySet().stream().map(String.class::cast).filter(not(newConfig::containsKey)).forEach(properties::clearProperty);
                currentConfigOnFile.forEach((key, value) -> {
                    if (newConfig.containsKey(key) && Cm3EasyCryptoUtils.isEncrypted(value) && !Cm3EasyCryptoUtils.isEncrypted(newConfig.get(key))) {
                        newConfig.put(key, Cm3EasyCryptoUtils.encryptValue(newConfig.get(key)));
                    }
                });
                newConfig.forEach(properties::setProperty);
                properties.save(configFile);
            }
        }

    }

}
