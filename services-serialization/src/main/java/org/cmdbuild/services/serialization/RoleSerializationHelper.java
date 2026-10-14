/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;

import org.cmdbuild.auth.role.GroupConfig;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

import static java.lang.String.format;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_USERS_VIEW;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author ldare
 */
public class RoleSerializationHelper {

    public static FluentMap<String, Object> serializeRoleInfo(RoleInfo role, ObjectTranslationService translationService) {
        return map(
                "_id", role.getId(),
                "name", role.getName(),
                "description", role.getDescription(),
                "_description_translation", translationService.translateRoleDescription(role.getName(), role.getDescription()));
    }

    public static FluentMap<String, Object> serializeBasicRole(Role role, ObjectTranslationService translationService) {
        return serializeRoleInfo(role, translationService).with(
                "type", role.getType().name().toLowerCase(),
                "email", role.getEmail(),
                "active", role.isActive());
    }

    public static FluentMap<String, Object> serializeDetailedRole(Role role, ObjectTranslationService translationService, UserRepository userRepository, OperationUserSupplier operationUser) {
        GroupConfig config = role.getConfig();
        boolean canAddUsers = userRepository.currentUserCanAddUsersToRole(role);
        return serializeBasicRole(role, translationService).with(
                "processWidgetAlwaysEnabled", config.getProcessWidgetAlwaysEnabled(),
                "startingClass", config.getStartingClass(),
                "bulkUpdate", config.getBulkUpdate(),
                "bulkDelete", config.getBulkDelete(),
                "bulkAbort", config.getBulkAbort(),
                "fullTextSearch", config.getFullTextSearch(),
                "_can_users_read", operationUser.hasPrivileges(p -> p.hasPrivileges(RP_ADMIN_USERS_VIEW)),
                "_can_users_modify", canAddUsers
        ).accept((m) -> role.getRolePrivilegesAsMap().forEach((k, v) -> m.put(format("_%s", k.name().toLowerCase()), v)));
    }
}