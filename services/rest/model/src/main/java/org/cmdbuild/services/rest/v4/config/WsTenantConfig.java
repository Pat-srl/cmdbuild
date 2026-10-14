/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.v4.config;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.auth.multitenant.config.MultitenantMode;

import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_MODE;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MULTITENANT_CONFIG_PROPERTY_TENANT_CLASS;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

/**
 * @author ldare
 */
public class WsTenantConfig {

    public final MultitenantMode multitenantMode;
    public final String tenantClass;

    public WsTenantConfig(
            @JsonProperty(MULTITENANT_CONFIG_PROPERTY_MODE) String multitenantMode,
            @JsonProperty(MULTITENANT_CONFIG_PROPERTY_TENANT_CLASS) String tenantClass) {
        this.multitenantMode = parseEnum(multitenantMode, MultitenantMode.class);
        this.tenantClass = trimToNull(tenantClass);
    }

}
