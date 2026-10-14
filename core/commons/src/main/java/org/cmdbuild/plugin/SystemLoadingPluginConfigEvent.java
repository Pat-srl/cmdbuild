/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ataboga
 */
public class SystemLoadingPluginConfigEvent {

    private final String pluginName;
    private final String configName;

    public SystemLoadingPluginConfigEvent() {
        this.pluginName = this.configName = StringUtils.EMPTY;
    }

    public SystemLoadingPluginConfigEvent(String pluginName, String configName) {
        this.pluginName = pluginName;
        this.configName = configName;
    }

    public String getPluginName() {
        return pluginName;
    }

    public String getConfigName() {
        return configName;
    }
}
