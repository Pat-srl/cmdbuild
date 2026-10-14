/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

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
import org.cmdbuild.service.rest.v4.endpoint.GeoserverLayerWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.GeoserverLayerWs_Management;
import org.cmdbuild.service.rest.v4.model.WsLayerData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/{typeName:cards|instances}/{" + CARD_ID + "}/geolayers/")
@Tag(name = "Geoserver Layers", description = "Operations related to geoserver layers")
@Produces(APPLICATION_JSON)
public class GeoserverLayerWs {

    private final GeoserverLayerWs_Administration geoserverLayerWs_adm;
    private final GeoserverLayerWs_Management geoserverLayerWs_mng;

    public GeoserverLayerWs(GeoserverLayerWs_Administration geoserverLayerWs_adm, GeoserverLayerWs_Management geoserverLayerWs_mng) {
        this.geoserverLayerWs_adm = checkNotNull(geoserverLayerWs_adm);
        this.geoserverLayerWs_mng = checkNotNull(geoserverLayerWs_mng);
    }

    @GET
    @Path("{attrName}/")
    @Operation(
            summary = "Get geoserver layer for a specific attribute",
            description = "Obtain the geoserver layer associated to a specific attribute of a card. The actual details included in the response depend on the view mode specified in the request header: if the view mode is admin, the response includes all details of the geoserver layer; otherwise, only basic details are included (i.e., the URL of the geoserver layer is not included",
            parameters = {
                    @Parameter( name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"}) ),
                    @Parameter( name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}) ),
                    @Parameter( name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process" ),
                    @Parameter( name = CARD_ID, in = ParameterIn.PATH, description = "Identifier of the card" ),
                    @Parameter( name = "attrName", in = ParameterIn.PATH, description = "Name of the attribute" )
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "404", description = "Geoserver layer not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getOneForCard(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam("attrName") String attrName
    ) {
        return geoserverLayerWs_mng.getOneForCard(classId, cardId, attrName);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get geoserver layers for a card",
            description = "Obtain the list of geoserver layers associated to a specific card. The actual details included in the response depend on the view mode specified in the request header: if the view mode is admin, the response includes all details of each geoserver layer; otherwise, only basic details are included (i.e., the URL of each geoserver layer is not included).",
            parameters = {
                    @Parameter( name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"}) ),
                    @Parameter( name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}) ),
                    @Parameter( name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode of the request", schema = @Schema(allowableValues = {"admin", "manager"})),
                    @Parameter( name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process" ),
                    @Parameter( name = CARD_ID, in = ParameterIn.PATH, description = "Identifier of the card" ),
                    @Parameter( name = FILTER, in = ParameterIn.QUERY, description = "Optional filter to apply to the list of geoserver layers. The actual semantics of the filter depend on the implementation of the service, but it can be used, for example, to specify conditions on the attributes of the geoserver layers to include in the response (e.g., only geoserver layers associated to a specific attribute)" ),
                    @Parameter( name = "visible", in = ParameterIn.QUERY, description = "Whether to include only visible geoserver layers in the response (i.e., those that should be included in the card view) or all geoserver layers (including those that should not be included in the card view)" )
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "404", description = "Card not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getMany(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) String cardId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam("visible") @DefaultValue(FALSE) Boolean isVisible
    ) {
        if (isAdminViewMode(viewMode)) {
            return geoserverLayerWs_adm.getMany(classId, cardId, filterStr);
        }
        return geoserverLayerWs_mng.getMany(classId, cardId, filterStr);
    }

    @PUT
    @Path("{attrName}/")
    @Operation(
            summary = "Create or update geoserver layer for a specific attribute",
            description = "Create or update the geoserver layer associated to a specific attribute of a card. If a geoserver layer for the specified attribute does not exist, a new geoserver layer is created; otherwise, the existing geoserver layer is updated. The actual details included in the response depend on the view mode specified in the request header: if the view mode is admin, the response includes all details of the geoserver layer; otherwise, only basic details are included (i.e., the URL of the geoserver layer is not included" ,
            parameters = {
                    @Parameter( name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"}) ),
                    @Parameter( name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}) ),
                    @Parameter( name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process" ),
                    @Parameter( name = CARD_ID, in = ParameterIn.PATH, description = "Identifier of the card" ),
                    @Parameter( name = "attrName", in = ParameterIn.PATH, description = "Name of the attribute" )
            },
            requestBody = @RequestBody(
                    description = "Data for creating or updating the geoserver layer. The actual content of the request body depends on the implementation of the service, but it can include, for example, the configuration of the geoserver layer (e.g., the URL of the geoserver layer, the style to apply to the geoserver layer, etc.) and/or the file containing the data to publish in the geoserver layer",
                    content = {@Content(mediaType = "multipart/form-data", schema = @Schema(implementation = WsLayerData.class))}
            ),
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Geoserver layer not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam("attrName") String attrName,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            @Nullable WsLayerData data
    ) {
        return geoserverLayerWs_mng.update(classId, cardId, attrName, dataHandler, data);
    }

    @DELETE
    @Path("{attrName}/")
    @Operation(
            summary = "Delete geoserver layer for a specific attribute",
            description = "Delete the geoserver layer associated to a specific attribute of a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "Identifier of the card"),
                    @Parameter(name = "attrName", in = ParameterIn.PATH, description = "Name of the attribute")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Geoserver layer not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(CLASS_ID) String classId,
            @PathParam(CARD_ID) Long cardId,
            @PathParam("attrName") String attrName
    ) {
        return geoserverLayerWs_mng.delete(classId, cardId, attrName);
    }
}
