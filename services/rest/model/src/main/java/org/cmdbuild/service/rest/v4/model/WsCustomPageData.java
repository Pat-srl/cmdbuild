/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author schursin
 */
public class WsCustomPageData {

    private final String description;

    public String getDescription() {
        return description;
    }

    public boolean isIsActive() {
        return isActive;
    }

    private final boolean isActive;

    private final String iconFont;

    public String getIconFont() {
        return iconFont;
    }

    public WsCustomPageData(
            @JsonProperty("description") String description,
            @JsonProperty("active") boolean active,
            @JsonProperty("icon_font") String iconFont) {
        this.description = description;
        this.isActive = active;
        this.iconFont = iconFont;
    }

}
