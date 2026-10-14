/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v3;


import org.cmdbuild.auth.grant.GrantData;
import org.cmdbuild.auth.grant.PrivilegedObjectType;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.service.rest.v3.endpoint.GrantWs;
import org.cmdbuild.service.rest.v4.command.GrantWsCommand;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.auth.grant.PrivilegedObjectType.POT_CLASS;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildGrantData;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildListRoleWithDifferentNames;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class GrantWsTest extends WsTestBase {

    private final GrantWs instance;

    private final List<Role> roleList;
    private final GrantData grantData1;
    private final GrantData grantData2;
    private final GrantData grantData3;

    private final String objectTypeName = "placeholderName";

    public GrantWsTest() {
        GrantWsCommand command = new GrantWsCommand(grantDataRepository, grantService, roleRepository);
        org.cmdbuild.service.rest.v4.endpoint.GrantWs v4Instance = new org.cmdbuild.service.rest.v4.endpoint.GrantWs(grantService, command);
        instance = new GrantWs(v4Instance, roleRepository, grantService);

        roleList = mockBuildListRoleWithDifferentNames();
        grantData1 = mockBuildGrantData(A_KNOWN_GRANTDATA_ID1, A_KNOWN_ROLE_ID1);
        grantData2 = mockBuildGrantData(A_KNOWN_GRANTDATA_ID2, A_KNOWN_ROLE_ID2);
        grantData3 = mockBuildGrantData(A_KNOWN_GRANTDATA_ID3, A_KNOWN_ROLE_ID3);
    }

    @Test
    public void testReadOneByObject_ALL() {
        System.out.println("readOneByObject_ALL");

        //arrange:
        when(roleRepository.getAllGroups()).thenReturn(roleList);
        when(roleRepository.getByNameOrId(roleList.get(0).getName())).thenReturn(roleList.get(0));
        when(roleRepository.getByNameOrId(roleList.get(1).getName())).thenReturn(roleList.get(1));
        when(roleRepository.getByNameOrId(roleList.get(2).getName())).thenReturn(roleList.get(2));
        when(grantService.getGrantDataByRoleAndTypeAndName(eq(roleList.get(0).getId()), any(PrivilegedObjectType.class), anyString())).thenReturn(grantData1);
        when(grantService.getGrantDataByRoleAndTypeAndName(eq(roleList.get(1).getId()), any(PrivilegedObjectType.class), anyString())).thenReturn(grantData2);
        when(grantService.getGrantDataByRoleAndTypeAndName(eq(roleList.get(2).getId()), any(PrivilegedObjectType.class), anyString())).thenReturn(grantData3);

        //act:
        Object resultObject = instance.readOneByObject(A_KNOWN_ALL_ID, POT_CLASS.toString(), objectTypeName);

        //assert:
        verify(roleRepository).getAllGroups();
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_NAME1);
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_NAME2);
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_NAME3);
        verify(grantService).getGrantDataByRoleAndTypeAndName(eq(roleList.get(0).getId()), any(PrivilegedObjectType.class), anyString());
        verify(grantService).getGrantDataByRoleAndTypeAndName(eq(roleList.get(1).getId()), any(PrivilegedObjectType.class), anyString());
        verify(grantService).getGrantDataByRoleAndTypeAndName(eq(roleList.get(2).getId()), any(PrivilegedObjectType.class), anyString());
        checkResultList(list(A_KNOWN_ROLE_ID1, A_KNOWN_ROLE_ID2, A_KNOWN_ROLE_ID3), resultObject, "role");
    }

    @Test
    public void testReadOneByObject() {
        System.out.println("readOneByObject");

        //arrange:
        when(roleRepository.getByNameOrId(A_KNOWN_ROLE_ID)).thenReturn(roleList.get(0));
        when(grantService.getGrantDataByRoleAndTypeAndName(eq(roleList.get(0).getId()), any(PrivilegedObjectType.class), anyString())).thenReturn(grantData1);

        //act:
        Object resultObject = instance.readOneByObject(A_KNOWN_ROLE_ID, POT_CLASS.toString(), objectTypeName);

        //assert:
        verify(roleRepository).getByNameOrId(A_KNOWN_ROLE_ID);
        verify(grantService).getGrantDataByRoleAndTypeAndName(eq(roleList.get(0).getId()), any(PrivilegedObjectType.class), anyString());
        checkResult(A_KNOWN_ROLE_ID1, resultObject, "role");
    }
}
