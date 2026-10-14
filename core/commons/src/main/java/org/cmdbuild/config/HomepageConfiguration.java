package org.cmdbuild.config;

public interface HomepageConfiguration {

    public static final String HP_NAMESPACE = "org.cmdbuild.homepages",
            HP_ENABLED = "enabled",
            HP_TYPE = "type",
            HP_SEARCH_CLASSES = "searchclasses",
            HOMEPAGE_CONFIG_PROPERTY_ENABLED = HP_NAMESPACE + "." + HP_ENABLED,
            HOMEPAGE_CONFIG_PROPERTY_TYPE = HP_NAMESPACE + "." + HP_TYPE,
            HOMEPAGE_CONFIG_PROPERTY_SEARCH_CLASSES = HP_NAMESPACE + "." + HP_SEARCH_CLASSES;

    boolean isEnabled();
    String getType();
    String getSearchClasses();
}
