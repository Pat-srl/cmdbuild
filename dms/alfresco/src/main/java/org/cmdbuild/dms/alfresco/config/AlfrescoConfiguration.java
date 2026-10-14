package org.cmdbuild.dms.alfresco.config;

public interface AlfrescoConfiguration {

    String ALFRESCO_USER = "user";
    String ALFRESCO_PASSWORD = "password";
    String ALFRESCO_PAGE_SIZE = "pageSize";

    String getAlfrescoUser();

    String getAlfrescoPassword();

    String getAlfrescoPageSize();
}
