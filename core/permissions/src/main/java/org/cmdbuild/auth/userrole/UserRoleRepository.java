/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.userrole;

import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.user.UserData;

public interface UserRoleRepository {

    void addRoleToUser(long userId, long roleId);

    void removeRoleFromUser(long userId, long roleId);

    default void addRoleToUser(UserData user, Role role) {
        addRoleToUser(user.getId(), role.getId());
    }
}
