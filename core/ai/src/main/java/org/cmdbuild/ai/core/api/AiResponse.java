/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.ai.core.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.ai.core.api.resource.AiResource;

import java.util.List;

import static java.util.Collections.emptyList;

/**
 *
 * @author ataboga
 */
public class AiResponse {

    private final String response;
    private final List<AiResource> resources;
    private final long durationMillis;

    public AiResponse(String response, long durationMillis) {
        this.response = response;
        this.resources = emptyList();
        this.durationMillis = durationMillis;
    }

    @JsonCreator
    public AiResponse(@JsonProperty("response") String response, @JsonProperty("resources") List<AiResource> resources, @JsonProperty("durationMillis") long durationMillis) {
        this.response = response;
        this.resources = resources;
        this.durationMillis = durationMillis;
    }

    public String getResponse() {
        return response;
    }

    public List<AiResource> getResources() {
        return resources;
    }

    public long getDurationMillis() {
        return durationMillis;
    }
}
