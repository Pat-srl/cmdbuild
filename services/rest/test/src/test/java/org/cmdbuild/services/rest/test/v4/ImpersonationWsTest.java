package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.service.rest.v4.command.ImpersonationWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ImpersonationWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkSuccess;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_NAME1;
import static org.mockito.Mockito.verify;

/**
 * @author ldare
 */
public class ImpersonationWsTest extends WsTestBase {

    private ImpersonationWs instance;

    public ImpersonationWsTest() {
        ImpersonationWsCommand command = new ImpersonationWsCommand(sessionService);
        this.instance = new ImpersonationWs(command);
    }

    @Test
    public void testImpersonate() {
        System.out.println("testImpersonate");

        //act:
        Object resultObject = instance.impersonate(A_KNOWN_NAME1);

        //assert:
        verify(sessionService).impersonate(A_KNOWN_NAME1);
        checkSuccess(resultObject);
    }

    @Test
    public void testDeimpersonare() {
        System.out.println("testDeimpersonare");

        //act:
        Object resultObject = instance.deimpersonare();

        //assert:
        verify(sessionService).deimpersonate();
        checkSuccess(resultObject);
    }
}

