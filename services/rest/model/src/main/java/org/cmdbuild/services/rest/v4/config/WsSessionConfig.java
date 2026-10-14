/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.v4.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.auth.session.config.SessionUserMappingMode;

import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.auth.session.config.SessionConfiguration.*;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

/**
 * @author mbuzzulini
 */
public class WsSessionConfig {

    public final SessionUserMappingMode sessionUserMappingMode;
    public final String userMappingClass;
    public final String userMappingAttribute;
    public final String userMappingDbFunction;

    public WsSessionConfig(
            @JsonProperty(SESSION_CONFIG_PROPERTY_MODE) String sessionUserMappingMode,
            @JsonProperty(SESSION_CONFIG_PROPERTY_USER_MAPPING_CLASS) String userMappingClass,
            @JsonProperty(SESSION_CONFIG_PROPERTY_USER_MAPPING_ATTRIBUTE) String userMappingAttribute,
            @JsonProperty(SESSION_CONFIG_PROPERTY_USER_MAPPING_DB_FUNCTION) String userMappingDbFunction
    ) {
        this.sessionUserMappingMode = parseEnum(sessionUserMappingMode, SessionUserMappingMode.class);
        this.userMappingClass = trimToNull(userMappingClass);
        this.userMappingAttribute = trimToNull(userMappingAttribute);
        this.userMappingDbFunction = trimToNull(userMappingDbFunction);
    }

}
