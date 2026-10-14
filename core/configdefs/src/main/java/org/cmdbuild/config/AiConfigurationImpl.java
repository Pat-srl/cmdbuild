/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.config;

import org.cmdbuild.ai.AiConfiguration;
import static org.cmdbuild.ai.AiConfiguration.AI_CONFIG_NAMESPACE;
import static org.cmdbuild.ai.AiConfiguration.AI_CONFIG_PROVIDER;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
@ConfigComponent(AI_CONFIG_NAMESPACE)
public class AiConfigurationImpl implements AiConfiguration {

    private static final String ENABLED = "enabled";

    @ConfigValue(key = ENABLED, description = "", defaultValue = FALSE)
    private boolean isEnabled;

    @ConfigValue(key = AI_CONFIG_PROVIDER, description = "ai service (e.g. `powerfinder`)", defaultValue = "")
    private String service;

    @ConfigValue(key = AI_CONFIG_CHAT_NAME, description = "ai chat name", defaultValue = "MyDA")
    private String chatName;

    @ConfigValue(key = AI_CONFIG_CHAT_AVATAR, description = "ai chat avatar, fontawesome or base64", defaultValue = "")
    private String chatAvatar;

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public String getName() {
        return chatName;
    }

    @Override
    public String getAvatar() {
        return chatAvatar;
    }
}
