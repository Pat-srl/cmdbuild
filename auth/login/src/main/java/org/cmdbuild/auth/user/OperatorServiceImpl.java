package org.cmdbuild.auth.user;

import jakarta.inject.Provider;
import org.cmdbuild.auth.login.LoginUserIdentity;
import org.cmdbuild.auth.session.OperatorService;
import org.cmdbuild.auth.session.config.SessionConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static org.cmdbuild.auth.session.config.SessionUserMappingMode.*;
import static org.cmdbuild.auth.user.UserData.USER_ATTR_EMAIL;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;

@Component
public class OperatorServiceImpl implements OperatorService {

    public static final String SESSION_USER_MAPPING_MODE_IS_NOT_DEFINED = "Session user mapping mode is not defined.";
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SessionConfiguration sessionConfiguration;
    private final JdbcTemplate jdbcTemplate;
    private final Provider<DaoService> dao; //TODO not great, remove dependency loop
    private final Provider<UserRepository> userRepository; //TODO not great, remove dependency loop

    public OperatorServiceImpl(
            SessionConfiguration sessionConfiguration,
            JdbcTemplate jdbcTemplate,
            Provider<DaoService> dao,
            Provider<UserRepository> userRepository
    ) {
        this.sessionConfiguration = checkNotNull(sessionConfiguration);
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
        this.dao = checkNotNull(dao);
        this.userRepository = checkNotNull(userRepository);
    }

    /**
     * Returns the properties of the authenticated user based on their login attribute value.
     * @param loginUser The login attribute value of the user.
     * @return A map containing the user's properties.
     */
    @Override
    public Map<String, Object> getProperties(LoginUserIdentity loginUser) {
        String userProperty = sessionConfiguration.getSessionUserMappingUserProperty();
        UserData userData = userRepository.get().getActiveUserData(loginUser);
        return switch (sessionConfiguration.getSessionUserMappingMode()) {
            case SUM_DISABLED -> emptyMap();
            case SUM_CMDBUILD_CLASS -> {
                String sqlExpr = format("SELECT m.\"Id\", m.\"IdClass\" FROM \"%s\" m WHERE m.\"Status\" = 'A' AND m.\"%s\" = ?", getAuthenticatedUserClass().getName(), getAuthenticatedUserAttribute());
                logger.debug("querying authenticated user properties using class {} and attribute {}", getAuthenticatedUserClass().getName(), getAuthenticatedUserAttribute());
                yield fetchAuthenticatedUserAttributes(USER_ATTR_EMAIL.equalsIgnoreCase(userProperty) ? userData.getEmail() : userData.getUsername(), sqlExpr);
            }
            case SUM_DB_FUNCTION -> {
                String functionName = trimAndCheckNotBlank(getAuthenticatedUserDbFunction());
                checkArgument(functionName.matches("[a-z0-9_]+"), "Invalid function name: %s. Only lowercase letters, numbers, and underscores are allowed.", functionName);
                String sqlExpr = format("SELECT * FROM %s(?)", functionName);
                logger.debug("querying authenticated user properties using function {}", functionName);
                yield fetchAuthenticatedUserAttributes(USER_ATTR_EMAIL.equalsIgnoreCase(userProperty) ? userData.getEmail() : userData.getUsername(), sqlExpr);
            }
        };
    }

    public String getReasonForUnsuccessfulSearch() {
        switch (sessionConfiguration.getSessionUserMappingMode()) {
            case SUM_CMDBUILD_CLASS -> {
                String className = sessionConfiguration.getSessionUserMappingClass();
                String attributeName = sessionConfiguration.getSessionUserMappingAttribute();
                if (className == null || className.isEmpty()) {
                    return "Session user mapping Class is not defined.";
                }
                if (attributeName == null || attributeName.isEmpty()) {
                    return "Session user mapping Attribute is not defined.";
                }
                Classe classe = dao.get().getClasseOrNull(className);
                if (classe == null) {
                    return format("Class '%s' does not exist.", className);
                }
                if (!classe.hasAttribute(attributeName)) {
                    return format("Attribute '%s' does not exist in class '%s'.", attributeName, className);
                }
            }
            case SUM_DB_FUNCTION -> {
                String functionName = sessionConfiguration.getSessionUserMappingDbFunction();
                if (functionName == null || functionName.isEmpty()) {
                    return "Session user mapping database function is not defined.";
                }
                if (dao.get().getFunctionOrNull(functionName) == null) {
                    return format("Session user mapping database function '%s' does not exist.", functionName);
                }
            }
            case SUM_DISABLED -> {
                return SESSION_USER_MAPPING_MODE_IS_NOT_DEFINED;
            }
            default -> {
                return SESSION_USER_MAPPING_MODE_IS_NOT_DEFINED;
            }
        }
        return "Authenticated user not found in the configured data source.";
    }

    private Classe getAuthenticatedUserClass() {
        checkArgument(SUM_CMDBUILD_CLASS.equals(sessionConfiguration.getSessionUserMappingMode()), SessionConfiguration.SESSION_CONFIG_PROPERTY_MODE + " must be CMDBUILD_CLASS");
        return dao.get().getClasse(trimAndCheckNotBlank(sessionConfiguration.getSessionUserMappingClass())); // Replace with actual implementation
    }

    private String getAuthenticatedUserAttribute() {
        checkArgument(SUM_CMDBUILD_CLASS.equals(sessionConfiguration.getSessionUserMappingMode()), SessionConfiguration.SESSION_CONFIG_PROPERTY_MODE + " must be CMDBUILD_CLASS");
        return trimAndCheckNotBlank(sessionConfiguration.getSessionUserMappingAttribute());
    }

    private String getAuthenticatedUserDbFunction() {
        checkArgument(SUM_DB_FUNCTION.equals(sessionConfiguration.getSessionUserMappingMode()), SessionConfiguration.SESSION_CONFIG_PROPERTY_MODE + " must be DB_FUNCTION");
        return trimAndCheckNotBlank(sessionConfiguration.getSessionUserMappingDbFunction());
    }

    private Map<String, Object> fetchAuthenticatedUserAttributes(String userRepoLoginAttributeValue, String sqlExpr) {
        Map<String, Object> map = map();
        logger.debug("fetchAuthenticatedUserAttributes: sqlExpr = {}", sqlExpr);
        jdbcTemplate.query(
                sqlExpr,
                rs -> {
                    // Process result set
                    for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                        map.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                    }
                },
                userRepoLoginAttributeValue);
        return map;
    }

}
