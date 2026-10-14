/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.service.rest.v4.command.EmailAccountWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EmailAccountWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsEmailAccountData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListEmailAccount;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsEmailAccountData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.*;

public class EmailAccountWs_AdministrationTest extends WsTestBase {

    private final EmailAccountWs_Administration instance;

    private final List<EmailAccount> listEmailAccount;
    private final WsEmailAccountData wsEmailAccountData;

    public EmailAccountWs_AdministrationTest() {
        EmailAccountWsCommand command = new EmailAccountWsCommand(emailAccountService);
        instance = new EmailAccountWs_Administration(emailAccountService, command);
        listEmailAccount = mockBuildListEmailAccount();
        wsEmailAccountData = mockBuildWsEmailAccountData();
    }

    @Test
    public void testReadAll_Detailed() {
        System.out.println("readAll_Detailed");

        //arrange:
        List<String> expListNames = list(A_KNOWN_EMAIL_NAME1, A_KNOWN_EMAIL_NAME2, A_KNOWN_EMAIL_NAME3);
        when(emailAccountService.getAll()).thenReturn(listEmailAccount);
        when(emailAccountService.getDefaultCodeOrNull()).thenReturn("name");

        //act:
        Object resultObject = instance.readAll(A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED);

        //assert:
        verify(emailAccountService).getAll();
        verify(emailAccountService, times(expListNames.size())).getDefaultCodeOrNull();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_NotDetailed() {
        System.out.println("readAll_NotDetailed");

        //arrange:
        List<String> expListNames = list(A_KNOWN_EMAIL_NAME1, A_KNOWN_EMAIL_NAME2, A_KNOWN_EMAIL_NAME3);
        when(emailAccountService.getAll()).thenReturn(listEmailAccount);

        //act:
        Object resultObject = instance.readAll(A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, NOT_DETAILED);

        //assert:
        verify(emailAccountService).getAll();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testRead_SystemViewMode() {
        System.out.println("read_SystemViewMode");

        //arrange:
        String expName = A_KNOWN_EMAIL_NAME1;
        when(emailAccountService.getAccountByIdOrCode(A_KNOWN_EMAIL_NAME1)).thenReturn(listEmailAccount.get(0));
        when(emailAccountService.getDefaultCodeOrNull()).thenReturn("name");

        //act:
        Object resultObject = instance.read(A_KNOWN_EMAIL_NAME1);

        //assert:
        verify(emailAccountService).getAccountByIdOrCode(A_KNOWN_EMAIL_NAME1);
        verify(emailAccountService).getDefaultCodeOrNull();
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_EMAIL_NAME1;
        when(emailAccountService.create(any(EmailAccount.class))).thenReturn(listEmailAccount.get(0));
        when(emailAccountService.getDefaultCodeOrNull()).thenReturn("name");

        //act:
        Object resultObject = instance.create(wsEmailAccountData);

        //assert:
        verify(emailAccountService).create(any(EmailAccount.class));
        verify(emailAccountService).getDefaultCodeOrNull();
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_EMAIL_NAME1;
        when(emailAccountService.update(any(EmailAccount.class))).thenReturn(listEmailAccount.get(0));
        when(emailAccountService.getDefaultCodeOrNull()).thenReturn("name");

        //act:
        Object resultObject = instance.update(A_RANDOM_ID, wsEmailAccountData);

        //assert:
        verify(emailAccountService).update(any(EmailAccount.class));
        verify(emailAccountService).getDefaultCodeOrNull();
        checkName(expName, resultObject);

    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_RANDOM_ID);

        //assert:
        verify(emailAccountService).delete(A_RANDOM_ID);
        checkSuccess(resultObject);
    }

    @Test
    public void testTestAccountConfig() {
        System.out.println("testAccountConfig");

        //act:
        Object resultObject = instance.testAccountConfig(wsEmailAccountData);

        //assert:
        checkSuccess(resultObject);
    }
}
