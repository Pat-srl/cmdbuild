/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.plugin.config.dao.PluginConfigAccess;
import org.cmdbuild.plugin.config.dao.PluginConfigData;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

/**
 * @author ldare
 */
public class WsPluginData {

    private String plugin;
    @JsonProperty
    private String key;
    @JsonProperty
    private final String value;
    @JsonProperty
    private final PluginConfigAccess access;

    public WsPluginData(PluginConfigData pluginConfig) {
        this.plugin = pluginConfig.getPluginName();
        this.key = pluginConfig.getKey();
        this.value = pluginConfig.getValue();
        this.access = pluginConfig.getAccess();
    }

    public WsPluginData(
            @JsonProperty("key") String key,
            @JsonProperty(required = true, value = "value") String value,
            @JsonProperty("access") String access
    ) {
        this.plugin = null;
        this.key = key;
        this.value = value;
        this.access = parseEnumOrNull(access, PluginConfigAccess.class);
    }

    public PluginConfigData toPluginConfigData() {
        return PluginConfigData.builder()
                .withPluginName(plugin)
                .withKey(key)
                .withValue(value)
                .withAccess(access)
                .build();
    }

    public WsPluginData setPlugin(String plugin) {
        this.plugin = plugin;
        return this;
    }

    public WsPluginData setKey(String key) {
        this.key = key;
        return this;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public PluginConfigAccess getAccess() {
        return access;
    }
}
