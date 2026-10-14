package org.cmdbuild.config;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.RestConfiguration.REST_CONFIG_NAMESPACE;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
@Primary
@ConfigComponent(REST_CONFIG_NAMESPACE)
public class RestConfigurationImpl implements RestConfiguration {

    @ConfigValue(key = REST_V2_CONFIG_ENABLED, description = "enable rest v2 service", defaultValue = FALSE)
    private boolean isRestV2Enabled;

    @Override
    public boolean isRestV2Enabled() {
        return isRestV2Enabled;
    }

}
