/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant.api;

import java.util.Set;
import jakarta.annotation.Nullable;

public interface UserTenantContext {

    Set<Long> getActiveTenantIds();

    boolean ignoreTenantPolicies();

    @Nullable
    Long getDefaultTenantId();

    default boolean canAccessTenant(long tenantId) {
        return ignoreTenantPolicies() || getActiveTenantIds().contains(tenantId);
    }

    default boolean hasAnyTenant() {
        return !getActiveTenantIds().isEmpty();
    }

}
