/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service.test;

import java.util.List;
import java.util.Map;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigEntryImpl;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.cmdbuild.config.utils.LegacyConfigUtils.translateLegacyConfigNames;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class LegacyConfigNameMapperTest {

    @Test
    public void testLog4jSkip() {
        List<ConfigEntry> map = translateLegacyConfigNames(list(new ConfigEntryImpl("org.cmdbuild.log4j.something", "asd"), new ConfigEntryImpl("org.cmdbuild.log4j.else", "dsa")));
        assertTrue(map.isEmpty());
    }

    @Test
    public void testNoMap() {
        Map<String, String> target = map(translateLegacyConfigNames(list(new ConfigEntryImpl("org.cmdbuild.something", "asd"), new ConfigEntryImpl("org.cmdbuild.else", "dsa"))), ConfigEntry::getKey, ConfigEntry::getValue);
        assertEquals(map("org.cmdbuild.something", "asd", "org.cmdbuild.else", "dsa"), target);
    }

    @Test
    public void testCoreMapping() {
        Map<String, String> target = map(translateLegacyConfigNames(list(new ConfigEntryImpl("org.cmdbuild.cmdbuild.something", "asd"))), ConfigEntry::getKey, ConfigEntry::getValue);
        assertEquals(1, target.size());
        assertEquals("asd", target.get("org.cmdbuild.core.something"));
    }

    @Test
    public void testDmsMapping() {
        Map<String, String> target = map(translateLegacyConfigNames(list(new ConfigEntryImpl("org.cmdbuild.dms.dms.something", "asd"))), ConfigEntry::getKey, ConfigEntry::getValue);
        assertEquals(1, target.size());
        assertEquals("asd", target.get("org.cmdbuild.dms.something"));
    }

}
