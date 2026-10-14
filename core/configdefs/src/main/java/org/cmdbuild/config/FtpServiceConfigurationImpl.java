/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.ftp")
public class FtpServiceConfigurationImpl implements FtpServiceConfiguration {

    @ConfigValue(key = "enabled", description = "", defaultValue = FALSE, experimental = true)
    private boolean enabled;

    @ConfigValue(key = "port", description = "", defaultValue = "2221", experimental = true)
    private int port;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
