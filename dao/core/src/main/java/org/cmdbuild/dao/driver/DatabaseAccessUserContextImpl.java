/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import jakarta.annotation.Nullable;

public class DatabaseAccessUserContextImpl implements DatabaseAccessUserContext {

    private final boolean ignoreTenantPolicies;
    private final Set<Long> tenantIdSet;
    private final String username, groupname, sessionId, language;
    private final DatabaseAccessUserScope scope;

    public DatabaseAccessUserContextImpl(@Nullable String username, @Nullable String groupname, @Nullable String sessionId, boolean ignoreTenantPolicies, @Nullable Set<Long> tenantIdSet, DatabaseAccessUserScope scope, @Nullable String language) {
        this.ignoreTenantPolicies = ignoreTenantPolicies;
        this.tenantIdSet = ignoreTenantPolicies ? Collections.emptySet() : ImmutableSet.copyOf(tenantIdSet);
        this.username = username;
        this.groupname = groupname;
        this.sessionId = sessionId;
        this.language = language;
        this.scope = checkNotNull(scope);
    }

    @Override
    public boolean ignoreTenantPolicies() {
        return ignoreTenantPolicies;
    }

    @Override
    public Set<Long> getTenantIds() {
        return tenantIdSet;
    }

    @Nullable
    @Override
    public String getUsername() {
        return username;
    }

    @Nullable
    @Override
    public String getRolename() {
        return groupname;
    }

    @Nullable
    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public DatabaseAccessUserScope getScope() {
        return scope;
    }

    @Override
    public DatabaseAccessUserContext withScope(DatabaseAccessUserScope newScope) {
        return new DatabaseAccessUserContextImpl(username, groupname, sessionId, ignoreTenantPolicies, tenantIdSet, newScope, language);
    }

    @Override
    public String toString() {
        return "SimpleDatabaseAccessUserContext{" + "ignoreTenantPolicies=" + ignoreTenantPolicies + ", tenantIdSet=" + tenantIdSet + ", username=" + username + '}';
    }

    @Override
    public String getLanguage() {
        return language;
    }

}
