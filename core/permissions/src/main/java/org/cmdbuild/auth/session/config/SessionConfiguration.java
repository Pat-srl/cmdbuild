package org.cmdbuild.auth.session.config;

import static com.google.common.base.Objects.equal;

public interface SessionConfiguration {

    public static final String SS_NAMESPACE = "org.cmdbuild.session.userMapping",
                               SS_CLASS = "class",
                               SS_ATTRIBUTE = "attribute",
                               SS_DB_FUNCTION = "dbFunction",
                               SS_USER_PROPERTY = "userProperty",
                               SS_MODE = "mode",
                               SESSION_CONFIG_PROPERTY_USER_MAPPING_CLASS = SS_NAMESPACE + "." + SS_CLASS,
                               SESSION_CONFIG_PROPERTY_USER_MAPPING_ATTRIBUTE = SS_NAMESPACE + "." + SS_ATTRIBUTE,
                               SESSION_CONFIG_PROPERTY_USER_MAPPING_DB_FUNCTION = SS_NAMESPACE + "." + SS_DB_FUNCTION,
                               SESSION_CONFIG_PROPERTY_USER_MAPPING_USER_PROPERTY = SS_NAMESPACE + "." + SS_USER_PROPERTY,
                               SESSION_CONFIG_PROPERTY_MODE = SS_NAMESPACE + "." + SS_MODE;

    static final String DEFAULT_DB_FUNCTION_NAME = "_cm3_session_user_mapping_get";

    /**
     * return homepage mode, which is uoe of <ul>
     *     <li>{@link SessionUserMappingMode#SUM_DISABLED}: session is disabled</li>
     *     <li>{@link SessionUserMappingMode#SUM_CMDBUILD_CLASS}: session will use the
     *     supplied class (and relation with cmdbuild user) to retrieve the
     *     user data</li>
     *     <li>{@link SessionUserMappingMode#SUM_DB_FUNCTION}: session will use the
     *     supplied database function to retrieve the user data</li>
     * </ul>
     * @return homepage mode
     */
    SessionUserMappingMode getSessionUserMappingMode();

    /**
     * @return cmdbuild class to be used as user data source
     * @throws IllegalArgumentException if mode is not {@link SessionUserMappingMode#SUM_CMDBUILD_CLASS}
     */
    String getSessionUserMappingClass();

    /**
     * @return cmdbuild attribute to be used as key to retrieve user data
     */
    String getSessionUserMappingAttribute();

    /**
     * @return cmdbuild function that returns user data to be used as user data source
     * @throws IllegalArgumentException if mode is not {@link SessionUserMappingMode#SUM_DB_FUNCTION}
     */
    String getSessionUserMappingDbFunction();


    /**
     * @return cmdbuild user property to be used as key to retrieve user data
     */
    String getSessionUserMappingUserProperty();

    default boolean isUserMappingModeDisabled() {
        return equal(getSessionUserMappingMode(), SessionUserMappingMode.SUM_DISABLED);
    }

    default boolean isUserMappingModeEnabled() {
        return !isUserMappingModeDisabled();
    }
}
