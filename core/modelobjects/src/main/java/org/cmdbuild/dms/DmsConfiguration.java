package org.cmdbuild.dms;

import static com.google.common.base.Objects.equal;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface DmsConfiguration {

    final String DMS_CONFIG_NAMESPACE = "org.cmdbuild.dms",
            DMS_CONFIG_PROVIDER = DMS_CONFIG_NAMESPACE + ".service.type";

    boolean isEnabled();

    boolean isAdvancedSearchEnabled();

    boolean isImageResizeEnabled();

    Integer getImageResizePixel();

    boolean isPgPreviewEnabled();

    Integer getPgPreviewMaxFileSize();

    String getService();

    String getDefaultDmsCategory();

    List<String> getRegularAttachmentsAllowedFileExtensions();

    List<String> getIncomingEmailAttachmentsAllowedFileExtensions();

    Integer getMaxFileSize();

    @Nullable
    String getAutolinkHelperScript();

    @Nullable
    String getAutolinkBasePath();

    boolean checkAttachmentExistanceOnlyOnDB();

    default boolean hasAutolinkHelperScript() {
        return isNotBlank(getAutolinkHelperScript());
    }

    default boolean isRegularAttachmentsFileExtensionCheckEnabled() {
        return !getRegularAttachmentsAllowedFileExtensions().isEmpty();
    }

    default boolean isMaxFileSizeCheckEnabled() {
        return getMaxFileSize() != null;
    }

    default boolean isIncomingEmailFileExtensionCheckEnabled() {
        return !getIncomingEmailAttachmentsAllowedFileExtensions().isEmpty();
    }

    default boolean isEnabled(String dmsProviderServiceName) {
        return isEnabled() && equal(getService(), dmsProviderServiceName);
    }
}
