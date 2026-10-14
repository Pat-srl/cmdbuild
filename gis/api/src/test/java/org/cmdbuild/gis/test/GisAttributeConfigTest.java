package org.cmdbuild.gis.test;

import org.cmdbuild.gis.GisAttributeConfigImpl;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class GisAttributeConfigTest {

    @Test
    public void testGisAttributeConfig() {
        GisAttributeConfigImpl gisAttrConfig = GisAttributeConfigImpl
                .builder()
                .withInfoWindowContent("infoContent")
                .withInfoWindowEnabled(true)
                .withInfoWindowImage("fileattr").build();
        assertEquals("infoContent", gisAttrConfig.getInfoWindowContent());
        assertEquals("fileattr", gisAttrConfig.getInfoWindowImage());
        assertEquals(true, gisAttrConfig.getInfoWindowEnabled());
        GisAttributeConfigImpl copyOfGisAttrConf = GisAttributeConfigImpl.copyOf(gisAttrConfig).build();
        assertEquals("infoContent", copyOfGisAttrConf.getInfoWindowContent());
        assertEquals("fileattr", copyOfGisAttrConf.getInfoWindowImage());
        assertEquals(true, copyOfGisAttrConf.getInfoWindowEnabled());
    }

    @Test
    public void testGisAttributeDefaults() {
        GisAttributeConfigImpl gisAttrConfig = GisAttributeConfigImpl.builder().build();
        assertEquals(null, gisAttrConfig.getInfoWindowContent());
        assertEquals(null, gisAttrConfig.getInfoWindowImage());
        assertEquals(false, gisAttrConfig.getInfoWindowEnabled());
    }

}
