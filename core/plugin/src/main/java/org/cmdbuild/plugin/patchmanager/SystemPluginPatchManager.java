/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.patchmanager;

import java.util.List;
import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.systemplugin.SystemPlugin;

/**
 *
 * @author ataboga
 */
public interface SystemPluginPatchManager {

    public void applyPatches(SystemPlugin plugin);

    public List<Patch> getPatchesOnDb(SystemPlugin plugin);

    public List<Patch> getPatchesOnFile(SystemPlugin plugin);

    public boolean hasPatches(SystemPlugin plugin);
}
