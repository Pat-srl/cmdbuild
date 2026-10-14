package org.cmdbuild.dms.alfresco.config;

import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.dms.alfresco.config.AlfrescoDmsConfiguration.ALFRESCO_CONFIG_NAMESPACE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(ALFRESCO_CONFIG_NAMESPACE)
public final class AlfrescoDmsConfigurationImpl implements AlfrescoDmsConfiguration {

    @ConfigValue(key = ALFRESCO_USER, description = "", defaultValue = "admin", category = CC_ENV)
    private String alfrescoUser;

    @ConfigValue(key = ALFRESCO_PASSWORD, description = "", defaultValue = "admin", category = CC_ENV)
    private String alfrescoPassword;

    @ConfigValue(key = ALFRESCO_PAGE_SIZE, description = "", defaultValue = "100", category = CC_ENV)
    private String alfrescoPageSize;

    @ConfigValue(key = "apiBaseUrl", description = "", defaultValue = "http://localhost:10080/alfresco/api/-default-/public/alfresco/versions/1", category = CC_ENV)
    private String alfrescoBaseUrl;

    @ConfigValue(key = "path", description = "", defaultValue = "/User Homes/cmdbuild", category = CC_ENV)
    private String path;

    @Override
    public String getAlfrescoUser() {
        return alfrescoUser;
    }

    @Override
    public String getAlfrescoPassword() {
        return alfrescoPassword;
    }

    @Override
    public String getAlfrescoPageSize() {
        return alfrescoPageSize;
    }

    @Override
    public String getAlfrescoApiBaseUrl() {
        return alfrescoBaseUrl;
    }

    @Override
    public String getAlfrescoPath() {
        return path;
    }
}
