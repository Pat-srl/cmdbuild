package org.cmdbuild.auth.login;

import org.cmdbuild.auth.user.SessionType;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.cmdbuild.auth.user.SessionType.ST_INTERACTIVE;
import org.cmdbuild.auth.multitenant.api.TenantLoginData;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.ui.TargetDevice.TD_MOBILE;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class LoginDataImpl implements LoginData, TenantLoginData {

    private final String loginString;
    private final String unencryptedPassword;
    private final String loginGroupName;
    private final boolean passwordRequired, serviceUsersAllowed, forceUserGroup, noPersist;
    private final Boolean ignoreTenantPolicies;
    private final Long defaultTenant;
    private final Set<Long> activeTenants;
    private final TargetDevice targetDevice;
    private final SessionType sessionType;

    private LoginDataImpl(LoginDataImplBuilder builder) {
        this.loginString = builder.loginString;
        this.unencryptedPassword = builder.unencryptedPassword;
        this.loginGroupName = builder.loginGroupName;
        this.passwordRequired = builder.passwordRequired;
        this.serviceUsersAllowed = builder.serviceUsersAllowed;
        this.defaultTenant = builder.defaultTenant;
        this.activeTenants = builder.activeTenants;
        this.ignoreTenantPolicies = builder.ignoreTenantPolicies;
        this.forceUserGroup = builder.forceUserGroup;
        this.noPersist = firstNotNull(builder.noPersist, false);
        this.targetDevice = builder.targetDevice;
        this.sessionType = firstNotNull(builder.sessionType, ST_INTERACTIVE); //TODO remove defalut, set value everywhere
        checkArgument(targetDevice == null || EnumSet.of(TD_DEFAULT, TD_MOBILE).contains(targetDevice), "invalid target device = %s", targetDevice);
    }

    @Override
    public SessionType getSessionType() {
        return sessionType;
    }

    @Override
    public boolean forceUserGroup() {
        return forceUserGroup;
    }

    @Override
    public boolean noPersist() {
        return noPersist;
    }

    @Override
    public String getLoginString() {
        return loginString;
    }

    @Override
    public String getPassword() {
        return unencryptedPassword;
    }

    @Override
    @Nullable
    public String getLoginGroupName() {
        return loginGroupName;
    }

    @Override
    public boolean isPasswordRequired() {
        return passwordRequired;
    }

    @Override
    public boolean isServiceUsersAllowed() {
        return serviceUsersAllowed;
    }

    @Nullable
    @Override
    public Long getDefaultTenant() {
        return defaultTenant;
    }

    @Nullable
    @Override
    public Set<Long> getActiveTenants() {
        return activeTenants;
    }

    @Override
    @Nullable
    public Boolean ignoreTenantPolicies() {
        return ignoreTenantPolicies;
    }

    @Override
    @Nullable
    public TargetDevice getTargetDevice() {
        return targetDevice;
    }

    @Override
    public String toString() {
        return "LoginDataImpl{" + "login=" + loginString + ", group=" + loginGroupName + '}';
    }

    public static LoginDataImplBuilder builder() {
        return new LoginDataImplBuilder();
    }

    public static LoginDataImplBuilder copyOf(LoginData source) {
        return new LoginDataImplBuilder()
                .withActiveTenants(source.getActiveTenants())
                .withDefaultTenant(source.getDefaultTenant())
                .withForceUserGroup(source.forceUserGroup())
                .withGroupName(source.getLoginGroupName())
                .withIgnoreTenantPolicies(source.ignoreTenantPolicies())
                .withLoginString(source.getLoginString())
                .withPasswordRequired(source.isPasswordRequired())
                .withPassword(source.getPassword())
                .withServiceUsersAllowed(source.isServiceUsersAllowed())
                .withSessionType(source.getSessionType())
                .withTargetDevice(source.getTargetDevice())
                .withNoPersist(source.noPersist());
    }

    public static LoginData buildNoPasswordRequired(String username) {
        return builder().withLoginString(username).withNoPasswordRequired().allowServiceUser().build();
    }

    public static class LoginDataImplBuilder implements Builder<LoginDataImpl, LoginDataImplBuilder> {

        private String loginString;
        private String unencryptedPassword;
        private String loginGroupName;
        public boolean passwordRequired = true, serviceUsersAllowed = false, forceUserGroup = false;
        public Boolean ignoreTenantPolicies, noPersist;
        private Long defaultTenant;
        private Set<Long> activeTenants;
        private TargetDevice targetDevice;
        private SessionType sessionType;

        @Override
        public LoginDataImpl build() {
            return new LoginDataImpl(this);
        }

        public LoginDataImplBuilder withLoginString(String loginString) {
            this.loginString = loginString;
            return this;
        }

        public LoginDataImplBuilder withSessionType(SessionType sessionType) {
            this.sessionType = sessionType;
            return this;
        }

        public LoginDataImplBuilder withTargetDevice(TargetDevice targetDevice) {
            this.targetDevice = targetDevice;
            return this;
        }

        public LoginDataImplBuilder withPassword(String unencryptedPassword) {
            this.unencryptedPassword = unencryptedPassword;
            this.passwordRequired = true;
            return this;
        }

        public LoginDataImplBuilder withGroupName(@Nullable String loginGroupName) {
            this.loginGroupName = loginGroupName;
            return this;
        }

        public LoginDataImplBuilder withNoPasswordRequired() {
            this.passwordRequired = false;
            return this;
        }

        public LoginDataImplBuilder withPasswordRequired(boolean passwordRequired) {
            this.passwordRequired = passwordRequired;
            return this;
        }

        public LoginDataImplBuilder allowServiceUser() {
            return this.withServiceUsersAllowed(true);
        }

        public LoginDataImplBuilder withIgnoreTenantPolicies(Boolean ignoreTenantPolicies) {
            this.ignoreTenantPolicies = ignoreTenantPolicies;
            return this;
        }

        public LoginDataImplBuilder withServiceUsersAllowed(boolean serviceUsersAllowed) {
            this.serviceUsersAllowed = serviceUsersAllowed;
            return this;
        }

        public LoginDataImplBuilder withForceUserGroup(boolean forceUserGroup) {
            this.forceUserGroup = forceUserGroup;
            return this;
        }

        public LoginDataImplBuilder withNoPersist(Boolean noPersist) {
            this.noPersist = noPersist;
            return this;
        }

        public LoginDataImplBuilder withDefaultTenant(@Nullable Long defaultTenant) {
            this.defaultTenant = defaultTenant;
            return this;
        }

        public LoginDataImplBuilder withActiveTenants(@Nullable Collection<Long> activeTenants) {
            this.activeTenants = activeTenants == null ? null : ImmutableSet.copyOf(activeTenants);
            return this;
        }

        public LoginDataImplBuilder withUser(LoginUser user) {
            return this.withLoginString(user.getUsername()).withGroupName(user.getDefaultGroupName());
        }

    }
}
