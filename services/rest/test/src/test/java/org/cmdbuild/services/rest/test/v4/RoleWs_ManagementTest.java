/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.service.rest.v4.command.RoleWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.RoleWs_Management;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkId;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListIds;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildRole;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class RoleWs_ManagementTest extends WsTestBase {

    private final RoleWs_Management instance;
    private final Role role1;
    private final Role role2;
    private final List<Role> roleList;

    public RoleWs_ManagementTest() {

        RoleWsCommand command = new RoleWsCommand(roleRepository, userRepository, userRoleRepository);
        instance = new RoleWs_Management(userRepository, roleRepository, operationUserSupplier, objectTranslationService, command);
        role1 = mockBuildRole(A_KNOWN_ROLE_ID1);
        role2 = mockBuildRole(A_KNOWN_ROLE_ID2);
        roleList = list(role1, role2);
    }

    @Test
    public void testReadMany() {
        System.out.println("readMany");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_ROLE_ID1, A_KNOWN_ROLE_ID2);

        OperationUser opUser = mock(OperationUser.class);
        when(operationUserSupplier.getUser()).thenReturn(opUser);
        LoginUser loginUser = mock(LoginUser.class);
        when(opUser.getLoginUser()).thenReturn(loginUser);
        // this operation requires that cast since Java generic are invariant -> List<Role> cannot be assigned to
        // List<RoleInfo> even if Role extends RoleInfo
        when(loginUser.getRoleInfos()).thenReturn((List<RoleInfo>) (List<?>) roleList);

        //act:
        Object resultObject = instance.readMany(A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET);

        //assert:
        verify(operationUserSupplier).getUser();
        verify(opUser).getLoginUser();
        verify(loginUser).getRoleInfos();
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        OperationUser opUser = mock(OperationUser.class);
        when(operationUserSupplier.getUser()).thenReturn(opUser);
        LoginUser loginUser = mock(LoginUser.class);
        when(opUser.getLoginUser()).thenReturn(loginUser);
        when(loginUser.getRoleInfoByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role1);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_ROLE_ID);

        //assert:
        verify(operationUserSupplier).getUser();
        verify(opUser).getLoginUser();
        verify(loginUser).getRoleInfoByNameOrId(A_KNOWN_ROLE_ID);
        checkId(A_KNOWN_ROLE_ID1, resultObject);
    }
}
