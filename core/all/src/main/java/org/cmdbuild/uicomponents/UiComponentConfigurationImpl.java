/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.uicomponents.UiComponentConfiguration.UICOMPONENT_CONFIG_NAMESPACE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(UICOMPONENT_CONFIG_NAMESPACE)
public class UiComponentConfigurationImpl implements UiComponentConfiguration {

    @ConfigValue(key = "js.compression.enabled", description = "enable compression of js files", defaultValue = TRUE)
    private boolean jsCompressionEnabled;

    @Override
    public boolean isJsCompressionEnabled() {
        return jsCompressionEnabled;
    }
}
