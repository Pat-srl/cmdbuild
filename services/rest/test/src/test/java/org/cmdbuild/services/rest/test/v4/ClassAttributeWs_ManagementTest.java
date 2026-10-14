/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.v4.command.ClassAttributeWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ClassAttributeWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkName;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author ldare
 */
public class ClassAttributeWs_ManagementTest extends WsTestBase {

    private final ClassAttributeWs_Management instance;

    private final Attribute attr1;
    private final Attribute attr2;
    private final Attribute attr3;
    private final Classe classe;

    public ClassAttributeWs_ManagementTest() {
        AttributeTypeConversionService attributeTypeConversionService = mockBuildAttributeTypeConversionService();

        ClassAttributeWsCommand command = new ClassAttributeWsCommand(userClassService);
        instance = new ClassAttributeWs_Management(userClassService, attributeTypeConversionService, command);

        List<AttributeWithoutOwner> listAttributesWO = list(
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME1),
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME2),
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME3)
        );
        classe = mockBuildClasse(A_KNOWN_CLASS_ID, emptyList(), listAttributesWO);
        attr1 = mockBuildAttribute(A_KNOWN_ATTR_NAME1, classe);
        attr2 = mockBuildAttribute(A_KNOWN_ATTR_NAME2, classe);
        attr3 = mockBuildAttribute(A_KNOWN_ATTR_NAME3, classe);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        String expName = A_KNOWN_ATTR_NAME1;
        when(userClassService.getUserAttribute(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_NAME1)).thenReturn(attr1);

        //act:
        Object resultObject = instance.read(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_NAME1);

        //assert:
        verify(userClassService).getUserAttribute(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_NAME1);
        checkName(expName, resultObject);
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        List<String> expListNames = list(A_KNOWN_ATTR_NAME1, A_KNOWN_ATTR_NAME2, A_KNOWN_ATTR_NAME3);
        List<Attribute> listAttributes = list(attr1, attr2, attr3);
        when(userClassService.getActiveUserAttributes(A_KNOWN_CLASS_ID)).thenReturn(listAttributes);

        //act:
        Object resulObject = instance.readAll(A_KNOWN_CLASS_ID, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(userClassService).getActiveUserAttributes(A_KNOWN_CLASS_ID);
        checkListNames(expListNames, resulObject);
    }
}
