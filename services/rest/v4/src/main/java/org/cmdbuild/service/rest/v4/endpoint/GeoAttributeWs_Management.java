/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.service.rest.v4.command.GeoAttributeWsCommand;
import org.cmdbuild.translation.TranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.serializationhelpers.GisAttributeSerializationHelper.serializeGisAttribute;

/**
 * @author ldare
 */
@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/geoattributes/")
@Tag(name = "Geo Attributes")
@Produces(APPLICATION_JSON)
@Component
public class GeoAttributeWs_Management {

    private final GisService gisService;
    private final EasyuploadService easyuploadService;
    private final UserClassService userClassService;
    private final TranslationService translationService;
    private final GeoAttributeWsCommand command;

    public GeoAttributeWs_Management(GisService gisService, EasyuploadService easyuploadService, UserClassService userClassService, TranslationService translationService, GeoAttributeWsCommand command) {
        this.gisService = checkNotNull(gisService);
        this.easyuploadService = checkNotNull(easyuploadService);
        this.userClassService = checkNotNull(userClassService);
        this.translationService = checkNotNull(translationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all geo attributes for the chosen class",
            description = "Obtain a list of all geo attributes for the specified class",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"})),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geo attributes"),
                    @ApiResponse(responseCode = "404", description = "The class was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllAttributes(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @QueryParam(LIMIT) @Parameter(description = "Number of attributes to return", schema = @Schema(ref = "DefaultLimitExample")) Integer offset,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer limit,
            @QueryParam(DETAILED) @Parameter(description = "Include or not full details in the response") @DefaultValue(FALSE) Boolean detailed,
            @QueryParam("visible") @Parameter(description = "Include or not only visible attributes") @DefaultValue(FALSE) Boolean visible
    ) {
        PagedElements<GisAttribute> elements = command.doReadAllAttributes(classId, visible, limit, offset);
        List<Object> filteredAttributes = elements.stream()
                .filter((a) -> userClassService.isActiveAndUserCanRead(a.getOwnerClassName()) && a.isActive() && userClassService.getUserClass(a.getOwnerClassName()).hasGisAttributeReadPermission(a.getLayerName()))
                .map(m -> serializeGisAttribute(
                        m,
                        translationService,
                        easyuploadService,
                        userClassService,
                        gisService
                )).collect(toList());
        return response(filteredAttributes, filteredAttributes.size());
    }

    @GET
    @Path("{" + ATTRIBUTE + "}/")
    @Operation(
            summary = "Get a geo attribute for the chosen class",
            description = "Obtain a specific geo attribute for the specified class",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"})),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geo attribute"),
                    @ApiResponse(responseCode = "404", description = "The class or attribute was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAttribute(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @PathParam(ATTRIBUTE) @Parameter(required = true, description = "ID of the attribute to query") String attributeId
    ) {
        GisAttribute layer = command.doReadAttribute(classId, attributeId);
        return response(serializeGisAttribute(layer, translationService, easyuploadService, userClassService, gisService));
    }
}
