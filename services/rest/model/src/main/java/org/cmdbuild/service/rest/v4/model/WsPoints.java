/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
public class WsPoints {

    public final List<WsPoint> points;

    public WsPoints(List<WsPoint> points) {
        this.points = checkNotNull(points);
    }
}
