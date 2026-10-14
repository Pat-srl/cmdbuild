/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
public class WsDefaultStoredFilterForClass {

    private final long id;
    private final String forClass;

    public WsDefaultStoredFilterForClass(@JsonProperty("_id") Long id, @JsonProperty("_defaultFor") String forClass) {
        this.id = id;
        this.forClass = checkNotNull(forClass);
    }

    public long getId() {
        return id;
    }

    public String getForClass() {
        return forClass;
    }
}
