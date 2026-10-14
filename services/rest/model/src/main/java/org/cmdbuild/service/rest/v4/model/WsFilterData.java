/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.cardfilter.StoredFilterImpl;
import org.cmdbuild.cardfilter.StoredFilterOwnerType;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author schursin
 */
public class WsFilterData {

    private final String name;
    private final String description;
    private final String target;
    private final String configuration;
    private final boolean shared;

    private final boolean active;
    private final StoredFilterOwnerType ownerType;

    public WsFilterData(@JsonProperty("name") String name,
                        @JsonProperty("description") String description,
                        @JsonProperty("target") String target,
                        @JsonProperty("configuration") String configuration,
                        @JsonProperty("active") Boolean active,
                        @JsonProperty("shared") Boolean shared,
                        @JsonProperty("ownerType") StoredFilterOwnerType ownerType) {
        this.name = checkNotBlank(name, "missing required param 'name'");
        this.description = description;
        this.target = checkNotBlank(target, "missing required param 'target'");
        this.configuration = configuration;
        this.shared = shared;
        if (shared) {
            this.active = active;
        } else {
            this.active = false;
        }
        this.ownerType = ownerType;
    }

    public boolean isShared() {
        return shared;
    }

    public StoredFilterImpl.StoredFilterImplBuilder toCardFilter() {
        return StoredFilterImpl.builder()
                .withOwnerName(target)
                .withConfiguration(configuration)
                .withDescription(description)
                .withName(name)
                .withActive(active)
                .withShared(shared)
                .withOwnerType(ownerType);
    }
}
