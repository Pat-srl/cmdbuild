package org.cmdbuild.auth.role;

import org.cmdbuild.auth.grant.Grant;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface Role extends RolePrivilegeHolder, RoleInfo {

    RoleType getType();

    String getEmail();

    boolean isActive();

    GroupConfig getConfig();

    Map<String, Boolean> getCustomPrivileges();

    List<Grant> getAllPrivileges();

    @Nullable
    default String getStartingClass() {
        return getConfig().getStartingClass();
    }

    default boolean hasStartingClass() {
        return isNotBlank(getStartingClass());
    }

}
