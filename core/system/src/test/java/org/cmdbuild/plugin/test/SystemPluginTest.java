/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.plugin.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.File;
import static java.util.Collections.emptyList;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.systemplugin.SystemPlugin;
import static org.cmdbuild.systemplugin.SystemPluginUtils.scanFolderForPlugins;
import static org.cmdbuild.systemplugin.SystemPluginUtils.scanWarFileForPlugins;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPluginTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Ignore
    @Test
    public void testSystemPluginWarScanner() {
        scanWarFileForPlugins(new File("../../cmdbuild/target/cmdbuild.war")).forEach(p -> logger.info("found plugin = {}", p));
    }

    @Test
    public void testSystemPluginScanner1() {
        File folder = tempDir();
        File lib1 = new File(folder, "cmdbuild-plugins-example-3.4-DEV-SNAPSHOT.jar");
        copy(getClass().getResourceAsStream("/org/cmdbuild/plugin/test/plugin-example-1.jar"), lib1);

        SystemPlugin plugin = getOnlyElement(scanFolderForPlugins(folder));

        assertEquals("cmdbuild-plugins-example", plugin.getName());
        assertEquals("3.4-DEV-SNAPSHOT", plugin.getVersion());
        assertEquals("My Example Plugin", plugin.getDescription());
        assertEquals("ridc-12.2.1.jar", getOnlyElement(plugin.getRequiredLibFiles()));
        assertEquals("3.4-DEV-SNAPSHOT", plugin.getRequiredCoreVersion());

        deleteQuietly(folder);
    }

    @Test
    public void testSystemPluginScanner2() {
        File folder = tempDir();
        File lib1 = new File(folder, "cmdbuild-plugins-example-3.4-DEV-SNAPSHOT.jar");
        copy(getClass().getResourceAsStream("/org/cmdbuild/plugin/test/plugin-example-2.jar"), lib1);

        SystemPlugin plugin = getOnlyElement(scanFolderForPlugins(folder));

        assertEquals("cmdbuild-plugins-example", plugin.getName());
        assertEquals("3.4-DEV-SNAPSHOT", plugin.getVersion());
        assertEquals("My Example Plugin", plugin.getDescription());
        assertEquals(emptyList(), plugin.getRequiredLibs());
        assertEquals("3.4-DEV-SNAPSHOT", plugin.getRequiredCoreVersion());

        deleteQuietly(folder);
    }

    @Test
    public void testSystemPluginScanner3() {
        File folder = tempDir();
        File lib1 = new File(folder, "cmdbuild-plugins-example-3.4-DEV-SNAPSHOT.jar");
        copy(getClass().getResourceAsStream("/org/cmdbuild/plugin/test/plugin-example-3.jar"), lib1);

        SystemPlugin plugin = getOnlyElement(scanFolderForPlugins(folder));

        assertEquals("cmdbuild-plugins-example", plugin.getName());
        assertEquals("3.4-DEV-SNAPSHOT", plugin.getVersion());
        assertEquals("My Example Plugin", plugin.getDescription());
        assertEquals(emptyList(), plugin.getRequiredLibs());
        assertEquals("1.0", plugin.getRequiredCoreVersion());

        deleteQuietly(folder);
    }

}
