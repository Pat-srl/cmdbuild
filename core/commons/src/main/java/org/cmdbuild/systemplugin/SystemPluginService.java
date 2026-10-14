/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.systemplugin;

import jakarta.activation.DataSource;
import java.util.Collection;
import java.util.List;

public interface SystemPluginService {

    List<SystemPlugin> getSystemPlugins();

    SystemPlugin getSystemPlugin(String pluginName);

    default boolean hasService(String pluginName) {
        return getSystemPlugin(pluginName).getService() != null;
    }

    void deploySystemPlugins(Collection<DataSource> dataFiles);

    void removeSystemPlugins();
}
