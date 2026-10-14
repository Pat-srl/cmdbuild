/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.pocket.test;

import java.io.File;
import java.util.Map;
import static org.cmdbuild.config.CoreConfiguration.CORE_LOGGER_AUTOCONFIGURE_PROPERTY;
import org.cmdbuild.pocket.PocketUtils;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.cmdbuild.pocket.PocketHelper;
import static org.cmdbuild.pocket.PocketUtils.POCKET_CONFIG_WARFILE;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;

public class PocketTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testPocketConfig() {
        PocketHelper tomcat = PocketUtils.pocket(new File("/tmp/file.war")).withConfig(CORE_LOGGER_AUTOCONFIGURE_PROPERTY, false);

        Map<String, String> config1 = tomcat.getConfig();

        assertEquals("/tmp/file.war", config1.get(POCKET_CONFIG_WARFILE));

        Map<String, String> config2 = PocketUtils.pocket(config1).getConfig();

        assertEquals(mapToLoggableStringInline(config1), mapToLoggableStringInline(config2));

        Map<String, String> config3 = PocketUtils.pocket(config2).getConfig();

        assertEquals(mapToLoggableStringInline(config2), mapToLoggableStringInline(config3));
    }

}
