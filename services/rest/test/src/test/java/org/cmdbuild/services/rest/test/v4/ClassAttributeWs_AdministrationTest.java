/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.service.rest.v4.command.ClassAttributeWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ClassAttributeWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsAttributeData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class ClassAttributeWs_AdministrationTest extends WsTestBase {

    private final ClassAttributeWs_Administration instance;

    private final Attribute attr1;
    private final Attribute attr2;
    private final Attribute attr3;
    private final Classe classe;

    public ClassAttributeWs_AdministrationTest() {
        AttributeTypeConversionService attributeTypeConversionService = mockBuildAttributeTypeConversionService();

        ClassAttributeWsCommand command = new ClassAttributeWsCommand(userClassService);
        instance = new ClassAttributeWs_Administration(userClassService, attributeTypeConversionService, command);

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
        when(userClassService.getUserAttributes(A_KNOWN_CLASS_ID)).thenReturn(listAttributes);

        //act:
        Object resulObject = instance.readAll(A_KNOWN_CLASS_ID, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(userClassService).getUserAttributes(A_KNOWN_CLASS_ID);
        checkListNames(expListNames, resulObject);
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_ATTR_NAME1;
        WsAttributeData wsAttributeData = mockBuildWsAttributeData(A_KNOWN_ATTR_NAME1);
        when(userClassService.getUserClass(A_KNOWN_CLASS_ID)).thenReturn(classe);
        when(userClassService.createAttribute(matchAttribute(attr1))).thenReturn(attr1);

        //act:
        Object resulObject = instance.create(A_KNOWN_CLASS_ID, wsAttributeData);

        //assert:
        verify(userClassService).getUserClass(A_KNOWN_CLASS_ID);
        verify(userClassService).createAttribute(matchAttribute(attr1));
        checkName(expName, resulObject);
    }

    @Test
    public void testUpdate() throws JsonProcessingException {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_ATTR_NAME1;
        WsAttributeData wsAttributeData = mockBuildWsAttributeData(A_KNOWN_ATTR_NAME1);
        when(userClassService.getUserClass(A_KNOWN_CLASS_ID)).thenReturn(classe);
        when(userClassService.updateAttribute(matchAttribute(attr1))).thenReturn(attr1);

        //act:
        Object resulObject = instance.update(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID,
                wsAttributeData);

        //assert:
        verify(userClassService).getUserClass(A_KNOWN_CLASS_ID);
        verify(userClassService).updateAttribute(matchAttribute(attr1));
        checkName(expName, resulObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);

        //assert:
        verify(userClassService).deleteAttribute(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);
        checkSuccess(resultObject);
    }

    @Test
    public void testReorder() {
        System.out.println("reorder");

        //arrange:
        List<String> expListNames = list(attr2.getName(), attr1.getName(), attr3.getName());
        when(userClassService.getUserClass(A_KNOWN_CLASS_ID)).thenReturn(classe);

        //act:
        Object resultObject = instance.reorder(A_KNOWN_CLASS_ID, expListNames);

        //assert:
        verify(userClassService, times(2)).getUserClass(A_KNOWN_CLASS_ID);
        checkListNames(expListNames, resultObject);
    }

    private static Attribute matchAttribute(Attribute expAttribute) {
        return argThat(new MyAttributeMatcher(expAttribute));
    }
}

class MyAttributeMatcher extends ArgumentMatcher<Attribute> {

    private final Attribute expLeft;

    MyAttributeMatcher(Attribute expLeft) {
        this.expLeft = expLeft;
    }

    @Override
    public boolean matches(Object obj) {
        Attribute actualRight = (Attribute) obj;
        return Objects.equals(expLeft.getName(), actualRight.getName());
    }
}
