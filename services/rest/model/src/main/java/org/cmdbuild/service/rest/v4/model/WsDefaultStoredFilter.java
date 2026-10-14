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
public class WsDefaultStoredFilter {

    private final long id;

    public WsDefaultStoredFilter(@JsonProperty("_id") Long roleId) {
        this.id = roleId;
    }

    public long getId() {
        return id;
    }

}
