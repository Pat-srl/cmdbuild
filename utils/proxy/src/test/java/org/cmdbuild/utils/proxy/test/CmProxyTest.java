/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.proxy.test;

import static org.cmdbuild.utils.io.CmNetUtils.getAvailablePort;
import static org.cmdbuild.utils.io.CmNetUtils.isPortAvailable;
import org.cmdbuild.utils.proxy.CmProxyHelper;
import org.cmdbuild.utils.proxy.CmProxyUtils;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CmProxyTest {

    @Test
    public void testProxy() {
        int port = getAvailablePort(13080);
        assertTrue(isPortAvailable(port));
        CmProxyHelper proxy = CmProxyUtils.newHttpProxy(port, 8080).withCustomHeader("MyCustomHeader", "test").start();
        assertFalse(isPortAvailable(port));
        //TODO test proxy
        proxy.stop();
        assertTrue(isPortAvailable(port));
    }

}
