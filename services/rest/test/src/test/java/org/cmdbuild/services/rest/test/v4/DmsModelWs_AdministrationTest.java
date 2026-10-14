/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassDefinition;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.service.rest.v4.command.DmsModelWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DmsModelWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_INACTIVE_ELEMENTS;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_DMSMODEL;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_PROCESS;
import static org.cmdbuild.services.rest.test.common.ClasseMatcher.matchClasse;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsAttributeData;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsClassData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class DmsModelWs_AdministrationTest extends WsTestBase {

    private final DmsModelWs_Administration instance;
    private final ClassSerializationHelper classSerializationHelper;
    private final AttributeTypeConversionService attributeTypeConversionService;

    private final Classe classeActive1;
    private final Classe classeActive2;
    private final Classe classeInactive;
    private final Classe invalidClass;
    private final List<Classe> listClasse;
    private final ExtendedClass extendedClass1;
    private final ExtendedClass extendedClass2;
    private final ExtendedClass extendedClass3;
    private final WsClassData wsClassData;
    private final WsAttributeData wsAttributeData;
    private final Widget widget;
    private final PagedElements<LookupValue> pagedLookupValue;
    private final Attribute attribute;
    private final List<String> attrOrder;


    public DmsModelWs_AdministrationTest() throws JsonProcessingException {

        attributeTypeConversionService = mockBuildAttributeTypeConversionService();
        classSerializationHelper = mockBuildClassSerializationHelper();
        DmsModelWsCommand command = new DmsModelWsCommand(daoService, userClassService, classSerializationHelper, sysReportService);
        instance = new DmsModelWs_Administration(classSerializationHelper, attributeTypeConversionService, command);

        classeActive1 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME1, CS_DMSMODEL);
        classeActive2 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME2, CS_DMSMODEL);
        classeInactive = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME3, CS_DMSMODEL);
        invalidClass = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME1, CS_PROCESS);
        listClasse = list(classeActive1, classeActive2, classeInactive, invalidClass);
        extendedClass1 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME1);
        extendedClass2 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME2);
        extendedClass3 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME3);
        wsClassData = mockBuildWsClassData(); // can throw JsonProcessingException
        wsAttributeData = mockBuildWsAttributeData(); // can throw JsonProcessingException
        widget = mockBuildWidget();
        pagedLookupValue = mockBuildPagedElementsLookupValue();
        attribute = mockBuildAttribute(A_KNOWN_ATTR_NAME1, classeActive1);
        attrOrder = list(A_KNOWN_ATTR_NAME3, A_KNOWN_ATTR_NAME2, A_KNOWN_ATTR_NAME1);
    }

    @Test
    public void testReadAll_Detailed() {
        System.out.println("readAll_Detailed");

        //arrange:
        List<String> expListName = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_CLASS_NAME3);
        when(daoService.getAllClasses()).thenReturn(listClasse);
        when(userClassService.getExtendedClass(matchClasse(classeActive1))).thenReturn(extendedClass1);
        when(userClassService.getExtendedClass(matchClasse(classeActive2))).thenReturn(extendedClass2);
        when(userClassService.getExtendedClass(matchClasse(classeInactive))).thenReturn(extendedClass3);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(pagedLookupValue);

        //act:
        Object resultObject = instance.readAll(DETAILED, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_EMPTY_FILTER);

        //assert:
        verify(daoService).getAllClasses();
        verify(userClassService).getExtendedClass(matchClasse(classeActive1));
        verify(userClassService).getExtendedClass(matchClasse(classeInactive));
        verify(userClassService).getExtendedClass(matchClasse(classeActive2));
        verify(widgetService, times(3)).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService, times(3)).getAllLookup(anyString());
        checkListNames(expListName, resultObject);
    }

    @Test
    public void testReadAll_notDetailed() {
        System.out.println("readAll_notDetailed");

        //arrange:
        List<String> expListName = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_CLASS_NAME3);
        when(daoService.getAllClasses()).thenReturn(listClasse);

        //act:
        Object resultObject = instance.readAll(NOT_DETAILED, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_EMPTY_FILTER);

        //assert:
        verify(daoService).getAllClasses();
        checkListNames(expListName, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);
        when(userClassService.getExtendedClass(classeActive1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER)).thenReturn(extendedClass1);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(pagedLookupValue);

        //act:
        Object resultObj = instance.read(A_KNOWN_CLASS_NAME1);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        verify(userClassService).getExtendedClass(classeActive1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER);
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObj);
    }

    @Test
    public void testRead_invalidClass() {
        System.out.println("read_invalidClass");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(invalidClass);

        //act:
        try {
            instance.read(A_KNOWN_CLASS_NAME1);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(daoService.getClasse(anyString())).thenReturn(classeActive1);
        when(userClassService.createClass(any(ExtendedClassDefinition.class))).thenReturn(extendedClass1);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(pagedLookupValue);

        //act
        Object resultObject = instance.create(wsClassData);

        //assert:
        verify(daoService).getClasse(anyString());
        verify(userClassService).createClass(any(ExtendedClassDefinition.class));
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate_invalidClass() throws JsonProcessingException {
        System.out.println("create_invalidClass");

        //arrange:
        when(daoService.getClasse(anyString())).thenReturn(invalidClass);

        //act:
        try {
            instance.create(wsClassData);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(anyString());
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);
        when(userClassService.updateClass(any(ExtendedClassDefinition.class))).thenReturn(extendedClass1);
        when(userClassService.getUserClass(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(pagedLookupValue);

        //act:
        Object resultObject = instance.update(A_KNOWN_CLASS_NAME1, wsClassData);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        verify(userClassService).updateClass(any(ExtendedClassDefinition.class));
        verify(userClassService).getUserClass(A_KNOWN_CLASS_NAME1);
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate_invalidClass() {
        System.out.println("update_invalidClass");

        //arrange:
        when(daoService.getClasse(anyString())).thenReturn(invalidClass);

        //act:
        try {
            instance.update(A_KNOWN_CLASS_NAME1, wsClassData);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(anyString());
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);

        //act:
        Object resultObject = instance.delete(A_KNOWN_CLASS_NAME1);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        checkSuccess(resultObject);
    }

    @Test
    public void testDelete_invalidClass() {
        System.out.println("delete_invalidClass");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(invalidClass);

        //act:
        try {
            instance.delete(A_KNOWN_CLASS_NAME1);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testReadAttribute() {
        System.out.println("readAttribute");

        //arrange:
        String expName = A_KNOWN_ATTR_NAME1;
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);

        //act:
        Object resultObject = instance.readAttribute(A_KNOWN_CLASS_NAME1, A_KNOWN_ATTR_NAME1);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        checkName(expName, resultObject);
    }

    @Test
    public void testReadAttribute_invalidClass() {
        System.out.println("readAttribute_invalidClass");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(invalidClass);

        //act:
        try {
            instance.readAttribute(A_KNOWN_CLASS_NAME1, A_KNOWN_ATTR_NAME1);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testReadAllAttributes() {
        System.out.println("readAllAttributes");

        //arrange:
        List<String> listExpNames = list(A_KNOWN_ATTR_NAME1, A_KNOWN_ATTR_NAME2, A_KNOWN_ATTR_NAME3);
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);

        //act:
        Object resultObject = instance.readAllAttributes(A_KNOWN_CLASS_NAME1, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        checkListNames(listExpNames, resultObject);

    }

    @Test
    public void testReadAllAttributes_invalidClass() {
        System.out.println("readAllAttributes_invalidClass");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(invalidClass);

        //act:
        try {
            instance.readAllAttributes(A_KNOWN_CLASS_NAME1, A_TEST_LIMIT, A_TEST_OFFSET);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testCreateAttribute() {
        System.out.println("createAttribute");

        //arrange:
        String expName = A_KNOWN_ATTR_NAME1;
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);
        when(userClassService.createAttribute(any(Attribute.class))).thenReturn(attribute);

        //act:
        Object resultObject = instance.createAttribute(A_KNOWN_CLASS_NAME1, wsAttributeData);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        verify(userClassService).createAttribute(any(Attribute.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testCreateAttribute_invalidClass() {
        System.out.println("createAttribute_invalidClass");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(invalidClass);

        //act:
        try {
            instance.createAttribute(A_KNOWN_CLASS_NAME1, wsAttributeData);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testUpdateAttributes() {
        System.out.println("updateAttributes");

        //arrange:
        String expName = A_KNOWN_ATTR_NAME1;
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);
        when(userClassService.updateAttribute(any(Attribute.class))).thenReturn(attribute);

        //act:
        Object resultObject = instance.updateAttributes(A_KNOWN_CLASS_NAME1, A_KNOWN_ATTR_NAME1, wsAttributeData);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        verify(userClassService).updateAttribute(any(Attribute.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdateAttributes_invalidClass() {
        System.out.println("updateAttributes_invalidClass");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(invalidClass);

        //act:
        try {
            instance.updateAttributes(A_KNOWN_CLASS_NAME1, A_KNOWN_ATTR_NAME1, wsAttributeData);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testDeleteAttributes() {
        System.out.println("deleteAttributes");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);

        //act:
        Object resultObject = instance.deleteAttributes(A_KNOWN_CLASS_NAME1, A_KNOWN_ATTR_NAME1);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        verify(userClassService).deleteAttribute(A_KNOWN_CLASS_NAME1, A_KNOWN_ATTR_NAME1);
        checkSuccess(resultObject);
    }

    @Test
    public void testDeleteAttributes_invalidClass() {
        System.out.println("deleteAttributes_invalidClass");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(invalidClass);

        //act:
        try {
            instance.deleteAttributes(A_KNOWN_CLASS_NAME1, A_KNOWN_ATTR_NAME1);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testReorderAttributes() {
        System.out.println("reorderAttributes");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);

        //act:
        Object resultObject = instance.reorderAttributes(A_KNOWN_CLASS_NAME1, attrOrder);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        verify(userClassService).updateAttributes(anyList());
        checkListNames(attrOrder, resultObject);
    }

    @Test
    public void testReorderAttributes_invalidClass() {
        System.out.println("reorderAttributes_invalidClass");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(invalidClass);

        //act:
        try {
            instance.reorderAttributes(A_KNOWN_CLASS_NAME1, attrOrder);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
    }

    @Test
    public void testPrintModelSchemaReport() {
        System.out.println("printModelSchemaReport");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);

        //act:
        // the return statement on the entrypoint tested returns a call to a mocked service
        // so we just assert the fact of it being called
        instance.printModelSchemaReport(A_KNOWN_CLASS_NAME1, A_KNOWN_FILE_NAME, A_CSV_REPORT_EXTENSION);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        verify(sysReportService).executeClassSchemaReport(eq(classeActive1), any(ReportFormat.class));
    }

    @Test
    public void testPrintModelSchemaReport_invalidClass() {
        System.out.println("printModelSchemaReport_invalidClass");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(invalidClass);

        //act:
        try {
            instance.printModelSchemaReport(A_KNOWN_CLASS_NAME1, A_KNOWN_FILE_NAME, A_CSV_REPORT_EXTENSION);
        } catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
    }
}