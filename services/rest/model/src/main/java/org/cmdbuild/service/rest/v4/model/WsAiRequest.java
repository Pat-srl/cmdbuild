/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ldare
 */
public class WsAiRequest {

    private final String query;
    private final String context;

    public WsAiRequest(@JsonProperty("query") String query,
                       @JsonProperty("context") String context) {
        this.query = query;
        this.context = context;
    }

    public String getQuery() {
        return query;
    }

    public String getContext() {
        return context;
    }
}
