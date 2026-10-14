/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.corecomponents.CoreComponent;
import org.cmdbuild.service.rest.v4.command.CoreComponentWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CoreComponentWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkName;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildCoreComponent;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_COMPONENT_CODE1;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author ldare
 */
public class CoreComponentWs_ManagementTest extends WsTestBase {

    private final CoreComponentWs_Management instance;

    private final CoreComponent coreComponent1;

    public CoreComponentWs_ManagementTest() {
        CoreComponentWsCommand command = new CoreComponentWsCommand(coreComponentService);
        instance = new CoreComponentWs_Management(coreComponentService, command);
        coreComponent1 = mockBuildCoreComponent(A_KNOWN_COMPONENT_CODE1);
    }

    @Test
    public void testGet() {
        System.out.println("get");

        //arrange:
        String expName = A_KNOWN_COMPONENT_CODE1;
        when(coreComponentService.getActiveComponent(A_KNOWN_COMPONENT_CODE1)).thenReturn(coreComponent1);

        //act:
        Object resultObject = instance.get(A_KNOWN_COMPONENT_CODE1);

        //assert:
        verify(coreComponentService).getActiveComponent(anyString());
        checkName(expName, resultObject);
    }
}
