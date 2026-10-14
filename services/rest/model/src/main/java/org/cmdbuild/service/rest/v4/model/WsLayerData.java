/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

/**
 *
 * @author schursin
 */
public class WsLayerData {

    private final Boolean active;

    public WsLayerData(@JsonProperty("layer_active") Boolean active) {
        this.active = firstNonNull(active, true);
    }

    public Boolean getActive() {
        return active;
    }
}
