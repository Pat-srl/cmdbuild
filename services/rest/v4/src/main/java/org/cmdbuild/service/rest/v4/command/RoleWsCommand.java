/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Supplier;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleImpl.RoleImplBuilder;
import org.cmdbuild.auth.role.RolePrivilege;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.auth.userrole.UserRoleRepository;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.service.rest.v4.model.WsRoleData;
import org.cmdbuild.service.rest.v4.model.WsRoleUsers;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_ROLES_MODIFY;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

/**
 *
 * @author ldare
 */
@Component
public class RoleWsCommand {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public RoleWsCommand(RoleRepository roleRepository, UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.roleRepository = checkNotNull(roleRepository);
        this.userRepository = checkNotNull(userRepository);
        this.userRoleRepository = checkNotNull(userRoleRepository);
    }

    public List<Role> doReadMany(Supplier<List<Role>> function) {
        return function.get();
    }

    public PagedElements<UserData> doReadRoleUsers(String roleId, String filterStr, String sort, Long limit, Long offset, Boolean assigned) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        CmdbSorter sorter = CmSorterUtils.parseSorter(sort);
        Role role = roleRepository.getByNameOrId(roleId);
        PagedElements<UserData> users;
        if (firstNotNull(assigned, true)) {
            users = userRepository.getAllWithRole(role.getId(), filter, sorter, offset, limit);
        } else {
            users = userRepository.getAllWithoutRole(role.getId(), filter, sorter, offset, limit);
        }
        return users;
    }

    public void doUpdateUsers(String roleId, WsRoleUsers users) {
        Role role = roleRepository.getByNameOrId(roleId);
        users.getListUsersToAdd().forEach((userId) -> userRoleRepository.addRoleToUser(userId, role.getId()));
        users.getListUsersToRemove().forEach((userId) -> userRoleRepository.removeRoleFromUser(userId, role.getId()));
    }

    public Role doCreate(String jsonData) {
        Role role = toRole(jsonData).build();
        return roleRepository.create(role);
    }

    public Role doUpdate(String roleId, String jsonData) {
        Role role = roleRepository.getByNameOrId(roleId);
        role = toRole(jsonData).withId(role.getId()).build();
        return roleRepository.update(role);
    }

    public RoleImplBuilder toRole(String jsonData) {
        WsRoleData data = fromJson(jsonData, WsRoleData.class);
        Map<String, Boolean> customPermissions = CmJsonUtils.fromJson(jsonData, MAP_OF_OBJECTS).entrySet().stream()
                .filter((e) -> e.getKey().startsWith("_rp_") && !equal(parseEnum(e.getKey().replaceFirst("^_rp_", ""), RolePrivilege.class), RP_ADMIN_ROLES_MODIFY))
                .collect(toMap((e) -> e.getKey().replaceFirst("^_rp_", ""), (e) -> toBoolean(e.getValue())));
        return data.toRole().withCustomPrivileges(customPermissions);
    }
}
