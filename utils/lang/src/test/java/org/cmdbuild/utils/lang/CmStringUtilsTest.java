/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.cmformat;
import static org.cmdbuild.utils.lang.CmStringUtils.escapeGroovyTripleSingleQuoteString;
import static org.cmdbuild.utils.lang.CmStringUtils.htmlToString;
import static org.cmdbuild.utils.lang.CmStringUtils.multilineWithOffset;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class CmStringUtilsTest {

    @Test
    public void testEscape() {
        assertEquals(null, escapeGroovyTripleSingleQuoteString(null));
        assertEquals("", escapeGroovyTripleSingleQuoteString(""));
        assertEquals("as\\'\\\\as", escapeGroovyTripleSingleQuoteString("as'\\as"));
    }

    @Test
    public void testCmformat() {
        assertEquals("hello", cmformat("hello"));
        assertEquals("hello there", cmformat("hello {}", "there"));
        assertEquals("hello there", cmformat("hello %s", "there"));
    }

    @Test
    public void testCmdbStringUtilsAbbreviate1() {
        String orig = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        String res = abbreviate(orig, 100);
        assertEquals(orig, res);
    }

    @Test
    public void testCmdbStringUtilsAbbreviate2() {
        String orig = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890";
        String res = abbreviate(orig, 99);
        assertEquals(99, res.length());
        assertEquals("123456789012345678901234567890123456789012345678901234567890123456789012345678901234... (100 chars)", res);
    }

    @Test
    public void testCmdbStringUtilsAbbreviate3() {
        String orig = "asd\n\rpppp\nasdasd";
        String res = abbreviate(orig, 99);
        assertEquals(orig.length() - 1, res.length());
        assertEquals("asd pppp asdasd", res);
    }

    @Test
    public void testCmdbStringUtilsAbbreviate4() {
        String orig = "123";
        String res = abbreviate(orig, 100);
        assertEquals(orig, res);
    }

    @Test
    public void testCmdbStringUtilsAbbreviate5() {
        String orig = null;
        String res = abbreviate(orig, 100);
        assertEquals(orig, res);
    }

    @Test
    public void testCmdbStringUtilsMultilineWithOffset() {
        assertEquals("    asd\n    dsa", multilineWithOffset("asd\ndsa", 4));
    }

    @Test
    public void testNumberToString() {
        assertEquals("0", toStringOrNull(0l));
        assertEquals("0", toStringOrNull(0.0));
        assertEquals("0.01", toStringOrNull(0.01));
        assertEquals("123", toStringOrNull(123));
        assertEquals("123", toStringOrNull(123.00));
    }

    @Test
    public void testHtmlToString() {
        assertEquals("hello\nworld", htmlToString("<b>hello</b><div><u>world</u></div>"));
    }
}
