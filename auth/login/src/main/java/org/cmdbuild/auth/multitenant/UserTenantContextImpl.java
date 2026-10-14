/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.Serializable;
import static java.util.Collections.emptySet;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;

/**
 * this may be serialized in session
 */
public class UserTenantContextImpl implements UserTenantContext, Serializable {

    private final static UserTenantContext FULL_ACCESS_USER = new UserTenantContextImpl(true, null),
            MINIMAL_ACCESS_USER = new UserTenantContextImpl(false, emptySet());

    private final boolean ignoreTenantPolicies;
    private final Set<Long> activeTenantIds;
    private final Long defaultTenantId;

    /**
     *
     * @param ignoreTenantPolicies
     * @param activeTenantIds ignored (nullable) if ignoreTenantPolicies is
     * true, non null otherwise
     */
    public UserTenantContextImpl(boolean ignoreTenantPolicies, @Nullable Iterable<Long> activeTenantIds) {
        this(ignoreTenantPolicies, activeTenantIds, null);
    }

    /**
     *
     * @param ignoreTenantPolicies
     * @param activeTenantIds ignored (nullable) if ignoreTenantPolicies is
     * true, non null otherwise
     * @param defaultTenantId
     */
    public UserTenantContextImpl(boolean ignoreTenantPolicies, @Nullable Iterable<Long> activeTenantIds, @Nullable Long defaultTenantId) {
        checkArgument(ignoreTenantPolicies || activeTenantIds != null);
        this.ignoreTenantPolicies = ignoreTenantPolicies;
        this.activeTenantIds = ImmutableSet.copyOf(firstNonNull(activeTenantIds, emptySet()));
        checkArgument(defaultTenantId == null || this.activeTenantIds.contains(defaultTenantId), "invalid default tenant id");
        this.defaultTenantId = this.activeTenantIds.size() == 1 ? getOnlyElement(this.activeTenantIds) : defaultTenantId;
    }

    @Nullable
    @Override
    public Long getDefaultTenantId() {
        return defaultTenantId;
    }

    @Override
    public Set<Long> getActiveTenantIds() {
        return activeTenantIds;
    }

    @Override
    public boolean ignoreTenantPolicies() {
        return ignoreTenantPolicies;
    }

    @Override
    public String toString() {
        return "SimpleUserTenantContext{" + "ignoreTenantPolicies=" + ignoreTenantPolicies + ", activeTenantIds=" + activeTenantIds + ", defaultTenantId=" + defaultTenantId + '}';
    }

    public static UserTenantContext fullAccessUser() {
        return FULL_ACCESS_USER;
    }

    public static UserTenantContext minimalAccessUser() {
        return MINIMAL_ACCESS_USER;
    }

}
