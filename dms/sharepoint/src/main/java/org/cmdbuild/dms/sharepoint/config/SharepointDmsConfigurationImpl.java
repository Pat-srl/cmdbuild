package org.cmdbuild.dms.sharepoint.config;

import javax.annotation.Nullable;
import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.cmdbuild.dms.sharepoint.SharepointDmsAuthProtocol;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_CLIENT_ID;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_CLIENT_SECRET;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_PROTOCOL;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_RESOURCE_ID;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_SERVICE_URL;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_SERVICE_URL_DEFAULT;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_AUTH_TENANT_ID;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_GRAPH_API_BASE_URL;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_GRAPH_API_BASE_URL_DEFAULT;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_PASSWORD;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_URL;
import static org.cmdbuild.dms.sharepoint.config.SharepointConfiguration.SHAREPOINT_USER;
import static org.cmdbuild.dms.sharepoint.config.SharepointDmsConfiguration.SHAREPOINT_CONFIG_NAMESPACE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(SHAREPOINT_CONFIG_NAMESPACE)
public final class SharepointDmsConfigurationImpl implements SharepointDmsConfiguration {

    @ConfigValue(key = SHAREPOINT_URL, description = "", defaultValue = "", category = CC_ENV)
    private String sharepointUrl;

    @ConfigValue(key = SHAREPOINT_USER, description = "", defaultValue = "admin", category = CC_ENV)
    private String sharepointUser;

    @ConfigValue(key = SHAREPOINT_PASSWORD, description = "", defaultValue = "admin", category = CC_ENV)
    private String sharepointPassword;

    @ConfigValue(key = "path", description = "", defaultValue = "/", category = CC_ENV)
    private String sharepointPath;

    @ConfigValue(key = SHAREPOINT_GRAPH_API_BASE_URL, defaultValue = SHAREPOINT_GRAPH_API_BASE_URL_DEFAULT, category = CC_ENV)
    private String sharepointGraphApiUrl;

    @ConfigValue(key = SHAREPOINT_AUTH_PROTOCOL, description = "sharepoint auth protocol (one of: `msazureoauth2_password`, `msazureoauth2_delegated`, `msazureauth2_application`)", defaultValue = "msazureoauth2_password", category = CC_ENV)
    private SharepointDmsAuthProtocol sharepointAuthProtocol;

    @ConfigValue(key = SHAREPOINT_AUTH_RESOURCE_ID, description = "sharepoint auth resource id", category = CC_ENV)
    private String sharepointAuthResourceId;

    @ConfigValue(key = SHAREPOINT_AUTH_CLIENT_ID, description = "sharepoint auth client id", category = CC_ENV)
    private String sharepointAuthClientId;

    @ConfigValue(key = SHAREPOINT_AUTH_TENANT_ID, description = "sharepoint auth tenant id", category = CC_ENV)
    private String sharepointAuthTenantId;

    @ConfigValue(key = SHAREPOINT_AUTH_SERVICE_URL, description = "sharepoint auth service url", defaultValue = SHAREPOINT_AUTH_SERVICE_URL_DEFAULT, category = CC_ENV)
    private String sharepointAuthServiceUrl;

    @ConfigValue(key = SHAREPOINT_AUTH_CLIENT_SECRET, description = "sharepoint auth client secret", category = CC_ENV)
    private String sharepointAuthClientSecret;

    @ConfigValue(key = "model.authorColumn", description = "sharepoint custom author column", category = CC_ENV)
    private String sharepointCustomAuthorColumn;

    @ConfigValue(key = "model.descriptionColumn", description = "sharepoint custom description column", defaultValue = "Label", category = CC_ENV)
    private String sharepointCustomDescriptionColumn;

    @ConfigValue(key = "model.categoryColumn", description = "sharepoint custom category column", category = CC_ENV)
    private String sharepointCustomCategoryColumn;

    @Override
    public String getSharepointUrl() {
        return sharepointUrl;
    }

    @Override
    public String getSharepointUser() {
        return sharepointUser;
    }

    @Override
    public String getSharepointPassword() {
        return sharepointPassword;
    }

    @Override
    public String getSharepointPath() {
        return sharepointPath;
    }

    @Override
    public SharepointDmsAuthProtocol getSharepointAuthProtocol() {
        return sharepointAuthProtocol;
    }

    @Override
    public String getSharepointAuthResourceId() {
        return sharepointAuthResourceId;
    }

    @Override
    public String getSharepointAuthClientId() {
        return sharepointAuthClientId;
    }

    @Override
    public String getSharepointAuthTenantId() {
        return sharepointAuthTenantId;
    }

    @Override
    public String getSharepointAuthServiceUrl() {
        return sharepointAuthServiceUrl;
    }

    @Override
    public String getSharepointAuthClientSecret() {
        return sharepointAuthClientSecret;
    }

    @Override
    @Nullable
    public String getSharepointCustomAuthorColumn() {
        return sharepointCustomAuthorColumn;
    }

    @Override
    @Nullable
    public String getSharepointCustomDescriptionColumn() {
        return sharepointCustomDescriptionColumn;
    }

    @Override
    @Nullable
    public String getSharepointCustomCategoryColumn() {
        return sharepointCustomCategoryColumn;
    }

    @Override
    public String getSharepointGraphApiBaseUrl() {
        return sharepointGraphApiUrl;
    }

}
