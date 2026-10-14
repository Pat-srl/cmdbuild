/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.json.test;

import com.google.common.collect.ImmutableMap;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.prettifyIfJsonLazy;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.json.test.JsonProcessingTest.MyEnum.ME_ONE;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class JsonProcessingTest {

    @Test
    public void testPrettyPrintLazy1() {
        String value = "{\"one\":[\"asd\",{\"two\":2}]}";
        Object out = prettifyIfJsonLazy(value);
        assertEquals("""
                     {
                       "one" : [ "asd", {
                         "two" : 2
                       } ]
                     }""", out.toString());
    }

    @Test
    public void testPrettyPrintLazy2() {
        String value = "ASD";
        Object out = prettifyIfJsonLazy(value);
        assertEquals(value, out.toString());
    }

    @Test
    public void testPrettyPrintLazy3() {
        String value = null;
        Object out = prettifyIfJsonLazy(value);
        assertEquals(String.valueOf(value), out.toString());
    }

    @Test
    public void testCustomEnumSerialization() {
        String json = toJson(ImmutableMap.of("val", ME_ONE));
        assertEquals("{\"val\":\"one\"}", json);

        MyEnum val = fromJson("\"one\"", MyEnum.class);
        assertEquals(ME_ONE, val);
    }

    public enum MyEnum {
        ME_ONE, ME_TWO
    }
}
