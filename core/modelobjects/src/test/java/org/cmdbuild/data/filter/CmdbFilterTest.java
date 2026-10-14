/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import static com.google.common.collect.Iterables.getOnlyElement;
import org.cmdbuild.data.filter.AttributeFilter.AttributeFilterMode;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.cmdbuild.data.filter.beans.AttributeFilterConditionImpl;
import static org.junit.Assert.assertTrue;

public class CmdbFilterTest {

	@Test
	public void testParsing() {
		String value = "{\n"
				+ "\"attribute\": {\n"
				+ "	\"simple\": {\n"
				+ "\"attribute\": \"code\",\n"
				+ "\"operator\": \"equal\",\n"
				+ "\"value\": [5]\n"
				+ "}\n"
				+ "}\n"
				+ "}";

		AttributeFilter filter = CmFilterUtils.parseFilter(value).getAttributeFilter();
		assertTrue(filter.isSimple());
		AttributeFilterCondition condition = filter.getCondition();
		assertEquals(AttributeFilterMode.SIMPLE, filter.getMode());
		assertEquals("code", condition.getKey());
		assertEquals("5", getOnlyElement(condition.getValues()));
		assertEquals("5", condition.getSingleValue());
		assertEquals(AttributeFilterConditionOperator.EQUAL, condition.getOperator());
	}

	@Test
	public void testParsing2() {
		String json = "{ attribute: { simple: { ClassName: 'MyClass',"
				+ "			attribute: 'foo',"
				+ "			operator: 'equal',"
				+ "			value:['bar'] } } }";
		AttributeFilter filter = CmFilterUtils.parseFilter(json).getAttributeFilter();
		assertTrue(filter.isSimple());
		assertEquals("MyClass", filter.getCondition().getClassName());
		assertEquals("bar", filter.getCondition().getSingleValue());
	}

	@Test
	public void testWriting() {
		CmdbFilter filter = AttributeFilterConditionImpl.builder().eq().withKey("code").withValues("5").build().toAttributeFilter().toCmdbFilters();

		String string = CmFilterUtils.serializeFilter(filter);
		assertEquals("{\"attribute\":{\"simple\":{\"attribute\":\"code\",\"operator\":\"equal\",\"value\":[\"5\"]}}}", string);
	}

	@Test
	public void testExportToJSONObject() throws JSONException {
		CmdbFilter filter = AttributeFilterConditionImpl.builder().eq().withKey("code").withValues("5").build().toAttributeFilter().toCmdbFilters();

		JSONObject jsono = CmFilterUtils.toJsonObject(filter);
		assertEquals("code", jsono.getJSONObject("attribute").getJSONObject("simple").getString("attribute"));
	}

}
