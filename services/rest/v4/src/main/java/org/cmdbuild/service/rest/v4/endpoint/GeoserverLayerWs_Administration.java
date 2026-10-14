/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.service.rest.v4.command.GeoserverLayerWsCommand;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_GIS_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.serializationhelpers.GeoserverLayerSerializationHelper.serializeLayer;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.command.WsUtils.filterSerializations;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author schursin
 */
@Path("administration/{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/{" + TYPE_NAME + ":cards|instances}/{" + CARD_ID + "}/geolayers/")
@Tags({
        @Tag(name = "Geoserver Layers", description = "APIs to manage geoserver layers."),
        @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class GeoserverLayerWs_Administration {

    private final GisService gisService;
    private final DaoService daoService;
    private final GeoserverLayerWsCommand command;

    public GeoserverLayerWs_Administration(GisService gisService, DaoService daoService, GeoserverLayerWsCommand command) {
        this.gisService = checkNotNull(gisService);
        this.daoService = checkNotNull(daoService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all geoserver layers for a given class and card",
            description = "Get all geoserver layers for a given class and card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "Type of the element: cards (for classes) or instances (for processes)", required = true, schema = @Schema(type = "string", allowableValues = {"cards", "instances"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geoserver layers"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_VIEW_AUTHORITY)
    public Object getMany(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(required = true, description = "Id of the card to query") String cardId,
            @QueryParam(FILTER) @Parameter(description = "Filter geoserver layers by name") String filterStr
    ) {
        List<GeoserverLayer> listGeoserverLayer = command.doGetMany(classId, cardId);
        List<FluentMap<String, Object>> listSerializedGeoserverLayer = list(listGeoserverLayer).map(m -> serializeLayer(m, gisService, daoService));
        listSerializedGeoserverLayer = filterSerializations(listSerializedGeoserverLayer, filterStr);
        return response(listSerializedGeoserverLayer);
    }
}
