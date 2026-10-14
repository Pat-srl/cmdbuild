/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static org.cmdbuild.config.api.ConfigCategory.CC_ENV;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.waterway")
public class WaterwayConfigImpl implements WaterwayConfig {

    @ConfigValue(key = "defaultStorage", description = "default storage component", defaultValue = "default")
    private String defaultStorage;

    @ConfigValue(key = "jobs.enabled", description = "enable waterway triggers (jobs)", defaultValue = FALSE, category = CC_ENV)
    private boolean triggersEnabled;

    @ConfigValue(key = "webhook.enabled", description = "enable webhook triggers", defaultValue = TRUE, category = CC_ENV)
    private boolean webhookEnabled;

    @Override
    public String getDefaultStorage() {
        return defaultStorage;
    }

    @Override
    public boolean isTriggersEnabled() {
        return triggersEnabled;
    }

    @Override
    public boolean isWebhookEnabled() {
        return webhookEnabled;
    }

}
