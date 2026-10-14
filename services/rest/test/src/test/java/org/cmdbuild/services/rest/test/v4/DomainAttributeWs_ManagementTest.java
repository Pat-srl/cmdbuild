/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.v4.command.DomainAttributeWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DomainAttributeWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListIds;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkName;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildClasseWithAttr;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildDomainWithAttributes;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DomainAttributeWs_ManagementTest extends WsTestBase {

    private final DomainAttributeWs_Management instance;

    private final Classe sourceClass;
    private final Domain domain1;

    public DomainAttributeWs_ManagementTest() {
        AttributeTypeConversionService attributeTypeConversionService = mockBuildAttributeTypeConversionService();

        DomainAttributeWsCommand command = new DomainAttributeWsCommand(daoService);
        instance = new DomainAttributeWs_Management(attributeTypeConversionService, command);

        sourceClass = mockBuildClasseWithAttr(A_SOURCE_CLASS_NAME1);
        domain1 = mockBuildDomainWithAttributes(A_KNOWN_DOMAIN_NAME1, A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_ATTR_NAME1);

    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        List<String> expListNames = list(A_KNOWN_ATTR_NAME1, A_KNOWN_DOMAIN_NAME1);
        when(daoService.getDomain(A_KNOWN_DOMAIN_NAME1)).thenReturn(domain1);
        when(daoService.getClasse(anyString())).thenReturn(sourceClass);

        //act:
        Object resultObject = instance.readAll(A_KNOWN_DOMAIN_NAME1, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(daoService).getDomain(A_KNOWN_DOMAIN_NAME1);
        verify(daoService).getClasse(anyString());
        checkListIds(expListNames, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        String expName = A_KNOWN_ATTR_NAME1;
        when(daoService.getDomain(A_KNOWN_DOMAIN_NAME1)).thenReturn(domain1);

        //act:
        Object resultObject = instance.read(A_KNOWN_DOMAIN_NAME1, A_KNOWN_ATTR_NAME1);

        //assert:
        verify(daoService).getDomain(A_KNOWN_DOMAIN_NAME1);
        checkName(expName, resultObject);
    }
}
