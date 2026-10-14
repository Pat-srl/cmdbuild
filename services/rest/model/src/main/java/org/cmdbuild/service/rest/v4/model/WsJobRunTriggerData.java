/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * @author ldare
 */
public class WsJobRunTriggerData {

    private final Map<String, Object> config;

    public WsJobRunTriggerData(@JsonProperty("config") Map<String, Object> config) {
        this.config = config;
    }

    public Map<String, Object> getConfig() {
        return config;
    }
}
