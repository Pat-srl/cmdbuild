/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.easyupload.EasyuploadService;
import org.cmdbuild.gis.GisAttribute;
import org.cmdbuild.gis.GisService;
import org.cmdbuild.service.rest.v4.command.GeoAttributeWsCommand;
import org.cmdbuild.service.rest.v4.model.WsGeoAttributeData;
import org.cmdbuild.translation.TranslationService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_GIS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_GIS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.serializationhelpers.GisAttributeSerializationHelper.serializeGisAttribute;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * @author ldare
 */
@Path("administration/{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/geoattributes/")
@Tags({
        @Tag(name = "Geo Attributes", description = "APIs to manage geo attributes."),
        @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class GeoAttributeWs_Administration {

    private final GisService gisService;
    private final EasyuploadService easyuploadService;
    private final UserClassService userClassService;
    private final TranslationService translationService;
    private final GeoAttributeWsCommand command;

    public GeoAttributeWs_Administration(GisService gisService, EasyuploadService easyuploadService, UserClassService userClassService, TranslationService translationService, GeoAttributeWsCommand command) {
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
            description = "Obtain a list of every available geo attribute for the specified class",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of geo attributes for the specified class"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_VIEW_AUTHORITY)
    public Object readAllAttributes(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @QueryParam(LIMIT) @Parameter(description = "Number of attributes to return", schema = @Schema(ref = "DefaultLimitExample")) Integer offset,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer limit,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response") Boolean detailed,
            @QueryParam("visible") @DefaultValue(FALSE) @Parameter(description = "Include or not only visible attributes") Boolean visible
    ) {
        PagedElements<GisAttribute> elements = command.doReadAllAttributes(classId, visible, limit, offset);
        return response(elements.stream().map(m -> serializeGisAttribute(
                m,
                translationService,
                easyuploadService,
                userClassService,
                gisService
        )).collect(toList()), elements.totalSize());
    }

    @GET
    @Path("{" + ATTRIBUTE + "}/")
    @Operation(
            summary = "Get a specific geo attribute",
            description = "Obtain details of a specific geo attribute",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geo attribute"),
                    @ApiResponse(responseCode = "404", description = "The geo attribute was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_VIEW_AUTHORITY)
    public Object readAttribute(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query", required = true) String classId,
            @PathParam(ATTRIBUTE) @Parameter(required = true, description = "ID of the attribute to query") String attributeId
    ) {
        GisAttribute layer = command.doReadAttribute(classId, attributeId);
        return response(serializeGisAttribute(layer, translationService, easyuploadService, userClassService, gisService));
    }

    @POST
    @Path("order")
    @Operation(
            summary = "Reorder geo attributes",
            description = "Reorder geo attributes",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful reorder of geo attributes"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object reorder(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query", required = true) String classId,
            @RequestBody(description = "Ids of the geo attributes, in the desired order", required = true) List<Long> attrOrder
    ) {
        List<GisAttribute> attrs = command.doReorder(classId, attrOrder);
        return response(attrs.stream().map(m -> serializeGisAttribute(
                m,
                translationService,
                easyuploadService,
                userClassService,
                gisService
        )));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new geo attribute",
            description = "Create a new geo attribute",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of geo attribute"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object create(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @RequestBody(description = "Geo Attribute data", required = true) WsGeoAttributeData data
    ) {
        GisAttribute layer = command.doCreate(classId, data);
        return response(serializeGisAttribute(layer, translationService, easyuploadService, userClassService, gisService));
    }

    @POST
    @Path("visibility")
    @Operation(
            summary = "Update geo attributes visibility for a class",
            description = "Update geo attributes visibility for a class",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of geo attributes visibility"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object updateVisibility(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query", required = true) String classId,
            @RequestBody(description = "Map of geo attribute id to visibility flag (true = visible)", required = true) Map<Long, Boolean> geoAttributes
    ) {
        List<GisAttribute> gisAttributeList = command.doUpdateVisibility(classId, geoAttributes);
        return response(list(gisAttributeList).map(m -> serializeGisAttribute(
                m,
                translationService,
                easyuploadService,
                userClassService,
                gisService
        )));
    }

    @PUT
    @Path("{" + ATTRIBUTE + "}/")
    @Operation(
            summary = "Update an existing geo attribute",
            description = "Update an existing geo attribute",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of geo attribute"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @PathParam(ATTRIBUTE) @Parameter(required = true, description = "ID of the attribute to query") String attributeId,
            @RequestBody(description = "Geo Attribute data", required = true) WsGeoAttributeData data
    ) {
        GisAttribute layer = command.doUpdate(classId, attributeId, data);
        return response(serializeGisAttribute(layer, translationService, easyuploadService, userClassService, gisService));
    }

    @DELETE
    @Path("{" + ATTRIBUTE + "}/")
    @Operation(
            summary = "Delete a geo attribute",
            description = "Delete a geo attribute",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of geo attribute"),
                    @ApiResponse(responseCode = "404", description = "The geo attribute was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @PathParam(ATTRIBUTE) @Parameter(required = true, description = "ID of the attribute to query") String attributeId
    ) {
        command.doDelete(classId, attributeId);
        return success();
    }
}
