/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.bim.BimObject;
import org.cmdbuild.bim.BimProject;
import org.cmdbuild.bim.BimProjectExt;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.modeldiff.dataset.data.Card_WithAttachments;
import org.cmdbuild.service.rest.v4.command.BimProjectWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.BimProjectWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsProjectData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsProjectData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class BimProjectWs_AdministrationTest extends WsTestBase {

    private final BimProjectWs_Administration instance;

    private final List<BimProjectExt> bimProjectExtList;
    private final BimObject bimObject;
    private final WsProjectData wsProjectData;
    private final DataHandler dataHandler;

    public BimProjectWs_AdministrationTest() {

        BimProjectWsCommand command = new BimProjectWsCommand(bimService);
        this.instance = new BimProjectWs_Administration(command);

        this.bimProjectExtList = mockBuildBimProjectExtList();
        this.bimObject = mockBuildBimObject(A_KNOWN_LONG_ID1);
        this.wsProjectData = mockBuildWsProjectData(A_KNOWN_NAME1);
        this.dataHandler = mockBuildDataHandler(A_TEST_DATAHANDLER_TYPE);
    }

    @Test
    public void testGetAll() {
        System.out.println("getAll");

        //arrange:
        when(bimService.getAllProjectsAndObjects()).thenReturn(bimProjectExtList);

        //act:
        Object resultObject = instance.getAll();

        //assert:
        verify(bimService).getAllProjectsAndObjects();
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
    }

    @Test
    public void testGetValue_exists_valueNotNull() {
        System.out.println("getValue_exists_valueNotNull");

        //arrange:
        when(bimService.getProjectById(A_KNOWN_LONG_ID1)).thenReturn(bimProjectExtList.get(0));
        when(bimService.getBimObjectForProjectGlobalIdOrNull(any(BimProject.class), anyString())).thenReturn(bimObject);

        //act:
        Object resultObject = instance.getValue(A_KNOWN_LONG_ID1, A_KNOWN_ANY_ID, true);

        //assert:
        verify(bimService).getProjectById(A_KNOWN_LONG_ID1);
        verify(bimService).getBimObjectForProjectGlobalIdOrNull(any(BimProject.class), anyString());
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
        checkResult(true, resultObject, "exists");
    }

    @Test
    public void testGetValue_doesntExist_valueNotNull() {
        System.out.println("getValue_doesntExist_valueNotNull");

        //arrange:
        when(bimService.getProjectById(A_KNOWN_LONG_ID1)).thenReturn(bimProjectExtList.get(0));
        when(bimService.getBimObjectForProjectGlobalIdOrNull(any(BimProject.class), anyString())).thenReturn(bimObject);

        //act:
        Object resultObject = instance.getValue(A_KNOWN_LONG_ID1, A_KNOWN_ANY_ID, false);

        //assert:
        verify(bimService).getProjectById(A_KNOWN_LONG_ID1);
        verify(bimService).getBimObjectForProjectGlobalIdOrNull(any(BimProject.class), anyString());
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
    }

    @Test
    public void testGetValue_exists_valueNull() {
        System.out.println("getValue");

        //act:
        Object resultObject = instance.getValue(A_KNOWN_LONG_ID1, A_KNOWN_ANY_ID, true);

        //assert:
        checkResult(false, resultObject, "exists");
    }

    @Test
    public void testGetValue_doesntExist_valueNull() {
        System.out.println("getValue_doesntExist_valueNull");

        //act:
        try {
            instance.getValue(A_KNOWN_LONG_ID1, A_KNOWN_ANY_ID, false);
            //assert:
        } catch (NullPointerException ex) {
            System.out.println("Failed successfully");
        }
    }

    @Test
    public void testGetOne() {
        System.out.println("getOne");

        //arrange:
        when(bimService.getProjectExt(A_KNOWN_LONG_ID1)).thenReturn(bimProjectExtList.get(0));

        //act:
        Object resultObject = instance.getOne(A_KNOWN_LONG_ID1);

        //assert:
        verify(bimService).getProjectExt(A_KNOWN_LONG_ID1);
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
    }

    @Test
    public void testCreateProjectWithFile_nullDataHandler() {
        System.out.println("createProjectWithFile");

        //arrange:
        when(bimService.createProjectExt(any(BimProject.class), any(Card_WithAttachments.class))).thenReturn(bimProjectExtList.get(0));

        //act:
        Object resultObject = instance.createProjectWithFile(null, wsProjectData);

        //assert:
        verify(bimService).createProjectExt(any(BimProject.class), any(Card_WithAttachments.class));
        checkResult("Test Project", resultObject, "name");
    }

    @Test
    public void testCreateProjectWithFile_notNullDataHandler() {
        System.out.println("createProjectWithFile_notNullDataHandler");

        //arrange:
        when(bimService.createProjectExt(any(BimProjectExt.class))).thenReturn(bimProjectExtList.get(0));
        when(bimService.getProjectExt(anyLong())).thenReturn(bimProjectExtList.get(0));

        //act:
        Object resultObject = instance.createProjectWithFile(dataHandler, wsProjectData);

        //assert:
        verify(bimService).createProjectExt(any(BimProjectExt.class));
        verify(bimService).uploadXktFile(anyLong(), eq(dataHandler), eq(true));
        verify(bimService).getProjectExt(anyLong());
        checkResult("Test Project", resultObject, "name");
    }

    @Test
    public void testUpdate_notNullDataHandler() {
        System.out.println("update_notNullDataHandler");

        //arrange:
        when(bimService.getProjectExt(A_KNOWN_LONG_ID1)).thenReturn(bimProjectExtList.get(0));

        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, dataHandler, wsProjectData);

        //assert:
        verify(bimService).updateProjectExt(any(BimProject.class), any(CardIdAndClassName.class));
        verify(bimService).uploadXktFile(A_KNOWN_LONG_ID1, dataHandler, false);
        verify(bimService).getProjectExt(A_KNOWN_LONG_ID1);
        checkResult("Test Project", resultObject, "name");
    }

    @Test
    public void testUpdate_nullDataHandler() {
        System.out.println("update_nullDataHandler");

        //arrange:
        when(bimService.getProjectExt(A_KNOWN_LONG_ID1)).thenReturn(bimProjectExtList.get(0));

        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, null, wsProjectData);

        //assert:
        verify(bimService).updateProjectExt(any(BimProject.class), any(CardIdAndClassName.class));
        verify(bimService).getProjectExt(A_KNOWN_LONG_ID1);
        checkResult("Test Project", resultObject, "name");
    }

    @Test
    public void testDownloadIfcFile_ifc() {
        System.out.println("downloadIfcFile_ifc");

        //arrange:
        when(bimService.downloadIfcFile(A_KNOWN_LONG_ID1, "ifcFormat")).thenReturn(dataHandler);

        //act:
        Object resultObject = instance.downloadIfcFile(A_KNOWN_LONG_ID1, "ifcFormat", "ifc");

        //assert:
        verify(bimService).downloadIfcFile(A_KNOWN_LONG_ID1, "ifcFormat");
        checkDataHandler("text/plain", resultObject);
    }

    @Test
    public void testDownloadIfcFile_xkt() {
        System.out.println("downloadIfcFile_xkt");

        //arrange:
        when(bimService.downloadXktFile(A_KNOWN_LONG_ID1)).thenReturn(dataHandler);

        //act:
        Object resultObject = instance.downloadIfcFile(A_KNOWN_LONG_ID1, "ifcFormat", "xkt");

        //assert:
        verify(bimService).downloadXktFile(A_KNOWN_LONG_ID1);
        checkDataHandler("text/plain", resultObject);
    }

    @Test
    public void testDownloadIfcFile_unsupported() {
        System.out.println("downloadIfcFile_unsupported");

        try {
            //act:
            Object resultObject = instance.downloadIfcFile(A_KNOWN_LONG_ID1, "ifcFormat", "randomGibberish");
        } catch (UnsupportedOperationException ex) {
            //assert:
            assertEquals("Invalid bim format randomGibberish", ex.getMessage());
        }
    }

    @Test
    public void testUploadIfcFile() throws IOException {
        System.out.println("uploadIfcFile");

        //arrange:
        when(bimService.uploadXktFile(A_KNOWN_LONG_ID1, dataHandler, false)).thenReturn(bimProjectExtList.get(0));

        //act:
        Object resultObject = instance.uploadIfcFile(A_KNOWN_LONG_ID1, dataHandler);

        //assert:
        verify(bimService).uploadXktFile(A_KNOWN_LONG_ID1, dataHandler, false);
        checkSuccess(resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_LONG_ID1);

        //assert:
        verify(bimService).deleteProject(A_KNOWN_LONG_ID1);
        checkSuccess(resultObject);
    }
}
