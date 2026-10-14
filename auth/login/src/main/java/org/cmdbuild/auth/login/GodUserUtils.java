/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.login;

import static com.google.common.base.Objects.equal;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.EnumSet;
import static org.cmdbuild.auth.AuthConst.GOD_DUMMY_GROUP_NAME;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.role.GroupConfigImpl;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.role.RoleType;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.LoginUserImpl;
import org.cmdbuild.auth.user.UserPrivilegesImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.auth.AuthConst.SYSTEM_USER;

public class GodUserUtils {

    public static boolean isGodUser(LoginUserIdentity identity) {
        return isGodUser(identity.getValue());
    }

    public static boolean isGodUser(LoginUser user) {
        return isGodUser(user.getUsername());
    }

    public static boolean isGodUser(String username) {
//        return equal(GOD_USER, username);
        return username.toLowerCase().matches("cmgod|system");//TODO remove this, replace with above after 3.1.1
    }

    public static boolean isGodDummyGroup(String groupName) {
        return equal(GOD_DUMMY_GROUP_NAME, groupName);
    }

    public static LoginUser getGodLoginUser() {
        return LoginUserImpl.builder().withUsername(SYSTEM_USER).withDescription("System").withGroups(list(getGodDummyGroup())).build();
    }

    public static Role getGodDummyGroup() {
        return RoleImpl.builder()
                .withName(GOD_DUMMY_GROUP_NAME)
                .withConfig(GroupConfigImpl.builder().build())
                .withCustomPrivileges(emptyMap())
                .withPrivileges(emptyList())
                .withType(RoleType.ADMIN)
                .build();
    }

    public static UserPrivileges getGodPrivilegeContext() {
        return UserPrivilegesImpl.builder()
                .withGroups(getGodDummyGroup())
                .withRolePrivileges(EnumSet.allOf(RolePrivilege.class))
                .build();
    }

}
