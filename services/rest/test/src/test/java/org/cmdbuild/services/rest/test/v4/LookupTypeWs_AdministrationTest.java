package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType;
import org.cmdbuild.service.rest.v4.command.LookupTypeWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.LookupTypeWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.lookup.LookupSpeciality.LS_DEFAULT;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildLookupTypeList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsLookupType;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class LookupTypeWs_AdministrationTest extends WsTestBase {

    private LookupTypeWs_Administration instance;

    private final List<LookupType> lookupTypeList;
    private final WsLookupType wsLookupType;

    public LookupTypeWs_AdministrationTest() {
        LookupTypeWsCommand command = new LookupTypeWsCommand(lookupService);
        this.instance = new LookupTypeWs_Administration(lookupService, command);

        this.lookupTypeList = mockBuildLookupTypeList(LS_DEFAULT);
        when(lookupService.getLookupType("parent")).thenReturn(lookupTypeList.get(0));
        this.wsLookupType = mockBuildWsLookupType(A_KNOWN_NAME1);
    }

    @Test
    public void testRead() {
        System.out.println("testRead");

        //arrange:
        when(lookupService.getLookupType(A_KNOWN_LOOKUP_NAME1)).thenReturn(lookupTypeList.get(0));

        //act:
        Object resultObject = instance.read(A_KNOWN_LOOKUP_NAME1);

        //assert:
        verify(lookupService).getLookupType(A_KNOWN_LOOKUP_NAME1);
        checkResult(A_KNOWN_LOOKUP_NAME1, resultObject, "_id");
    }

    @Test
    public void testReadAll() {
        System.out.println("testReadAll");

        //arrange:
        when(lookupService.getAllTypes(A_TEST_EMPTY_FILTER)).thenReturn(lookupTypeList);

        //act:
        Object resultObject = instance.readAll(A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_EMPTY_FILTER);

        //assert:
        verify(lookupService).getAllTypes(A_TEST_EMPTY_FILTER);
        checkResultList(list(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LOOKUP_NAME2, A_KNOWN_LOOKUP_NAME3), resultObject, "_id");
    }

    @Test
    public void testCreateLookupType() {
        System.out.println("testCreateLookupType");

        //arrange:
        when(lookupService.createLookupType(any(LookupType.class))).thenReturn(lookupTypeList.get(0));

        //act:
        Object resultObject = instance.createLookupType(wsLookupType);

        //assert:
        verify(lookupService).createLookupType(any(LookupType.class));
        checkResult(A_KNOWN_LOOKUP_NAME1, resultObject, "_id");
    }

    @Test
    public void testDeleteLookupType() {
        System.out.println("testDeleteLookupType");

        //act:
        Object resultObject = instance.deleteLookupType(A_KNOWN_LOOKUP_NAME1);

        //assert:
        verify(lookupService).deleteLookupType(A_KNOWN_LOOKUP_NAME1);
        checkSuccess(resultObject);
    }
}

