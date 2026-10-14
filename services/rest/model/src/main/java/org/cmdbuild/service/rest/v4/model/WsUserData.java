/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.auth.user.UserDataImpl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
public class WsUserData {

    private final Long id, defaultRole;
    private final String username, description, email, password, initialPage, lang;
    private final Boolean isActive, isService, multiTenant, multiGroup;
    private final boolean changePasswordRequired;
    private final List<WsRoleOrTenantData> userTenants, userGroups;
    private final UserAvailableTenantContext.TenantActivationPrivileges multiTenantActivationPrivileges;

    public WsUserData(@JsonProperty("_id") Long id,
            @JsonProperty("username") String username,
            @JsonProperty("description") String description,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("initialPage") String initialPage,
            @JsonProperty("changePasswordRequired") Boolean changePasswordRequired,
            @JsonProperty("active") Boolean isActive,
            @JsonProperty("service") Boolean isService,
            @JsonProperty("language") String lang,
            @JsonProperty("multiGroup") Boolean multiGroup,
            @JsonProperty("multiTenant") Boolean multiTenant,
            @JsonProperty("multiTenantActivationPrivileges") String multiTenantActivationPrivileges,
            @JsonProperty("defaultUserGroup") Long defaultRole,
            @JsonProperty("userTenants") List<WsRoleOrTenantData> userTenants,
            @JsonProperty("userGroups") List<WsRoleOrTenantData> userGroups) {
        this.id = id;
        this.username = checkNotBlank(username, "'username' is null");
        this.password = password;
        this.description = description;
        this.email = email;
        this.changePasswordRequired = firstNotNull(changePasswordRequired, false);
        this.isActive = isActive;
        this.isService = isService;
        this.lang = lang;
        this.initialPage = initialPage;
        this.multiGroup = multiGroup;
        this.multiTenant = multiTenant;
        this.userTenants = nullToEmpty(userTenants);
        this.userGroups = checkNotNull(userGroups, "'userGroups' is null");
        this.defaultRole = defaultRole;
        this.multiTenantActivationPrivileges = parseEnumOrNull(multiTenantActivationPrivileges, UserAvailableTenantContext.TenantActivationPrivileges.class);
    }

    public UserDataImpl.UserDataImplBuilder toUserData() {
        return UserDataImpl.builder()
                .withId(id)
                .withUsername(username)
                .withDescription(description)
                .withEmail(email)
                .withActive(isActive)
                .withService(isService);
    }

    public Boolean isChangePasswordRequired() {
        return this.changePasswordRequired;
    }

    public String getPassword() {
        return this.password;
    }

    public List<WsRoleOrTenantData> getUserGroups() {
        return this.userGroups;
    }

    public List<WsRoleOrTenantData> getUserTenants() {
        return this.userTenants;
    }

    public String getInitialPage() {
        return this.initialPage;
    }

    public Boolean getMultiGroup() {
        return this.multiGroup;
    }

    public String getLang() {
        return this.lang;
    }

    public UserAvailableTenantContext.TenantActivationPrivileges getMultiTenantActivationPrivileges() {
        return this.multiTenantActivationPrivileges;
    }

    public Long getDefaultRole() {
        return this.defaultRole;
    }

}
