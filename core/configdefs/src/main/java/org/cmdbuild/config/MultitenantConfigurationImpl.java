package org.cmdbuild.config;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;

import org.springframework.stereotype.Component;
import static org.cmdbuild.auth.multitenant.config.MultitenantConfiguration.MT_NAMESPACE;
import org.cmdbuild.auth.multitenant.config.MultitenantMode;
import static org.cmdbuild.auth.multitenant.config.MultitenantMode.MTM_CMDBUILD_CLASS;
import static org.cmdbuild.auth.multitenant.config.MultitenantMode.MTM_DB_FUNCTION;
import static org.cmdbuild.config.api.ConfigCategory.CC_DATA;
import static org.cmdbuild.config.api.ConfigValue.TRUE;

@Component
@ConfigComponent(MT_NAMESPACE)
public final class MultitenantConfigurationImpl implements MultitenantConfiguration {

    @ConfigValue(key = MT_TENANT_CLASS, isProtected = true, category = CC_DATA)
    private String multitenantTenantClass;

    @ConfigValue(key = MT_TENANT_DOMAIN, description = "default to \"User\" + tenant_class_name", isProtected = true, category = CC_DATA)
    private String multitenantTenantDomain;

    @ConfigValue(key = "dbFunction", defaultValue = DEFAULT_DB_FUNCTION_NAME, isProtected = true, category = CC_DATA)
    private String multitenantDbFunction;

    @ConfigValue(key = MT_MODE, defaultValue = "DISABLED", description = "valid values are DISABLED, CMDBUILD_CLASS, DB_FUNCTION", isProtected = true, category = CC_DATA)
    private MultitenantMode multitenantMode;

    @ConfigValue(key = "adminIgnoresTenantsDefault", defaultValue = TRUE, description = "configure if ignore_tenants flag should be enabled or disabled by default (applies only to tenant admin users)")
    private boolean tenantAdminIgnoresTenantByDefault;

    @ConfigValue(key = "name", description = "multitenant field name (label)", category = CC_DATA)
    private String tenantName;

    @Override
    public MultitenantMode getMultitenantMode() {
        return multitenantMode;
    }

    @Override
    public String getTenantClass() throws IllegalArgumentException {
        checkArgument(equal(getMultitenantMode(), MTM_CMDBUILD_CLASS));
        return multitenantTenantClass;
    }

    @Override
    public String getDbFunction() throws IllegalArgumentException {
        checkArgument(equal(getMultitenantMode(), MTM_DB_FUNCTION));
        return multitenantDbFunction;
    }

    @Override
    @Nullable
    public String getTenantDomain() {
        return trimToNull(multitenantTenantDomain);
    }

    @Override
    public boolean tenantAdminIgnoresTenantByDefault() {
        return tenantAdminIgnoresTenantByDefault;
    }

    @Override
    public String getTenantName() {
        return tenantName;
    }

}
