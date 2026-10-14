/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ClassWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ClassWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkName;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class ClassWs_ManagementTest extends WsTestBase {

    private final ClassWs_Management instance;

    private final Classe sourceClass1;
    private final Classe targetClass1;
    private final Classe anotherClass;
    private final List<Classe> listClasse;
    private final ExtendedClass extendedClass1;
    private final ExtendedClass extendedClass2;
    private final ExtendedClass extendedClass3;
    private final Widget widget;
    private final PagedElements<LookupValue> lookupValuePagedElements;

    public ClassWs_ManagementTest() {
        ClassSerializationHelper classSerializationHelper = mockBuildClassSerializationHelper();
        ClassWsCommand command = new ClassWsCommand(userClassService, classSerializationHelper);
        instance = new ClassWs_Management(userClassService, classSerializationHelper, command);

        sourceClass1 = mockBuildClasse(1L, A_SOURCE_CLASS_NAME1);
        targetClass1 = mockBuildClasse(2L, A_TARGET_CLASS_NAME1);
        anotherClass = mockBuildClasse(3L, A_DIFFERENT_CLASS_NAME);
        listClasse = list(sourceClass1, targetClass1, anotherClass);
        extendedClass1 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME1);
        extendedClass2 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME2);
        extendedClass3 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME3);
        widget = mockBuildWidget();
        lookupValuePagedElements = mockBuildPagedElementsLookupValue();
    }

    @Test
    public void testReadAll_Detailed_IncludeValues() {
        System.out.println("readAll_Detailed_IncludeValues");

        //arrange:
        List<String> expListNames = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_CLASS_NAME3);
        when(userClassService.getActiveUserClasses()).thenReturn(listClasse);
        when(userClassService.getExtendedClass(sourceClass1, CQ_FOR_USER, CQ_FILTER_DEVICE, CQ_INCLUDE_LOOKUP_VALUES)).thenReturn(extendedClass1);
        when(userClassService.getExtendedClass(targetClass1, CQ_FOR_USER, CQ_FILTER_DEVICE, CQ_INCLUDE_LOOKUP_VALUES)).thenReturn(extendedClass2);
        when(userClassService.getExtendedClass(anotherClass, CQ_FOR_USER, CQ_FILTER_DEVICE, CQ_INCLUDE_LOOKUP_VALUES)).thenReturn(extendedClass3);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.readAll(DETAILED, INCLUDE, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_NULL_FILTER);

        //assert:
        verify(userClassService).getActiveUserClasses();
        verify(userClassService).getExtendedClass(sourceClass1, CQ_FOR_USER, CQ_FILTER_DEVICE, CQ_INCLUDE_LOOKUP_VALUES);
        verify(userClassService).getExtendedClass(targetClass1, CQ_FOR_USER, CQ_FILTER_DEVICE, CQ_INCLUDE_LOOKUP_VALUES);
        verify(userClassService).getExtendedClass(anotherClass, CQ_FOR_USER, CQ_FILTER_DEVICE, CQ_INCLUDE_LOOKUP_VALUES);
        verify(widgetService, times(3)).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService, times(3)).getAllLookup(anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_NonDetailed_IncludeValues() {
        System.out.println("readAll_NonDetailed_IncludeValues");

        //arrange:
        List<String> expListNames = list(A_SOURCE_CLASS_NAME1, A_TARGET_CLASS_NAME1, A_DIFFERENT_CLASS_NAME);
        when(userClassService.getActiveUserClasses()).thenReturn(listClasse);

        //act:
        Object resultObject = instance.readAll(NOT_DETAILED, INCLUDE, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_NULL_FILTER);

        //assert:
        verify(userClassService).getActiveUserClasses();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_Detailed_NonIncludeValues() {
        System.out.println("readAll_Detailed_NonIncludeValues");

        //arrange:
        List<String> expListNames = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2, A_KNOWN_CLASS_NAME3);
        when(userClassService.getActiveUserClasses()).thenReturn(listClasse);
        when(userClassService.getExtendedClass(sourceClass1, CQ_FOR_USER, CQ_FILTER_DEVICE)).thenReturn(extendedClass1);
        when(userClassService.getExtendedClass(targetClass1, CQ_FOR_USER, CQ_FILTER_DEVICE)).thenReturn(extendedClass2);
        when(userClassService.getExtendedClass(anotherClass, CQ_FOR_USER, CQ_FILTER_DEVICE)).thenReturn(extendedClass3);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.readAll(DETAILED, DONT_INCLUDE, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_NULL_FILTER);

        //assert:
        verify(userClassService).getActiveUserClasses();
        verify(userClassService).getExtendedClass(sourceClass1, CQ_FOR_USER, CQ_FILTER_DEVICE);
        verify(userClassService).getExtendedClass(targetClass1, CQ_FOR_USER, CQ_FILTER_DEVICE);
        verify(userClassService).getExtendedClass(anotherClass, CQ_FOR_USER, CQ_FILTER_DEVICE);
        verify(widgetService, times(3)).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService, times(3)).getAllLookup(anyString());
        checkListNames(expListNames, resultObject);
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testRead_IncludeValues() {
        System.out.println("read_IncludeValues");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(userClassService.getExtendedClass(A_KNOWN_CLASS_ID, CQ_FOR_USER, CQ_FILTER_DEVICE, CQ_INCLUDE_LOOKUP_VALUES)).thenReturn(extendedClass1);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(mockBuildWidget());
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.read(A_KNOWN_CLASS_ID, INCLUDE);

        //assert:
        verify(userClassService).getExtendedClass(eq(A_KNOWN_CLASS_ID), eq(CQ_FOR_USER), eq(CQ_FILTER_DEVICE), eq(CQ_INCLUDE_LOOKUP_VALUES));
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testRead_NonIncludeValues() {
        System.out.println("read_NonIncludeValues");

        //arrange:
        String expName = A_KNOWN_CLASS_NAME1;
        when(userClassService.getExtendedClass(A_KNOWN_CLASS_ID, CQ_FOR_USER, CQ_FILTER_DEVICE, null)).thenReturn(extendedClass1);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(lookupValuePagedElements);

        //act:
        Object resultObject = instance.read(A_KNOWN_CLASS_ID, DONT_INCLUDE);

        //assert:
        verify(userClassService).getExtendedClass(eq(A_KNOWN_CLASS_ID), eq(CQ_FOR_USER), eq(CQ_FILTER_DEVICE), eq(null));
        verify(widgetService).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService).getAllLookup(anyString());
        checkName(expName, resultObject);
    }
}
