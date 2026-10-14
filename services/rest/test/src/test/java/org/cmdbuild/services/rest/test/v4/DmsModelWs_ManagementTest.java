/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.v4.command.DmsModelWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.DmsModelWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FILTER_DEVICE;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_DMSMODEL;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_PROCESS;
import static org.cmdbuild.services.rest.test.common.ClasseMatcher.matchClasse;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class DmsModelWs_ManagementTest extends WsTestBase {

    private final DmsModelWs_Management instance;
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
    private final Widget widget;
    private final PagedElements<LookupValue> pagedLookupValue;

    public DmsModelWs_ManagementTest() {

        attributeTypeConversionService = mockBuildAttributeTypeConversionService();
        classSerializationHelper = mockBuildClassSerializationHelper();
        DmsModelWsCommand command = new DmsModelWsCommand(daoService, userClassService, classSerializationHelper, sysReportService);
        instance = new DmsModelWs_Management(classSerializationHelper, attributeTypeConversionService, command);

        classeActive1 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME1, CS_DMSMODEL);
        classeActive2 = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME2, CS_DMSMODEL);
        classeInactive = toDeactivated(mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME3, CS_DMSMODEL));
        invalidClass = mockBuildClasseWithMetadata(A_KNOWN_CLASS_NAME1, CS_PROCESS);
        listClasse = list(classeActive1, classeActive2, classeInactive, invalidClass);
        extendedClass1 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME1);
        extendedClass2 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME2);
        extendedClass3 = mockBuildExtendedClass(A_KNOWN_CLASS_NAME3);
        widget = mockBuildWidget();
        pagedLookupValue = mockBuildPagedElementsLookupValue();
    }

    @Test
    public void testReadAll_Detailed() {
        System.out.println("readAll_Detailed");

        //arrange:
        List<String> expListName = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2);
        when(daoService.getAllClasses()).thenReturn(listClasse);
        when(userClassService.getExtendedClass(matchClasse(classeActive1))).thenReturn(extendedClass1);
        when(userClassService.getExtendedClass(matchClasse(classeActive2))).thenReturn(extendedClass2);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(pagedLookupValue);


        //act:
        Object resultObject = instance.readAll(DETAILED, A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_EMPTY_FILTER);

        //assert:
        verify(daoService).getAllClasses();
        verify(userClassService).getExtendedClass(matchClasse(classeActive1));
        verify(userClassService).getExtendedClass(matchClasse(classeActive2));
        verify(widgetService, times(2)).widgetDataToWidget(any(WidgetData.class), any(Classe.class));
        verify(lookupService, times(2)).getAllLookup(anyString());
        checkListNames(expListName, resultObject);
    }

    @Test
    public void testReadAll_notDetailed() {
        System.out.println("readAll_notDetailed");

        //arrange:
        List<String> expListName = list(A_KNOWN_CLASS_NAME1, A_KNOWN_CLASS_NAME2);
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
        when(userClassService.getExtendedClass(classeActive1, CQ_FOR_USER, CQ_FILTER_DEVICE)).thenReturn(extendedClass1);
        when(widgetService.widgetDataToWidget(any(WidgetData.class), any(Classe.class))).thenReturn(widget);
        when(lookupService.getAllLookup(anyString())).thenReturn(pagedLookupValue);

        //act:
        Object resultObj = instance.read(A_KNOWN_CLASS_NAME1);

        //assert:
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        verify(userClassService).getExtendedClass(classeActive1, CQ_FOR_USER, CQ_FILTER_DEVICE);
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
    public void testReadAttribute() {
        System.out.println("readAttribute");

        //arrange:
        when(daoService.getClasse(A_KNOWN_CLASS_NAME1)).thenReturn(classeActive1);

        //act:
        Object resultObject = instance.readAttribute(A_KNOWN_CLASS_NAME1, A_KNOWN_ATTR_NAME1);

        //assert;
        verify(daoService).getClasse(A_KNOWN_CLASS_NAME1);
        checkSuccess(resultObject);
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
