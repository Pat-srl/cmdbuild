/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import java.util.EnumSet;
import org.cmdbuild.auth.grant.GrantPrivilege;
import static org.cmdbuild.auth.grant.GrantPrivilege.GP_WRITE;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_SERVICE;
import static org.cmdbuild.auth.grant.GrantPrivilegeScope.GPS_UI;
import org.cmdbuild.auth.grant.GroupOfPrivileges;
import org.cmdbuild.auth.grant.GroupOfPrivilegesImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.junit.Test;

public class UserClassUtilsTest {

    @Test(expected = Exception.class)
    public void testOtherPermissionsMerge() {

        GroupOfPrivileges privileges = GroupOfPrivilegesImpl.builder()
                .withSource("ASD")
                .withPrivileges(GPS_SERVICE, set(GP_WRITE))
                .withCustomPrivileges(map())
                .build();

    }

    @Test
    public void testOtherPermissionsMerge2() {

        GroupOfPrivileges privileges = GroupOfPrivilegesImpl.builder()
                .withSource("ASD")
                .withPrivileges(GPS_SERVICE, set(GP_WRITE))
                .withPrivileges(GPS_UI, EnumSet.allOf(GrantPrivilege.class))
                .withCustomPrivileges(map())
                .build();

    }

}
