/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.ecql;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class EcqlSourceUtilsTest {

    @Test
    public void testBuildEcqlId() {
        String encodedId = EcqlSourceUtils.buildEcqlId(EcqlSource.CLASS_ATTRIBUTE, 1234);
        assertEquals("7d5fwl7t", encodedId);
        EcqlId ecqlId = EcqlSourceUtils.parseEcqlId(encodedId);
        assertEquals(EcqlSource.CLASS_ATTRIBUTE, ecqlId.getSource());
        assertEquals(String.valueOf(1234), getOnlyElement(ecqlId.getId()));
    }

    @Test
    public void testParseEcqlId() {
        String encodedId = "ekfufsvssidcxn";
        EcqlId ecqlId = EcqlSourceUtils.parseEcqlId(encodedId);
        assertEquals(EcqlSource.CLASS_ATTRIBUTE, ecqlId.getSource());
        assertEquals(String.valueOf(1234), getOnlyElement(ecqlId.getId()));
    }
    
    @Test
    public void testBuildEcqlId2() {
        String encodedId = EcqlSourceUtils.buildEcqlId(EcqlSource.CLASS_ATTRIBUTE, 1234, "Something");
        assertEquals("33wdpdl29k27lu2bo2wvk073u7jy5bt17", encodedId);
        EcqlId ecqlId = EcqlSourceUtils.parseEcqlId(encodedId);
        assertEquals(EcqlSource.CLASS_ATTRIBUTE, ecqlId.getSource());
        assertEquals(String.valueOf(1234), ecqlId.getId().get(0));
        assertEquals("Something", ecqlId.getId().get(1));
    }

} // end EcqlSourceUtilsTest class
