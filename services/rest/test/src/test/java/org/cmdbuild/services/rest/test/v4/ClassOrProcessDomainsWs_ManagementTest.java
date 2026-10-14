/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ClassOrProcessDomainsWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ClassOrProcessDomainsWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildClasse;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildDomainsList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author ldare
 */
public class ClassOrProcessDomainsWs_ManagementTest extends WsTestBase {

    private final ClassOrProcessDomainsWs_Management instance;

    private final List<Domain> listDomain;
    private final Classe classe;
    private final List<String> expListNames;

    public ClassOrProcessDomainsWs_ManagementTest() {
        DomainSerializationHelper domainSerializationHelper = mockBuildDomainSerializationHelper();
        ClassOrProcessDomainsWsCommand command = new ClassOrProcessDomainsWsCommand();
        instance = new ClassOrProcessDomainsWs_Management(userClassService, userDomainService, domainSerializationHelper, command);

        listDomain = mockBuildDomainsList();
        classe = mockBuildClasse(A_KNOWN_CLASS_ID);
        expListNames = list("sourceClass1targetClass1", "sourceClass2targetClass2");
    }

    @Test
    public void testGetDomains_includeFullDetails() {
        System.out.println("getDomains_includeFullDetails");

        //arrange:
        when(userDomainService.getActiveUserDomainsForClasse(A_KNOWN_CLASS_ID)).thenReturn(listDomain);
        when(userClassService.getUserClass(A_KNOWN_CLASS_ID)).thenReturn(classe);

        //act:
        Object resultObject = instance.getDomains(A_KNOWN_CLASS_ID, DETAILED);

        //assert:
        verify(userDomainService).getActiveUserDomainsForClasse(A_KNOWN_CLASS_ID);
        verify(userClassService).getUserClass(A_KNOWN_CLASS_ID);
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testGetDomains_nonIncludeFullDetails() {
        System.out.println("getDomains_nonIncludeFullDetails");

        //arrange:
        when(userDomainService.getActiveUserDomainsForClasse(A_KNOWN_CLASS_ID)).thenReturn(listDomain);
        when(userClassService.getUserClass(A_KNOWN_CLASS_ID)).thenReturn(classe);

        //act:
        Object resultObject = instance.getDomains(A_KNOWN_CLASS_ID, NOT_DETAILED);

        //assert:
        verify(userDomainService).getActiveUserDomainsForClasse(A_KNOWN_CLASS_ID);
        verify(userClassService).getUserClass(A_KNOWN_CLASS_ID);
        checkListNames(expListNames, resultObject);
    }
}
