package org.cmdbuild.config;

import jakarta.annotation.Nullable;
import static java.util.Collections.emptyList;
import java.util.List;
import static org.cmdbuild.config.api.ConfigCategory.CC_DATA;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dms.DmsConfiguration;
import static org.cmdbuild.dms.DmsConfiguration.DMS_CONFIG_NAMESPACE;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(DMS_CONFIG_NAMESPACE)
public final class DmsConfigurationImpl implements DmsConfiguration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String ENABLED = "enabled",
            ADVANCED_SEARCH = "advanced.search",
            CATEGORY_LOOKUP = "category",
            POSTGRES_CONFIG = "service.postgres.";

    @ConfigValue(key = DMS_CONFIG_PROVIDER, description = "dms service (alfresco, cmis, postgres, sharepoint_online); cmis is a standard protocol used, for example, by Alfresco dms; postgres is an embedded dms implementation that relies upon cmdbuild postgres db", defaultValue = "alfresco")
    private String service;

    @ConfigValue(key = ENABLED, description = "", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = ADVANCED_SEARCH, description = "enable advanced search, uses dms api search", defaultValue = TRUE)
    private boolean isAdvancedSearchEnabled;

    @ConfigValue(key = "checkAttachmentExistanceOnlyOnDB", description = "", defaultValue = FALSE)
    private boolean checkAttachmentExistanceOnlyOnDB;

    @ConfigValue(key = "autolink.script", description = "autolink helper script")
    private String autolinkHelperScript;

    @ConfigValue(key = "autolink.path", description = "autolink base path (replaces dms provider base path, for links)")
    private String autolinkPath;

    @ConfigValue(key = "regularAttachments.allowedFileExtensions", description = "allowed file extensions, lowercase, for card/email attachments (via ui/attachment ws)")
    private List<String> regularAttachmentsAllowedFileExtensions;

    @ConfigValue(key = "incomingEmailAttachments.allowedFileExtensions", description = "allowed file extensions, lowercase, for incoming email (rejected attachments will be ignored and print a warning, without affecting email processing)")
    private List<String> incomingEmailAttachmentsAllowedFileExtensions;

    @ConfigValue(key = "image.resize.enabled", description = "enable image resize before upload", defaultValue = FALSE)
    private boolean isImageResizeEnabled;

    @ConfigValue(key = "image.resize.pixel", description = "image will be resized with this size (max length or width)", defaultValue = "1024")
    private Integer imageResizePixel;

    @ConfigValue(key = "regularAttachments.maxFileSize", description = "maximum allowed file size, expressed in MB")
    private Integer maxFileSize;

    @ConfigValue(key = CATEGORY_LOOKUP, description = "", defaultValue = "AlfrescoCategory", category = CC_DATA)
    private String dmsCategory;

    @ConfigValue(key = POSTGRES_CONFIG + "preview", description = "generate preview when postgres dms service is enabled (images, pdf and text)", defaultValue = FALSE)
    private boolean isPgPreviewEnabled;

    @ConfigValue(key = POSTGRES_CONFIG + "preview.maxFileSize", description = "maximum postgres preview file size, expressed in MB", defaultValue = "5")
    private Integer pgPreviewMaxFileSize;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public boolean isAdvancedSearchEnabled() {
        return isAdvancedSearchEnabled;
    }

    @Override
    public boolean isImageResizeEnabled() {
        return isImageResizeEnabled;
    }

    @Override
    public Integer getImageResizePixel() {
        return imageResizePixel;
    }

    @Override
    public boolean isPgPreviewEnabled() {
        return isPgPreviewEnabled;
    }

    @Override
    public Integer getPgPreviewMaxFileSize() {
        return pgPreviewMaxFileSize;
    }

    @Override
    public Integer getMaxFileSize() {
        return maxFileSize;
    }

    @Override
    @Nullable
    public String getAutolinkHelperScript() {
        return autolinkHelperScript;
    }

    @Override
    @Nullable
    public String getAutolinkBasePath() {
        return autolinkPath;
    }

    @Override
    public boolean checkAttachmentExistanceOnlyOnDB() {
        return checkAttachmentExistanceOnlyOnDB;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getDefaultDmsCategory() {
        return dmsCategory;
    }

    @Override
    public List<String> getRegularAttachmentsAllowedFileExtensions() {
        return firstNotNull(regularAttachmentsAllowedFileExtensions, emptyList());
    }

    @Override
    public List<String> getIncomingEmailAttachmentsAllowedFileExtensions() {
        return firstNotNull(incomingEmailAttachmentsAllowedFileExtensions, emptyList());
    }

}
