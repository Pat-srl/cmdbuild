/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockScope;
import org.cmdbuild.service.rest.v4.command.CardLockWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CardLockWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.Map;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class CardLockWsTest extends WsTestBase {

    private final CardLockWs instance;

    private final ItemLock itemLock;
    private final LockResponse successfulLockResponse, unsuccessfullLockResponse;
    private final Session session;

    public CardLockWsTest() {
        CardLockWsCommand command = new CardLockWsCommand(lockService);
        this.instance = new CardLockWs(lockService, sessionService, coreConfiguration, command);

        this.itemLock = mockBuildItemLock(A_KNOWN_GENERIC_ID1, A_KNOWN_GENERIC_ID2, LockScope.LS_SESSION);
        this.successfulLockResponse = mockBuildSuccessfulLockResponse();
        this.unsuccessfullLockResponse = mockBuildUnsuccessfulLockResponse();
        this.session = mockBuildSession(A_KNOWN_GENERIC_ID1);
    }

    @Test
    public void testGetLock() {
        System.out.println("getLock");

        //arrange:
        when(lockService.getLockOrNull(anyString())).thenReturn(itemLock);

        //act:
        Object resultObject = instance.getLock(A_KNOWN_CARD_ID1);

        //assert:
        verify(lockService).getLockOrNull(anyString());
        checkResult(A_KNOWN_GENERIC_ID1, resultObject, "_id");
    }

    @Test
    public void testGetLock_notFound() {
        System.out.println("getLock");

        //arrange:
        Map<String, Object> expMap = map("success", null, "found", null);

        //act:
        Object resultObject = instance.getLock(A_KNOWN_CARD_ID1);

        //assert:
        verify(lockService).getLockOrNull(anyString());
        checkSuccess(resultObject);
        checkResultStructure(expMap, (Map<String, Object>) resultObject);
    }

    @Test
    public void testCreateLock_success() {
        System.out.println("createLock_success");

        //arrange:
        when(lockService.aquireLock(anyString())).thenReturn(successfulLockResponse);

        //act:
        Object resultObject = instance.createLock(A_KNOWN_CARD_ID1);

        //assert:
        verify(lockService).aquireLock(anyString());
        checkResult(A_KNOWN_GENERIC_ID1, resultObject, "_id");
    }

    @Test
    public void testCreateLock_failure1() {
        System.out.println("createLock_failure1");

        //arrange:
        Map<String, Object> expMap = map("success", null, "user", null);
        when(lockService.aquireLock(anyString())).thenReturn(unsuccessfullLockResponse);
        when(coreConfiguration.getCardlockShowUser()).thenReturn(true);
        when(lockService.getLock(anyString())).thenReturn(itemLock);
        when(sessionService.getSessionById(anyString())).thenReturn(session);

        //act:
        Object resultObject = instance.createLock(A_KNOWN_CARD_ID1);

        //assert:
        verify(lockService).aquireLock(anyString());
        verify(coreConfiguration).getCardlockShowUser();
        verify(lockService).getLock(anyString());
        verify(sessionService).getSessionById(anyString());
        checkResultStructure(expMap, (Map<String, Object>) resultObject);
    }

    @Test
    public void testCreateLock_failure2() {
        System.out.println("createLock_failure2");

        //arrange:
        Map<String, Object> expMap = map("success", null);
        when(lockService.aquireLock(anyString())).thenReturn(unsuccessfullLockResponse);
        when(coreConfiguration.getCardlockShowUser()).thenReturn(false);

        //act:
        Object resultObject = instance.createLock(A_KNOWN_CARD_ID1);

        //assert:
        verify(lockService).aquireLock(anyString());
        verify(coreConfiguration).getCardlockShowUser();
        checkResultStructure(expMap, (Map<String, Object>) resultObject);
    }

    @Test
    public void testReleaseLock() {
        System.out.println("releaseLock");

        //act:
        Object resultObject = instance.releaseLock(A_KNOWN_CARD_ID1);

        //assert:
        verify(lockService).releaseLock(anyString());
        checkSuccess(resultObject);
    }
}
