/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.systemplugin;

import com.google.common.collect.ImmutableList;
import jakarta.annotation.Nullable;
import static java.util.Collections.emptyList;
import java.util.List;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class SystemPluginImpl implements SystemPlugin {

    private final String name, description, tag, service, version, requiredCoreVersion, checksum;
    private final List<String> requiredLibs;
    private final List<FaultEvent> healthCheck;

    private SystemPluginImpl(SystemPluginImplBuilder builder) {
        this.name = checkNotBlank(builder.name);
        this.description = firstNotBlank(builder.description, name);
        this.tag = builder.tag;
        this.service = builder.service;
        this.version = checkNotBlank(builder.version);
        this.checksum = checkNotBlank(builder.checksum);
        this.requiredCoreVersion = firstNotBlank(builder.requiredCoreVersion, version);
        this.requiredLibs = ImmutableList.copyOf(set(builder.requiredLibs));
        this.healthCheck = ImmutableList.copyOf(firstNotNull(builder.healthCheck, emptyList()));
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    @Override
    public String getTag() {
        return tag;
    }

    @Nullable
    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public List<String> getRequiredLibs() {
        return requiredLibs;
    }

    @Override
    public List<FaultEvent> getHealthCheck() {
        return healthCheck;
    }

    @Override
    public String getChecksum() {
        return checksum;
    }

    @Override
    public String getRequiredCoreVersion() {
        return requiredCoreVersion;
    }

    @Override
    public String toString() {
        return "SystemPlugin{" + "name=" + name + ", version=" + version + '}';
    }

    public static SystemPluginImplBuilder builder() {
        return new SystemPluginImplBuilder();
    }

    public static SystemPluginImplBuilder copyOf(SystemPlugin source) {
        return new SystemPluginImplBuilder()
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withTag(source.getTag())
                .withService(source.getService())
                .withVersion(source.getVersion())
                .withRequiredLibs(source.getRequiredLibs())
                .withHealthCheck(source.getHealthCheck())
                .withRequiredCoreVersion(source.getRequiredCoreVersion())
                .withChecksum(source.getChecksum());
    }

    public static class SystemPluginImplBuilder implements Builder<SystemPluginImpl, SystemPluginImplBuilder> {

        private String name;
        private String description, tag, service, checksum;
        private String version, requiredCoreVersion;
        private List<String> requiredLibs;
        private List<FaultEvent> healthCheck;

        public SystemPluginImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public SystemPluginImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public SystemPluginImplBuilder withTag(String tag) {
            this.tag = tag;
            return this;
        }

        public SystemPluginImplBuilder withService(String service) {
            this.service = service;
            return this;
        }

        public SystemPluginImplBuilder withChecksum(String checksum) {
            this.checksum = checksum;
            return this;
        }

        public SystemPluginImplBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        public SystemPluginImplBuilder withRequiredCoreVersion(String requiredCoreVersion) {
            this.requiredCoreVersion = requiredCoreVersion;
            return this;
        }

        public SystemPluginImplBuilder withRequiredLibs(List<String> requiredLibs) {
            this.requiredLibs = requiredLibs;
            return this;
        }

        public SystemPluginImplBuilder withHealthCheck(List<FaultEvent> healthCheck) {
            this.healthCheck = healthCheck;
            return this;
        }

        @Override
        public SystemPluginImpl build() {
            return new SystemPluginImpl(this);
        }

    }
}
