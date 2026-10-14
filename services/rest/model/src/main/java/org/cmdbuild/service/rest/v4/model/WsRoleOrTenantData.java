/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ldare
 */
public class WsRoleOrTenantData {

    private final long id;

    public WsRoleOrTenantData(@JsonProperty("_id") Long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
