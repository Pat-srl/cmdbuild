package org.cmdbuild.view;

import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.cleanup.ViewType;

public interface ViewBase extends PrivilegeSubjectWithInfo {

    final String ATTR_SHARED = "Shared", ATTR_USER_ID = "UserId";

    @Override
    @Nullable
    Long getId();

    @Override
    String getName();

    @Override
    String getDescription();

    ViewType getType();

    boolean isActive();

    boolean isShared();

    @Nullable
    Long getUserId();

    @Override
    String getPrivilegeId();

    @Nullable
    String getIconFont();

    default boolean isOfType(ViewType type) {
        return equal(getType(), type);
    }

}
