/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import static org.cmdbuild.utils.io.CmIoUtils.getCharsetFromContentType;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.setCharsetInContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class ContentTypeTest {

    @Test
    public void testGetContentType1() {
        assertEquals("utf-8", getCharsetFromContentType("text/html; charset=utf-8"));
    }

    @Test
    public void testGetContentType2() {
        assertEquals("ISO-8859-1", getCharsetFromContentType("text/html;Charset=ISO-8859-1"));
    }

    @Test
    public void testGetContentType3() {
        assertEquals(null, getCharsetFromContentType("text/html"));
    }

    @Test
    public void testContentTypeMatch() {
        assertEquals(true, isContentType("text/plain", "text/plain"));
        assertEquals(true, isContentType("TEXT/PLAIN", "text/plain"));
        assertEquals(true, isContentType("TEXT/PLAIN", "text/*"));
        assertEquals(true, isContentType("text/html;Charset=ISO-8859-1", "text/html"));
        assertEquals(true, isContentType("text/html;Charset=ISO-8859-1", "text/*"));
        assertEquals(true, isContentType("text/html;Charset=ISO-8859-1", "text"));
    }

    @Test
    public void testHtmlDetect() {
        assertEquals("text/html", getContentType(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/content_type_html_test_1.txt"))));
        assertEquals("text/html", getContentType("hello <b>html</b>".getBytes()));
        assertEquals("text/plain", getContentType(toByteArray(getClass().getResourceAsStream("/org/cmdbuild/utils/io/test/content_type_html_test_2.txt"))));
    }

    @Test
    public void testGetCharset() {
        assertEquals("ISO-8859-1", getCharsetFromContentType("text/html;Charset=ISO-8859-1"));
        assertEquals("ISO-8859-1", getCharsetFromContentType("text/html;charset=ISO-8859-1"));
        assertEquals("ISO-8859-1", getCharsetFromContentType("text/html; charset= ISO-8859-1 "));
        assertEquals("ks_c_5601-1987", getCharsetFromContentType("text/plain; charset=\"ks_c_5601-1987\""));
    }

    @Test
    public void testSetCharset() {
        assertEquals("text/html; charset=utf-8", setCharsetInContentType("text/html", "utf-8"));
        assertEquals("text/html;charset=utf-8", setCharsetInContentType("text/html;Charset=ISO-8859-1", "utf-8"));
    }

}
