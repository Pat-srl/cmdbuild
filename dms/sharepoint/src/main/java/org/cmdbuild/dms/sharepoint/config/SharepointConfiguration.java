package org.cmdbuild.dms.sharepoint.config;

import org.cmdbuild.dms.sharepoint.SharepointDmsAuthProtocol;

public interface SharepointConfiguration {

    final String SHAREPOINT_URL = "url",
            SHAREPOINT_USER = "user",
            SHAREPOINT_PASSWORD = "password",
            SHAREPOINT_AUTH_PROTOCOL = "auth.protocol",
            SHAREPOINT_AUTH_RESOURCE_ID = "auth.resourceId",
            SHAREPOINT_AUTH_CLIENT_ID = "auth.clientId",
            SHAREPOINT_AUTH_TENANT_ID = "auth.tenantId",
            SHAREPOINT_AUTH_SERVICE_URL = "auth.serviceUrl",
            SHAREPOINT_AUTH_SERVICE_URL_DEFAULT = "https://login.microsoftonline.com",
            SHAREPOINT_AUTH_CLIENT_SECRET = "auth.clientSecret",
            SHAREPOINT_GRAPH_API_BASE_URL = "graphApi.url",
            SHAREPOINT_GRAPH_API_BASE_URL_DEFAULT = "https://graph.microsoft.com/v1.0/";

    String getSharepointUrl();

    String getSharepointUser();

    String getSharepointPassword();

    SharepointDmsAuthProtocol getSharepointAuthProtocol();

    String getSharepointAuthResourceId();

    String getSharepointAuthClientId();

    String getSharepointAuthTenantId();

    String getSharepointAuthServiceUrl();

    String getSharepointAuthClientSecret();

    String getSharepointGraphApiBaseUrl();

    boolean autodeleteEmptyDirectories();

}
