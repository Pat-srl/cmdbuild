/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import com.google.common.eventbus.EventBus;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface NamespacedConfigService {

    String getNamespace();

    @Nullable
    String getString(String key);

    @Nullable
    String getStringOrDefault(String key, String defaultKey);

    @Nullable
    <T> T getOrDefault(String key, String defaultKey, Class<T> valueType);

    Map<String, String> getAsMap();

    void set(String key, String value);

    void delete(String key);

    /**
     * return an eventbus that can be used to listen to
     * {@link ConfigUpdateEvent} events. Only events impacting this
     * configuration (namespace) are fowarded via this eventbus
     *
     * @return
     */
    EventBus getEventBus();

    ConfigDefinition getDefinition(String key);

    Map<String, ConfigDefinition> getAllDefinitions();

    ConfigDefinition addDefinition(ConfigDefinition configDefinition);

    @Nullable
    default String getStringOrDefault(String key) {
        return getStringOrDefault(key, key);
    }

    @Nullable
    default <T> T getOrDefault(String key, Class<T> valueType) {
        return getOrDefault(key, key, valueType);
    }

    @Nullable
    default String getDefault(String key) {
        return getDefinition(key).getDefaultValue();
    }

    default Map<String, String> getAllOrDefaults() {
        return getAllDefinitions().keySet().stream().collect(toMap(identity(), this::getStringOrDefault));
    }

    default boolean hasDefinitionForKey(String key) {
        checkNotBlank(key);
        return getAllDefinitions().containsKey(key);
    }

}
