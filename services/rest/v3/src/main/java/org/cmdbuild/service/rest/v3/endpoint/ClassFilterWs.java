package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.ClassFilterWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ClassFilterWs_Management;
import org.cmdbuild.service.rest.v4.model.WsDefaultStoredFilter;
import org.cmdbuild.service.rest.v4.model.WsFilterData;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("{type:classes|processes|views}/{" + CLASS_ID + "}/filters/")
@Tag(name = "Class Filters", description = "Operations related to filters of a specific class")
@Produces(APPLICATION_JSON)
public class ClassFilterWs {

    private final ClassFilterWs_Administration classFilterWs_adm;
    private final ClassFilterWs_Management classFilterWs_mng;

    public ClassFilterWs(ClassFilterWs_Administration classFilterWs_adm, ClassFilterWs_Management classFilterWs_mng) {
        this.classFilterWs_adm = checkNotNull(classFilterWs_adm);
        this.classFilterWs_mng = checkNotNull(classFilterWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get filters for a class",
            description = "Obtain the list of filters defined for a specific class. If admin view mode is enabled, also " +
                    "include filters shared by other users (if any).",
            parameters =  {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes, classes or views", schema = @Schema(allowableValues = {"processes", "classes", "views"})),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode (if admin view mode enabled, also include filters shared by other users)", schema = @Schema(allowableValues = {"admin", "default"})),
                    @Parameter(name = "limit", in = ParameterIn.QUERY, description = "Maximum number of filters to return"),
                    @Parameter(name = "start", in = ParameterIn.QUERY, description = "Index of the first filter to return (for pagination)"),
                    @Parameter(name = "shared", in = ParameterIn.QUERY, description = "If true, return only filters shared by other users (if admin view mode enabled)", schema = @Schema(allowableValues = {"true", "false"}))
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam("shared") @DefaultValue(FALSE) Boolean sharedOnly
    ) {
        if (isAdminViewMode(viewMode)) {
            return classFilterWs_adm.readAll(classId, limit, offset, sharedOnly);
        }
        return classFilterWs_mng.readAll(classId, limit, offset, sharedOnly);
    }

    @GET
    @Path("{" + FILTER_ID + "}/")
    @Operation(
            summary = "Get a specific filter for a class",
            description = "Obtain the details of a specific filter defined for a class. If admin view mode is enabled, " +
                    "also allow to retrieve filters shared by other users (if any).",
            parameters =  {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes, classes or views", schema = @Schema(allowableValues = {"processes", "classes", "views"})),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode (if admin view mode enabled, also allow to retrieve filters shared by other users)", schema = @Schema(allowableValues = {"admin", "default"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = FILTER_ID, in = ParameterIn.PATH, description = "Identifier of the filter to retrieve")
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Filter not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(CLASS_ID) String classId,
            @PathParam(FILTER_ID) Long filterId
    ) {
        return classFilterWs_mng.read(classId, filterId);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new filter for a class",
            description = "Create a new filter for a class. The filter will be created with the current user as owner. " +
                    "If admin view mode is enabled, also allow to create filters shared by other users.",
            parameters =  {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes, classes or views", schema = @Schema(allowableValues = {"processes", "classes", "views"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
            },
            requestBody = @RequestBody(description = "Element"),
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(CLASS_ID) String classId,
            WsFilterData element
    ) {
        return classFilterWs_mng.create(classId, element);
    }

    @PUT
    @Path("{" + FILTER_ID + "}/")
    @Operation(
            summary = "Update an existing filter for a class",
            description = "Update an existing filter for a class. The filter must be owned by the current user. If " +
                    "admin view mode is enabled, also allow to update filters shared by other users.",
            parameters =  {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes, classes or views", schema = @Schema(allowableValues = {"processes", "classes", "views"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = FILTER_ID, in = ParameterIn.PATH, description = "Identifier of the filter to update")
            },
            requestBody = @RequestBody(description = "Element"),
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Filter not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(CLASS_ID) String classId,
            @PathParam(FILTER_ID) Long filterId,
            WsFilterData element
    ) {
        return classFilterWs_mng.update(classId, filterId, element);
    }

    @DELETE
    @Path("{" + FILTER_ID + "}/")
    @Operation(
            summary = "Delete an existing filter for a class",
            description = "Delete an existing filter for a class. The filter must be owned by the current user. If " +
                    "admin view mode is enabled, also allow to delete filters shared by other users.",
            parameters =  {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes, classes or views", schema = @Schema(allowableValues = {"processes", "classes", "views"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = FILTER_ID, in = ParameterIn.PATH, description = "Identifier of the filter to delete")
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Filter not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(CLASS_ID) String classId,
            @PathParam(FILTER_ID) Long filterId
    ) {
        return classFilterWs_mng.delete(classId, filterId);
    }

    @GET
    @Path("{filterId}/defaultFor/")
    @Operation(
            summary = "Get default filter for roles",
            description = "Obtain the default filter for a specific filter and the roles for which it is set as default. " +
                    "If admin view mode is enabled, also allow to retrieve default filter for roles for filters shared " +
                    "by other users (if any).",
            parameters =  {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes, classes or views", schema = @Schema(allowableValues = {"processes", "classes", "views"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = FILTER_ID, in = ParameterIn.PATH, description = "Identifier of the filter to retrieve default filter for roles")
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Filter not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getDefaultForRoles(
            @PathParam(CLASS_ID) String classId,
            @PathParam(FILTER_ID) Long filterId
    ) {
        return classFilterWs_mng.getDefaultForRoles(filterId);
    }

    @POST
    @Path("{filterId}/defaultFor/")
    @Operation(
            summary = "Set default filter for roles",
            description = "Set the default filter for a specific filter and a list of roles. The filter must be owned " +
                    "by the current user. If admin view mode is enabled, also allow to set default filter for roles for " +
                    "filters shared by other users.",
            parameters =  {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes, classes or views", schema = @Schema(allowableValues = {"processes", "classes", "views"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = FILTER_ID, in = ParameterIn.PATH, description = "Identifier of the filter to set default for roles")
            },
            requestBody = @RequestBody(description = "List of roles"),
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Filter not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object updateWithPost(
            @PathParam(CLASS_ID) String classId,
            @PathParam("filterId") Long filterId,
            List<WsDefaultStoredFilter> roles
    ) {
        return classFilterWs_mng.updateWithPost(filterId, roles);
    }
}
