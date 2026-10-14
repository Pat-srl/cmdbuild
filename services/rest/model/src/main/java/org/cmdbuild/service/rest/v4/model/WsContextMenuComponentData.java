/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ldare
 */
public class WsContextMenuComponentData {

    private final String description;
    private final Boolean isActive;

    public WsContextMenuComponentData(@JsonProperty("description") String description, @JsonProperty("active") Boolean isActive) {
        this.description = description;
        this.isActive = isActive;
    }

    public String getDescription() {
        return this.description;
    }

    public Boolean getIsActive() {
        return this.isActive;
    }
}
