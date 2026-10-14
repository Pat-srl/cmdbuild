/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.service.rest.common.serializationhelpers.LookupSerializationHelper;
import org.cmdbuild.service.rest.v4.command.DmsCategoryWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DmsCategoryWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.lookup.LookupSpeciality.LS_DMSCATEGORY;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildLookupTypeList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class DmsCategoryWs_ManagementTest extends WsTestBase {

    private final DmsCategoryWs_Management instance;

    private final List<LookupType> lookupTypeList;

    public DmsCategoryWs_ManagementTest() {
        LookupSerializationHelper lookupSerializationHelper = mockBuildLookupSerializationHelper();
        DmsCategoryWsCommand command = new DmsCategoryWsCommand(lookupService);
        this.instance = new DmsCategoryWs_Management(lookupSerializationHelper, command);

        this.lookupTypeList = mockBuildLookupTypeList(LS_DMSCATEGORY);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        when(lookupService.getLookupType(anyString())).thenReturn(lookupTypeList.get(0));
        when(lookupService.getLookupType(anyLong())).thenReturn(lookupTypeList.get(1));

        //act:
        Object resultObject = instance.read(A_KNOWN_LOOKUP_NAME1);

        //assert:
        verify(lookupService).getLookupType(anyString());
        verify(lookupService).getLookupType(anyLong());
        checkResult(A_KNOWN_LOOKUP_NAME1, resultObject, "_id");
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        when(lookupService.getAllTypes(A_TEST_FILTER)).thenReturn(lookupTypeList);
        when(lookupService.getLookupType(anyLong())).thenReturn(lookupTypeList.get(1));

        //act:
        Object resultObject = instance.readAll(A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_FILTER);

        //assert:
        verify(lookupService).getAllTypes(A_TEST_FILTER);
        verify(lookupService, times(3)).getLookupType(anyLong());
        checkResultList(list(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LOOKUP_NAME2, A_KNOWN_LOOKUP_NAME3), resultObject, "_id");
    }
}
