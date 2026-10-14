package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.service.rest.v4.command.LockWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.LockWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildItemLockList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class LockWsTest extends WsTestBase {

    private LockWs instance;

    private final List<ItemLock> itemLockList;

    public LockWsTest() {
        LockWsCommand command = new LockWsCommand(lockService);
        this.instance = new LockWs(command);

        this.itemLockList = mockBuildItemLockList();
    }

    @Test
    public void testGetLocks() {
        System.out.println("testGetLocks");

        //arrange:
        when(lockService.getAllLocks()).thenReturn(itemLockList);

        //act:
        Object resultObject = instance.getLocks();

        //assert:
        verify(lockService).getAllLocks();
        checkResultList(list(A_KNOWN_GENERIC_ID3, A_KNOWN_GENERIC_ID2, A_KNOWN_GENERIC_ID1), resultObject, "_id");
    }

    @Test
    public void testGetLock() {
        System.out.println("testGetLock");

        //arrange:
        when(lockService.getLock(A_KNOWN_GENERIC_ID1)).thenReturn(itemLockList.get(0));

        //act:
        Object resultObject = instance.getLock(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(lockService).getLock(A_KNOWN_GENERIC_ID1);
        checkResult(A_KNOWN_GENERIC_ID1, resultObject, "_id");
    }

    @Test
    public void testDeleteLock() {
        System.out.println("testDeleteLock");

        //act:
        Object resultObject = instance.deleteLock(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(lockService).deleteLock(A_KNOWN_GENERIC_ID1);
        checkSuccess(resultObject);
    }

    @Test
    public void testDeleteAllLocks() {
        System.out.println("testDeleteAllLocks");

        //act:
        instance.deleteAllLocks();

        //assert:
        verify(lockService).releaseAllLocks();
    }
}

