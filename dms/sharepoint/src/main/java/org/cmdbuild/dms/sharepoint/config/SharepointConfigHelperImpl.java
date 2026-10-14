/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dms.sharepoint.config;

import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dms.sharepoint.utils.SharepointUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.enumToString;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author ataboga
 */
public class SharepointConfigHelperImpl implements SharepointConfigHelper {

    private final Map<String, String> translations;
    private final String url, user, password, path, graphApiBaseUrl, protocol, resourceId, clientId, tenantId, serviceUrl, clientSecret, authorColumn, descriptionColumn, categoryColumn;

    private final String SHAREPOINT_URL = "org.cmdbuild.dms.service.sharepoint.url",
            SHAREPOINT_USER = "org.cmdbuild.dms.service.sharepoint.user",
            SHAREPOINT_PASSWORD = "org.cmdbuild.dms.service.sharepoint.password",
            SHAREPOINT_PATH = "org.cmdbuild.dms.service.sharepoint.path",
            SHAREPOINT_GRAPH_API_BASE_URL = "org.cmdbuild.dms.service.sharepoint.graphApi.url",
            SHAREPOINT_AUTH_PROTOCOL = "org.cmdbuild.dms.service.sharepoint.auth.protocol",
            SHAREPOINT_AUTH_RESOURCE_ID = "org.cmdbuild.dms.service.sharepoint.auth.resourceId",
            SHAREPOINT_AUTH_CLIENT_ID = "org.cmdbuild.dms.service.sharepoint.auth.clientId",
            SHAREPOINT_AUTH_TENANT_ID = "org.cmdbuild.dms.service.sharepoint.auth.tenantId",
            SHAREPOINT_AUTH_SERVICE_URL = "org.cmdbuild.dms.service.sharepoint.auth.serviceUrl",
            SHAREPOINT_AUTH_CLIENT_SECRET = "org.cmdbuild.dms.service.sharepoint.auth.clientSecret",
            SHAREPOINT_MODEL_AUTHOR_COLUMN = "org.cmdbuild.dms.service.sharepoint.model.authorColumn",
            SHAREPOINT_MODEL_DESCRIPTION_COLUMN = "org.cmdbuild.dms.service.sharepoint.model.descriptionColumn",
            SHAREPOINT_MODEL_CATEGORY_COLUMN = "org.cmdbuild.dms.service.sharepoint.model.categoryColumn";

    private SharepointConfigHelperImpl(SharepointConfigHelperBuilder builder) {
        this.translations = builder.translations;
        this.url = builder.url;
        this.user = builder.user;
        this.password = builder.password;
        this.path = builder.path;
        this.graphApiBaseUrl = builder.graphApiBaseUrl;
        this.protocol = builder.protocol;
        this.resourceId = builder.resourceId;
        this.clientId = builder.clientId;
        this.tenantId = builder.tenantId;
        this.serviceUrl = builder.serviceUrl;
        this.clientSecret = builder.clientSecret;
        this.authorColumn = builder.authorColumn;
        this.descriptionColumn = builder.descriptionColumn;
        this.categoryColumn = builder.categoryColumn;
    }

    @Override
    @Nullable
    public Map<String, String> getTranslations() {
        return translations;
    }

    @Override
    @Nullable
    public String getUrl() {
        return url;
    }

    @Override
    @Nullable
    public String getUser() {
        return user;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    @Nullable
    public String getGraphApiBaseUrl() {
        return graphApiBaseUrl;
    }

    @Override
    @Nullable
    public String getProtocol() {
        return protocol;
    }

    @Override
    @Nullable
    public String getResourceId() {
        return resourceId;
    }

    @Override
    @Nullable
    public String getClientId() {
        return clientId;
    }

    @Override
    @Nullable
    public String getTenantId() {
        return tenantId;
    }

    @Override
    @Nullable
    public String getServiceUrl() {
        return serviceUrl;
    }

    @Override
    @Nullable
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    @Nullable
    public String getAuthorColumn() {
        return authorColumn;
    }

    @Override
    @Nullable
    public String getDescriptionColumn() {
        return descriptionColumn;
    }

    @Override
    @Nullable
    public String getCategoryColumn() {
        return categoryColumn;
    }

    @Override
    public Map<String, Object> getUiConfigs() {
        return map(
                SHAREPOINT_URL, url,
                SHAREPOINT_USER, user,
                SHAREPOINT_PASSWORD, password,
                SHAREPOINT_PATH, path,
                SHAREPOINT_GRAPH_API_BASE_URL, graphApiBaseUrl,
                SHAREPOINT_AUTH_PROTOCOL, protocol,
                SHAREPOINT_AUTH_RESOURCE_ID, resourceId,
                SHAREPOINT_AUTH_CLIENT_ID, clientId,
                SHAREPOINT_AUTH_TENANT_ID, tenantId,
                SHAREPOINT_AUTH_SERVICE_URL, serviceUrl,
                SHAREPOINT_AUTH_CLIENT_SECRET, clientSecret,
                SHAREPOINT_MODEL_AUTHOR_COLUMN, authorColumn,
                SHAREPOINT_MODEL_DESCRIPTION_COLUMN, descriptionColumn,
                SHAREPOINT_MODEL_CATEGORY_COLUMN, categoryColumn,
                "_model", map("attributes", list(
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_URL, "Url", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_USER, "Username", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_PASSWORD, "Password", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_PATH, "Path", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_GRAPH_API_BASE_URL, "Graph API URL", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_AUTH_PROTOCOL, "Protocol", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_AUTH_RESOURCE_ID, "Resource id", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_AUTH_CLIENT_ID, "Client id", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_AUTH_TENANT_ID, "Tenant id", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_AUTH_SERVICE_URL, "Service url", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_AUTH_CLIENT_SECRET, "Client secret", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_MODEL_AUTHOR_COLUMN, "Author column", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_MODEL_DESCRIPTION_COLUMN, "Description column", translations, AttributeTypeName.STRING, set()),
                        SharepointUtils.generateAttributeConfig(SHAREPOINT_MODEL_CATEGORY_COLUMN, "Category column", translations, AttributeTypeName.STRING, set())
                ))
        );
    }

    @Override
    public String toString() {
        return "OracleUcmUiConfiguration{" + "user=" + user + ", url=" + url + '}';
    }

    public static SharepointConfigHelperBuilder builder() {
        return new SharepointConfigHelperBuilder();
    }

    public static SharepointConfigHelperBuilder builder(SharepointDmsConfiguration config) {
        return new SharepointConfigHelperBuilder()
                .withUrl(config.getSharepointUrl())
                .withUser(config.getSharepointUser())
                .withPassword(config.getSharepointPassword())
                .withPath(config.getSharepointPath())
                .withGraphApiBaseUrl(config.getSharepointGraphApiBaseUrl())
                .withProtocol(enumToString(config.getSharepointAuthProtocol()))
                .withResourceId(config.getSharepointAuthResourceId())
                .withClientId(config.getSharepointAuthClientId())
                .withTenantId(config.getSharepointAuthTenantId())
                .withServiceUrl(config.getSharepointAuthServiceUrl())
                .withClientSecret(config.getSharepointAuthClientSecret())
                .withAuthorColumn(config.getSharepointCustomAuthorColumn())
                .withDescriptionColumn(config.getSharepointCustomDescriptionColumn())
                .withCategoryColumn(config.getSharepointCustomCategoryColumn());
    }

    public static SharepointConfigHelperBuilder copyOf(SharepointConfigHelper source) {
        return new SharepointConfigHelperBuilder()
                .withUrl(source.getUrl())
                .withUser(source.getUser())
                .withPassword(source.getPassword())
                .withPath(source.getPath())
                .withGraphApiBaseUrl(source.getGraphApiBaseUrl())
                .withProtocol(source.getProtocol())
                .withResourceId(source.getResourceId())
                .withClientId(source.getClientId())
                .withTenantId(source.getTenantId())
                .withServiceUrl(source.getServiceUrl())
                .withClientSecret(source.getClientSecret())
                .withAuthorColumn(source.getAuthorColumn())
                .withDescriptionColumn(source.getDescriptionColumn())
                .withCategoryColumn(source.getCategoryColumn())
                .withTranslations(source.getTranslations());
    }

    public static class SharepointConfigHelperBuilder {

        Map<String, String> translations;
        String url, user, password, path, graphApiBaseUrl, protocol, resourceId, clientId, tenantId, serviceUrl, clientSecret, authorColumn, descriptionColumn, categoryColumn;

        public SharepointConfigHelperBuilder withTranslations(Map<String, String> translations) {
            this.translations = translations;
            return this;
        }

        public SharepointConfigHelperBuilder withUrl(String url) {
            this.url = url;
            return this;
        }

        public SharepointConfigHelperBuilder withUser(String user) {
            this.user = user;
            return this;
        }

        public SharepointConfigHelperBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public SharepointConfigHelperBuilder withPath(String path) {
            this.path = path;
            return this;
        }

        public SharepointConfigHelperBuilder withGraphApiBaseUrl(String graphApiBaseUrl) {
            this.graphApiBaseUrl = graphApiBaseUrl;
            return this;
        }

        public SharepointConfigHelperBuilder withProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public SharepointConfigHelperBuilder withResourceId(String resourceId) {
            this.resourceId = resourceId;
            return this;
        }

        public SharepointConfigHelperBuilder withClientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public SharepointConfigHelperBuilder withTenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public SharepointConfigHelperBuilder withServiceUrl(String serviceUrl) {
            this.serviceUrl = serviceUrl;
            return this;
        }

        public SharepointConfigHelperBuilder withClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public SharepointConfigHelperBuilder withAuthorColumn(String authorColumn) {
            this.authorColumn = authorColumn;
            return this;
        }

        public SharepointConfigHelperBuilder withDescriptionColumn(String descriptionColumn) {
            this.descriptionColumn = descriptionColumn;
            return this;
        }

        public SharepointConfigHelperBuilder withCategoryColumn(String categoryColumn) {
            this.categoryColumn = categoryColumn;
            return this;
        }

        public SharepointConfigHelperImpl build() {
            return new SharepointConfigHelperImpl(this);
        }

    }
}
