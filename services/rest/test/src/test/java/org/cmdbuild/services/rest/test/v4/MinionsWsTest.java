package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.minions.*;
import org.cmdbuild.service.rest.v4.command.MinionsWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.MinionsWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildMinionHandler;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildMinionList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_GENERIC_ID1;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_NAME1;
import static org.mockito.Mockito.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * @author ldare
 */
public class MinionsWsTest extends WsTestBase {

    private MinionsWs instance;

    private final List<Minion> minionList;

    public MinionsWsTest() {
        MinionsWsCommand command = new MinionsWsCommand(minionService);
        this.instance = new MinionsWs(command);

        this.minionList = mockBuildMinionList(globalConfigService);
    }

    @Test
    public void testGetAll() {
        System.out.println("testGetAll");

        //arrange:
        when(minionService.getMinions()).thenReturn(minionList);

        //act:
        Object resultObject = instance.getAll("default");

        //assert:
        verify(minionService).getMinions();
        checkResultList(list(A_KNOWN_NAME1), resultObject, "_id");
    }

    @Test
    public void testGetOne() {
        System.out.println("testGetOne");

        //arrange:
        when(minionService.getMinion(A_KNOWN_GENERIC_ID1)).thenReturn(minionList.get(0));

        //act:
        Object resultObject = instance.getOne(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(minionService).getMinion(A_KNOWN_GENERIC_ID1);
        checkResult(A_KNOWN_NAME1, resultObject, "_id");
    }

    @Test
    public void testStart() {
        System.out.println("testStart");

        //arrange:
        when(minionService.getMinion(A_KNOWN_GENERIC_ID1)).thenReturn(minionList.get(0));

        //act:
        Object resultObject = instance.start(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(minionService).startMinion(A_KNOWN_GENERIC_ID1);
        verify(minionService).getMinion(A_KNOWN_GENERIC_ID1);
        checkResult(A_KNOWN_NAME1, resultObject, "_id");
    }

    @Test
    public void testStop() {
        System.out.println("testStop");

        //arrange:
        when(minionService.getMinion(A_KNOWN_GENERIC_ID1)).thenReturn(minionList.get(0));

        //act:
        Object resultObject = instance.stop(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(minionService).stopMinion(A_KNOWN_GENERIC_ID1);
        verify(minionService).getMinion(A_KNOWN_GENERIC_ID1);
        checkResult(A_KNOWN_NAME1, resultObject, "_id");
    }
}

