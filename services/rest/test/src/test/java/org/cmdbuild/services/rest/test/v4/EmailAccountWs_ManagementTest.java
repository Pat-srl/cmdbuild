/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.service.rest.v4.command.EmailAccountWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EmailAccountWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkName;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListEmailAccount;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_EMAIL_NAME1;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EmailAccountWs_ManagementTest extends WsTestBase {

    private final EmailAccountWs_Management instance;

    private final List<EmailAccount> emailAccountList;

    public EmailAccountWs_ManagementTest() {
        EmailAccountWsCommand command = new EmailAccountWsCommand(emailAccountService);
        instance = new EmailAccountWs_Management(command);
        emailAccountList = mockBuildListEmailAccount();
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        String expName = A_KNOWN_EMAIL_NAME1;
        when(emailAccountService.getAccountByIdOrCode(A_KNOWN_EMAIL_NAME1)).thenReturn(emailAccountList.get(0));

        //act:
        Object resultObject = instance.read(A_KNOWN_EMAIL_NAME1);

        //assert:
        verify(emailAccountService).getAccountByIdOrCode(A_KNOWN_EMAIL_NAME1);
        checkName(expName, resultObject);
    }
}