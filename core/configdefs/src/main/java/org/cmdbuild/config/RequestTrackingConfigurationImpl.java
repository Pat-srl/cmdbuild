/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static org.cmdbuild.config.RequestTrackingConfiguration.LogTrackingMode.LTM_NEVER;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.audit")
public class RequestTrackingConfigurationImpl implements RequestTrackingConfiguration {

    @ConfigValue(key = "enabled", defaultValue = TRUE)
    private boolean isRequestTrackingEnabled;

    @ConfigValue(key = "includePayload", defaultValue = TRUE)
    private boolean includeRequestPayload;

    @ConfigValue(key = "includeResponse", defaultValue = TRUE)
    private boolean includeResponsePayload;

    @ConfigValue(key = "includeHeaders", defaultValue = TRUE)
    private boolean includeHeaders;

    @ConfigValue(key = "includeTcpDump", defaultValue = TRUE)
    private boolean includeTcpDump;

    @ConfigValue(key = "filterPayload", description = "filter sensitive data in payload or apply other custom rules to payload", defaultValue = TRUE, isProtected = true, experimental = true)//TODO hidden??

    private boolean filterPayload;

    @ConfigValue(key = "maxPayloadSize", defaultValue = "50000") // 50 KB -- "100000000" 100 MB
    private int maxPayloadLength;

    @ConfigValue(key = "exclude")
    private String excludeRegex;

    @ConfigValue(key = "include", defaultValue = "^/services/")
    private String includeRegex;

    @ConfigValue(key = "logTrackingMode", defaultValue = "always", description = "log tacking mode, one of `always`, `never`, `on_error`; recommended production setting is `on_error`")
    private LogTrackingMode logTrackingMode;

    @Override
    public boolean includeHeaders() {
        return includeHeaders;
    }

    @Override
    public boolean isRequestTrackingEnabled() {
        return isRequestTrackingEnabled;
    }

    @Override
    public boolean includeRequestPayload() {
        return includeRequestPayload;
    }

    @Override
    public boolean includeResponsePayload() {
        return includeResponsePayload;
    }

    @Override
    public boolean includeTcpDump() {
        return includeTcpDump;
    }

    @Override
    public int getMaxPayloadLength() {
        return maxPayloadLength;
    }

    @Override
    public String getRegexForPathsToExclude() {
        return excludeRegex;
    }

    @Override
    public String getRegexForPathsToInclude() {
        return includeRegex;
    }

    @Override
    public LogTrackingMode getLogTrackingMode() {
        return firstNotNull(logTrackingMode, LTM_NEVER);
    }

    @Override
    public boolean filterPayload() {
        return filterPayload;
    }

}
