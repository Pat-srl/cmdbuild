package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisService;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Path("classes/{classId}/geoattributes/")
@Produces(APPLICATION_JSON)
public class GeoAttributesWsV2 {

    private final GisService service;
    private final EasyuploadService easyuploadService;

    public GeoAttributesWsV2(GisService service, EasyuploadService easyuploadService) {
        this.service = checkNotNull(service);
        this.easyuploadService = checkNotNull(easyuploadService);
    }

    @GET
    @Path(EMPTY)
    public Object readAllAttributes(@PathParam("classId") String classId, @QueryParam("visible") boolean visible) {
        List<GisAttribute> elements;
        if (visible) {
            elements = service.getGisAttributesVisibleFromClass(classId);
        } else {
            elements = service.getGisAttributesByOwnerClassIncludeInherited(classId);
        }
        return map("data", elements.stream().map(this::serializeGisAttribute).collect(toList()), "meta", map("total", elements.size()));
    }

    @GET
    @Path("{attributeId}/")
    public Object readAttribute(@PathParam("classId") String classId, @PathParam("attributeId") String attributeId) {
        GisAttribute layer = service.getGisAttributeIncludeInherited(classId, attributeId);
        return map("data", serializeGisAttribute(layer), "meta", map());
    }

    private Object serializeGisAttribute(GisAttribute layer) {
        return map(
                "_id", layer.getId(),
                "owner_type", layer.getOwnerClassName(),
                "type", "geometry",
                "subtype", layer.getType(),
                "description", layer.getDescription(),
                "index", layer.getIndex(),
                "name", layer.getLayerName(),
                "visibility", list(layer.getVisibility()),
                "zoomMin", layer.getMinimumZoom(),
                "zoomMax", layer.getMaximumZoom(),
                "zoomDef", layer.getDefaultZoom(),
                "style", map((Map<String, Object>) fromJson(layer.getMapStyle(), MAP_OF_OBJECTS)).accept((m) -> {
                    String icon = toStringOrNull(m.get("externalGraphic"));
                    if (isNotBlank(icon)) {
                        m.put("_icon", Optional.ofNullable(easyuploadService.getByPathOrNull(icon)).map(EasyuploadItem::getId).orElse(null));
                    }
                }));
    }

}
