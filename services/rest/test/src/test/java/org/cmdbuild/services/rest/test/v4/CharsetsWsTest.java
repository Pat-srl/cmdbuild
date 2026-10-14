/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.service.rest.v4.command.CharsetsWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CharsetsWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultSize;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkSuccess;

/**
 * @author ldare
 */
public class CharsetsWsTest extends WsTestBase {

    private final CharsetsWs instance;

    public CharsetsWsTest() {
        CharsetsWsCommand command = new CharsetsWsCommand();
        this.instance = new CharsetsWs(command);
    }

    @Test
    public void testReadAvailableCharsets() {
        System.out.println("readAvailableCharsets");

        //act:
        Object resultObject = instance.readAvailableCharsets();

        //assert:
        checkSuccess(resultObject);
        checkResultSize(resultObject, 173);
    }
}
