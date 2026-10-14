/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.gis.GeoserverLayer;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.service.rest.v4.command.GeoserverLayerWsCommand;
import org.cmdbuild.service.rest.v4.model.WsLayerData;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.serializationhelpers.GeoserverLayerSerializationHelper.serializeLayer;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.command.WsUtils.filterSerializations;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author schursin
 */
@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/{" + TYPE_NAME + ":cards|instances}/{" + CARD_ID + "}/geolayers/")
@Tag(name = "Geoserver Layers")
@Produces(APPLICATION_JSON)
@Component
public class GeoserverLayerWs_Management {

    private final GisService gisService;
    private final DaoService daoService;
    private final GeoserverLayerWsCommand command;

    public GeoserverLayerWs_Management(GisService gisService, DaoService daoService, GeoserverLayerWsCommand command) {
        this.gisService = checkNotNull(gisService);
        this.daoService = checkNotNull(daoService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{attrName}/")
    @Operation(
            summary = "Get a geoserver layer for a given class and card",
            description = "Get a geoserver layer for a given class and card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "Type of the element: cards (for classes) or instances (for processes)", required = true, schema = @Schema(type = "string", allowableValues = {"cards", "instances"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geoserver layer"),
                    @ApiResponse(responseCode = "404", description = "Geoserver layer not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getOneForCard(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(required = true, description = "Id of the card to query") Long cardId,
            @PathParam("attrName") @Parameter(required = true, description = "Name of the attribute to query") String layerCodeOrId
    ) {
        GeoserverLayer layer = command.doGetOneForCard(classId, cardId, layerCodeOrId);
        return response(serializeLayer(layer, gisService, daoService));
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
    public Object getMany(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam(CARD_ID) @Parameter(required = true, description = "Id of the card to query") String cardId,
            @QueryParam(FILTER) @Parameter(description = "Filter geoserver layers by name") String filterStr
    ) {
        List<GeoserverLayer> listGeoserverLayer = command.doGetMany(classId, cardId);
        listGeoserverLayer = command.filterForManagement(listGeoserverLayer);

        List<FluentMap<String, Object>> listSerializedGeoserverLayer = list(listGeoserverLayer).map(m -> serializeLayer(m, gisService, daoService));

        listSerializedGeoserverLayer = filterSerializations(listSerializedGeoserverLayer, filterStr);
        return response(listSerializedGeoserverLayer);
    }

    @PUT
    @Path("{attrName}/")
    @Operation(
            requestBody = @RequestBody(
                    description = "Optional layer file and layer data",
                    required = false,
                    content = @Content(
                            mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA,
                            schemaProperties = {
                                @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary")),
                                @SchemaProperty(name = "data", schema = @Schema(implementation = WsLayerData.class))
                    })),
            summary = "Update a geoserver layer for a given class and card",
            description = "Update a geoserver layer for a given class and card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "Type of the element: cards (for classes) or instances (for processes)", required = true, schema = @Schema(type = "string", allowableValues = {"cards", "instances"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of geoserver layer"),
                    @ApiResponse(responseCode = "404", description = "Geoserver layer not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to update") String classId,
            @PathParam(CARD_ID) @Parameter(required = true, description = "Id of the card to query") Long cardId,
            @PathParam("attrName") @Parameter(required = true, description = "Name of the attribute to query") String attrName,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @Nullable @RequestBody(description = "Layer data", required = false) WsLayerData data
    ) {
        GeoserverLayer layer = command.doUpdate(classId, cardId, attrName, dataHandler, data);
        return response(serializeLayer(layer, gisService, daoService));
    }

    @DELETE
    @Path("{attrName}/")
    @Operation(
            summary = "Delete a geoserver layer for a given class and card",
            description = "Delete a geoserver layer for a given class and card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "Type of the element: cards (for classes) or instances (for processes)", required = true, schema = @Schema(type = "string", allowableValues = {"cards", "instances"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of geoserver layer"),
                    @ApiResponse(responseCode = "404", description = "Geoserver layer not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to delete") String classId,
            @PathParam(CARD_ID) @Parameter(required = true, description = "Id of the card to query") Long cardId,
            @PathParam("attrName") @Parameter(required = true, description = "Name of the attribute to query") String attrName
    ) {
        command.doDelete(classId, cardId, attrName);
        return success();
    }
}
