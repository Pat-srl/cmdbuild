/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.dms.sharepoint.config;

import java.util.Map;

/**
 *
 * @author ataboga
 */
public interface SharepointConfigHelper {

    public String getUrl();

    public String getUser();

    public String getPassword();

    public String getPath();

    public String getGraphApiBaseUrl();

    public String getProtocol();

    public String getResourceId();

    public String getClientId();

    public String getTenantId();

    public String getServiceUrl();

    public String getClientSecret();

    public String getAuthorColumn();

    public String getDescriptionColumn();

    public String getCategoryColumn();

    public Map<String, Object> getUiConfigs();

    public Map<String, String> getTranslations();
}
