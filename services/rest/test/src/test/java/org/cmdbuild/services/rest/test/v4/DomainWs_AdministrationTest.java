/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper;
import org.cmdbuild.service.rest.v4.command.DomainWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DomainWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildClasse;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildDomainsList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsDomainData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.*;

public class DomainWs_AdministrationTest extends WsTestBase {

    private final DomainWs_Administration instance;

    private final List<Domain> domainList;
    private final DomainSerializationHelper.WsDomainData data;
    private final Classe classe;

    public DomainWs_AdministrationTest() {

        DomainSerializationHelper domainSerializationHelper = mockBuildDomainSerializationHelper();
        DomainWsCommand command = new DomainWsCommand(daoService, domainRepository, domainSerializationHelper);
        instance = new DomainWs_Administration(domainService, domainSerializationHelper, command);
        domainList = mockBuildDomainsList();
        data = mockBuildWsDomainData();
        classe = mockBuildClasse(A_RANDOM_ID, A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testReadAll_hasFilter_includeFullDetails() {
        System.out.println("readAll_hasFilter_includeFullDetails");

        //arrange:
        List<String> expListNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
        when(domainService.getUserDomainsAsAdmin()).thenReturn(domainList);

        //act:
        Object resultObject = instance.readAll(A_TEST_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, DETAILED);

        //assert:
        verify(domainService).getUserDomainsAsAdmin();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_NothasFilter_includeFullDetails() {
        System.out.println("readAll_NothasFilter_includeFullDetails");

        //arrange:
        List<String> expListNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
        when(domainService.getUserDomainsAsAdmin()).thenReturn(domainList);

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, DETAILED);

        //assert:
        verify(domainService).getUserDomainsAsAdmin();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_hasFilter_NotIncludeFullDetails() {
        System.out.println("readAll_hasFilter_NotIncludeFullDetails");

        //arrange:
        List<String> expListNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
        when(domainService.getUserDomainsAsAdmin()).thenReturn(domainList);

        //act:
        Object resultObject = instance.readAll(A_TEST_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, NOT_DETAILED);

        //assert:
        verify(domainService).getUserDomainsAsAdmin();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_NothasFilter_NotIncludeFullDetails() {
        System.out.println("readAll_NothasFilter_NotIncludeFullDetails");

        //arrange:
        List<String> expListNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
        when(domainService.getUserDomainsAsAdmin()).thenReturn(domainList);

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET, NOT_DETAILED);

        //assert:
        verify(domainService).getUserDomainsAsAdmin();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        String expName = "sourceClass1targetClass1";
        when(domainService.getDomain(A_KNOWN_DOMAIN_NAME1)).thenReturn(domainList.get(0));

        //act:
        Object resultObject = instance.read(A_KNOWN_DOMAIN_NAME1);

        //assert:
        verify(domainService).getDomain(A_KNOWN_DOMAIN_NAME1);
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        String expName = "sourceClass1targetClass1";
        when(daoService.getClasse(anyString())).thenReturn(classe);
        when(domainRepository.createDomain(any(DomainDefinition.class))).thenReturn(domainList.get(0));

        //act:
        Object resultObject = instance.create(data);

        //assert:
        verify(daoService, times(2)).getClasse(anyString());
        verify(domainRepository).createDomain(any(DomainDefinition.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        String expName = "sourceClass2targetClass2";
        when(domainRepository.getDomain(A_KNOWN_DOMAIN_NAME1)).thenReturn(domainList.get(0));
        when(domainRepository.updateDomain(any(DomainDefinition.class))).thenReturn(domainList.get(1));
        when(daoService.getClasse(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.update(A_KNOWN_DOMAIN_NAME1, data);

        //assert:
        verify(domainRepository).getDomain(A_KNOWN_DOMAIN_NAME1);
        verify(domainRepository).updateDomain(any(DomainDefinition.class));
        verify(daoService, times(2)).getClasse(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //arrange:
        when(domainRepository.getDomain(A_KNOWN_DOMAIN_NAME1)).thenReturn(domainList.get(0));

        //act:
        Object resultObject = instance.delete(A_KNOWN_DOMAIN_NAME1);

        //assert:
        verify(domainRepository).getDomain(A_KNOWN_DOMAIN_NAME1);
        verify(domainRepository).deleteDomain(any(Domain.class));
        checkSuccess(resultObject);
    }
}
