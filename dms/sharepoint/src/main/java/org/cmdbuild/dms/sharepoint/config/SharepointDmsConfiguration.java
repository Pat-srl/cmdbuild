package org.cmdbuild.dms.sharepoint.config;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.dms.DmsConfiguration.DMS_CONFIG_NAMESPACE;

public interface SharepointDmsConfiguration extends SharepointConfiguration {

    final String SHAREPOINT_CONFIG_NAMESPACE = DMS_CONFIG_NAMESPACE + ".service.sharepoint";

    String getSharepointPath();

    @Nullable
    String getSharepointCustomAuthorColumn();

    @Nullable
    String getSharepointCustomDescriptionColumn();

    @Nullable
    String getSharepointCustomCategoryColumn();

    default boolean hasSharepointCustomAuthorColumn() {
        return isNotBlank(getSharepointCustomAuthorColumn());
    }

    default boolean hasSharepointCustomDescriptionColumn() {
        return isNotBlank(getSharepointCustomDescriptionColumn());
    }

    default boolean hasSharepointCustomCategoryColumn() {
        return isNotBlank(getSharepointCustomCategoryColumn());
    }

    @Override
    default boolean autodeleteEmptyDirectories() {
        return true;
    }
}
