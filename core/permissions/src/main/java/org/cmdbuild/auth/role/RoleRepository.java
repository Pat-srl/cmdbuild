package org.cmdbuild.auth.role;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.auth.userrole.UserRole;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface RoleRepository {

    List<Role> getAllGroups();

    @Nullable
    Role getByIdOrNull(long groupId);

    Map<String, Role> getGroupsByName(Iterable<String> groupNames);

    Role update(Role group);

    Role create(Role group);

    List<UserRole> getUserGroups(long userId);

    @Nullable
    Role getGroupWithNameOrNull(String groupName);

    void setUserGroups(long userId, Collection<Long> userGroupIds, @Nullable Long defaultGroupId);

    void setUserGroupsByName(long userId, Collection<String> userGroups, @Nullable String defaultGroup);

    default void setUserGroups(long userId, String... userGroups) {
        setUserGroupsByName(userId, ImmutableList.copyOf(userGroups), (String) null);
    }

    default List<Role> getActiveGroups() {
        return getAllGroups().stream().filter(Role::isActive).collect(toImmutableList());
    }

    default Role getById(long groupId) {
        return checkNotNull(getByIdOrNull(groupId), "role not found for id = %s", groupId);
    }

    default Role getGroupWithName(String groupName) {
        return checkNotNull(getGroupWithNameOrNull(groupName), "group not found for name = %s", groupName);
    }

    default List<UserRole> getActiveUserGroups(long userId) {
        return getUserGroups(userId).stream().filter((r) -> r.isActive()).collect(toList());
    }

    default Role getByNameOrId(String roleId) {
        checkNotBlank(roleId);
        if (NumberUtils.isCreatable(roleId)) {
            return getById(toLong(roleId));
        } else {
            return getGroupWithName(roleId);
        }
    }

    default Role getByNameOrIdOrNull(String roleId) {
        if (NumberUtils.isCreatable(roleId)) {
            return getByIdOrNull(toLong(roleId));
        } else {
            return getGroupWithNameOrNull(roleId);
        }
    }

}
