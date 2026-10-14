/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.type.test;

import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import org.apache.commons.codec.binary.Base64;
import static org.cmdbuild.utils.io.CmIoUtils.deserializeObject;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class LegacyWorkflowTypeDeserializationTest {

    /*
		System.out.println(Base64.encodeBase64String(serializeObject(new ReferenceType()))); //legacy bean was serialized here
		System.out.println(Base64.encodeBase64String(serializeObject(new ReferenceType(123, 321, "something")))); //legacy bean was serialized here
		System.out.println(Base64.encodeBase64String(serializeObject(new ReferenceType(123456, 654321, "something else")))); //legacy bean was serialized here
		System.out.println(Base64.encodeBase64String(serializeObject(new ReferenceType(1234567891011121314l, 654321l, "something else with long ids"))));  //new bean was serialized here
		System.out.println(Base64.encodeBase64String(serializeObject(new LookupType()))); //legacy bean was serialized here
		System.out.println(Base64.encodeBase64String(serializeObject(new LookupType(123, "type one", "description one", "code one")))); //legacy bean was serialized here
		System.out.println(Base64.encodeBase64String(serializeObject(new LookupType(123456, "type two", "description two", "code two")))); //legacy bean was serialized here
		System.out.println(Base64.encodeBase64String(serializeObject(new LookupType(1234567891011121314l, "type three", "description three", "code three"))));  //new bean was serialized here
     */
    private final static List<byte[]> LEGACY_SERIALIZED_DATA = list(
            "rO0ABXNyAChvcmcuY21kYnVpbGQud29ya2Zsb3cudHlwZS5SZWZlcmVuY2VUeXBlAAAAAAAAAAECAANJAAJpZEkAB2lkQ2xhc3NMAAtkZXNjcmlwdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO3hw//////////90AAA=",
            "rO0ABXNyAChvcmcuY21kYnVpbGQud29ya2Zsb3cudHlwZS5SZWZlcmVuY2VUeXBlAAAAAAAAAAECAANJAAJpZEkAB2lkQ2xhc3NMAAtkZXNjcmlwdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwAAAAewAAAUF0AAlzb21ldGhpbmc=",
            "rO0ABXNyAChvcmcuY21kYnVpbGQud29ya2Zsb3cudHlwZS5SZWZlcmVuY2VUeXBlAAAAAAAAAAECAANJAAJpZEkAB2lkQ2xhc3NMAAtkZXNjcmlwdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO3hwAAHiQAAJ+/F0AA5zb21ldGhpbmcgZWxzZQ==",
            "rO0ABXNyAChvcmcuY21kYnVpbGQud29ya2Zsb3cudHlwZS5SZWZlcmVuY2VUeXBlAAAAAAAAAAECAARJAAJpZEkAB2lkQ2xhc3NMAAtkZXNjcmlwdGlvbnQAEkxqYXZhL2xhbmcvU3RyaW5nO0wABmxvbmdJZHQAEExqYXZhL2xhbmcvTG9uZzt4cH////8ACfvxdAAcc29tZXRoaW5nIGVsc2Ugd2l0aCBsb25nIGlkc3NyAA5qYXZhLmxhbmcuTG9uZzuL5JDMjyPfAgABSgAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHARIhD0stIwog==",
            "rO0ABXNyACVvcmcuY21kYnVpbGQud29ya2Zsb3cudHlwZS5Mb29rdXBUeXBlAAAAAAAAAAECAARJAAJpZEwABGNvZGV0ABJMamF2YS9sYW5nL1N0cmluZztMAAtkZXNjcmlwdGlvbnEAfgABTAAEdHlwZXEAfgABeHD/////dAAAcQB+AANxAH4AAw==",
            "rO0ABXNyACVvcmcuY21kYnVpbGQud29ya2Zsb3cudHlwZS5Mb29rdXBUeXBlAAAAAAAAAAECAARJAAJpZEwABGNvZGV0ABJMamF2YS9sYW5nL1N0cmluZztMAAtkZXNjcmlwdGlvbnEAfgABTAAEdHlwZXEAfgABeHAAAAB7dAAIY29kZSBvbmV0AA9kZXNjcmlwdGlvbiBvbmV0AAh0eXBlIG9uZQ==",
            "rO0ABXNyACVvcmcuY21kYnVpbGQud29ya2Zsb3cudHlwZS5Mb29rdXBUeXBlAAAAAAAAAAECAARJAAJpZEwABGNvZGV0ABJMamF2YS9sYW5nL1N0cmluZztMAAtkZXNjcmlwdGlvbnEAfgABTAAEdHlwZXEAfgABeHAAAeJAdAAIY29kZSB0d290AA9kZXNjcmlwdGlvbiB0d290AAh0eXBlIHR3bw==",
            "rO0ABXNyACVvcmcuY21kYnVpbGQud29ya2Zsb3cudHlwZS5Mb29rdXBUeXBlAAAAAAAAAAECAAVJAAJpZEwABGNvZGV0ABJMamF2YS9sYW5nL1N0cmluZztMAAtkZXNjcmlwdGlvbnEAfgABTAAGbG9uZ0lkdAAQTGphdmEvbGFuZy9Mb25nO0wABHR5cGVxAH4AAXhwf////3QACmNvZGUgdGhyZWV0ABFkZXNjcmlwdGlvbiB0aHJlZXNyAA5qYXZhLmxhbmcuTG9uZzuL5JDMjyPfAgABSgAFdmFsdWV4cgAQamF2YS5sYW5nLk51bWJlcoaslR0LlOCLAgAAeHARIhD0stIwonQACnR5cGUgdGhyZWU="
    ).stream().map(Base64::decodeBase64).collect(toImmutableList());

    @Test
    public void testLegacyReferenceTypeWithNullContent() {
        ReferenceType bean = deserializeObject(LEGACY_SERIALIZED_DATA.get(0));
        assertEquals(-1, bean.getId());
        assertEquals(-1, bean.getIdClass());
        assertEquals("", bean.getDescription());
    }

    @Test
    public void testLegacyReferenceTypeWithContentOne() {
        ReferenceType bean = deserializeObject(LEGACY_SERIALIZED_DATA.get(1));
        assertEquals(123, bean.getId());
        assertEquals(321, bean.getIdClass());
        assertEquals("something", bean.getDescription());
    }

    @Test
    public void testLegacyReferenceTypeWithContentTwo() {
        ReferenceType bean = deserializeObject(LEGACY_SERIALIZED_DATA.get(2));
        assertEquals(123456, bean.getId());
        assertEquals(654321, bean.getIdClass());
        assertEquals("something else", bean.getDescription());
    }

    @Test
    public void testNewReferenceTypeWithLongContent() {
        ReferenceType bean = deserializeObject(LEGACY_SERIALIZED_DATA.get(3));
        assertEquals(1234567891011121314l, bean.getId());
        assertEquals(654321l, bean.getIdClass());
        assertEquals("something else with long ids", bean.getDescription());
    }

    @Test
    public void testLegacyLookupTypeWithNullContent() {
        LookupType bean = deserializeObject(LEGACY_SERIALIZED_DATA.get(4));
        assertEquals(-1, bean.getId());
        assertEquals("", bean.getType());
        assertEquals("", bean.getCode());
        assertEquals("", bean.getDescription());
    }

    @Test
    public void testLegacyLookupTypeWithContentOne() {
        LookupType bean = deserializeObject(LEGACY_SERIALIZED_DATA.get(5));
        assertEquals(123, bean.getId());
        assertEquals("type one", bean.getType());
        assertEquals("description one", bean.getDescription());
        assertEquals("code one", bean.getCode());
    }

    @Test
    public void testLegacyLookupTypeWithContentTwo() {
        LookupType bean = deserializeObject(LEGACY_SERIALIZED_DATA.get(6));
        assertEquals(123456, bean.getId());
        assertEquals("type two", bean.getType());
        assertEquals("description two", bean.getDescription());
        assertEquals("code two", bean.getCode());
    }

    @Test
    public void testNewLookupTypeWithLongContent() {
        LookupType bean = deserializeObject(LEGACY_SERIALIZED_DATA.get(7));
        assertEquals(1234567891011121314l, bean.getId());
        assertEquals("type three", bean.getType());
        assertEquals("description three", bean.getDescription());
        assertEquals("code three", bean.getCode());
    }

}
