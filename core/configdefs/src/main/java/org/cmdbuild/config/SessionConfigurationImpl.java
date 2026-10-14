package org.cmdbuild.config;

import org.cmdbuild.auth.session.config.SessionConfiguration;
import org.cmdbuild.auth.session.config.SessionUserMappingMode;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.springframework.stereotype.Component;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static org.cmdbuild.auth.session.config.SessionConfiguration.SS_NAMESPACE;
import static org.cmdbuild.config.api.ConfigCategory.CC_DATA;

@Component
@ConfigComponent(SS_NAMESPACE)
public class SessionConfigurationImpl implements SessionConfiguration {

    @ConfigValue(key = SS_MODE, defaultValue = "DISABLED", description = "valid values are `DISABLED`, `CMDBUILD_CLASS` or `DB_FUNCTION`", category = CC_DATA)
    private SessionUserMappingMode sessionUserMappingMode;

    @ConfigValue(key = SS_CLASS, description = "class to retrieve operator data", category = CC_DATA)
    private String sessionUserMappingClass;

    @ConfigValue(key = SS_DB_FUNCTION, defaultValue = DEFAULT_DB_FUNCTION_NAME, description = "database function to retrieve operator data", category = CC_DATA)
    private String sessionUserMappingDbFunction;

    @ConfigValue(key = SS_ATTRIBUTE, description = "login attribute of class to use to retrieve operator data", category = CC_DATA)
    private String sessionUserMappingAttribute;

    @ConfigValue(key = SS_USER_PROPERTY, defaultValue = "username", description = "user property to retrieve operator data", category = CC_DATA)
    private String sessionUserMappingUserProperty;

    @Override
    public SessionUserMappingMode getSessionUserMappingMode() {
        return sessionUserMappingMode;
    }

    @Override
    public String getSessionUserMappingClass() throws IllegalArgumentException {
        checkArgument(equal(getSessionUserMappingMode(), SessionUserMappingMode.SUM_CMDBUILD_CLASS),
                "userMapping.class is only valid when sessionUserMappingMode is CMDBUILD_CLASS");
        return sessionUserMappingClass;
    }

    @Override
    public String getSessionUserMappingDbFunction() throws IllegalArgumentException {
        checkArgument(equal(getSessionUserMappingMode(), SessionUserMappingMode.SUM_DB_FUNCTION),
                "userMapping.dbFunction is only valid when sessionUserMappingMode is DB_FUNCTION");
        return sessionUserMappingDbFunction;
    }

    @Override
    public String  getSessionUserMappingAttribute() throws IllegalArgumentException {
        checkArgument(equal(getSessionUserMappingMode(), SessionUserMappingMode.SUM_CMDBUILD_CLASS),
                "userMapping.attribute is only valid when sessionUserMappingMode is CMDBUILD_CLASS");
        return sessionUserMappingAttribute;
    }

    @Override
    public String getSessionUserMappingUserProperty() {
        checkArgument(sessionUserMappingUserProperty != null,
                "userMapping.userProperty is required");
        return sessionUserMappingUserProperty;
    }
}
