package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.cmdbuild.auth.login.NullPrivilegeContext.nullPrivilegeContext;
import static org.cmdbuild.auth.grant.SystemPrivilegeContext.systemPrivilegeContext;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import static org.cmdbuild.auth.multitenant.UserTenantContextImpl.fullAccessUser;
import static org.cmdbuild.auth.multitenant.UserTenantContextImpl.minimalAccessUser;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RolePrivilege;
import static org.cmdbuild.auth.user.LoginUserImpl.ANONYMOUS_LOGIN_USER;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class OperationUserImpl implements OperationUser {

    private final static OperationUser ANONYMOUS = builder().withAuthenticatedUser(ANONYMOUS_LOGIN_USER).build(); //default user is anonymous with minimal access

    private final UserPrivileges privilegeContext;
    private final LoginUser loginUser, sponsor;
    private final UserTenantContext userTenantContext;
    private final Role defaultGroup;
    private final Map<String, String> params;

    private OperationUserImpl(LoginUser loginUser, LoginUser sponsor, UserPrivileges privilegeCtx, @Nullable Role defaultGroup, UserTenantContext userTenantContext, Map<String, String> params) {
        this.privilegeContext = checkNotNull(privilegeCtx);
        this.loginUser = checkNotNull(loginUser);
        this.sponsor = firstNotNull(sponsor, loginUser);
        this.userTenantContext = checkNotNull(userTenantContext);
        this.defaultGroup = defaultGroup;
        this.params = isNullOrEmpty(params) ? emptyMap() : map(params).immutable();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public Map<String, UserPrivilegesForObject> getAllPrivileges() {
        return privilegeContext.getAllPrivileges();
    }

    @Override
    public Set<RolePrivilege> getRolePrivileges() {
        return privilegeContext.getRolePrivileges();
    }

    @Override
    public UserTenantContext getUserTenantContext() {
        return userTenantContext;
    }

    @Override
    public LoginUser getLoginUser() {
        return loginUser;
    }

    @Override
    public LoginUser getSponsor() {
        return sponsor;
    }

    @Override
    @Nullable
    public Role getDefaultGroupOrNull() {
        return defaultGroup;
    }

    @Override
    public UserPrivileges getPrivilegeContext() {
        return privilegeContext;
    }

    @Override
    public String toString() {
        return "OperationUserImpl{" + "privilegeContext=" + privilegeContext + ", authUser=" + loginUser + ", userTenantContext=" + userTenantContext + ", defaultGroup=" + defaultGroup + '}';
    }

    public static OperationUserBuilder builder() {
        return new OperationUserBuilder();
    }

    public static OperationUserBuilder copyOf(OperationUser operationUser) {
        return builder()
                .withAuthenticatedUser(operationUser.getLoginUser())
                .withSponsor(operationUser.getSponsor())
                .withDefaultGroup(operationUser.getDefaultGroupOrNull())
                .withPrivilegeContext(operationUser.getPrivilegeContext())
                .withUserTenantContext(operationUser.getUserTenantContext())
                .withParams(operationUser.getParams());
    }

    public static OperationUser anonymousOperationUser() {
        return ANONYMOUS;
    }

    public static OperationUser sysAdminOperationUser() {
        return builder()
                .withPrivilegeContext(systemPrivilegeContext())
                .withUserTenantContext(fullAccessUser())
                .build();
    }

    public static class OperationUserBuilder implements Builder<OperationUser, OperationUserBuilder> {

        private UserPrivileges privilegeContext = nullPrivilegeContext();
        private LoginUser authenticatedUser, sponsor;
        private UserTenantContext userTenantContext = minimalAccessUser();
        private Role defaultGroup;
        private final Map<String, String> params = map();

        @Override
        public OperationUser build() {
            return new OperationUserImpl(authenticatedUser, sponsor, privilegeContext, defaultGroup, userTenantContext, params);
        }

        public OperationUserBuilder withSessionType(SessionType sessionType) {
            return this.withParam(SESSION_TYPE, serializeEnum(sessionType));
        }

        public OperationUserBuilder withPrivilegeContext(UserPrivileges privilegeContext) {
            this.privilegeContext = privilegeContext;
            return this;
        }

        public OperationUserBuilder withAuthenticatedUser(LoginUser authenticatedUser) {
            this.authenticatedUser = authenticatedUser;
            return this;
        }

        public OperationUserBuilder withSponsor(LoginUser sponsor) {
            this.sponsor = sponsor;
            return this;
        }

        public OperationUserBuilder withUserTenantContext(UserTenantContext userTenantContext) {
            this.userTenantContext = userTenantContext;
            return this;
        }

        public OperationUserBuilder withParams(Map<String, String> params) {
            this.params.putAll(params);
            return this;
        }

        public OperationUserBuilder withParam(String key, String value) {
            this.params.put(key, value);
            return this;
        }

        public OperationUserBuilder withTargetDevice(TargetDevice targetDevice) {
            return this.withParam(TARGET_DEVICE, serializeEnum(targetDevice));
        }

        public OperationUserBuilder withDefaultGroup(@Nullable Role defaultGroup) {
            this.defaultGroup = defaultGroup;
            return this;
        }

    }

}
