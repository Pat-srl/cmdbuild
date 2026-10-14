/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.config.dao;

import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.plugin.config.dao.PluginConfigAccess.PCA_PRIVATE;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@CardMapping("_PluginConfig")
public class PluginConfigData {

    private final Long id;
    private final String pluginName, key, value;
    private final PluginConfigAccess access;

    private PluginConfigData(PluginConfigDataBuilder builder) {
        this.id = builder.id;
        this.pluginName = checkNotNull(builder.pluginName);
        this.key = checkNotNull(builder.key);
        this.value = checkNotBlank(builder.value);
        this.access = firstNotNull(builder.access, PCA_PRIVATE);
    }

    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @CardAttr(ATTR_CODE)
    public String getPluginName() {
        return pluginName;
    }

    @CardAttr
    public String getKey() {
        return key;
    }

    @CardAttr
    public String getValue() {
        return value;
    }

    @CardAttr
    public PluginConfigAccess getAccess() {
        return access;
    }

    public static PluginConfigDataBuilder builder() {
        return new PluginConfigDataBuilder();
    }

    public static PluginConfigDataBuilder copyOf(PluginConfigData source) {
        return new PluginConfigDataBuilder()
                .withId(source.getId())
                .withPluginName(source.getPluginName())
                .withKey(source.getKey())
                .withValue(source.getValue())
                .withAccess(source.getAccess());
    }

    public static class PluginConfigDataBuilder implements Builder<PluginConfigData, PluginConfigDataBuilder> {

        private Long id;
        private String pluginName, key, value;
        private PluginConfigAccess access;

        public PluginConfigDataBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public PluginConfigDataBuilder withPluginName(String pluginName) {
            this.pluginName = pluginName;
            return this;
        }

        public PluginConfigDataBuilder withKey(String key) {
            this.key = key;
            return this;
        }

        public PluginConfigDataBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        public PluginConfigDataBuilder withAccess(PluginConfigAccess access) {
            this.access = access;
            return this;
        }

        @Override
        public PluginConfigData build() {
            return new PluginConfigData(this);
        }

    }

}
