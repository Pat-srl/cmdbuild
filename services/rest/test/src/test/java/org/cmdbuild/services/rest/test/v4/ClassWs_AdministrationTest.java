/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.ExtendedClassDefinition;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ClassWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ClassWs_Administration;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsClassData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class ClassWs_AdministrationTest extends WsTestBase {

    private final ClassWs_Administration instance;

    private final Classe sourceClass1;
    private final Classe targetClass1;
    private final Classe anotherClass;
    private final List<Classe> listClasse;
    private final ExtendedClass extendedClass1;
    private final ExtendedClass extendedClass2;
    private final ExtendedClass extendedClass3;
    private final ClassSerializationHelper.WsClassData data;
    private final Widget widget;
    private final PagedElements<LookupValue> lookupValuePagedElements;

    public ClassWs_AdministrationTest() throws JsonProcessingException {
        ClassSerializationHelper classSerializationHelper = mockBuildClassSerializationHelper();
        ClassWsCommand command = new ClassWsCommand(userClassService, classSerializationHelper);
        instance = new ClassWs_Administration(userClassService, classSerializationHelper, command);

        sourceClass1 = mockBuildClasse(1L, A_SOURCE_CLASS_NAME1);
        targetClass1 = mockBuildClasse(2L, A_TARGET_CLASS_NAME1);
        anotherClass = mockBuildClasse(3L, A_DIFFERENT_CLASS_NAME);
        listClasse = list(sourceClass1, targetClass1, anotherClass);
        extendedClass1 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME1);
        extendedClass2 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME2);
        extendedClass3 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME3);
        data = mockBuildWsClassData();  // throws JsonProcessingException
        widget = mockBuildWidget();
        lookupValuePagedElements = mockBuildPagedElementsLookupValue();

    }

    @Test
    public void testReadAll_Detailed_IncludeValues() {
        System.out.println("readAll_Detailed_IncludeValues");

        //arrange:
        List<String> expListNames = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_CLASS_NAME3);
        when(userClassService.getAllUserClasses()).thenReturn(listClasse);
        when(userClassService.getExtendedClass(sourceClass1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES)).thenReturn(extendedClass1);
        when(userClassService.getExtendedClass(targetClass1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES)).thenReturn(extendedClass2);
        when(userClassService.getExtendedClass(anotherClass, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES)).thenReturn(extendedClass3);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.readAll(DETAILED, INCLUDE, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_NULL_FILTER);

        //assert:
        verify(userClassService).getAllUserClasses();
        verify(userClassService).getExtendedClass(sourceClass1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES);
        verify(userClassService).getExtendedClass(targetClass1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES);
        verify(userClassService).getExtendedClass(anotherClass, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES);
        verify(widgetService, times(3)).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService, times(3)).getAllLookup(anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_NonDetailed_IncludeValues() {
        System.out.println("readAll_NonDetailed_IncludeValues");

        //arrange:
        List<String> expListNames = list(A_SOURCE_CLASS_NAME1, A_TARGET_CLASS_NAME1, A_DIFFERENT_CLASS_NAME);
        when(userClassService.getAllUserClasses()).thenReturn(listClasse);

        //act:
        Object resultObject = instance.readAll(NOT_DETAILED, INCLUDE, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_NULL_FILTER);

        //assert:
        verify(userClassService).getAllUserClasses();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_Detailed_NonIncludeValues() {
        System.out.println("readAll_Detailed_NonIncludeValues");

        //arrange:
        List<String> expListNames = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_CLASS_NAME3);
        when(userClassService.getAllUserClasses()).thenReturn(listClasse);
        when(userClassService.getExtendedClass(sourceClass1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER)).thenReturn(extendedClass1);
        when(userClassService.getExtendedClass(targetClass1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER)).thenReturn(extendedClass2);
        when(userClassService.getExtendedClass(anotherClass, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER)).thenReturn(extendedClass3);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.readAll(DETAILED, DONT_INCLUDE, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_NULL_FILTER);

        //assert:
        verify(userClassService).getAllUserClasses();
        verify(userClassService).getExtendedClass(sourceClass1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER);
        verify(userClassService).getExtendedClass(targetClass1, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER);
        verify(userClassService).getExtendedClass(anotherClass, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER);
        verify(widgetService, times(3)).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService, times(3)).getAllLookup(anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testRead_IncludeValues() {
        System.out.println("read_IncludeValues");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(userClassService.getExtendedClass(A_KNOWN_CLASS_ID, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, CQ_INCLUDE_LOOKUP_VALUES)).thenReturn(extendedClass1);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.read(A_KNOWN_CLASS_ID, INCLUDE);

        //assert:
        verify(userClassService).getExtendedClass(eq(A_KNOWN_CLASS_ID), eq(CQ_INCLUDE_INACTIVE_ELEMENTS), eq(CQ_FOR_USER), eq(CQ_INCLUDE_LOOKUP_VALUES));
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testRead_NonIncludeValues() {
        System.out.println("read_NonIncludeValues");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(userClassService.getExtendedClass(A_KNOWN_CLASS_ID, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, null)).thenReturn(extendedClass1);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.read(A_KNOWN_CLASS_ID, DONT_INCLUDE);

        //assert:
        verify(userClassService).getExtendedClass(eq(A_KNOWN_CLASS_ID), eq(CQ_INCLUDE_INACTIVE_ELEMENTS), eq(CQ_FOR_USER), eq(null));
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate() throws JsonProcessingException {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(userClassService.createClass(any(ExtendedClassDefinition.class))).thenReturn(extendedClass1);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.create(data);

        //assert:
        verify(userClassService).createClass(any(ExtendedClassDefinition.class));
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate() throws JsonProcessingException {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(userClassService.updateClass(any(ExtendedClassDefinition.class))).thenReturn(extendedClass1);
        when(userClassService.getUserClass(A_KNOWN_CLASS_ID)).thenReturn(anotherClass);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.update(A_KNOWN_CLASS_ID, data);

        //assert:
        verify(userClassService).updateClass(any(ExtendedClassDefinition.class));
        verify(userClassService).getUserClass(A_KNOWN_CLASS_ID);
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_CLASS_NAME1);

        //assert:
        verify(userClassService).deleteClass(A_KNOWN_CLASS_NAME1);
        checkSuccess(resultObject);
    }
}
