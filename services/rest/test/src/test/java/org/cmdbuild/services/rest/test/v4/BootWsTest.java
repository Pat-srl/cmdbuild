/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.service.rest.v4.command.BootWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.BootWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.cmdbuild.minions.SystemStatus.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildDataHandler;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildPatchList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class BootWsTest extends WsTestBase {

    private final BootWsCommand command;
    private final BootWs instance;

    private final DataHandler dataHandler;
    private final Map<String, String> dbConfig;
    private final List<Patch> patchList;

    public BootWsTest() {

        this.command = new BootWsCommand(minionService, directoryService, globalConfigService, patchService);
        this.instance = new BootWs(command);
        this.dataHandler = mockBuildDataHandler(A_TEST_DATAHANDLER_TYPE);
        this.dbConfig = map("test", "config", "for", "db");
        this.patchList = mockBuildPatchList();
    }

    @Test
    public void testStatus_SYST_WAITING_FOR_PATCH_MANAGER() {
        System.out.println("status_SYST_WAITING_FOR_PATCH_MANAGER");

        //arrange:
        Map<String, Object> expMap = map("success", null, "status", null, "operationRequired", null);
        when(minionService.getSystemStatus()).thenReturn(SYST_WAITING_FOR_PATCH_MANAGER);

        //act:
        Object resultObject = instance.status();

        //assert:
        verify(minionService, times(2)).getSystemStatus();
        checkSuccess(resultObject);
        checkResultStructure(expMap, (Map) resultObject);
        assertEquals("applyPatch", ((Map) resultObject).get("operationRequired"));
    }

    @Test
    public void testStatus_SYST_WAITING_FOR_DATABASE_CONFIGURATION() {
        System.out.println("status_SYST_WAITING_FOR_DATABASE_CONFIGURATION");

        //arrange:
        Map<String, Object> expMap = map("success", null, "status", null, "operationRequired", null);
        when(minionService.getSystemStatus()).thenReturn(SYST_WAITING_FOR_DATABASE_CONFIGURATION);

        //act:
        Object resultObject = instance.status();

        //assert:
        verify(minionService, times(2)).getSystemStatus();
        checkSuccess(resultObject);
        checkResultStructure(expMap, (Map) resultObject);
        assertEquals("databaseConfiguration", ((Map) resultObject).get("operationRequired"));
    }

    @Test
    public void testStatus_SYST_READY_RESTART_REQUIRED() {
        System.out.println("status_SYST_READY_RESTART_REQUIRED");

        //arrange:
        Map<String, Object> expMap = map("success", null, "status", null, "operationRequired", null);
        when(minionService.getSystemStatus()).thenReturn(SYST_READY_RESTART_REQUIRED);

        //act:
        Object resultObject = instance.status();

        //assert:
        verify(minionService, times(2)).getSystemStatus();
        checkSuccess(resultObject);
        checkResultStructure(expMap, (Map) resultObject);
        assertEquals("restart", ((Map) resultObject).get("operationRequired"));
    }

    @Test
    public void testGetPendingPatches() {
        System.out.println("getPendingPatches");

        //arrange:
        when(patchService.getAvailableCorePatches()).thenReturn(patchList);

        //act:
        Object resultObject = instance.getPendingPatches();

        //assert:
        verify(patchService).getAvailableCorePatches();
        checkResultList(list(A_KNOWN_PATCH1, A_KNOWN_PATCH2, A_KNOWN_PATCH3), resultObject, "description");
    }

    @Test
    public void testApplyPendingPatches() {
        System.out.println("applyPendingPatches");

        //act:
        Object resultObject = instance.applyPendingPatches();

        //assert:
        verify(patchService).applyPendingPatchesAndFunctions();
        checkSuccess(resultObject);
    }
}
