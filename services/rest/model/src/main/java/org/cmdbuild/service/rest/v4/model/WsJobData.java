/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.jobs.beans.JobDataImpl;

import java.util.Map;

import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;

/**
 * @author ldare
 */
public class WsJobData {

    private final String code, description, type;
    private final Boolean enabled;
    private final Map<String, Object> config;

    public WsJobData(
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("type") String type,
            @JsonProperty("enabled") Boolean enabled,
            @JsonProperty("config") Map<String, Object> config) {
        this.code = code;
        this.description = description;
        this.type = type;
        this.enabled = enabled;
        this.config = config;
    }

    public JobDataImpl.JobDataImplBuilder toJobData() {
        return JobDataImpl.builder()
                .withCode(code)
                .withConfig(flattenMaps(config))
                .withDescription(description)
                .withEnabled(enabled)
                .withType(type);
    }

}
