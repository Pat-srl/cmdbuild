/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.manager;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.systemplugin.SystemPlugin;
import org.cmdbuild.uicomponents.UiComponentInfo;

/**
 *
 * @author ataboga
 */
public interface PluginManagerService {

    String PT_AI = "ai", PT_AUTH = "auth", PT_DMS = "dms", PT_WATERWAY = "waterway", PT_GENERIC = "generic";

    String getModuleType(SystemPlugin plugin);

    UiComponentInfo getAdminCustomPageOrNull(SystemPlugin plugin);

    Map<String, Object> getConfigs(SystemPlugin plugin);

    List<Patch> getPatches(SystemPlugin plugin);

    Date getExpirationDate(SystemPlugin plugin);

    boolean hasPatches(SystemPlugin plugin);

    void applyPatches(SystemPlugin plugin);
}
