/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.etl.gate.inner.EtlGateImpl;
import org.cmdbuild.etl.gate.inner.EtlProcessingMode;

import java.util.List;
import java.util.Map;

import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

public class WsImportExportGateData {

    private final String code, description;
    private final Boolean enabled, allowPublicAccess;
    private final Map<String, String> config;
    private final EtlProcessingMode processingMode;
    private final List<Map<String, String>> handlers;

    public WsImportExportGateData(
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("processingMode") String processingMode,
            @JsonProperty("allowPublicAccess") Boolean allowPublicAccess,
            @JsonProperty("enabled") Boolean enabled,
            @JsonProperty("config") Map<String, String> config,
            @JsonProperty("handlers") List<Map<String, String>> handlers) {
        this.code = code;
        this.description = description;
        this.processingMode = parseEnumOrNull(processingMode, EtlProcessingMode.class);
        this.allowPublicAccess = allowPublicAccess;
        this.enabled = enabled;
        this.config = config;
        this.handlers = handlers;
    }

    public EtlGateImpl.EtlGateImplBuilder toEtlGate() {
        return EtlGateImpl.builder()
                .withCode(code)
                .withDescription(description)
                .withAllowPublicAccess(allowPublicAccess)
                .withConfig(config)
                .withEnabled(enabled)
                .withProcessingMode(processingMode)
                .withHandlersConfig(handlers);
    }
}
