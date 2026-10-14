package org.cmdbuild.auth.user;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.transform;
import java.util.Collection;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.auth.multitenant.api.UserAvailableTenantContext;
import org.cmdbuild.auth.role.RoleInfo;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface LoginUser extends LoginUserInfo {

    String getDescription();

    List<RoleInfo> getRoleInfos();

    @Nullable
    String getDefaultGroupName();

    UserAvailableTenantContext getAvailableTenantContext();

    @Nullable
    String getEmail();

    boolean isActive();

    boolean isService();

    boolean hasMultigroupEnabled();

    default RoleInfo getRoleInfoByNameOrId(String nameOrId) {
        checkNotBlank(nameOrId);
        return getRoleInfos().stream().filter(r -> equal(r.getName(), nameOrId) || (NumberUtils.isCreatable(nameOrId) && equal(toLong(nameOrId), r.getId()))).collect(onlyElement("role info not found for name or id =< %s >", nameOrId));
    }

    default boolean isNotService() {
        return !isService();
    }

    default long getIdNotNull() {
        return checkNotNull(getId(), "id not available for user = %s (not a db user, missing id)", this);
    }

    default Collection<String> getGroupNames() {
        return transform(getRoleInfos(), RoleInfo::getName);
    }

    default Collection<String> getGroupDescriptions() {
        return transform(getRoleInfos(), RoleInfo::getDescription);
    }

    default boolean hasDefaultGroup() {
        return isNotBlank(getDefaultGroupName());
    }

    default boolean hasGroup(String groupName) {
        return getGroupNames().contains(groupName);
    }

    default boolean hasEmail() {
        return isNotBlank(getEmail());
    }

}
