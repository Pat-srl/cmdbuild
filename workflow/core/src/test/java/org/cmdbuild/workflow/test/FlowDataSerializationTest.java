/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.test;

import java.util.Date;
import java.util.List;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.lookup.LookupRepository;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.workflow.WorkflowConfiguration;
import org.cmdbuild.workflow.inner.RiverTypeConverterImpl;
import org.cmdbuild.workflow.river.engine.RiverVariableInfo;
import org.cmdbuild.workflow.type.LookupType;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.deserializeValue;
import static org.cmdbuild.workflow.utils.FlowDataSerializerUtils.serializeValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlowDataSerializationTest {

    private final DaoService dao = mock(DaoService.class);
    private final LookupRepository lookupStore = mock(LookupRepository.class);
    private final WorkflowConfiguration workflowConfiguration = mock(WorkflowConfiguration.class);
    private final CacheService cacheService = mock(CacheService.class);

    @Test
    public void testDateSerialization() {
        Date date = toJavaDate("2019-03-24T12:33:14Z");
        String value = (String) serializeValue(date);
        assertEquals("2019-03-24T12:33:14Z", value);

        RiverVariableInfo variable = mock(RiverVariableInfo.class);
        when(variable.getJavaType()).thenReturn(Date.class);

        Date newDate = (Date) deserializeValue(value, variable);
        assertEquals(date.getTime(), newDate.getTime());
    }

    @Test
    public void testNullDateSerialization() {
        Object value = serializeValue(null);
        assertNull(value);

        RiverVariableInfo variable = mock(RiverVariableInfo.class);
        when(variable.getJavaType()).thenReturn(Date.class);

        Date newDate = (Date) deserializeValue(value, variable);
        assertNull(newDate);
    }

    @Test
    public void testLookupArraySerialization() {
        LookupType[] lookupArray = list(
                new LookupType(1001L, "LookupType", "LookupDescription1", "LookupCode1"),
                new LookupType(1002L, "LookupType", "LookupDescription2", "LookupCode2"),
                new LookupType(1003L, "LookupType", "LookupDescription3", "LookupCode3")
        ).toArray(LookupType[]::new);

        RiverTypeConverterImpl riverTypeConverter = new RiverTypeConverterImpl(dao, lookupStore, workflowConfiguration, cacheService);

        List<Long> listLookup = list((List) riverTypeConverter.flowValueToCardValue(lookupArray));

        assertEquals(Long.valueOf(1001), listLookup.get(0));
        assertEquals(Long.valueOf(1002), listLookup.get(1));
        assertEquals(Long.valueOf(1003), listLookup.get(2));
    }

}
