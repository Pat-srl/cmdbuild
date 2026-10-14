/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant.api;

import java.util.Set;
import jakarta.annotation.Nullable;

public interface UserAvailableTenantContext {

    final static TenantActivationPrivileges TAP_DEFAULT = TenantActivationPrivileges.TAP_ANY;

    @Nullable
    Long getDefaultTenantId();

    Set<Long> getAvailableTenantIds();

    boolean ignoreTenantPolicies();

    TenantActivationPrivileges getTenantActivationPrivileges();

    enum TenantActivationPrivileges {

        TAP_ANY, TAP_ONE
    }

}
