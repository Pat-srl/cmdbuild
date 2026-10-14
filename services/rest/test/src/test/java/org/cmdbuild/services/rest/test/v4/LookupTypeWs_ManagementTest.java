package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.service.rest.v4.command.LookupTypeWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.LookupTypeWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.lookup.LookupSpeciality.LS_DEFAULT;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildLookupTypeList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class LookupTypeWs_ManagementTest extends WsTestBase {

    private LookupTypeWs_Management instance;

    private final List<LookupType> lookupTypeList;

    public LookupTypeWs_ManagementTest() {
        LookupTypeWsCommand command = new LookupTypeWsCommand(lookupService);
        this.instance = new LookupTypeWs_Management(lookupService, command);

        this.lookupTypeList = mockBuildLookupTypeList(LS_DEFAULT);
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
}

