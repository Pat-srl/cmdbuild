/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.config;

import java.util.List;
import java.util.Map;
import org.cmdbuild.plugin.config.dao.PluginConfigData;

/**
 *
 * @author ataboga
 */
public interface PluginConfigService {

    public List<Map<String, String>> getAll();

    public List<Map<String, String>> getAllByPlugin(String plugin);

    public String getValueByPluginAndKey(String plugin, String key);

    public PluginConfigData getConfigByPluginAndKeyOrNull(String plugin, String key);

    public List<Map<String, String>> getAllPublicByPlugin(String plugin);

    public String getPublicValueByPluginAndKey(String plugin, String key);

    public PluginConfigData createConfig(PluginConfigData config);

    public List<PluginConfigData> createOrUpdateConfigs(List<PluginConfigData> config);

    public PluginConfigData updateConfig(PluginConfigData config);

    public void deleteConfig(String plugin, String key);
}
