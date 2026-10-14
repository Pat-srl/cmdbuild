/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.v4.command.DmsCategoryValueWsCommand;
import org.cmdbuild.service.rest.v4.command.LookupValueWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DmsCategoryValueWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.lookup.LookupSpeciality.LS_DMSCATEGORY;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkCode;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListIds;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class DmsCategoryValueWs_ManagementTest extends WsTestBase {

    private final DmsCategoryValueWs_Management instance;

    private final String expFieldDescr_Transl = "my_description_translation";

    private final LookupValue lookupValue;
    private final LookupType lookupType;

    public DmsCategoryValueWs_ManagementTest() {
        LookupValueWsCommand lookupValueWsCommand = new LookupValueWsCommand(lookupService);
        DmsCategoryValueWsCommand command = new DmsCategoryValueWsCommand(lookupService, objectTranslationService, lookupValueWsCommand);
        instance = new DmsCategoryValueWs_Management(objectTranslationService, lookupService, command);
        lookupValue = mockBuildLookupValue(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LOOKUP_ID1);
        lookupType = mockBuildLookupType(A_KNOWN_LOOKUP_NAME1);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        String expName = A_KNOWN_LOOKUP_NAME1;
        when(lookupService.getLookup(A_KNOWN_LOOKUP_ID1)).thenReturn(lookupValue);
        when(lookupService.getLookupType(anyLong())).thenReturn(lookupType);
        when(lookupService.getLookupType(anyString())).thenReturn(lookupType);
        when(objectTranslationService.translateLookupDescriptionSafe(anyString(), anyString(), anyString())).thenReturn(expFieldDescr_Transl);

        //act:
        Object resultObject = instance.read(A_KNOWN_LOOKUP_ID1);

        //assert:
        verify(lookupService).getLookup(A_KNOWN_LOOKUP_ID1);
        verify(lookupService).getLookupType(anyLong());
        verify(lookupService).getLookupType(anyString());
        verify(objectTranslationService).translateLookupDescriptionSafe(anyString(), anyString(), anyString());
        checkCode(expName, resultObject);
    }

    @Test
    public void testReadAll_lookupTypeId_ALL() {
        System.out.println("readAll_lookupTypeId_ALL");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_LOOKUP_ID1, A_KNOWN_LOOKUP_ID1, A_KNOWN_LOOKUP_ID1);
        List<LookupType> listLookupType = mockBuildLookupTypeList(LS_DMSCATEGORY);
        when(lookupService.getAllTypes(anyString())).thenReturn(listLookupType);
        when(lookupService.getActiveLookup(anyString(), anyInt(), anyInt(), any(CmdbFilter.class))).thenReturn(mockBuildPagedElementsLookupValue());
        when(lookupService.getLookupType(anyLong())).thenReturn(lookupType);
        when(lookupService.getLookupType(anyString())).thenReturn(lookupType);
        when(objectTranslationService.translateLookupDescriptionSafe(anyString(), anyString(), anyString())).thenReturn(expFieldDescr_Transl);

        //act:
        Object resultObject = instance.readAll(A_KNOWN_ALL_ID, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_EMPTY_FILTER);

        //assert:
        verify(lookupService).getAllTypes(anyString());
        verify(lookupService, times(3)).getActiveLookup(anyString(), anyInt(), anyInt(), any(CmdbFilter.class));
        verify(lookupService, times(3)).getLookupType(anyLong());
        verify(lookupService, times(3)).getLookupType(anyString());
        verify(objectTranslationService, times(3)).translateLookupDescriptionSafe(anyString(), anyString(), anyString());
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testReadAll_lookupTypeId_NotALL() {
        System.out.println("readAll_lookupTypeId_NotALL");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_LOOKUP_ID1);
        when(lookupService.getActiveLookup(anyString(), anyInt(), anyInt(), any(CmdbFilter.class))).thenReturn(mockBuildPagedElementsLookupValue());
        when(lookupService.getLookupType(anyLong())).thenReturn(lookupType);
        when(lookupService.getLookupType(anyString())).thenReturn(lookupType);
        when(objectTranslationService.translateLookupDescriptionSafe(anyString(), anyString(), anyString())).thenReturn(expFieldDescr_Transl);

        //act:
        Object resultObject = instance.readAll(A_KNOWN_NOTALL_ID, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_EMPTY_FILTER);

        //assert:
        verify(lookupService).getActiveLookup(anyString(), anyInt(), anyInt(), any(CmdbFilter.class));
        verify(lookupService).getLookupType(anyLong());
        verify(lookupService).getLookupType(anyString());
        verify(objectTranslationService).translateLookupDescriptionSafe(anyString(), anyString(), anyString());
        checkListIds(expListIds, resultObject);
    }
}
