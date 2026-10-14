/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.service.rest.v4.command.RoleWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.RoleWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsRoleUsers;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Map;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildRole;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildUserDataImpl;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 *
 * @author ldare
 */
public class RoleWs_AdministrationTest extends WsTestBase {

    // the following number will require to be updated if the admin permission dependencies change
    private static final int NUM_ADMIN_PERMISSION_DEPENDENCIES = 60;
    private static final List<Long> A_USER_LIST_TO_ADD = list(1L, 2L, 3L);
    private static final List<Long> A_USER_LIST_TO_REMOVE = list(3L, 4L, 5L);
    private static final String A_ROLE_JSONDATA = "{ \"_id\": 2, \"name\": \"roleName\" }";

    private final RoleWs_Administration instance;
    private final Role role1;
    private final Role role2;
    private final List<Role> listRole;
    private final UserData userData1;
    private final UserData userData2;
    private final PagedElements<UserData> pagedUserData;
    private final WsRoleUsers roleUsers;

    public RoleWs_AdministrationTest() {

        RoleWsCommand command = new RoleWsCommand(roleRepository, userRepository, userRoleRepository);
        instance = new RoleWs_Administration(userRepository, roleRepository, operationUserSupplier, objectTranslationService, command);
        role1 = mockBuildRole(A_KNOWN_ROLE_ID1);
        role2 = mockBuildRole(A_KNOWN_ROLE_ID2);
        listRole = list(role1, role2);
        userData1 = mockBuildUserDataImpl(A_KNOWN_USERDATA_ID1);
        userData2 = mockBuildUserDataImpl(A_KNOWN_USERDATA_ID2);
        pagedUserData = new PagedElements<>(list(userData1, userData2));
        roleUsers = new WsRoleUsers(A_USER_LIST_TO_ADD, A_USER_LIST_TO_REMOVE);
    }

    @Test
    public void testReadMany() {
        System.out.println("readMany");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_ROLE_ID1, A_KNOWN_ROLE_ID2);

        when(roleRepository.getAllGroups()).thenReturn(listRole);

        //act:
        Object resultObject = instance.readMany(A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED);

        //assert:
        verify(roleRepository).getAllGroups();
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role1);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_ROLE_ID);

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        checkId(A_KNOWN_ROLE_ID1, resultObject);
    }

    @Test
    public void testReadRoleUsers_conditionTrue() {
        System.out.println("readRoleUsers");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_USERDATA_ID1, A_KNOWN_USERDATA_ID2);
        CmdbFilter filter = CmFilterUtils.parseFilter(A_TEST_EMPTY_FILTER);
        CmdbSorter sorter = CmSorterUtils.parseSorter(A_TEST_EMPTY_FILTER);
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role1);
        when(userRepository.getAllWithRole(role1.getId(), filter, sorter, A_TEST_LONG_OFFSET, A_TEST_LONG_LIMIT)).thenReturn(pagedUserData);

        //act:
        Object resultObject = instance.readRoleUsers(A_KNOWN_ROLE_ID, A_TEST_EMPTY_FILTER, A_TEST_EMPTY_FILTER, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, true);

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        verify(userRepository).getAllWithRole(role1.getId(), filter, sorter, A_TEST_LONG_OFFSET, A_TEST_LONG_LIMIT);
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testReadRoleUsers_conditionFalse() {
        System.out.println("readRoleUsers");

        //arrange:
        List<Long> expListIds = list(A_KNOWN_USERDATA_ID1, A_KNOWN_USERDATA_ID2);
        CmdbFilter filter = CmFilterUtils.parseFilter(A_TEST_EMPTY_FILTER);
        CmdbSorter sorter = CmSorterUtils.parseSorter(A_TEST_EMPTY_FILTER);
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role1);
        when(userRepository.getAllWithoutRole(role1.getId(), filter, sorter, A_TEST_LONG_OFFSET, A_TEST_LONG_LIMIT)).thenReturn(pagedUserData);

        //act:
        Object resultObject = instance.readRoleUsers(A_KNOWN_ROLE_ID, A_TEST_EMPTY_FILTER, A_TEST_EMPTY_FILTER, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, false);

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        verify(userRepository).getAllWithoutRole(role1.getId(), filter, sorter, A_TEST_LONG_OFFSET, A_TEST_LONG_LIMIT);
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testGetAdminDependencies() {
        System.out.println("getAdminDependencies");

        //act:
        Object resultObject = instance.getAdminDependencies();

        //assert:
        checkSuccess(resultObject);
        // the constant used here must be updated if the admin permission dependencies change
        assertEquals(NUM_ADMIN_PERMISSION_DEPENDENCIES, ((Map) ((Map) resultObject).get("data")).size());
    }

//    @Test
//    public void testUpdateUsersPut() {
//        System.out.println("updateUsersPut");
//
//        //arrange:
//        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
//        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
//        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role1);
//
//        //act:
//        Object resultObject = instance.updateUsers(A_KNOWN_ROLE_ID, roleUsers);
//
//        //assert:
//        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
//        verify(userRoleRepository, times(A_USER_LIST_TO_ADD.size())).addRoleToUser(
//                captor1.capture(),
//                eq(role1.getId())
//        );
//        verify(userRoleRepository, times(A_USER_LIST_TO_REMOVE.size())).removeRoleFromUser(
//                captor2.capture(),
//                eq(role1.getId())
//        );
//        List<Long> capturedIdsAdded = captor1.getAllValues();
//        List<Long> capturedIdsRemoved = captor2.getAllValues();
//        assertTrue(capturedIdsAdded.containsAll(A_USER_LIST_TO_ADD));
//        assertTrue(capturedIdsRemoved.containsAll(A_USER_LIST_TO_REMOVE));
//        checkSuccess(resultObject);
//    }

    @Test
    public void testUpdateUsers() {
        System.out.println("updateUsers");

        //arrange:
        ArgumentCaptor<Long> captor1 = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> captor2 = ArgumentCaptor.forClass(Long.class);
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role1);

        //act:
        Object resultObject = instance.updateUsers(A_KNOWN_ROLE_ID, roleUsers);

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        verify(userRoleRepository, times(A_USER_LIST_TO_ADD.size())).addRoleToUser(
                captor1.capture(),
                eq(role1.getId())
        );
        verify(userRoleRepository, times(A_USER_LIST_TO_REMOVE.size())).removeRoleFromUser(
                captor2.capture(),
                eq(role1.getId())
        );
        List<Long> capturedIdsAdded = captor1.getAllValues();
        List<Long> capturedIdsRemoved = captor2.getAllValues();
        assertTrue(capturedIdsAdded.containsAll(A_USER_LIST_TO_ADD));
        assertTrue(capturedIdsRemoved.containsAll(A_USER_LIST_TO_REMOVE));
        checkSuccess(resultObject);
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        when(roleRepository.create(any(Role.class))).thenReturn(role2);

        //act:
        Object resultObject = instance.create(A_ROLE_JSONDATA);

        //assert:
        verify(roleRepository).create(any(Role.class));
        checkId(A_KNOWN_ROLE_ID2, resultObject);

    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role1);
        // simulating update by returning different RoleImpl
        when(roleRepository.update(any(Role.class))).thenReturn(role2);

        //act:
        Object resultObject = instance.update(A_KNOWN_ROLE_ID, A_ROLE_JSONDATA);

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        verify(roleRepository).update(any(Role.class));
        checkId(A_KNOWN_ROLE_ID2, resultObject);
    }

}
