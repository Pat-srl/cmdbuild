/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.userrole;

import org.cmdbuild.auth.role.Role;

public interface UserRole {

    Role getRole();

    boolean isDefault();

    default boolean isActive() {
        return getRole().isActive();
    }

    default Long getId() {
        return getRole().getId();
    }

    default String getDescription() {
        return getRole().getDescription();
    }
}
