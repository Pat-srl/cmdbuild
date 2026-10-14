/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper;
import org.cmdbuild.service.rest.v4.command.DomainWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DomainWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkName;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildDomainsList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DomainWs_ManagementTest extends WsTestBase {

    private final DomainWs_Management instance;

    private final List<Domain> domainList;

    public DomainWs_ManagementTest() {

        DomainSerializationHelper domainSerializationHelper = mockBuildDomainSerializationHelper();
        DomainWsCommand command = new DomainWsCommand(daoService, domainRepository, domainSerializationHelper);
        instance = new DomainWs_Management(domainService, domainSerializationHelper, command);
        domainList = mockBuildDomainsList();
    }

    @Test
    public void testReadAll_hasFilter_includeFullDetails() {
        System.out.println("readAll_hasFilter_includeFullDetails");

        //arrange:
        List<String> expListNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
        when(domainService.getActiveUserDomains()).thenReturn(domainList);

        //act:
        Object resultObject = instance.readAll(A_TEST_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, DETAILED);

        //assert:
        verify(domainService).getActiveUserDomains();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_NothasFilter_includeFullDetails() {
        System.out.println("readAll_NothasFilter_includeFullDetails");

        //arrange:
        List<String> expListNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
        when(domainService.getActiveUserDomains()).thenReturn(domainList);

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, DETAILED);

        //assert:
        verify(domainService).getActiveUserDomains();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_hasFilter_NotIncludeFullDetails() {
        System.out.println("readAll_hasFilter_NotIncludeFullDetails");

        //arrange:
        List<String> expListNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
        when(domainService.getActiveUserDomains()).thenReturn(domainList);

        //act:
        Object resultObject = instance.readAll(A_TEST_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, NOT_DETAILED);

        //assert:
        verify(domainService).getActiveUserDomains();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_NothasFilter_NotIncludeFullDetails() {
        System.out.println("readAll_UserViewMode_NothasFilter_NotIncludeFullDetails");

        //arrange:
        List<String> expListNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
        when(domainService.getActiveUserDomains()).thenReturn(domainList);

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, NOT_DETAILED);

        //assert:
        verify(domainService).getActiveUserDomains();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        String expName = "sourceClass2targetClass2";
        when(domainService.getUserDomain(A_KNOWN_DOMAIN_NAME1)).thenReturn(domainList.get(1));

        //act:
        Object resultObject = instance.read(A_KNOWN_DOMAIN_NAME1);

        //assert:
        verify(domainService).getUserDomain(A_KNOWN_DOMAIN_NAME1);
        checkName(expName, resultObject);
    }
}
