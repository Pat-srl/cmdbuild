/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static java.util.Collections.emptyList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.serializeListOfStrings;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.parseListOfStrings;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.unkey;

public class CmKeyUtilsTest {

    @Test
    public void testKeyUtils() {
        assertEquals("", key(""));
        assertEquals("a", key("a"));
        assertEquals("one|two", key("one", "two"));
        assertEquals("one|two", key(list("one", "two")));
        assertEquals("one|two", key(new Object[]{"one", "two"}));
        assertEquals("on\\|e|two", key("on|e", "two"));

        assertEquals(emptyList(), unkey(null));
        assertEquals(emptyList(), unkey(""));
        assertEquals(emptyList(), unkey("  "));
        assertEquals(list("a"), unkey("a"));
        assertEquals(list("a"), unkey("  a "));
        assertEquals(list("one", "two"), unkey("one|two"));
        assertEquals(list("on|e", "two"), unkey("on\\|e|two"));

        assertEquals("one/two", serializeListOfStrings("/", "\\", list("one", "two")));
        assertEquals("on\\/e/two", serializeListOfStrings("/", "\\", list("on/e", "two")));
        assertEquals(list("one", "two"), parseListOfStrings("/", "\\", "one/two"));
        assertEquals(list("on/e", "two"), parseListOfStrings("/", "\\", "on\\/e/two"));

        assertEquals("one/two", serializeListOfStrings("/", "$", list("one", "two")));
        assertEquals("on$/e/two", serializeListOfStrings("/", "$", list("on/e", "two")));
        assertEquals(list("one", "two"), parseListOfStrings("/", "$", "one/two"));
        assertEquals(list("on/e", "two"), parseListOfStrings("/", "$", "on$/e/two"));

        assertEquals("one/two", serializeListOfStrings("/", list("one", "two")));
        assertEquals("on\\/e/two", serializeListOfStrings("/", list("on/e", "two")));
        assertEquals(list("one", "two"), parseListOfStrings("/", "one/two"));
        assertEquals(list("on/e", "two"), parseListOfStrings("/", "on\\/e/two"));
    }

}
