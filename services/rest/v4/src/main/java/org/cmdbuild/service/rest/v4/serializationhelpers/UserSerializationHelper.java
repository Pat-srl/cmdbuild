/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.serializationhelpers;


import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserFilteredRepository;
import org.cmdbuild.auth.userrole.UserRole;
import org.cmdbuild.userconfig.UserConfigService;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.collect.MoreCollectors.toOptional;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.userconfig.UserConfigConst.*;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;

/**
 * @author ldare
 */
@Component
public class UserSerializationHelper {

    private final UserFilteredRepository userFilteredRepository;
    private final RoleRepository roleRepository;
    private final UserConfigService userConfigService;
    private final MultitenantService multitenantService;
    private final SessionService sessionService;

    public UserSerializationHelper(UserFilteredRepository userFilteredRepository, RoleRepository roleRepository, UserConfigService userConfigService, MultitenantService multitenantService, SessionService sessionService) {
        this.userFilteredRepository = userFilteredRepository;
        this.roleRepository = roleRepository;
        this.userConfigService = userConfigService;
        this.multitenantService = multitenantService;
        this.sessionService = sessionService;

    }

    public static CmMapUtils.FluentMap<String, Object> serializeMinimalUser(UserData user) {
        return map(
                "_id", user.getId(),
                "username", user.getUsername(),
                "description", user.getDescription()
        );
    }

    public CmMapUtils.FluentMap<String, Object> serializeUser(UserData user) {
        return serializeMinimalUser(user).with(
                "email", user.getEmail(),
                "active", user.isActive(),
                "service", user.isService(),
                "_can_write", userFilteredRepository.currentUserCanModify(user)
        );
    }

    public CmMapUtils.FluentMap<String, Object> serializeFastDetailedUser(UserData user) {
        List<UserRole> userGroups = roleRepository.getUserGroups(user.getId());
        List roles = userGroups.stream().map(UserRole::getRole).map(r -> map(
                "_id", r.getId(),
                "name", r.getName(),
                "description", r.getDescription(),
                "_description_translation", r.getDescription()//TODO
        )).collect(toList());
        UserRole defaultGroup = userGroups.stream().filter(UserRole::isDefault).collect(toOptional()).orElse(null);
        Map<String, String> prefs = userConfigService.getByUsername(user.getUsername());
        return serializeUser(user).with(
                "userGroups", roles,
                "defaultUserGroup", Optional.ofNullable(defaultGroup).map(UserRole::getId).orElse(null),
                "_defaultUserGroup_description", Optional.ofNullable(defaultGroup).map(UserRole::getDescription).orElse(null),
                "language", prefs.get(USER_CONFIG_LANGUAGE),
                "initialPage", prefs.get("cm_user_initialPage"),
                "multiGroup", toBooleanOrDefault(prefs.get(USER_CONFIG_MULTIGROUP), false),
                "multiTenantActivationPrivileges", prefs.get(USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES)
        );
    }

    public Object serializeDetailedUser(UserData user) {
        UserAvailableTenantContext tenantContext = multitenantService.getAvailableTenantContextForUser(user.getId());
        List<UserRole> userGroups = roleRepository.getUserGroups(user.getId());
        this.sessionService.getCurrentSession().getOperationUser();
        // TODO commented because of performance, this is not used by ui; return available groups to limited user
        //        List<Map<String, Object>> currentUserAvailableGroups = groupRepository.getAllGroups().stream().filter(repository::currentUserCanAddUsersToRole).map(r -> mapOf(String.class, Object.class).with(
        //                "_id", r.getId(),
        //                "name", r.getName(),
        //                "description", r.getDescription(),
        //                "_description_translation", r.getDescription()//TODO
        //        )).collect(toList());
        List<Map<String, Object>> roles = userGroups.stream().map(UserRole::getRole).map(r -> mapOf(String.class, Object.class).with(
                "_id", r.getId(),
                "name", r.getName(),
                "description", r.getDescription(),
                "_description_translation", r.getDescription()//TODO
        )).collect(toList());
        UserRole defaultGroup = userGroups.stream().filter(UserRole::isDefault).collect(toOptional()).orElse(null);
        List<Map<String, Object>> tenants = multitenantService.getAvailableUserTenants(tenantContext).stream().map(t -> mapOf(String.class, Object.class).with(
                "_id", t.getId(),
                "name", t.getDescription(),
                "description", t.getDescription(),
                "_description_translation", t.getDescription()//TODO
        )).collect(toList());
        Map<String, String> prefs = userConfigService.getByUsername(user.getUsername());
        return serializeUser(user).with("userTenants", tenants,
                "defaultUserTenant", tenantContext.getDefaultTenantId(),
                "userGroups", roles,
                //                "_availableUserGroups", currentUserAvailableGroups,
                "defaultUserGroup", Optional.ofNullable(defaultGroup).map(UserRole::getId).orElse(null),
                "_defaultUserGroup_description", Optional.ofNullable(defaultGroup).map(UserRole::getDescription).orElse(null),
                "language", prefs.get(USER_CONFIG_LANGUAGE),
                "initialPage", prefs.get("cm_user_initialPage"),
                "multiGroup", toBooleanOrDefault(prefs.get(USER_CONFIG_MULTIGROUP), false),
                "multiTenantActivationPrivileges", prefs.get(USER_CONFIG_MULTITENANT_ACTIVATION_PRIVILEGES),
                "changePasswordRequired", user.hasRecoveryToken() && !user.hasPassword());
    }
}
