/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.service.rest.v4.command.GeoAttributeWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.GeoAttributeWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsGeoAttributeData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildClasseWithGisPermissions;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildGisAttribute;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockbuildWsGeoAttributeData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class GeoAttributeWs_AdministrationTest extends WsTestBase {

    private final GeoAttributeWs_Administration instance;

    private final GisAttribute gisAttribute1;
    private final GisAttribute gisAttribute2;
    private final GisAttribute gisAttribute3;
    private final List<GisAttribute> listGisAttribute;
    private final Classe classe;
    private final WsGeoAttributeData wsGeoAttributeData;
    private final List<Long> ordering;

    public GeoAttributeWs_AdministrationTest() {
        GeoAttributeWsCommand command = new GeoAttributeWsCommand(gisService, coreConfiguration, easyuploadService, userClassService);
        instance = new GeoAttributeWs_Administration(gisService, easyuploadService, userClassService, translationService, command);
        gisAttribute1 = mockBuildGisAttribute(A_KNOWN_GISATTRIBUTE_NAME1, true, ACTIVE, true);
        gisAttribute2 = mockBuildGisAttribute(A_KNOWN_GISATTRIBUTE_NAME2, false, NOT_ACTIVE, false);
        gisAttribute3 = mockBuildGisAttribute(A_KNOWN_GISATTRIBUTE_NAME3, false, ACTIVE, false);
        listGisAttribute = list(gisAttribute1, gisAttribute2, gisAttribute3);
        classe = mockBuildClasseWithGisPermissions(A_KNOWN_CLASS_NAME1, list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3));
        wsGeoAttributeData = mockbuildWsGeoAttributeData(A_KNOWN_GISATTRIBUTE_NAME1);
        ordering = list(1L, 2L, 3L);
    }

    @Test
    public void testReadAll_AnyClass_GeoServerEnabled() {
        System.out.println("readAll_AnyClass_GeoServerEnabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.getGisAttributes()).thenReturn(listGisAttribute);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(gisService.isGeoserverEnabled()).thenReturn(ACTIVE);

        //act:
        Object resultObj = instance.readAllAttributes(A_KNOWN_ANY_ID, A_TEST_OFFSET, A_TEST_LIMIT, DETAILED, VISIBLE);

        //assert:
        verify(gisService).getGisAttributes();
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService, times(3)).getUserClass(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        checkListNames(expListNames, resultObj);
    }

    @Test
    public void testReadAll_AnyClass_GeoServerDisabled() {
        System.out.println("readAll_AnyClass_GeoServerDisabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.getGisAttributes()).thenReturn(listGisAttribute);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(gisService.isGeoserverEnabled()).thenReturn(NOT_ACTIVE);

        //act:
        Object resultObj = instance.readAllAttributes(A_KNOWN_ANY_ID, A_TEST_OFFSET, A_TEST_LIMIT, DETAILED, VISIBLE);

        //assert:
        verify(gisService).getGisAttributes();
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService).getUserClass(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        checkListNames(expListNames, resultObj);
    }

    @Test
    public void testReadAll_VisibleClass_GeoServerEnabled() {
        System.out.println("readAll_AnyClass_GeoServerEnabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.getGisAttributesVisibleFromClass(A_KNOWN_CLASS_ID)).thenReturn(listGisAttribute);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(gisService.isGeoserverEnabled()).thenReturn(ACTIVE);

        //act:
        Object resultObj = instance.readAllAttributes(A_KNOWN_CLASS_ID, A_TEST_OFFSET, A_TEST_LIMIT, DETAILED, VISIBLE);

        //assert:
        verify(gisService).getGisAttributesVisibleFromClass(A_KNOWN_CLASS_ID);
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService, times(3)).getUserClass(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        checkListNames(expListNames, resultObj);
    }

    @Test
    public void testReadAll_VisibleClass_GeoServerDisabled() {
        System.out.println("readAll_AnyClass_GeoServerDisabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.getGisAttributesVisibleFromClass(A_KNOWN_CLASS_ID)).thenReturn(listGisAttribute);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(gisService.isGeoserverEnabled()).thenReturn(NOT_ACTIVE);

        //act:
        Object resultObj = instance.readAllAttributes(A_KNOWN_CLASS_ID, A_TEST_OFFSET, A_TEST_LIMIT, DETAILED, VISIBLE);

        //assert:
        verify(gisService).getGisAttributesVisibleFromClass(A_KNOWN_CLASS_ID);
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService).getUserClass(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        checkListNames(expListNames, resultObj);
    }

    @Test
    public void testReadAll_NonVisibleClass_GeoServerEnabled() {
        System.out.println("readAll_AnyClass_GeoServerEnabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.getGisAttributesByOwnerClassIncludeInherited(A_KNOWN_CLASS_ID)).thenReturn(listGisAttribute);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(gisService.isGeoserverEnabled()).thenReturn(ACTIVE);

        //act:
        Object resultObj = instance.readAllAttributes(A_KNOWN_CLASS_ID, A_TEST_OFFSET, A_TEST_LIMIT, DETAILED, NOT_VISIBLE);

        //assert:
        verify(gisService).getGisAttributesByOwnerClassIncludeInherited(A_KNOWN_CLASS_ID);
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService, times(3)).getUserClass(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        checkListNames(expListNames, resultObj);
    }

    @Test
    public void testReadAll_NonVisibleClass_GeoServerDisabled() {
        System.out.println("readAll_AnyClass_GeoServerDisabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.getGisAttributesByOwnerClassIncludeInherited(A_KNOWN_CLASS_ID)).thenReturn(listGisAttribute);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(gisService.isGeoserverEnabled()).thenReturn(NOT_VISIBLE);

        //act:
        Object resultObj = instance.readAllAttributes(A_KNOWN_CLASS_ID, A_TEST_OFFSET, A_TEST_LIMIT, DETAILED, NOT_VISIBLE);

        //assert:
        verify(gisService).getGisAttributesByOwnerClassIncludeInherited(A_KNOWN_CLASS_ID);
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService).getUserClass(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        checkListNames(expListNames, resultObj);
    }

    @Test
    public void testReadAttribute_WithIcon_Postgis() {
        System.out.println("readAttribute_WithIcon_Postgis");

        //arrange:
        String expName = A_KNOWN_GISATTRIBUTE_NAME1;
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID)).thenReturn(gisAttribute1);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.readAttribute(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);

        //assert:
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);
        verify(userClassService, times(2)).getUserClass(anyString());
        verify(easyuploadService).getByPathOrNull(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testReadAttribute_NoIcon_NotPostgis_GeoServerEnabled() {
        System.out.println("readAttribute_NoIcon_NotPostgis");

        //arrange:
        String expName = A_KNOWN_GISATTRIBUTE_NAME2;
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID)).thenReturn(gisAttribute2);
        when(gisService.isGeoserverEnabled()).thenReturn(ACTIVE);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.readAttribute(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);

        //assert:
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);

        verify(userClassService, times(2)).getUserClass(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testReadAttribute_NoIcon_NotPostgis_GeoServerDisabled() {
        System.out.println("readAttribute_NoIcon_NotPostgis");

        //arrange:
        String expName = A_KNOWN_GISATTRIBUTE_NAME2;
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID)).thenReturn(gisAttribute2);
        when(gisService.isGeoserverEnabled()).thenReturn(NOT_ACTIVE);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.readAttribute(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);

        //assert:
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);

        verify(userClassService).getUserClass(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate_WithIcon_Postgis() {
        System.out.println("create_WithIcon_Postgis");

        //arrange:
        when(gisService.createGisAttribute(any(GisAttribute.class))).thenReturn(gisAttribute1);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.create(A_KNOWN_CLASS_ID, wsGeoAttributeData);

        //assert:
        verify(gisService).createGisAttribute(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService).getUserClass(anyString());
        checkName(A_KNOWN_GISATTRIBUTE_NAME1, resultObject);
    }

    @Test
    public void testCreate_NoIcon_NotPostgis_GeoserverEnabled() {
        System.out.println("create_NoIcon_NotPostgis_GeoserverEnabled");

        //arrange:
        when(gisService.createGisAttribute(any(GisAttribute.class))).thenReturn(gisAttribute2);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(gisService.isGeoserverEnabled()).thenReturn(ACTIVE);

        //act:
        Object resultObject = instance.create(A_KNOWN_CLASS_ID, wsGeoAttributeData);

        //assert:
        verify(gisService).createGisAttribute(any(GisAttribute.class));
        verify(userClassService).getUserClass(anyString());
        verify(gisService).isGeoserverEnabled();
        checkName(A_KNOWN_GISATTRIBUTE_NAME2, resultObject);
    }

    @Test
    public void testCreate_NoIcon_NotPostgis_GeoserverDisabled() {
        System.out.println("create_NoIcon_NotPostgis_GeoserverDisabled");

        //arrange:
        when(gisService.createGisAttribute(any(GisAttribute.class))).thenReturn(gisAttribute2);
        when(gisService.isGeoserverEnabled()).thenReturn(NOT_ACTIVE);

        //act:
        Object resultObject = instance.create(A_KNOWN_CLASS_ID, wsGeoAttributeData);

        //assert:
        verify(gisService).createGisAttribute(any(GisAttribute.class));
        verify(gisService).isGeoserverEnabled();
        checkName(A_KNOWN_GISATTRIBUTE_NAME2, resultObject);
    }

    @Test
    public void testReorder_GeoserverEnabled() {
        System.out.println("reorder_GeoserverEnabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.updateGisAttributesOrder(ordering)).thenReturn(listGisAttribute);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(gisService.isGeoserverEnabled()).thenReturn(ACTIVE);

        //act:
        Object resultObject = instance.reorder(A_KNOWN_ANY_ID, ordering);

        //assert:
        verify(gisService).updateGisAttributesOrder(ordering);
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService, times(3)).getUserClass(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReorder_GeoserverDisabled() {
        System.out.println("reorder_GeoserverDisabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.updateGisAttributesOrder(ordering)).thenReturn(listGisAttribute);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);
        when(gisService.isGeoserverEnabled()).thenReturn(NOT_ACTIVE);

        //act:
        Object resultObject = instance.reorder(A_KNOWN_ANY_ID, ordering);

        //assert:
        verify(gisService).updateGisAttributesOrder(ordering);
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService).getUserClass(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReorderError() {
        System.out.println("reorder");

        //act:
        try {
            instance.reorder(A_KNOWN_CLASS_ID, ordering);

        }
        //assert:
        catch (IllegalArgumentException ex) {
            System.out.println("Correctly failed");
        }
    }

    @Test
    public void testUpdateVisibility_GeoserverEnabled() {
        System.out.println("updateVisibility_GeoserverEnabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.getGisAttributesVisibleFromClass(A_KNOWN_CLASS_ID)).thenReturn(listGisAttribute);
        when(gisService.isGeoserverEnabled()).thenReturn(ACTIVE);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.updateVisibility(A_KNOWN_CLASS_ID, map(1L, true));

        //assert:
        verify(gisService).updateGeoAttributesVisibilityForClass(A_KNOWN_CLASS_ID, map(1L, true));
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        verify(userClassService, times(3)).getUserClass(anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testUpdateVisibility_GeoserverDisabled() {
        System.out.println("updateVisibility_GeoserverDisabled");

        //arrange:
        List<String> expListNames = list(A_KNOWN_GISATTRIBUTE_NAME1, A_KNOWN_GISATTRIBUTE_NAME2, A_KNOWN_GISATTRIBUTE_NAME3);
        when(gisService.getGisAttributesVisibleFromClass(A_KNOWN_CLASS_ID)).thenReturn(listGisAttribute);
        when(gisService.isGeoserverEnabled()).thenReturn(NOT_ACTIVE);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.updateVisibility(A_KNOWN_CLASS_ID, map(1L, true));

        //assert:
        verify(gisService).updateGeoAttributesVisibilityForClass(A_KNOWN_CLASS_ID, map(1L, true));
        verify(translationService, times(3)).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(gisService, times(2)).isGeoserverEnabled();
        verify(userClassService).getUserClass(anyString());
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID)).thenReturn(gisAttribute1);
        when(gisService.updateGisAttribute(any(GisAttribute.class))).thenReturn(gisAttribute1);
        when(userClassService.getUserClass(anyString())).thenReturn(classe);

        //act:
        Object resultObject = instance.update(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID, wsGeoAttributeData);

        //assert:
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);
        verify(gisService).updateGisAttribute(any(GisAttribute.class));
        verify(translationService).translateGisAttributeDescription(any(GisAttribute.class));
        verify(easyuploadService).getByPathOrNull(anyString());
        verify(userClassService).getUserClass(anyString());
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //arrange:
        when(gisService.getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID)).thenReturn(gisAttribute1);

        //act:
        Object resultObject = instance.delete(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);

        //assert:
        verify(gisService).getGisAttributeWithCurrentUserByClassAndNameOrId(A_KNOWN_CLASS_ID, A_KNOWN_ATTR_ID);
        verify(gisService).deleteGisAttribute(eq(A_KNOWN_CLASS_ID), anyString());
        checkSuccess(resultObject);
    }
}
