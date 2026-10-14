/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ldare
 */
public class WsPoint {

    public final double x, y;

    public WsPoint(@JsonProperty("x") Double x, @JsonProperty("y") Double y) {
        this.x = x;
        this.y = y;
    }
}

