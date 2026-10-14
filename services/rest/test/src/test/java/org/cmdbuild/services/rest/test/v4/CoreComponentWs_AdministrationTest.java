/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.corecomponents.CoreComponent;
import org.cmdbuild.corecomponents.CoreComponentType;
import org.cmdbuild.service.rest.v4.command.CoreComponentWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CoreComponentWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsCoreComponentData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.corecomponents.CoreComponentType.CCT_SCRIPT;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildCoreComponent;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListCoreComponent;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsCoreComponentData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author ldare
 */
public class CoreComponentWs_AdministrationTest extends WsTestBase {

    private final CoreComponentWs_Administration instance;

    private final CoreComponent coreComponent1;
    private final CoreComponent coreComponent2;
    private final List<CoreComponent> listCoreComponent;
    private final WsCoreComponentData data;

    public CoreComponentWs_AdministrationTest() {
        CoreComponentWsCommand command = new CoreComponentWsCommand(coreComponentService);
        instance = new CoreComponentWs_Administration(coreComponentService, command);
        coreComponent1 = mockBuildCoreComponent(A_KNOWN_COMPONENT_CODE1);
        coreComponent2 = mockBuildCoreComponent(A_KNOWN_COMPONENT_CODE2);
        listCoreComponent = mockBuildListCoreComponent();
        data = mockBuildWsCoreComponentData();
    }

    @Test
    public void testListByType_Detailed() {
        System.out.println("listByType_Detailed");

        //arrange:
        List<String> expListNames = list(A_KNOWN_COMPONENT_CODE1, A_KNOWN_COMPONENT_CODE2, A_KNOWN_COMPONENT_CODE3);
        when(coreComponentService.getComponentsByType(CoreComponentType.CCT_SCRIPT)).thenReturn(listCoreComponent);

        //act:
        Object resultObject = instance.listByType(CoreComponentType.CCT_SCRIPT, DETAILED);

        //assert:
        verify(coreComponentService).getComponentsByType(any(CoreComponentType.class));
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testListByType_NotDetailed() {
        System.out.println("listByType_NotDetailed");

        //arrange:
        List<String> expListNames = list(A_KNOWN_COMPONENT_CODE1, A_KNOWN_COMPONENT_CODE2, A_KNOWN_COMPONENT_CODE3);
        when(coreComponentService.getComponentsByType(CoreComponentType.CCT_SCRIPT)).thenReturn(listCoreComponent);

        //act:
        Object resultObject = instance.listByType(CoreComponentType.CCT_SCRIPT, NOT_DETAILED);

        //assert:
        verify(coreComponentService).getComponentsByType(any(CoreComponentType.class));
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testGet() {
        System.out.println("get");

        //arrange:
        String expName = A_KNOWN_COMPONENT_CODE1;
        when(coreComponentService.getComponent(expName)).thenReturn(coreComponent1);

        //act:
        Object resultObject = instance.get(A_KNOWN_COMPONENT_CODE1);

        //assert:
        verify(coreComponentService).getComponent(anyString());
        checkName(expName, resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete("exampleCode");

        //assert:
        verify(coreComponentService).deleteComponent(anyString());
        checkSuccess(resultObject);
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_COMPONENT_CODE1;
        when(coreComponentService.createComponent(any(CoreComponent.class))).thenReturn(coreComponent1);

        //act:
        Object resultObject = instance.create(CCT_SCRIPT, data);

        //assert:
        verify(coreComponentService).createComponent(any(CoreComponent.class));
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_COMPONENT_CODE2;
        when(coreComponentService.getComponent(anyString())).thenReturn(coreComponent1);
        when(coreComponentService.updateComponent(any(CoreComponent.class))).thenReturn(coreComponent2);

        //act:
        Object resultObject = instance.update(A_KNOWN_COMPONENT_CODE1, data);

        //assert:
        verify(coreComponentService).getComponent(anyString());
        verify(coreComponentService).updateComponent(any(CoreComponent.class));
        checkName(expName, resultObject);
    }
}
