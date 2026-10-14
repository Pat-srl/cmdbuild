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
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.GeoAttributeWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.GeoAttributeWs_Management;
import org.cmdbuild.service.rest.v4.model.WsGeoAttributeData;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_GIS_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/geoattributes/")
@Tag(name = "Geo Attributes", description = "Operations related to geo attributes")
@Produces(APPLICATION_JSON)
public class GeoAttributeWs {

    private final GeoAttributeWs_Administration geoAttributeWs_adm;
    private final GeoAttributeWs_Management geoAttributeWs_mng;

    public GeoAttributeWs(GeoAttributeWs_Administration geoAttributeWs_adm, GeoAttributeWs_Management geoAttributeWs_mng) {
        this.geoAttributeWs_adm = checkNotNull(geoAttributeWs_adm);
        this.geoAttributeWs_mng = checkNotNull(geoAttributeWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get geo attributes for a class or process",
            description = "Obtain the list of geo attributes associated to a specific class or process. The actual details included in the response depend on the view mode specified in the request header: if the view mode is admin, the response includes all details of each geo attribute; otherwise, only basic details are included (i.e., the list of valid values is not included, and only the count of valid values is provided).",
            parameters = {
                    @Parameter( name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"}) ),
                    @Parameter( name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode of the request", schema = @Schema(allowableValues = {"admin", "manager"})),
                    @Parameter( name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process" ),
                    @Parameter( name = START, in = ParameterIn.QUERY, description = "Offset of the first geo attribute to return (for pagination)" ),
                    @Parameter( name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of geo attributes to return (for pagination)" ),
                    @Parameter( name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information for each geo attribute" ),
                    @Parameter( name = "visible", in = ParameterIn.QUERY, description = "Whether to include only visible geo attributes in the response (i.e., those that should be included in the card view) or all geo attributes (including those that should not be included in the card view)")
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Class or process not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAllAttributes(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            @QueryParam(START) Integer offset,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam("visible") @DefaultValue(FALSE) Boolean visible) {
        if (isAdminViewMode(viewMode)) {
            return geoAttributeWs_adm.readAllAttributes(classId, offset, limit, detailed, visible);
        }
        return geoAttributeWs_mng.readAllAttributes(classId, offset, limit, detailed, visible);
    }

    @POST
    @Path("order")
    @Operation(
            summary = "Reorder geo attributes for a class or process",
            description = "Reorder geo attributes for a class or process. The request body should contain the list of geo attribute IDs in the desired order.",
            parameters = {
                    @Parameter( name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"}) ),
                    @Parameter( name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process" )
            },
            requestBody = @RequestBody(description = "List of geo attribute IDs in the desired order", required = true, content = @Content( mediaType = APPLICATION_JSON, schema = @Schema(implementation = List.class))),
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Class or process not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object reorder(
            @PathParam(CLASS_ID) String classId,
            List<Long> attrOrder
    ) {
        return geoAttributeWs_adm.reorder(classId, attrOrder);
    }

    @GET
    @Path("{" + ATTRIBUTE + "}/")
    @Operation(
            summary = "Get details of a specific geo attribute",
            description = "Obtain detailed information about a specific geo attribute associated to a class or process.",
            parameters = {
                    @Parameter( name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"}) ),
                    @Parameter( name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process" ),
                    @Parameter( name = ATTRIBUTE, in = ParameterIn.PATH, description = "Identifier of the geo attribute" )
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Class, process, or geo attribute not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAttribute(
            @PathParam(CLASS_ID) String classId,
            @PathParam(ATTRIBUTE) String attributeId
    ) {
        return geoAttributeWs_mng.readAttribute(classId, attributeId);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new geo attribute for a class or process",
            description = "Create a new geo attribute for a class or process. The request body should contain the details of the geo attribute to be created.",
            parameters = {
                    @Parameter( name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"}) ),
                    @Parameter( name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process" )
            },
            requestBody = @RequestBody(description = "Data for creating the geo attribute", required = true, content = @Content( mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsGeoAttributeData.class))),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Geo attribute created successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Class or process not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}

    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object create(
            @PathParam(CLASS_ID) String classId,
            WsGeoAttributeData attributeData
    ) {
        return geoAttributeWs_adm.create(classId, attributeData);
    }

    @POST
    @Path("visibility")
    @Operation(
            summary = "Update visibility of geo attributes for a class or process",
            description = "Update visibility of geo attributes for a class or process. The request body should contain a map where the keys are geo attribute IDs and the values are booleans indicating the desired visibility for each geo attribute.",
            parameters = {
                    @Parameter( name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"}) ),
                    @Parameter( name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process" )
            },
            requestBody = @RequestBody(description = "Data for updating the visibility of geo attributes", required = true, content = @Content( mediaType = APPLICATION_JSON, schema = @Schema(implementation = Map.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Geo attributes visibility updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Class or process not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object updateVisibility(
            @PathParam(CLASS_ID) String classId,
            Map<Long, Boolean> geoAttributes
    ) {
        return geoAttributeWs_adm.updateVisibility(classId, geoAttributes);
    }

    @PUT
    @Path("{" + ATTRIBUTE + "}/")
    @Operation(
            summary = "Update details of a specific geo attribute",
            description = "Update details of a specific geo attribute associated to a class or process. The request body should contain the updated details of the geo attribute.",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process"),
                    @Parameter(name = ATTRIBUTE, in = ParameterIn.PATH, description = "Identifier of the geo attribute")
            },
            requestBody = @RequestBody(description = "Data for updating the geo attribute", required = true, content = @Content( mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsGeoAttributeData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Geo attribute updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Class, process, or geo attribute not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(CLASS_ID) String classId,
            @PathParam(ATTRIBUTE) String attributeId,
            WsGeoAttributeData attributeData
    ) {
        return  geoAttributeWs_adm.update(classId, attributeId, attributeData);
    }

    @DELETE
    @Path("{" + ATTRIBUTE + "}/")
    @Operation(
            summary = "Delete a specific geo attribute",
            description = "Delete a specific geo attribute associated to a class or process.",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Identifier of the class or process"),
                    @Parameter(name = ATTRIBUTE, in = ParameterIn.PATH, description = "Identifier of the geo attribute")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Geo attribute deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Class, process, or geo attribute not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_GIS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(CLASS_ID) String classId,
            @PathParam(ATTRIBUTE) String attributeId
    ) {
        return geoAttributeWs_adm.delete(classId, attributeId);
    }
}
