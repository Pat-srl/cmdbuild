/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ldare
 */
public class WsUploadData {

    private final String path;
    private final String description;

    public WsUploadData(@JsonProperty("path") String path, @JsonProperty("description") String description) {
        this.path = path;
        this.description = description;
    }

    public String getPath() {
        return this.path;
    }

    public String getDescription() {
        return this.description;
    }
}
