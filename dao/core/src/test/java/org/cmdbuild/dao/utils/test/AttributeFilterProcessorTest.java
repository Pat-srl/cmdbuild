/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils.test;

import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.dao.entrytype.attributetype.LinkAttributeType;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.validateLinkAttrValue;
import org.cmdbuild.data.filter.AttributeFilter;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.BETWEEN;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.CONTAIN;
import static org.cmdbuild.data.filter.AttributeFilterConditionOperator.EQUAL;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

public class AttributeFilterProcessorTest {

    @Test
    public void testEquals() {
        AttributeFilter filter = AttributeFilterConditionImpl.eq("myKey", "value").toAttributeFilter();
        AttributeFilterProcessor<Map<String, Object>> processor = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter).build();

        assertTrue(processor.match(map("myKey", "value")));
        assertFalse(processor.match(map("myKey", "invalid")));
        assertFalse(processor.match(emptyMap()));
    }

    @Test
    public void testEqualsWithConversion1() {
        AttributeFilter filter = AttributeFilterConditionImpl.eq("myKey", "123").toAttributeFilter();
        AttributeFilterProcessor<Map<String, Object>> processor = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter).build();

        assertFalse(processor.match(map("myKey", "value")));
        assertTrue(processor.match(map("myKey", "123")));
        assertTrue(processor.match(map("myKey", 123L)));
        assertTrue(processor.match(map("myKey", 123.00)));
        assertFalse(processor.match(emptyMap()));
    }

    @Test
    public void testEqualsWithConversion2() {
        AttributeFilter filter = AttributeFilterConditionImpl.eq("myKey", "123.45").toAttributeFilter();
        AttributeFilterProcessor<Map<String, Object>> processor = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter).build();

        assertFalse(processor.match(map("myKey", "value")));
        assertTrue(processor.match(map("myKey", "123.45")));
        assertTrue(processor.match(map("myKey", 123.45)));
        assertTrue(processor.match(map("myKey", 123.4500)));
        assertFalse(processor.match(emptyMap()));
    }

    @Test
    @Ignore
    public void testEqualsWithConversion3() {
        AttributeFilter filter = AttributeFilterConditionImpl.eq("myKey", "123.4500").toAttributeFilter();
        AttributeFilterProcessor<Map<String, Object>> processor = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter).build();

        assertFalse(processor.match(map("myKey", "value")));
        assertTrue(processor.match(map("myKey", "123.4500")));
        assertTrue(processor.match(map("myKey", 123.4500)));
        assertTrue(processor.match(map("myKey", 123.45)));
        assertFalse(processor.match(emptyMap()));
    }

    @Test
    public void testBetweenInt() {
        AttributeFilter filter = AttributeFilterConditionImpl.builder().withOperator(BETWEEN).withKey("myKey").withValues(4, 6).build().toAttributeFilter();
        AttributeFilterProcessor<Map<String, Object>> processor = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter).build();

        assertFalse(processor.match(map("myKey", 2)));
        assertFalse(processor.match(map("myKey", 10)));
        assertTrue(processor.match(map("myKey", 5)));
        assertTrue(processor.match(map("myKey", 4)));
        assertTrue(processor.match(map("myKey", 6)));
        assertTrue(processor.match(map("myKey", 6.00)));
        assertFalse(processor.match(map("myKey", 6.01)));
        assertTrue(processor.match(map("myKey", 4.00)));
        assertTrue(processor.match(map("myKey", 4.01)));
        assertFalse(processor.match(map("myKey", 3.99)));
        assertFalse(processor.match(emptyMap()));
    }

    @Test
    public void testBetweenTimestamp() {
        AttributeFilter filter = AttributeFilterConditionImpl.builder().withOperator(BETWEEN).withKey("myKey").withValues("2019-03-22T10:12:14Z", "2019-03-25T11:13:18Z").build().toAttributeFilter();
        AttributeFilterProcessor<Map<String, Object>> processor = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter).build();

        assertFalse(processor.match(map("myKey", toDateTime("2019-03-22T10:12:12Z"))));
        assertFalse(processor.match(map("myKey", toDateTime("2019-03-25T11:13:22Z"))));
        assertTrue(processor.match(map("myKey", toDateTime("2019-03-22T10:14:14Z"))));
        assertTrue(processor.match(map("myKey", toDateTime("2019-03-22T10:12:14Z"))));
        assertTrue(processor.match(map("myKey", toDateTime("2019-03-25T11:13:18Z"))));
        assertFalse(processor.match(emptyMap()));

    }

    @Test
    public void testBetweenTimes() {
        AttributeFilter filter = AttributeFilterConditionImpl.builder().withOperator(BETWEEN).withKey("myKey").withValues("10:12:14", "11:13:18").build().toAttributeFilter();
        AttributeFilterProcessor<Map<String, Object>> processor = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter).build();

        assertFalse(processor.match(map("myKey", toTime("10:12:12"))));
        assertFalse(processor.match(map("myKey", toTime("11:13:22"))));
        assertTrue(processor.match(map("myKey", toTime("10:14:14"))));
        assertTrue(processor.match(map("myKey", toTime("10:12:14"))));
        assertTrue(processor.match(map("myKey", toTime("11:13:18"))));
        assertFalse(processor.match(emptyMap()));
    }

    @Test
    public void testEqualTimes() {
        AttributeFilter filter = AttributeFilterConditionImpl.builder().withOperator(EQUAL).withKey("myKey").withValues("11:13:18").build().toAttributeFilter();
        AttributeFilterProcessor<Map<String, Object>> processor = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter).build();

        assertFalse(processor.match(map("myKey", toTime("10:12:12"))));
        assertFalse(processor.match(map("myKey", toTime("11:13:22"))));
        assertTrue(processor.match(map("myKey", toTime("11:13:18"))));
        assertFalse(processor.match(emptyMap()));
    }

    @Test
    public void testContains() {
        AttributeFilter filter = AttributeFilterConditionImpl.builder().withKey("myKey").withOperator(CONTAIN).withValues("123").build().toAttributeFilter();
        AttributeFilterProcessor<Map<String, Object>> processor = AttributeFilterProcessor.<Map<String, Object>>builder().withKeyToValueFunction((k, c) -> c.get(k)).withFilter(filter).build();

        assertFalse(processor.match(map("myKey", "value")));
        assertTrue(processor.match(map("myKey", "123")));
        assertTrue(processor.match(map("myKey", "aa123")));
        assertTrue(processor.match(map("myKey", "123aa")));
        assertTrue(processor.match(map("myKey", "aa123aa")));
        assertTrue(processor.match(map("myKey", 123L)));
        assertTrue(processor.match(map("myKey", 51235.00)));
        assertFalse(processor.match(emptyMap()));
    }

    @Test
    public void testLinkAttrValidation() {
        assertEquals("<a href=\"http://test\">test</a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"http://test\">test</a>"));
        assertEquals("<a href=\"http://test\"> </a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"http://test\"> </a>"));
        assertEquals("<a href=\"http://test\"></a>", rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"http://test\"></a>"));
//        assertFail(()-> rawToSystem(LinkAttributeType.INSTANCE, "<a href=\"http://test\"></a>")); TODO
    }

    @Test
    public void testLinkAttributeValuesMatching() {
        assertEquals("<a href=\"http://test\">test</a>", validateLinkAttrValue("<a href=\"http://test\">test</a>"));
        assertEquals("<a href=\"http://test\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>", validateLinkAttrValue("<a href=\"http://test\" target=\"_blank\" rel=\"noopener noreferrer\">test</a>"));
    }

}
