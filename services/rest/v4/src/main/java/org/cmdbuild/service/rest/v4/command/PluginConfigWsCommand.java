/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.plugin.config.PluginConfigService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class PluginConfigWsCommand {

    private final PluginConfigService pluginConfigService;

    public PluginConfigWsCommand(PluginConfigService pluginConfigService) {
        this.pluginConfigService = checkNotNull(pluginConfigService);
    }

    public List<Map<String, String>> doGetAllPublicPluginConfigs(String pluginName) {
        return pluginConfigService.getAllPublicByPlugin(pluginName);
    }

    public String doGetPublicPluginConfigValue(String pluginName, String key) {
        return pluginConfigService.getPublicValueByPluginAndKey(pluginName, key);
    }
}
