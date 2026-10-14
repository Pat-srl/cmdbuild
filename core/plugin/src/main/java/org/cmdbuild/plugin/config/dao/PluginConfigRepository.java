/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.config.dao;

import java.util.List;

/**
 *
 * @author ataboga
 */
public interface PluginConfigRepository {

    public List<PluginConfigData> getAll();

    public List<PluginConfigData> getAllByPlugin(String plugin);

    public PluginConfigData getValueByPluginAndKeyOrNull(String plugin, String key);

    public PluginConfigData getValueByPluginAndKey(String plugin, String key);

    public PluginConfigData create(PluginConfigData pluginConfigData);

    public PluginConfigData update(PluginConfigData pluginConfigData);

    public void delete(PluginConfigData pluginConfigData);
}
