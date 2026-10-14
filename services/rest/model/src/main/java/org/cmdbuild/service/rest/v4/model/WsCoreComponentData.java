/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.corecomponents.CoreComponentImpl;

/**
 *
 * @author schursin
 */
public class WsCoreComponentData {

    private final String description, data, code;
    private final Boolean isActive;

    public WsCoreComponentData(@JsonProperty("name") String code,
                               @JsonProperty("description") String description,
                               @JsonProperty("data") String data,
                               @JsonProperty("active") Boolean isActive) {
        this.description = description;
        this.code = code;
        this.data = data;
        this.isActive = isActive;
    }

    public CoreComponentImpl.CoreComponentImplBuilder toCoreComponent() {
        return CoreComponentImpl.builder()
                .withActive(isActive)
                .withCode(code)
                .withDescription(description)
                .withData(data);
    }
}
