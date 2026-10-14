package org.cmdbuild.dms.alfresco.config;

import static org.cmdbuild.dms.DmsConfiguration.DMS_CONFIG_NAMESPACE;

public interface AlfrescoDmsConfiguration extends AlfrescoConfiguration {

    String ALFRESCO_CONFIG_NAMESPACE = DMS_CONFIG_NAMESPACE + ".service.alfresco";

    String getAlfrescoApiBaseUrl();

    String getAlfrescoPath();
}
