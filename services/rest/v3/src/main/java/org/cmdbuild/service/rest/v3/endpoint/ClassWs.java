package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import org.cmdbuild.service.rest.v4.endpoint.ClassWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ClassWs_Management;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("classes/")
@Tag(name = "Classes", description = "Operations related to classes")
@Produces(APPLICATION_JSON)
public class ClassWs {

    private final ClassWs_Administration classWs_adm;
    private final ClassWs_Management classWs_mng;

    public ClassWs(ClassWs_Administration classWs_adm, ClassWs_Management classWs_mng) {
        this.classWs_adm = checkNotNull(classWs_adm);
        this.classWs_mng = checkNotNull(classWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all classes",
            description = "Get all classes. If the user has admin view permissions, all classes will be returned. Otherwise, only classes for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about classes, such as attributes and relations"),
                    @Parameter(name = "includeLookupValues", in = ParameterIn.QUERY, description = "Whether to include lookup values for attributes of type 'lookup' or 'multivalue_lookup' in the response"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results"),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the list of classes, in the format 'attribute:value'. Supported attributes for filtering are 'id' and 'name'")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam("includeLookupValues") @DefaultValue(FALSE) Boolean includeLookupValues,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(FILTER) String filterStr
    ) {
        if (isAdminViewMode(viewMode)) {
            return classWs_adm.readAll(detailed, includeLookupValues, limit, offset, filterStr);
        }
        return classWs_mng.readAll(detailed, includeLookupValues, limit, offset, filterStr);
    }

    @GET
    @Path("{"+ CLASS_ID + "}/")
    @Operation(
            summary = "Get class by id",
            description = "Get class by id. If the user has admin view permissions, the class will be returned with all details. Otherwise, only details for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "includeLookupValues", in = ParameterIn.QUERY, description = "Whether to include lookup values for attributes of type 'lookup' or 'multivalue_lookup' in the response")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Class not found")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("classId") String classId,
            @QueryParam("includeLookupValues") @DefaultValue(FALSE) Boolean includeLookupValues
    ) {
        if (isAdminViewMode(viewMode)) {
            return classWs_adm.read(classId, includeLookupValues);
        }
        return classWs_mng.read(classId, includeLookupValues);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new class",
            description = "Create a new class with the provided data",
            requestBody = @RequestBody(description = "Data for the new class", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsClassData data
    ) {
        return classWs_adm.create(data);
    }

    @PUT
    @Path("{"+ CLASS_ID + "}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an existing class",
            description = "Update an existing class with the provided data",
            parameters = {
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the class to update")
            },
            requestBody = @RequestBody(description = "Updated data for the class", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("classId") String classId,
            WsClassData data
    ) {
        return classWs_adm.update(classId, data);
    }

    @DELETE
    @Path("{"+ CLASS_ID + "}/")
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a class",
            description = "Delete a class by its ID",
            parameters = {
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the class to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("classId") String classId
    ) {
        return classWs_adm.delete(classId);
    }
}
