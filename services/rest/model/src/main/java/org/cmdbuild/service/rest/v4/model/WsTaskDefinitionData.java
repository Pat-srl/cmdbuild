/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * @author ldare
 */
public class WsTaskDefinitionData {

    public final JsonNode formStructure;

    public WsTaskDefinitionData(@JsonProperty("formStructure") JsonNode formStructure) {
        this.formStructure = formStructure;
    }
}
