/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.v4.command.LookupValueWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.LookupValueWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkId;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListIds;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class LookupValueWs_ManagementTest extends WsTestBase {

    private final LookupValueWs_Management instance;

    private final String A_LOOKUP_TYPE_ID = "exampLookupTypeId";
    private final Long expId = A_KNOWN_LOOKUP_ID1;
    private final List<Long> expListIds = list(expId);

    private final LookupType lookupType;
    private final LookupValue lookupValue;

    public LookupValueWs_ManagementTest() {
        LookupValueWsCommand command = new LookupValueWsCommand(lookupService);
        instance = new LookupValueWs_Management(lookupService, objectTranslationService, command);

        lookupValue = mockBuildLookupValue(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LOOKUP_ID1);
        lookupType = mockBuildLookupType(A_KNOWN_LOOKUP_NAME1);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(lookupService.getLookup(A_KNOWN_LOOKUP_ID1)).thenReturn(lookupValue);
        when(objectTranslationService.translateLookupDescriptionSafe(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");
        when(lookupService.getLookupType(anyLong())).thenReturn(lookupType);
        when(lookupService.getLookupType(anyString())).thenReturn(lookupType);

        //act:
        Object resultObject = instance.read(A_LOOKUP_TYPE_ID, A_KNOWN_LOOKUP_ID1);

        //assert:
        verify(lookupService).getLookup(A_KNOWN_LOOKUP_ID1);
        verify(objectTranslationService).translateLookupDescriptionSafe(anyString(), anyString(), anyString());
        verify(lookupService).getLookupType(anyLong());
        verify(lookupService).getLookupType(anyString());
        checkId(expId, resultObject);
    }

    @Test
    public void testReadAll_isBlankTrue() {
        System.out.println("readAll_isBlankTrue");

        //arrange:
        when(objectTranslationService.translateLookupDescriptionSafe(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");
        when(lookupService.getLookupType(anyLong())).thenReturn(lookupType);
        when(lookupService.getLookupType(anyString())).thenReturn(lookupType);
        when(lookupService.getActiveLookup(anyString(), anyInt(), anyInt(), any(CmdbFilter.class))).thenReturn(mockBuildPagedElementsLookupValue());

        //act:
        Object resultObject = instance.readAll(A_LOOKUP_TYPE_ID, A_TEST_LIMIT, A_TEST_OFFSET, "", A_TEST_EMPTY_FILTER, "exampForAttr");

        //assert:
        verify(objectTranslationService).translateLookupDescriptionSafe(anyString(), anyString(), anyString());
        verify(lookupService).getLookupType(anyLong());
        verify(lookupService).getLookupType(anyString());
        verify(lookupService).getActiveLookup(anyString(), anyInt(), anyInt(), any(CmdbFilter.class));
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testReadAll_isNotBlankTrue() {
        System.out.println("readAll_isNotBlankTrue");

        //arrange:
        when(objectTranslationService.translateLookupDescriptionSafe(anyString(), anyString(), anyString())).thenReturn("examp_description_translation");
        when(lookupService.getLookupType(anyLong())).thenReturn(lookupType);
        when(lookupService.getLookupType(anyString())).thenReturn(lookupType);
        when(lookupService.getDistinctActiveLookup(anyString(), anyInt(), anyInt(), anyString(), anyString())).thenReturn(mockBuildPagedElementsLookupValue());

        //act:
        Object resultObject = instance.readAll(A_LOOKUP_TYPE_ID, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_EMPTY_FILTER, "exampForClass", "exampForAttr");

        //assert:
        verify(objectTranslationService).translateLookupDescriptionSafe(anyString(), anyString(), anyString());
        verify(lookupService).getLookupType(anyLong());
        verify(lookupService).getLookupType(anyString());
        verify(lookupService).getDistinctActiveLookup(anyString(), anyInt(), anyInt(), anyString(), anyString());
        checkListIds(expListIds, resultObject);
    }
}
