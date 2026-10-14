/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.service.rest.v4.command.DomainAttributeWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DomainAttributeWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsAttributeData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class DomainAttributeWs_AdministrationTest extends WsTestBase {

    private final DomainAttributeWs_Administration instance;

    private final Classe sourceClass;
    private final Domain domain1;
    private final Domain domain2;
    private final Attribute attrWithForeignKey;
    private final WsAttributeData wsAttributeData;

    public DomainAttributeWs_AdministrationTest() throws JsonProcessingException {
        AttributeTypeConversionService attributeTypeConversionService = mockBuildAttributeTypeConversionService();

        DomainAttributeWsCommand command = new DomainAttributeWsCommand(daoService);
        instance = new DomainAttributeWs_Administration(attributeTypeConversionService, command);

        sourceClass = mockBuildClasseWithAttr(A_SOURCE_CLASS_NAME1);
        domain1 = mockBuildDomainWithAttributes(A_KNOWN_DOMAIN_NAME1, A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_ATTR_NAME1);
        domain2 = mockBuildDomainWithAttributes(A_KNOWN_DOMAIN_NAME2, A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_ATTR_NAME2);

        attrWithForeignKey = mockBuildAttribute_ForeignKeyAttributeType(A_KNOWN_ATTR_NAME1, sourceClass);
        wsAttributeData = mockBuildWsAttributeData(A_KNOWN_ATTR_NAME1);  // throws JsonProcessingException
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

    @Test
    public void testCreate() throws JsonProcessingException {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_ATTR_NAME1;
        when(daoService.getDomain(A_KNOWN_DOMAIN_NAME2)).thenReturn(domain2);
        when(daoService.createAttribute(any(Attribute.class))).thenReturn(attrWithForeignKey);
        when(daoService.getClasse(anyString())).thenReturn(sourceClass);

        //act:
        Object resultObject = instance.create(A_KNOWN_DOMAIN_NAME2, wsAttributeData);

        //assert:
        verify(daoService).getDomain(A_KNOWN_DOMAIN_NAME2);
        verify(daoService).createAttribute(any(Attribute.class));
        verify(daoService).getClasse(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate() throws JsonProcessingException {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_ATTR_NAME1;
        when(daoService.getDomain(A_KNOWN_DOMAIN_NAME1)).thenReturn(domain1);
        when(daoService.updateAttribute(any(Attribute.class))).thenReturn(attrWithForeignKey);
        when(daoService.getClasse(anyString())).thenReturn(sourceClass);

        //act:
        Object resultObject = instance.update(A_KNOWN_DOMAIN_NAME1, A_KNOWN_ATTR_NAME1, wsAttributeData);

        //assert:
        verify(daoService).getDomain(A_KNOWN_DOMAIN_NAME1);
        verify(daoService).updateAttribute(any(Attribute.class));
        verify(daoService).getClasse(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testDelete() throws JsonProcessingException {
        System.out.println("delete");

        //arrange:
        when(daoService.getDomain(A_KNOWN_DOMAIN_NAME1)).thenReturn(domain1);

        //act:
        Object resultObject = instance.delete(A_KNOWN_DOMAIN_NAME1, A_KNOWN_ATTR_NAME1);

        //assert:
        verify(daoService).getDomain(A_KNOWN_DOMAIN_NAME1);
        verify(daoService).deleteAttribute(any(Attribute.class));
        checkSuccess(resultObject);
    }

    @Test
    public void testReorder() {
        System.out.println("reorder");

        //arrange:
        List<String> expListNames = list(A_KNOWN_ATTR_NAME1, A_KNOWN_DOMAIN_NAME1);
        when(daoService.getDomain(A_KNOWN_DOMAIN_NAME1)).thenReturn(domain1);
        when(daoService.updateAttributes(anyList())).thenReturn(list(attrWithForeignKey));
        when(daoService.getClasse(anyString())).thenReturn(sourceClass);

        //act:
        Object resultObject = instance.reorder(A_KNOWN_DOMAIN_NAME1, list(A_KNOWN_ATTR_NAME1, A_KNOWN_DOMAIN_NAME1));

        //assert:
        verify(daoService, times(2)).getDomain(A_KNOWN_DOMAIN_NAME1);
        verify(daoService).updateAttributes(anyList());
        verify(daoService).getClasse(anyString());
        checkListNames(expListNames, resultObject);
    }
}
