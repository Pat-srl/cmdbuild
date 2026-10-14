/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.systemplugin.test;

import java.io.File;
import org.cmdbuild.minions.MinionServiceImpl;
import static org.cmdbuild.systemplugin.SystemPluginUtils.getPluginFolderFromContext;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SystemPluginTest {

    @Test
    public void parseContextTest() {
        String contextWithDiscoveryPlugin = readToString(MinionServiceImpl.class.getResourceAsStream("/org/cmdbuild/systemplugin/context.xml"));
        assertEquals(getPluginFolderFromContext(contextWithDiscoveryPlugin), new File("###folder###"));
    }
}
