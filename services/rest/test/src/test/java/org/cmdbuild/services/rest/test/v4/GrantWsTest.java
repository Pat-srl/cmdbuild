package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.auth.grant.GrantData;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.service.rest.v4.command.GrantWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.GrantWs;
import org.cmdbuild.service.rest.v4.model.WsGrantData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsGrantDataList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class GrantWsTest extends WsTestBase {

    private final GrantWs instance;

    private final Role role;
    private final List<GrantData> grantDataList;
    private final List<Role> roleList;
    private final List<WsGrantData> wsGrantDataList;

    public GrantWsTest() {
        GrantWsCommand command = new GrantWsCommand(grantDataRepository, grantService, roleRepository);
        this.instance = new GrantWs(grantService, command);

        this.role = mockBuildRole(A_KNOWN_ROLE_ID1);
        this.grantDataList = mockBuildGrantDataList();
        this.roleList = mockBuildRoleList();
        this.wsGrantDataList = mockBuildWsGrantDataList();
    }

    @Test
    public void testReadMany_includeRecordsWithoutGrant() {
        System.out.println("testReadMany_includeRecordsWithoutGrant");

        //arrange:
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role);
        when(grantService.getGrantsForRoleIncludeRecordsWithoutGrant(anyLong())).thenReturn(grantDataList);

        //act:
        Object resultObject = instance.readMany(A_KNOWN_ROLE_ID, A_TEST_EMPTY_FILTER, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, INCLUDE, INCLUDE);

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        verify(grantService).getGrantsForRoleIncludeRecordsWithoutGrant(anyLong());
        checkResultList(list(A_KNOWN_GRANTDATA_ID1, A_KNOWN_GRANTDATA_ID2, A_KNOWN_GRANTDATA_ID3), resultObject, "_id");
    }

    @Test
    public void testReadMany_notIncludeRecordsWithoutGrant() {
        System.out.println("testReadMany_notIncludeRecordsWithoutGrant");

        //arrange:
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role);
        when(grantDataRepository.getGrantsForRole(anyLong())).thenReturn(grantDataList);

        //act:
        Object resultObject = instance.readMany(A_KNOWN_ROLE_ID, A_TEST_EMPTY_FILTER, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, INCLUDE, DONT_INCLUDE);

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        verify(grantDataRepository).getGrantsForRole(anyLong());
        checkResultList(list(A_KNOWN_GRANTDATA_ID1, A_KNOWN_GRANTDATA_ID2, A_KNOWN_GRANTDATA_ID3), resultObject, "_id");
    }

    @Test
    public void testReadOneByObject() {
        System.out.println("testReadOneByObject");

        //arrange:
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role);
        when(grantService.getGrantDataByRoleAndTypeAndName(anyLong(), eq(POT_CLASS), eq("objectTypeName"))).thenReturn(grantDataList.get(0));

        //act:
        Object resultObject = instance.readOneByObject(A_KNOWN_ROLE_ID, POT_CLASS.toString(), "objectTypeName");

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        verify(grantService).getGrantDataByRoleAndTypeAndName(anyLong(), eq(POT_CLASS), eq("objectTypeName"));
        checkResultList(list(A_KNOWN_GRANTDATA_ID1), resultObject, "_id");
    }

    @Test
    public void testReadOneByObject_ALL() {
        System.out.println("testReadOneByObject_ALL");

        //arrange:
        when(roleRepository.getAllGroups()).thenReturn(roleList);
        when(roleRepository.getByNameOrId(roleList.get(0).getName())).thenReturn(roleList.get(0));
        when(roleRepository.getByNameOrId(roleList.get(1).getName())).thenReturn(roleList.get(1));
        when(roleRepository.getByNameOrId(roleList.get(2).getName())).thenReturn(roleList.get(2));
        when(grantService.getGrantDataByRoleAndTypeAndName(roleList.get(0).getId(), POT_CLASS, "objectTypeName")).thenReturn(grantDataList.get(0));
        when(grantService.getGrantDataByRoleAndTypeAndName(roleList.get(1).getId(), POT_CLASS, "objectTypeName")).thenReturn(grantDataList.get(1));
        when(grantService.getGrantDataByRoleAndTypeAndName(roleList.get(2).getId(), POT_CLASS, "objectTypeName")).thenReturn(grantDataList.get(2));

        //act:
        Object resultObject = instance.readOneByObject(A_KNOWN_ALL_ID, POT_CLASS.toString(), "objectTypeName");

        //assert:
        verify(roleRepository).getAllGroups();
        verify(roleRepository).getByNameOrId(roleList.get(0).getName());
        verify(roleRepository).getByNameOrId(roleList.get(1).getName());
        verify(roleRepository).getByNameOrId(roleList.get(2).getName());
        verify(grantService).getGrantDataByRoleAndTypeAndName(roleList.get(0).getId(), POT_CLASS, "objectTypeName");
        verify(grantService).getGrantDataByRoleAndTypeAndName(roleList.get(1).getId(), POT_CLASS, "objectTypeName");
        verify(grantService).getGrantDataByRoleAndTypeAndName(roleList.get(2).getId(), POT_CLASS, "objectTypeName");
        checkResultList(list(A_KNOWN_GRANTDATA_ID1, A_KNOWN_GRANTDATA_ID2, A_KNOWN_GRANTDATA_ID3), resultObject, "_id");
    }

    @Test
    public void testUpdate() {
        System.out.println("testUpdate");

        //arrange:
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(role);
        when(grantDataRepository.updateGrantsForRole(anyLong(), anyCollection())).thenReturn(grantDataList);

        //act:
        Object resultObject = instance.update(A_KNOWN_ROLE_ID, wsGrantDataList);

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        verify(grantDataRepository).updateGrantsForRole(anyLong(), anyCollection());
        checkResultList(list(A_KNOWN_GRANTDATA_ID1, A_KNOWN_GRANTDATA_ID2, A_KNOWN_GRANTDATA_ID3), resultObject, "_id");
    }

    @Test
    public void testUpdate_ALL() {
        System.out.println("testUpdate_ALL");

        //arrange:
        when(roleRepository.getByNameOrId(wsGrantDataList.get(0).getRole())).thenReturn(roleList.get(0));
        when(roleRepository.getByNameOrId(wsGrantDataList.get(1).getRole())).thenReturn(roleList.get(1));
        when(roleRepository.getByNameOrId(wsGrantDataList.get(2).getRole())).thenReturn(roleList.get(2));
        when(grantDataRepository.updateGrantsForRole(eq(roleList.get(0).getId()), anyCollection())).thenReturn(grantDataList);
        when(grantDataRepository.updateGrantsForRole(eq(roleList.get(1).getId()), anyCollection())).thenReturn(grantDataList);
        when(grantDataRepository.updateGrantsForRole(eq(roleList.get(2).getId()), anyCollection())).thenReturn(grantDataList);

        //act:
        Object resultObject = instance.update(A_KNOWN_ALL_ID, wsGrantDataList);

        //assert:
        verify(roleRepository).getByNameOrId(wsGrantDataList.get(0).getRole());
        verify(roleRepository).getByNameOrId(wsGrantDataList.get(1).getRole());
        verify(roleRepository).getByNameOrId(wsGrantDataList.get(2).getRole());
        verify(grantDataRepository).updateGrantsForRole(eq(roleList.get(0).getId()), anyCollection());
        verify(grantDataRepository).updateGrantsForRole(eq(roleList.get(1).getId()), anyCollection());
        verify(grantDataRepository).updateGrantsForRole(eq(roleList.get(2).getId()), anyCollection());
        checkResultList(list(A_KNOWN_GRANTDATA_ID1, A_KNOWN_GRANTDATA_ID2, A_KNOWN_GRANTDATA_ID3, A_KNOWN_GRANTDATA_ID1, A_KNOWN_GRANTDATA_ID2, A_KNOWN_GRANTDATA_ID3, A_KNOWN_GRANTDATA_ID1, A_KNOWN_GRANTDATA_ID2, A_KNOWN_GRANTDATA_ID3), resultObject, "_id");
    }
}

