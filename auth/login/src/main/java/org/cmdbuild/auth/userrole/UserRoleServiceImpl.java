/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.userrole;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.UserRoleUpdateEvent;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserFilteredRepository;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.eventbus.EventBusService;
import org.springframework.stereotype.Component;

@Component
public class UserRoleServiceImpl implements UserRoleService, UserFilteredRepository {

    private final UserRoleRepository userRoleRepository;
    private final UserFilteredRepository userRepository;
    private final RoleRepository roleRepository;
    private final EventBus eventBus;

    public UserRoleServiceImpl(UserRoleRepository userRoleRepository, UserFilteredRepository userRepository, RoleRepository roleRepository, EventBusService eventBusService) {
        this.userRoleRepository = checkNotNull(userRoleRepository);
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        eventBus = eventBusService.getGrantEventBus();
    }

    @Override
    public UserData getActiveUserDataOrNull(LoginUserIdentity login) {
        return userRepository.getActiveUserDataOrNull(login);
    }

    @Override
    public UserData getUserDataOrNull(LoginUserIdentity login) {
        return userRepository.getUserDataOrNull(login);
    }

    @Override
    public void addRoleToUser(long userId, long roleId) {
        userRoleRepository.addRoleToUser(userId, roleId);
        eventBus.post(UserRoleUpdateEvent.INSTANCE);
    }

    @Override
    public void removeRoleFromUser(long userId, long roleId) {
        userRoleRepository.removeRoleFromUser(userId, roleId);
        eventBus.post(UserRoleUpdateEvent.INSTANCE);
    }

    @Override
    public LoginUser getActiveValidUserOrNull(LoginUserIdentity login) {
        return userRepository.getActiveValidUserOrNull(login);
    }

    @Override
    public LoginUser getUserByIdOrNull(Long userId) {
        return userRepository.getUserByIdOrNull(userId);
    }

    @Override
    public LoginUser getActiveUser(LoginUserIdentity identity) {
        return userRepository.getActiveUser(identity);
    }

    @Override
    public LoginUser getActiveUserByEmailOrNull(String email) {
        return userRepository.getActiveUserByEmailOrNull(email);
    }

    @Override
    public LoginUser getActiveUserByUsernameOrNull(String username) {
        return userRepository.getActiveUserByUsernameOrNull(username);
    }

    @Override
    public LoginUser getActiveUserByUsername(String username) {
        return userRepository.getActiveUserByUsername(username);
    }

    @Override
    public PagedElements<UserData> getMany(DaoQueryOptions queryOptions) {
        return userRepository.getMany(queryOptions);
    }

    @Override
    public PagedElements<UserData> getAllWithRole(long roleId, CmdbFilter filter, CmdbSorter sorter, Long offset, Long limit) {
        return userRepository.getAllWithRole(roleId, filter, sorter, offset, limit);
    }

    @Override
    public List<UserData> getAllWithRole(long roleId) {
        return userRepository.getAllWithRole(roleId);
    }

    @Override
    public PagedElements<UserData> getAllWithoutRole(long roleId, CmdbFilter filter, CmdbSorter sorter, Long offset, Long limit) {
        return userRepository.getAllWithoutRole(roleId, filter, sorter, offset, limit);
    }

    @Override
    public UserData getUserDataById(long id) {
        return userRepository.getUserDataById(id);
    }

    @Override
    @Nullable
    public UserData getUserDataByUsernameOrNull(String username) {
        return userRepository.getUserDataByUsernameOrNull(username);
    }

    @Override
    @Nullable
    public List<UserData> getUserDataByRoleId(long roleId) {
        return userRepository.getUserDataByRoleId(roleId);
    }

    @Override
    public UserData create(UserData user) {
        return userRepository.create(user);
    }

    @Override
    public UserData update(UserData user) {
        return userRepository.update(user);
    }

    @Override
    public boolean currentUserCanModify(UserData user) {
        return userRepository.currentUserCanModify(user);
    }

    @Override
    public boolean currentUserCanModify(UserData user, Collection<Role> roles, Collection<Long> tenants) {
        return userRepository.currentUserCanModify(user, roles, tenants);
    }

    @Override
    public boolean currentUserCanAddUsersToRole(Role role) {
        return userRepository.currentUserCanAddUsersToRole(role);
    }

    @Override
    public List<Role> getAllGroups() {
        return roleRepository.getAllGroups();
    }

    @Override
    public Role getByIdOrNull(long groupId) {
        return roleRepository.getByIdOrNull(groupId);
    }

    @Override
    public Role getById(long groupId) {
        return roleRepository.getById(groupId);
    }

    @Override
    public Role getGroupWithNameOrNull(String groupName) {
        return roleRepository.getGroupWithNameOrNull(groupName);
    }

    @Override
    public Role getGroupWithName(String groupName) {
        return roleRepository.getGroupWithName(groupName);
    }

    @Override
    public Map<String, Role> getGroupsByName(Iterable<String> groupNames) {
        return roleRepository.getGroupsByName(groupNames);
    }

    @Override
    public Role update(Role group) {
        group = roleRepository.update(group);
        eventBus.post(UserRoleUpdateEvent.INSTANCE);
        return group;
    }

    @Override
    public Role create(Role group) {
        group = roleRepository.create(group);
        eventBus.post(UserRoleUpdateEvent.INSTANCE);
        return group;
    }

    @Override
    public List<UserRole> getUserGroups(long userId) {
        return roleRepository.getUserGroups(userId);
    }

    @Override
    public void setUserGroups(long userId, Collection<Long> userGroupIds, Long defaultGroupId) {
        roleRepository.setUserGroups(userId, userGroupIds, defaultGroupId);
        eventBus.post(UserRoleUpdateEvent.INSTANCE);
    }

    @Override
    public void setUserGroupsByName(long userId, Collection<String> userGroups, String defaultGroup) {
        roleRepository.setUserGroupsByName(userId, userGroups, defaultGroup);
        eventBus.post(UserRoleUpdateEvent.INSTANCE);
    }

    @Override
    public Role getByNameOrId(String roleId) {
        return roleRepository.getByNameOrId(roleId);
    }
}
