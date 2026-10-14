/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import com.google.common.eventbus.EventBus;
import java.util.Map;
import jakarta.annotation.Nullable;

public interface GlobalConfigService {

    /**
     * get config object to access config values within a namespace (this is the
     * usual entry point to config values from within cmdbuild).
     *
     * Config object are linked to config service eventbus, so they consume
     * resources that cannot be released. Config objects are meant as singleton,
     * and reused through the whole application lifecycle.
     *
     * @param namespace
     * @return
     */
    NamespacedConfigService getConfig(String namespace);

    /**
     * get event bus; mostly used to handle {@link ConfigUpdateEvent} events.
     *
     * @return
     */
    EventBus getEventBus();

    Map<String, String> getConfigAsMap();

    Map<String, String> getStoredConfigAsMap();

    Map<String, String> getConfigOrDefaultsAsMap();

    Map<String, ConfigDefinition> getConfigDefinitions();

    void putString(String key, @Nullable String value);

    void putString(String namespace, String key, @Nullable String value);

    void putStrings(Map<String, String> map);

    void putStrings(String namespace, Map<String, String> map);

    void delete(String key);

    void putString(String key, @Nullable String value, boolean encrypt);

    @Nullable
    String getString(String key);

    @Nullable
    String getStringOrDefault(String key);

    @Nullable
    String getStringOrDefault(String namespace, String key);

    void reload();

    String getConfigNamespaceFromConfigBeanClass(Class configBeanClass);

    @Nullable
    ConfigDefinition getConfigDefinitionOrNull(String key);

    default boolean isProtected(String key) {
        ConfigDefinition configDefinition = getConfigDefinitionOrNull(key);
        if (configDefinition == null) {
            return false;
        } else {
            return configDefinition.isProtected();
        }
    }

    default NamespacedConfigService getConfigFromBeanClass(Class configBeanClass) {
        return getConfig(getConfigNamespaceFromConfigBeanClass(configBeanClass));
    }

}
