/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.dao.beans;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SessionDataJsonBean {

    public final static int BEAN_VERSION_ID = 3;

    private final int version;
    private final List<OperationUserJsonBean> operationUserStack;
    private final Map<String, Object> sessionData;

    public SessionDataJsonBean(List<OperationUserJsonBean> operationUserStack, Map<String, Object> sessionData) {
        this(BEAN_VERSION_ID, operationUserStack, sessionData);
    }

    @JsonCreator
    public SessionDataJsonBean(
            @JsonProperty("version") int version,
            @JsonProperty("operationUserStack") List<OperationUserJsonBean> operationUserStack,
            @JsonProperty("sessionData") Map<String, Object> sessionData) {
        checkArgument(version == BEAN_VERSION_ID);
        this.version = BEAN_VERSION_ID;
        this.operationUserStack = ImmutableList.copyOf(operationUserStack);
        this.sessionData = map(sessionData).immutable();
    }

    public int getVersion() {
        return version;
    }

    public List<OperationUserJsonBean> getOperationUserStack() {
        return operationUserStack;
    }

    public Map<String, Object> getSessionData() {
        return sessionData;
    }

    public static class OperationUserJsonBean {

        private final PrivilegeContextJsonBean privilegeContext;
        private final String group;
        private final AuthenticatedUserJsonBean authenticatedUser, sponsor;
        private final UserTenantContextJsonBean userTenantContext;
        private final Map<String, String> params;

        public OperationUserJsonBean(
                @JsonProperty("privilegeContext") PrivilegeContextJsonBean privilegeContext,
                @JsonProperty("group") String group,
                @JsonProperty("authenticatedUser") AuthenticatedUserJsonBean authenticatedUser,
                @JsonProperty("sponsor") AuthenticatedUserJsonBean sponsor,
                @JsonProperty("userTenantContext") UserTenantContextJsonBean userTenantContext,
                @JsonProperty("params") Map<String, String> params) {
            this.privilegeContext = checkNotNull(privilegeContext);
            this.group = group;
            this.authenticatedUser = checkNotNull(authenticatedUser);
            this.sponsor = firstNotNull(sponsor, authenticatedUser);
            this.userTenantContext = checkNotNull(userTenantContext);
            this.params = map(firstNotNull(params, emptyMap())).immutable();
        }

        public PrivilegeContextJsonBean getPrivilegeContext() {
            return privilegeContext;
        }

        public String getGroup() {
            return group;
        }

        public AuthenticatedUserJsonBean getAuthenticatedUser() {
            return authenticatedUser;
        }

        public AuthenticatedUserJsonBean getSponsor() {
            return sponsor;
        }

        public UserTenantContextJsonBean getUserTenantContext() {
            return userTenantContext;
        }

        public Map<String, String> getParams() {
            return params;
        }

    }

    /**
     * privileges are build from active groups
     */
    public static class PrivilegeContextJsonBean {

        private final List<String> groups;

        public PrivilegeContextJsonBean(@JsonProperty("groups") List<String> groups) {
            this.groups = ImmutableList.copyOf(groups);
        }

        public List<String> getGroups() {
            return groups;
        }

    }

    public static class AuthenticatedUserJsonBean {

        private final String userType, username;

        public AuthenticatedUserJsonBean(
                @JsonProperty("userType") String userType,
                @JsonProperty("username") String username) {
            this.userType = checkNotBlank(userType);
            this.username = checkNotBlank(username);
        }

        public String getUserType() {
            return userType;
        }

        public String getUsername() {
            return username;
        }

    }

    public static class UserTenantContextJsonBean {

        private final List<Long> activeTenantIds;
        private final Long defaultTenantId;
        private final boolean ignoreTenantPolicies;

        public UserTenantContextJsonBean(
                @JsonProperty("activeTenantIds") Collection<Long> activeTenantIds,
                @JsonProperty("defaultTenantId") Long defaultTenantId,
                @JsonProperty("ignoreTenantPolicies") boolean ignoreTenantPolicies) {
            this.activeTenantIds = ImmutableList.copyOf(activeTenantIds);
            this.defaultTenantId = defaultTenantId;
            this.ignoreTenantPolicies = ignoreTenantPolicies;
        }

        public List<Long> getActiveTenantIds() {
            return activeTenantIds;
        }

        public Long getDefaultTenantId() {
            return defaultTenantId;
        }

        public boolean getIgnoreTenantPolicies() {
            return ignoreTenantPolicies;
        }

    }
}
