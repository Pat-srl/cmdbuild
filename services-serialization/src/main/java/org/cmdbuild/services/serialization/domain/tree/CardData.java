/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.services.serialization.domain.tree;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author afelice
 */
public class CardData {
    @JsonProperty("_id")
    public Long id;

    @JsonProperty("Code")
    public String code;
    @JsonProperty("Description")
    public String description;

    @JsonProperty("_type")
    public String className;
}
