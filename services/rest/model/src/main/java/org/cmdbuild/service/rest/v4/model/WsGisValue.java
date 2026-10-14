/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.gis.GisValueType;

import java.util.List;

import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * @author ldare
 */
public class WsGisValue {

    public final GisValueType type;
    public final Object geometry;

    public WsGisValue(
            @JsonProperty("_type") String type,
            @JsonProperty("x") Double x,
            @JsonProperty("y") Double y,
            @JsonProperty("points") List<WsPoint> points) {
        this.type = GisValueType.valueOf(checkNotBlank(type).toUpperCase());
        geometry = switch (this.type) {
            case POINT -> new WsPoint(x, y);
            case LINESTRING, POLYGON -> new WsPoints(points);
            default -> throw unsupported("unsupported geometry type = %s", this.type);
        };
    }
}