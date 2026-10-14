/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.bim.BimObject;
import org.cmdbuild.bim.BimProject;
import org.cmdbuild.bim.BimProjectExt;
import org.cmdbuild.service.rest.v4.command.BimProjectWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.BimProjectWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class BimProjectWs_ManagementTest extends WsTestBase {

    private final BimProjectWs_Management instance;

    private final List<BimProjectExt> bimProjectExtList;
    private final BimObject bimObject;
    private final DataHandler dataHandler;

    public BimProjectWs_ManagementTest() {

        BimProjectWsCommand command = new BimProjectWsCommand(bimService);
        instance = new BimProjectWs_Management(command);

        bimProjectExtList = mockBuildBimProjectExtList();
        this.bimObject = mockBuildBimObject(A_KNOWN_LONG_ID1);
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
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2), resultObject, "_id");
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
    public void testGetOne_active() {
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
    public void testGetOne_notActive() {
        System.out.println("getOne");

        //arrange:
        when(bimService.getProjectExt(A_KNOWN_LONG_ID1)).thenReturn(bimProjectExtList.get(2));

        //act:
        Object resultObject = instance.getOne(A_KNOWN_LONG_ID1);

        //assert:
        verify(bimService).getProjectExt(A_KNOWN_LONG_ID1);
        checkSuccess(resultObject);
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
}
