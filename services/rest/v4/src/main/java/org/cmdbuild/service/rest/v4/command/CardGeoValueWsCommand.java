/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.gis.GisService;
import org.cmdbuild.gis.GisValue;
import org.cmdbuild.gis.model.GisValueImpl;
import org.cmdbuild.gis.model.LinestringImpl;
import org.cmdbuild.gis.model.PointImpl;
import org.cmdbuild.gis.model.PolygonImpl;
import org.cmdbuild.service.rest.v4.model.WsGisValue;
import org.cmdbuild.service.rest.v4.model.WsPoint;
import org.cmdbuild.service.rest.v4.model.WsPoints;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class CardGeoValueWsCommand {

    private final GisService gisService;

    public CardGeoValueWsCommand(GisService gisService) {
        this.gisService = checkNotNull(gisService);
    }

    public List<GisValue> doGetAllForCard(String classId, Long cardId) {
        return gisService.getGisValuesForCurrentUser(classId, cardId);
    }

    public GisValue doGet(String classId, Long cardId, String attributeId) {
        return gisService.getGisValueForCurrentUser(classId, cardId, attributeId);
    }

    public GisValue doSet(String classId, Long cardId, String attributeId, WsGisValue data) {
        GisValue value = GisValueImpl.builder()
                .withOwnerClassId(classId)
                .withOwnerCardId(cardId)
                .withLayerName(attributeId)
                .accept((b) -> {
                    switch (data.type) {
                        case POINT ->
                                b.withGeometry(new PointImpl(((WsPoint) data.geometry).x, ((WsPoint) data.geometry).y));
                        case LINESTRING ->
                                b.withGeometry(new LinestringImpl(((WsPoints) data.geometry).points.stream().map((p) -> new PointImpl(p.x, p.y)).collect(toList())));
                        case POLYGON ->
                                b.withGeometry(new PolygonImpl(((WsPoints) data.geometry).points.stream().map((p) -> new PointImpl(p.x, p.y)).collect(toList())));
                        default -> throw unsupported("unsupported geometry type = %s", data.type);
                    }
                }).build();
        return gisService.setGisValueWithCurrentUser(value);
    }

    public void doDelete(String classId, Long cardId, String attributeId) {
        gisService.deleteGisValueWithCurrentUser(classId, cardId, attributeId);
    }
}
