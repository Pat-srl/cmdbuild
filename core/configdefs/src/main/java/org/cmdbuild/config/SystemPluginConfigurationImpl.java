/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.config;

import java.util.List;
import org.cmdbuild.config.api.ConfigCategory;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.systemplugin.SystemPluginConfiguration;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
@ConfigComponent(value = "org.cmdbuild.plugin")
public class SystemPluginConfigurationImpl implements SystemPluginConfiguration {

    @ConfigValue(key = "list", description = "list of system plugins (read only)", category = ConfigCategory.CC_ENV)
    private List<String> list;

    @ConfigValue(key = "info", description = "info of system plugins (read only)", category = ConfigCategory.CC_ENV)
    private String info;

    @ConfigValue(key = "versioncheck.enabled", description = "enable version check for system plugins (plugin version must be the same as core)", defaultValue = TRUE)
    private Boolean versionCheck;

    @Override
    public boolean isVersionCheckEnabled() {
        return versionCheck;
    }
}
