/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.Maps;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Nullable;

public interface ConfigDefinitionRepository {

    void put(ConfigDefinition configDefinition);

    @Nullable
    ConfigDefinition getOrNull(String key);

    Map<String, ConfigDefinition> getAll();

    boolean containsConfigOrModuleConfig(String key);

    default ConfigDefinition get(String key) {
        return checkNotNull(getOrNull(key), "unable to find config definition for key = %s", key);
    }

    default Map<String, String> getAllDefaults() {
        return Maps.transformValues(getAll(), ConfigDefinition::getDefaultValue);
    }

    @Nullable
    default String getDefaultOrNull(String key) {
        ConfigDefinition definition = getOrNull(key);
        return definition == null ? null : definition.getDefaultValue();
    }

    default boolean isLocationFileOnly(String key) {
        return Optional.ofNullable(getOrNull(key)).map(ConfigDefinition::isLocationFileOnly).orElse(false);
    }

    default boolean isLocationFileOnly(ConfigUpdate entry) {
        return isLocationFileOnly(entry.getKey());
    }

    default boolean isLocationDefault(String key) {
        return Optional.ofNullable(getOrNull(key)).map(ConfigDefinition::isLocationDefault).orElse(true);
    }

    default boolean isLocationDefault(ConfigUpdate entry) {
        return isLocationDefault(entry.getKey());
    }

    default boolean isDefault(ConfigEntry entry) {
        return equal(entry.getValue(), nullToEmpty(getDefaultOrNull(entry.getKey())));
    }

}
