package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newLinkedHashSet;
import java.util.Collection;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.GrantPrivilege;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.grant.UserPrivileges;
import org.cmdbuild.auth.grant.UserPrivilegesForObject;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.grant.PrivilegeSubject;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.OperationUserSupplier.PrivilegeChecker;
import static org.cmdbuild.auth.user.SessionType.ST_INTERACTIVE;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;

/**
 * an operation user is the user which does an operations, with privileges,
 * groups, etc
 */
public interface OperationUser extends UserPrivileges, SessionTypeInfo, UserTenantContext {

    static final String OPERATION_SCOPE = "OPERATION_SCOPE",
            OPERATION_SCOPE_DEFAULT = "default",
            OPERATION_SCOPE_SYSTEM = "system",
            TARGET_DEVICE = "device", //TODO move this somewhere else, match enum in dao module
            SESSION_TYPE = "cm_session_type";
    static final String USER_ATTR_SESSION_ID = "SessionId";

    UserTenantContext getUserTenantContext();

    LoginUser getLoginUser();//TODO replace with 'userInfo'

    LoginUser getSponsor();

    /**
     * Returns the group with which the user logged in. It can be the default
     * group or the only group which the user belongs to or the selected group
     *
     * note: if the user has not a default group, this will return an instance
     * of NullGroup
     *
     * @return
     */
    @Nullable
    Role getDefaultGroupOrNull();

    UserPrivileges getPrivilegeContext();

    Map<String, String> getParams();

    @Override
    default Set<Long> getActiveTenantIds() {
        return getUserTenantContext().getActiveTenantIds();
    }

    @Override
    default boolean ignoreTenantPolicies() {
        return getUserTenantContext().ignoreTenantPolicies();
    }

    @Nullable
    @Override
    default Long getDefaultTenantId() {
        return getUserTenantContext().getDefaultTenantId();
    }

    default boolean hasMultitenant() {
        return !getUserTenantContext().ignoreTenantPolicies();
    }

    @Override
    default SessionType getSessionType() {
        return parseEnumOrDefault(getParams().get(SESSION_TYPE), ST_INTERACTIVE);
    }

    default TargetDevice getTargetDevice() {
        return parseEnumOrDefault(getParams().get(TARGET_DEVICE), TD_DEFAULT);
    }

    @Override
    default boolean hasReadAccess(PrivilegeSubject privilegedObject) {
        return getPrivilegeContext().hasReadAccess(privilegedObject);
    }

    @Override
    default boolean hasWriteAccess(PrivilegeSubject privilegedObject) {
        return getPrivilegeContext().hasWriteAccess(privilegedObject);
    }

    @Override
    @Deprecated
    default boolean hasAdminAccess() {
        return getPrivilegeContext().hasAdminAccess();
    }

    @Override
    default boolean hasServicePrivilege(GrantPrivilege requested, PrivilegeSubject privilegedObject) {
        return getPrivilegeContext().hasServicePrivilege(requested, privilegedObject);
    }

    @Override
    public default UserPrivilegesForObject getPrivilegesForObject(PrivilegeSubject object) {
        return getPrivilegeContext().getPrivilegesForObject(object);
    }

    @Nullable
    default Long getId() {
        return getLoginUser().getId();
    }

    default boolean hasId() {
        return getId() != null;
    }

    /**
     * An authenticated user is valid if it has a preferred group selected. The
     * preferred group is the group that the user chose at the login. If the
     * user belongs to one group or if it belongs to multiple groups but it has
     * a default group, the preferred group is already selected.
     *
     * @return
     */
    default boolean hasDefaultGroup() {
        return getDefaultGroupOrNull() != null;
    }

    default boolean hasDefaultGroupId() {
        return hasDefaultGroup() && getDefaultGroup().hasId();
    }

    default String getUsername() {
        return getLoginUser().getUsername();
    }

    default String getSponsorUsername() {
        return getSponsor().getUsername();
    }

    default Role getDefaultGroup() {
        return checkNotNull(getDefaultGroupOrNull(), "default group not set for user = %s", this);
    }

    default long getDefaultGroupId() {
        return getDefaultGroup().getId();
    }

    default String getDefaultGroupName() {
        return getDefaultGroup().getName();
    }

    @Nullable
    default String getDefaultGroupNameOrNull() {
        return hasDefaultGroup() ? getDefaultGroupName() : null;
    }

    default Collection<Long> getActiveGroupIds() {
        if (isMultiGroup()) {
            return getGroupIds();
        } else {
            return singleton(getDefaultGroupId());
        }
    }

    default Set<Long> getGroupIds() {
        return getLoginUser().getRoleInfos().stream().map(RoleInfo::getId).collect(toImmutableSet());
    }

    default Collection<String> getActiveGroupNames() {
        if (isMultiGroup()) {
            return getGroupNames();
        } else {
            return singleton(getDefaultGroupName());
        }
    }

    default boolean hasPrivileges(PrivilegeChecker checker) {
        return checker.hasPrivileges(getPrivilegeContext());
    }

    default void checkPrivileges(PrivilegeChecker checker) {
        checkArgument(hasPrivileges(checker), "access denied: user does not have the required privileges for this operation");
    }

    default List<String> getGroupNamesDefaultFirst() {
        return newArrayList(newLinkedHashSet(concat(hasDefaultGroup() ? singleton(getDefaultGroupName()) : emptyList(), getGroupNames())));
    }

    default Collection<String> getGroupNames() {
        return getLoginUser().getGroupNames();
    }

    default boolean isMultiGroup() {
        return getLoginUser().hasMultigroupEnabled();
    }

    default boolean hasActiveGroupName(String groupName) {
        return getActiveGroupNames().contains(groupName);
    }

}
