/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import org.cmdbuild.config.utils.ConfigBeanUtils;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Maps;
import static com.google.common.collect.Maps.filterEntries;
import static com.google.common.collect.MoreCollectors.onlyElement;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import static java.util.function.Function.identity;
import static java.util.stream.Stream.concat;
import jakarta.annotation.Nullable;
import org.cmdbuild.cluster.ClusterMessageImpl;
import org.cmdbuild.cluster.ClusterMessageReceivedEvent;
import org.cmdbuild.config.api.AfterConfigReloadEventImpl;
import org.cmdbuild.config.api.ConfigBeanRepository;
import org.cmdbuild.config.api.ConfigReloadEventImpl;
import org.cmdbuild.config.api.ConfigDefinition;
import org.cmdbuild.config.api.ConfigUpdateEventImpl;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.config.api.NamespacedConfigService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import org.cmdbuild.cluster.ClusterService;
import org.cmdbuild.config.api.ConfigDefinitionImpl;
import org.cmdbuild.config.api.ConfigDefinitionRepository;
import org.cmdbuild.config.api.ConfigDeleteImpl;
import org.cmdbuild.config.api.ConfigEvent;
import static org.cmdbuild.config.utils.ConfigUtils.addNamespaceToKey;
import static org.cmdbuild.config.utils.ConfigUtils.stripNamespaceFromKey;
import org.cmdbuild.minions.AppContextReadyEvent;
import org.cmdbuild.minions.SystemLoadingConfigEvent;
import org.cmdbuild.minions.SystemLoadingConfigFilesEvent;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.config.utils.ConfigUtils.hasNamespace;
import org.cmdbuild.eventbus.EventBusService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;

@Component
public class ConfigServiceImpl implements GlobalConfigService {

    private final static String CLUSTER_MESSAGE_RELOAD_MANY = "config.reload_many",
            CLUSTER_MESSAGE_DATA_KEYS_PARAM = "keys",
            CLUSTER_MESSAGE_RELOAD_ALL = "config.reload_all";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus configEventBus;

    private final ConfigRepositoryFacade configRepository;
    private final ConfigDefinitionRepository configDefinitionStore;
    private final ClusterService clusteringService;
    private final ConfigBeanRepository configBeanRepository;

    public ConfigServiceImpl(
            ConfigRepositoryFacade configStore,
            ConfigBeanRepository configBeanRepository,
            ClusterService clusteringService,
            ConfigDefinitionRepository configDefinitionStore,
            EventBusService eventBusService
    ) {
        this.configRepository = checkNotNull(configStore);
        this.configDefinitionStore = checkNotNull(configDefinitionStore);
        this.configBeanRepository = checkNotNull(configBeanRepository);
        this.clusteringService = checkNotNull(clusteringService);
        configEventBus = eventBusService.getConfigEventBus();
        eventBusService.getClusterMessagesEventBus().register(new Object() {
            @Subscribe
            public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent event) {
                if (event.isOfType(CLUSTER_MESSAGE_RELOAD_ALL)) {
                    logger.debug("invalidate all config in response to a cluster message");
                    configStore.reloadAllConfig();
                    postUpdate();
                } else if (event.isOfType(CLUSTER_MESSAGE_RELOAD_MANY)) {
                    List<String> keys = checkNotEmpty(event.<List>getData(CLUSTER_MESSAGE_DATA_KEYS_PARAM));
                    logger.debug("invalidate config keys in response to a cluster message, keys = {}", keys);
                    configStore.reloadAllConfig();
                    postUpdate(keys);
                }
            }
        });
        eventBusService.getSystemEventBus().register(new Object() {

            @Subscribe
            public void handleAppContextReadyEvent(AppContextReadyEvent event) {
                logger.info("init config beans");
                configBeanRepository.getConfigHelpers().forEach(b -> b.processBean(ConfigServiceImpl.this));
            }

            @Subscribe
            public void handleSystemLoadingConfigFilesEvent(SystemLoadingConfigFilesEvent event) {
                logger.info("load config from files");
                configStore.loadConfigFromFiles();
                postUpdateAll();
            }

            @Subscribe
            public void handleSystemLoadingConfigEvent(SystemLoadingConfigEvent event) {
                logger.info("load config from db");
                configStore.loadConfigFromFilesAndDb();
                postUpdateAll();
            }
        });
    }

    private void postUpdate(String... keys) {
        postUpdate(asList(keys));
    }

    private void postUpdate(Iterable<String> keys) {
        configEventBus.post(new ConfigUpdateEventImpl(keys));
        configEventBus.post(new ConfigReloadEventImpl(keys));
        configEventBus.post(new AfterConfigReloadEventImpl(keys));
    }

    private void postUpdateAll() {
        configEventBus.post(new ConfigUpdateEventImpl());
        configEventBus.post(new ConfigReloadEventImpl());
        configEventBus.post(new AfterConfigReloadEventImpl());
    }

    @Override
    public String getConfigNamespaceFromConfigBeanClass(Class configBeanClass) {
        return configBeanRepository.getConfigBeans().stream().filter(configBeanClass::isInstance).map(ConfigBeanUtils::getNamespace).collect(onlyElement());
    }

    @Override
    public NamespacedConfigService getConfig(String namespace) {
        return new NamespacedConfigServiceImpl(namespace);
    }

    @Override
    public EventBus getEventBus() {
        return configEventBus;
    }

    @Override
    @Nullable
    public String getString(String key) {
        Optional<String> optional = doGetConfig(key);
        return optional != null && optional.isPresent() ? optional.get() : null;
    }

    @Override
    @Nullable
    public String getStringOrDefault(String key) {
        Optional<String> config = doGetConfig(key);
        if (config == null) {
            config = Optional.ofNullable(configDefinitionStore.getDefaultOrNull(key));
        }
        return config.orElse(null);
    }

    @Override
    @Nullable
    public String getStringOrDefault(String namespace, String key) {
        return getStringOrDefault(addNamespaceToKey(namespace, key));
    }

    @Nullable
    private Optional<String> doGetConfig(String key) {
        Map<String, String> config = configRepository.getAllConfig();
        if (config.containsKey(key)) {
            return Optional.ofNullable(config.get(key));
        } else {
            return null;
        }
    }

    @Override
    public void putString(String key, @Nullable String value, boolean encrypt) {
        if (encrypt) {
            value = Cm3EasyCryptoUtils.encryptValue(value);
        }
        putString(key, value);
    }

    @Override
    public void putString(String key, @Nullable String value) {
        putStrings(map(key, value));
    }

    @Override
    public void putString(String namespace, String key, String value) {
        putStrings(namespace, map(key, value));
    }

    @Override
    public void putStrings(String namespace, Map<String, String> map) {
        putStrings(map.entrySet().stream().collect(toMap(e -> addNamespaceToKey(namespace, e.getKey()), Entry::getValue)));
    }

    @Override
    public void putStrings(Map<String, String> map) {
        Set<String> updatedKeys = putStringsNoEvents(map);
        if (!updatedKeys.isEmpty()) {
            postUpdate(updatedKeys);
            notifyConfigUpdateOnCluster(updatedKeys);
        }
    }

    private Set<String> putStringsNoEvents(Map<String, String> map) {
        map.keySet().forEach((key) -> checkNotBlank(key));
        map = map(filterEntries(map, (e) -> !equal(e.getValue(), getString(e.getKey()))));
        configRepository.updateConfig(map);
        return map.keySet();
    }

    @Override
    public void delete(String key) {
        configRepository.updateConfig(list(new ConfigDeleteImpl(key)));
        postUpdate(key);
        notifyConfigUpdateOnCluster(key);
    }

    @Override
    public Map<String, String> getConfigAsMap() {
        return configRepository.getAllConfig();
    }

    @Override
    public Map<String, String> getStoredConfigAsMap() {
        return configRepository.getAllStoredConfig();
    }

    @Override
    public Map<String, String> getConfigOrDefaultsAsMap() {
        return map(configDefinitionStore.getAllDefaults()).with(getConfigAsMap());
    }

    @Override
    public Map<String, ConfigDefinition> getConfigDefinitions() {
        return configDefinitionStore.getAll();
    }

    @Override
    @Nullable
    public ConfigDefinition getConfigDefinitionOrNull(String key) {
        return configDefinitionStore.getOrNull(key);
    }

    @Override
    public synchronized void reload() {
        configRepository.reloadAllConfig();
        postUpdate();
        notifyConfigReloadOnCluster();
    }

    private void notifyConfigReloadOnCluster() {
        clusteringService.sendMessage(ClusterMessageImpl.builder().withMessageType(CLUSTER_MESSAGE_RELOAD_ALL).build());
    }

    private void notifyConfigUpdateOnCluster(String... keys) {
        notifyConfigUpdateOnCluster(list(keys));
    }

    private void notifyConfigUpdateOnCluster(Collection<String> keys) {
        clusteringService.sendMessage(ClusterMessageImpl.builder()
                .withMessageType(CLUSTER_MESSAGE_RELOAD_MANY)
                .withMessageData(map(CLUSTER_MESSAGE_DATA_KEYS_PARAM, list(keys)))
                .build());
    }

    private class NamespacedConfigServiceImpl implements NamespacedConfigService {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final String namespace;
        private final EventBus namespacedEventBus = new EventBus();

        public NamespacedConfigServiceImpl(String namespace) {
            this.namespace = checkNotBlank(namespace);
            configEventBus.register(new Object() {

                @Subscribe
                public void handleConfigEvent(ConfigEvent event) {
                    if (event.impactNamespace(namespace)) {
                        namespacedEventBus.post(event);
                    }
                }
            });
        }

        @Override
        @Nullable
        public String getString(String key) {
            return configRepository.getAllConfig().get(addNamespaceToKey(namespace, key));
        }

        @Override
        public void set(String key, String value) {
            ConfigServiceImpl.this.putString(addNamespaceToKey(namespace, key), value);
        }

        @Override
        public void delete(String key) {
            ConfigServiceImpl.this.delete(addNamespaceToKey(namespace, key));
        }

        @Override
        @Nullable
        public String getStringOrDefault(String key, String defaultKey) {
            key = addNamespaceToKey(namespace, key);
            defaultKey = addNamespaceToKey(namespace, defaultKey);
            return configRepository.getAllConfig().getOrDefault(key, configDefinitionStore.getDefaultOrNull(defaultKey));
        }

        @Override
        public <T> T getOrDefault(String key, String defaultKey, Class<T> valueType) {
            String value = getStringOrDefault(key, defaultKey);
            try {
                return CmConvertUtils.convert(value, valueType);
            } catch (Exception ex) {
                logger.error(marker(), "error converting config value =< {} > for key = {}.{} to type = {}; returning default value", value, namespace, key, valueType, ex);
                return CmConvertUtils.convert(getDefault(key), valueType);
            }
        }

        @Override
        public String getNamespace() {
            return namespace;
        }

        @Override
        public Map<String, String> getAsMap() {
            Map<String, String> map = map();
            concat(configDefinitionStore.getAllDefaults().entrySet().stream(), configRepository.getAllConfig().entrySet().stream())
                    .filter((entry) -> hasNamespace(namespace, entry.getKey()))
                    .forEach((java.util.Map.Entry<java.lang.String, java.lang.String> entry) -> map.put(stripNamespaceFromKey(namespace, entry.getKey()), entry.getValue()));
            return map;
        }

        @Override
        public EventBus getEventBus() {
            return namespacedEventBus;
        }

        @Override
        public ConfigDefinition getDefinition(String key) {
            ConfigDefinition namespacedConfigDefinition = configDefinitionStore.get(addNamespaceToKey(namespace, key));
            ConfigDefinition localConfigDefinition = ConfigDefinitionImpl.copyOf(namespacedConfigDefinition).withKey(stripNamespaceFromKey(namespace, namespacedConfigDefinition.getKey())).build();
            return localConfigDefinition;
        }

        @Override
        public Map<String, ConfigDefinition> getAllDefinitions() {
            return Maps.filterKeys(configDefinitionStore.getAll(), (key) -> key.startsWith(namespace + ".")).values().stream()
                    .map(c -> ConfigDefinitionImpl.copyOf(c).withKey(stripNamespaceFromKey(namespace, c.getKey())).build())
                    .collect(toMap(ConfigDefinition::getKey, identity()));
        }

        @Override
        public ConfigDefinition addDefinition(ConfigDefinition configDefinition) {
            ConfigDefinition namespacedConfigDefinition = ConfigDefinitionImpl.copyOf(configDefinition)
                    .withKey(addNamespaceToKey(namespace, configDefinition.getKey())).build();
            configDefinitionStore.put(namespacedConfigDefinition);
            return namespacedConfigDefinition;
        }

    }

}
