/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.config.api.ConfigCategory.CC_DEFAULT;
import static org.cmdbuild.config.api.ConfigDefinition.ModularConfigDefinition.MCD_NONE;
import static org.cmdbuild.config.api.ConfigLocation.CL_DEFAULT;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ConfigDefinitionImpl implements ConfigDefinition {

    private final String key, description, defaultValue, enumValues, moduleNamespace;
    private final boolean isProtected, isExperimental;
    private final ConfigLocation location;
    private final ConfigCategory category;
    private final ModularConfigDefinition modular;

    private ConfigDefinitionImpl(ConfigDefinitionImplBuilder builder) {
        this.key = checkNotBlank(builder.key);
        this.description = checkNotNull(builder.description);
        this.location = firstNotNull(builder.location, CL_DEFAULT);
        this.category = firstNotNull(builder.category, CC_DEFAULT);
        this.defaultValue = builder.defaultValue;
        this.enumValues = builder.enumValues;
        this.isProtected = builder.isProtected;
        this.isExperimental = builder.isExperimental;
        this.modular = firstNotNull(builder.modular, MCD_NONE);
        switch (modular) {
            case MCD_MODULE:
            case MCD_OWNER:
                this.moduleNamespace = checkNotBlank(builder.moduleNamespace);
                break;
            case MCD_NONE:
                this.moduleNamespace = null;
                break;
            default:
                throw new IllegalArgumentException("invalid modular value = " + modular);
        }
    }

    @Override
    public ModularConfigDefinition getModular() {
        return modular;
    }

    @Nullable
    @Override
    public String getModuleNamespace() {
        return moduleNamespace;
    }

    @Override
    public ConfigLocation getLocation() {
        return location;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    @Override
    public String getEnumValues() {
        return enumValues;
    }

    @Override
    public boolean isProtected() {
        return isProtected;
    }

    @Override
    public ConfigCategory getCategory() {
        return category;
    }

    @Override
    public boolean isExperimental() {
        return isExperimental;
    }

    @Override
    public String toString() {
        return "ConfigDefinition{" + "key=" + key + ", description=" + description + ", defaultValue=" + defaultValue + '}';
    }

    public static ConfigDefinitionImplBuilder builder() {
        return new ConfigDefinitionImplBuilder();
    }

    public static ConfigDefinitionImplBuilder copyOf(ConfigDefinition configDefinition) {
        return builder()
                .withKey(configDefinition.getKey())
                .withDescription(configDefinition.getDescription())
                .withDefaultValue(configDefinition.getDefaultValue())
                .withEnumValues(configDefinition.getEnumValues())
                .withLocation(configDefinition.getLocation())
                .withCategory(configDefinition.getCategory())
                .withProtected(configDefinition.isProtected())
                .withExperimental(configDefinition.isExperimental())
                .withModular(configDefinition.getModular())
                .withModuleNamespace(configDefinition.getModuleNamespace());
    }

    public static class ConfigDefinitionImplBuilder implements Builder<ConfigDefinitionImpl, ConfigDefinitionImplBuilder> {

        private String key, description, defaultValue, enumValues, moduleNamespace;
        private boolean isProtected = false, isExperimental = false;
        private ConfigLocation location;
        private ConfigCategory category;
        private ModularConfigDefinition modular;

        private ConfigDefinitionImplBuilder() {
        }

        public ConfigDefinitionImplBuilder withModular(ModularConfigDefinition modular) {
            this.modular = modular;
            return this;
        }

        public ConfigDefinitionImplBuilder withModuleNamespace(String moduleNamespace) {
            this.moduleNamespace = moduleNamespace;
            return this;
        }

        public ConfigDefinitionImplBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public ConfigDefinitionImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ConfigDefinitionImplBuilder withLocation(ConfigLocation location) {
            this.location = location;
            return this;
        }

        public ConfigDefinitionImplBuilder withCategory(ConfigCategory category) {
            this.category = category;
            return this;
        }

        public ConfigDefinitionImplBuilder withDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public ConfigDefinitionImplBuilder withEnumValues(String enumValues) {
            this.enumValues = enumValues;
            return this;
        }

        public ConfigDefinitionImplBuilder withProtected(boolean protect) {
            this.isProtected = protect;
            return this;
        }

        public ConfigDefinitionImplBuilder withExperimental(boolean isExperimental) {
            this.isExperimental = isExperimental;
            return this;
        }

        @Override
        public ConfigDefinitionImpl build() {
            return new ConfigDefinitionImpl(this);
        }

    }

}
