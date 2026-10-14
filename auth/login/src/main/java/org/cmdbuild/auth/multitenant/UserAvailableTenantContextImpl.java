/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.multitenant;

import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collection;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;

public class UserAvailableTenantContextImpl implements UserAvailableTenantContext {

    private final static UserAvailableTenantContext MINIMAL_ACCESS_AVAILABLE_TENANT_CONTEXT = builder().build(),
            FULL_ACCESS_AVAILABLE_TENANT_CONTEXT = builder().withIgnoreTenantPolicies(true).build();

    private final Long defaultTenantId;
    private final Set<Long> availableTenantIds;
    private final boolean ignoreTenantPolicies;
    private final TenantActivationPrivileges tenantActivationPrivileges;

    private UserAvailableTenantContextImpl(UserAvailableTenantContextImplBuilder builder) {
        this.availableTenantIds = set(nullToEmpty(builder.availableTenantIds)).immutable();
        if (isNullOrLtEqZero(builder.defaultTenantId) && availableTenantIds.size() == 1) {
            defaultTenantId = getOnlyElement(availableTenantIds);
        } else {
            this.defaultTenantId = builder.defaultTenantId;
        }
        this.ignoreTenantPolicies = firstNotNull(builder.ignoreTenantPolicies, false);
        this.tenantActivationPrivileges = firstNotNull(builder.tenantActivationPrivileges, TAP_DEFAULT);
    }

    @Override
    public TenantActivationPrivileges getTenantActivationPrivileges() {
        return tenantActivationPrivileges;
    }

    @Override
    public @Nullable
    Long getDefaultTenantId() {
        return defaultTenantId;
    }

    @Override
    public Set<Long> getAvailableTenantIds() {
        return availableTenantIds;
    }

    @Override
    public boolean ignoreTenantPolicies() {
        return ignoreTenantPolicies;
    }

    public static UserAvailableTenantContext minimalAccess() {
        return MINIMAL_ACCESS_AVAILABLE_TENANT_CONTEXT;
    }

    public static UserAvailableTenantContext fullAccess() {
        return FULL_ACCESS_AVAILABLE_TENANT_CONTEXT;
    }

    public static UserAvailableTenantContextImplBuilder builder() {
        return new UserAvailableTenantContextImplBuilder();
    }

    public static UserAvailableTenantContextImplBuilder copyOf(UserAvailableTenantContext source) {
        return new UserAvailableTenantContextImplBuilder()
                .withDefaultTenantId(source.getDefaultTenantId())
                .withAvailableTenantIds(source.getAvailableTenantIds())
                .withIgnoreTenantPolicies(source.ignoreTenantPolicies())
                .withTenantActivationPrivileges(source.getTenantActivationPrivileges());
    }

    public static class UserAvailableTenantContextImplBuilder implements Builder<UserAvailableTenantContextImpl, UserAvailableTenantContextImplBuilder> {

        private Long defaultTenantId;
        private Collection<Long> availableTenantIds;
        private Boolean ignoreTenantPolicies;
        private TenantActivationPrivileges tenantActivationPrivileges;

        public UserAvailableTenantContextImplBuilder withDefaultTenantId(Long defaultTenantId) {
            this.defaultTenantId = defaultTenantId;
            return this;
        }

        public UserAvailableTenantContextImplBuilder withAvailableTenantIds(Collection<Long> availableTenantIds) {
            this.availableTenantIds = availableTenantIds;
            return this;
        }

        public UserAvailableTenantContextImplBuilder withIgnoreTenantPolicies(Boolean ignoreTenantPolicies) {
            this.ignoreTenantPolicies = ignoreTenantPolicies;
            return this;
        }

        public UserAvailableTenantContextImplBuilder withTenantActivationPrivileges(TenantActivationPrivileges tenantActivationPrivileges) {
            this.tenantActivationPrivileges = tenantActivationPrivileges;
            return this;
        }

        @Override
        public UserAvailableTenantContextImpl build() {
            return new UserAvailableTenantContextImpl(this);
        }

    }
}
