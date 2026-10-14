/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.userrole;

import java.util.List;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;

public interface UserRoleService extends UserRoleRepository, UserRepository, RoleRepository {

    default List<UserData> getUsersWithRole(String code) {
        return getAllWithRole(getGroupWithName(code).getId());
    }
}
