/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ldare
 */
public class WsWidgetComponentData {

    private final String description;
    private final Boolean active;

    public WsWidgetComponentData(@JsonProperty("description") String description, @JsonProperty("active") Boolean active) {
        this.description = description;
        this.active = active;
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean isActive() {
        return this.active;
    }
}
