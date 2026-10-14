/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigUpdate;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Lists.transform;
import static com.google.common.collect.Maps.transformValues;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.config.CoreConfiguration.CORE_CONFIG_READY_PROPERTY;
import org.cmdbuild.config.api.ConfigDefinitionRepository;
import org.cmdbuild.config.api.ConfigDeleteImpl;
import org.cmdbuild.config.api.ConfigEntryImpl;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.config.utils.ConfigUtils;
import org.cmdbuild.dao.ConfigurableDataSource;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;

@Component
public class ConfigRepositoryFacadeImpl implements ConfigRepositoryFacade {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigRepository databaseConfigRepository, filesystemConfigRepository;
    private final ConfigDefinitionRepository configDefinitionRepository;
    private final Map<String, ConfigEntry> defaultAndEnvConfigs;
    private final ConfigurableDataSource dataSource;

    private Map<String, ConfigEntry> configFromFile = emptyMap(), configFromDb = emptyMap(), allConfig = emptyMap();

    public ConfigRepositoryFacadeImpl(ConfigurableDataSource dataSource, DefaultConfigRepositoryImpl defaultConfigRepository, EnvConfigRepositoryImpl envConfigRepository, DatabaseConfigRepositoryImpl databaseConfigRepository, FilesystemConfigRepositoryImpl filesystemConfigRepository, ConfigDefinitionRepository configDefinitionRepository) {
        this.databaseConfigRepository = checkNotNull(databaseConfigRepository);
        this.filesystemConfigRepository = checkNotNull(filesystemConfigRepository);
        this.configDefinitionRepository = checkNotNull(configDefinitionRepository);
        this.dataSource = checkNotNull(dataSource);
        defaultAndEnvConfigs = map(defaultConfigRepository.getConfigEntries(), ConfigEntry::getKey, identity())
                .with(map(envConfigRepository.getConfigEntries(), ConfigEntry::getKey, identity()))
                .immutableCopy();
    }

    @Override
    public synchronized void loadConfigFromFiles() {
        clearConfig();
        configFromFile = map(filesystemConfigRepository.getConfigEntries(), ConfigEntry::getKey, identity()).immutableCopy();
        allConfig = map(defaultAndEnvConfigs).with(configFromFile).immutableCopy();
    }

    @Override
    public synchronized void loadConfigFromFilesAndDb() {
        doReloadAll();
        synchronizeFileConfigsAndDbConfigs();
    }

    @Override
    public synchronized void updateConfig(List<? extends ConfigUpdate> configs) {
        if (!configs.isEmpty()) {
            configs = list(transform(configs, (configUpdate) -> {
                if (configUpdate.isUpdate() && configDefinitionRepository.isDefault((ConfigEntry) configUpdate)) {
                    return new ConfigDeleteImpl(configUpdate.getKey());
                } else {
                    return configUpdate;
                }
            }));

            if (dataSource.isReady()) {
                databaseConfigRepository.updateConfigs(configs.stream().filter(configDefinitionRepository::isLocationDefault).collect(toList()));
                filesystemConfigRepository.updateConfigs(list(configs).withOnly(ConfigUpdate::hasCmNamespace));
                reloadAllConfig();
            } else {
                filesystemConfigRepository.updateConfigs(list(configs).withOnly(ConfigUpdate::hasCmNamespace));
                loadConfigFromFiles();
            }
        }
    }

    private synchronized void doReloadAll() {
        loadConfigFromFiles();
        configFromDb = map(databaseConfigRepository.getConfigEntries(), ConfigEntry::getKey, identity()).immutableCopy();
        allConfig = map(allConfig).with(configFromDb).immutableCopy();
    }

    private synchronized void synchronizeFileConfigsAndDbConfigs() {
        Set<String> mismatchingKeys = set(configFromFile.keySet()).with(configFromDb.keySet()).stream()
                .filter(ConfigUtils::hasCmNamespace)
                .filter(configDefinitionRepository::isLocationDefault)
                .filter(k -> !equal(Optional.ofNullable(configFromDb.get(k)).map(ConfigEntry::getValue).orElse(null), Optional.ofNullable(configFromFile.get(k)).map(ConfigEntry::getValue).orElse(null)))
                .collect(toImmutableSet());
        if (!mismatchingKeys.isEmpty()) {
            if (toBooleanOrDefault(Optional.ofNullable(configFromDb.get(CORE_CONFIG_READY_PROPERTY)).map(ConfigEntry::getValue).orElse(null), false) == false) {
                logger.info("config not ready on db (first config load/import), will load config from file and set config.ready = true");
                List<ConfigUpdate> toUpdate = configFromFile.values().stream()
                        .filter(configDefinitionRepository::isLocationDefault)
                        .filter(e -> !equal(e.getKey(), CORE_CONFIG_READY_PROPERTY))
                        .collect(toList());
                toUpdate.add(new ConfigEntryImpl(CORE_CONFIG_READY_PROPERTY, TRUE));
                logger.info("update on db configs from file = \n\n{}\n", mapToLoggableString(map(toUpdate, ConfigUpdate::getKey, identity())));
                databaseConfigRepository.updateConfigs(toUpdate);
            } else {
                List<ConfigUpdate> toUpdate = mismatchingKeys.stream().map(k -> configFromDb.containsKey(k) ? configFromDb.get(k) : new ConfigDeleteImpl(k)).collect(toImmutableList());
                logger.info("update on filesystem configs from db = \n\n{}\n", mapToLoggableString(map(toUpdate, ConfigUpdate::getKey, identity())));
                filesystemConfigRepository.updateConfigs(toUpdate);
            }
            doReloadAll();
        }
    }

    @Override
    public Map<String, String> getAllConfig() {
        return transformValues(allConfig, ConfigEntry::getValue);
    }

    @Override
    public Map<String, String> getAllStoredConfig() {
        return transformValues(allConfig, ConfigEntry::getStoredValue);
    }

    @Override
    public Map<String, String> getConfigFromFile() {
        return transformValues(configFromFile, ConfigEntry::getValue);
    }

    @Override
    public Map<String, String> getConfigFromDb() {
        return transformValues(configFromDb, ConfigEntry::getValue);
    }

    private synchronized void clearConfig() {
        configFromFile = configFromDb = allConfig = emptyMap();
    }

}
