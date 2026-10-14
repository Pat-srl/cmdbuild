/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.wshelpers;

import java.util.List;
import java.util.Map;
import static org.cmdbuild.minions.MinionStatus.MS_ERROR;
import static org.cmdbuild.minions.MinionStatus.MS_READY;
import org.cmdbuild.plugin.manager.PluginManagerService;
import org.cmdbuild.systemplugin.SystemPlugin;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.maven.MavenUtils.mavenGavToFilename;
import static org.cmdbuild.utils.maven.MavenUtils.versionRangeToString;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class SystemPluginHelper {

    private final PluginManagerService pluginManager;

    public SystemPluginHelper(PluginManagerService pluginManager) {
        this.pluginManager = checkNotNull(pluginManager);

    }

    public Object getAdminCustomPage(SystemPlugin plugin) {
        return applyOrNull(pluginManager.getAdminCustomPageOrNull(plugin), adminCustomPage -> map(
                "name", adminCustomPage.getName(),
                "description", adminCustomPage.getDescription(),
                "alias", adminCustomPage.getExtjsAlias(),
                "componentId", adminCustomPage.getExtjsComponentId()));
    }

    public void applyPatches(SystemPlugin plugin) {
        pluginManager.applyPatches(plugin);
    }

    public Map<String, Object> serializePlugin(SystemPlugin plugin) {
        return map(
                "_id", plugin.getName(),
                "name", plugin.getName(),
                "description", plugin.getDescription(),
                "tag", pluginManager.getModuleType(plugin),
                "service", plugin.getService(),
                "version", plugin.getVersion(),
                "status", serializeEnum(plugin.isOk() ? MS_READY : MS_ERROR)
        );
    }

    public Map<String, Object> serializePluginDetailed(SystemPlugin plugin) {
        return map(serializePlugin(plugin))
                .with(
                        "checksum", plugin.getChecksum(),
                        "requiredCoreVersion", versionRangeToString(plugin.getRequiredCoreVersion()),
                        "requiredLibs", list(plugin.getRequiredLibs()).map(l -> map("gav", l, "fileName", mavenGavToFilename(l))),
                        "healthCheck", list(plugin.getHealthCheck()).map(f -> map("level", serializeEnum(f.getLevel()), "message", f.getMessage())),//TODO duplicate code
                        "_healthCheck_message", plugin.getHealthCheckMessage(),
                        "patches", getPatches(plugin),
                        "_hasPatches", hasPatches(plugin),
                        "custompage", getAdminCustomPage(plugin),
                        "configs", pluginManager.getConfigs(plugin),
                        "expirationDate", toIsoDateTime(pluginManager.getExpirationDate(plugin))
                );
    }

    private List<Map<String, Object>> getPatches(SystemPlugin plugin) {
        return list(pluginManager.getPatches(plugin)).map(p -> map("_id", p.getVersion(), "date", toIsoDateTime(p.getApplyDate())));
    }

    private boolean hasPatches(SystemPlugin plugin) {
        return pluginManager.hasPatches(plugin);
    }
}
