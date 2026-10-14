/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import static org.cmdbuild.temp.TempInfoSource.TS_OTHER;
import static org.cmdbuild.temp.TempInfoSource.TS_SECURE;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TempServiceTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testTempInfoSerialization1() {
        String val = toJson(TempInfoImpl.builder().withContentType("application/pdf").withSize(123l).withSource(TS_SECURE).withFileName("file.pdf").withTimeToLive(12345L).build());
        logger.info("val =< {} >", val);
        assertEquals("{\"contentType\":\"application/pdf\",\"fileName\":\"file.pdf\",\"size\":123,\"source\":\"secure\",\"timeToLive\":12345}", val);
        TempInfo info = fromJson(val, TempInfoImpl.class);
        assertEquals("application/pdf", info.getContentType());
        assertEquals(123l, (long) info.getSize());
        assertEquals(TS_SECURE, info.getSource());
        assertEquals("file.pdf", info.getFileName());
    }

    @Test
    public void testTempInfoSerialization2() {
        String val = toJson(TempInfoImpl.builder().build());
        logger.info("val =< {} >", val);
        assertEquals("{\"contentType\":\"application/octet-stream\",\"fileName\":null,\"size\":null,\"source\":\"other\",\"timeToLive\":43200}", val);
        TempInfo info = fromJson(val, TempInfoImpl.class);
        assertEquals("application/octet-stream", info.getContentType());
        assertNull(info.getSize());
        assertEquals(TS_OTHER, info.getSource());
        assertNull(info.getFileName());
    }
}
