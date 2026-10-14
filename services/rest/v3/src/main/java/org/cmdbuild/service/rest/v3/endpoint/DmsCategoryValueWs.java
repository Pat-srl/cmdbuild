package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.cmdbuild.service.rest.v4.endpoint.DmsCategoryValueWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.DmsCategoryValueWs_Management;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;


@Path("dms/categories/{" + LOOKUP_TYPE_ID + "}/values/")
@Produces(APPLICATION_JSON)
@Tag(name = "DMS Category Values", description = "Operations related to DMS Category Values")
public class DmsCategoryValueWs {

    private final DmsCategoryValueWs_Administration dmsCategoryValueWs_adm;
    private final DmsCategoryValueWs_Management dmsCategoryValueWs_mng;

    public DmsCategoryValueWs(DmsCategoryValueWs_Administration dmsCategoryValueWs_adm, DmsCategoryValueWs_Management dmsCategoryValueWs_mng) {
        this.dmsCategoryValueWs_adm = checkNotNull(dmsCategoryValueWs_adm);
        this.dmsCategoryValueWs_mng = checkNotNull(dmsCategoryValueWs_mng);
    }

    @GET
    @Path("{" + LOOKUP_VALUE_ID + "}/")
    @Operation(
            summary = "Get a DMS category value by id",
            description = "Get a DMS category value by id",
            parameters = {
                    @Parameter(name = LOOKUP_VALUE_ID, in = ParameterIn.PATH, description = "Id of the DMS category value (lookup value)")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "DMS category value with the specified id does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(LOOKUP_VALUE_ID) Long lookupValueId
    ) {
        return dmsCategoryValueWs_mng.read(lookupValueId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all values for a DMS category",
            description = "Get all values for a DMS category. If the user has admin view permissions, all values will be returned. Otherwise, only values for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Id of the DMS category (lookup type)"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results"),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the results, in the format 'attributeId|operator|value', where operator can be 'eq' (equals), 'ne' (not equals), 'like' (contains), 'nlike' (does not contain), 'gt' (greater than), 'lt' (less than), 'ge' (greater or equal than), 'le' (less or equal than)"),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view DMS category values"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(FILTER) String filterStr,
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode
    ) {
        if (isAdminViewMode(viewMode)) {
            return dmsCategoryValueWs_adm.readAll(lookupTypeId, limit, offset, filterStr);
        }
        return dmsCategoryValueWs_mng.readAll(lookupTypeId, limit, offset, filterStr);
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new DMS category value",
            description = "Create a new DMS category value for the specified DMS category (lookup type). The request body should contain the details of the DMS category value to create, in the form of a JSON object with the following structure: {\"code\": \"value_code\", \"description\": \"value_description\", \"properties\": {\"property1\": \"value1\", \"property2\": \"value2\"}}. The 'code' field is mandatory and must be unique among the values of the same DMS category (lookup type). The 'description' field is optional. The 'properties' field is optional and can contain any additional properties to associate to the DMS category value",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Id of the DMS category (lookup type)", required = true)
            },
            requestBody = @RequestBody(description = "Data for the new DMS category value", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid or the DMS category value code is not unique"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create DMS category values"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            WsLookupValue wsLookupValue
    ) {
        return dmsCategoryValueWs_adm.create(lookupTypeId, wsLookupValue);
    }

    @PUT
    @Path("{lookupValueId}")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update a DMS category value",
            description = "Update a DMS category value for the specified DMS category (lookup type). The request body should contain the details of the DMS category value to update, in the form of a JSON object with the following structure: {\"code\": \"value_code\", \"description\": \"value_description\", \"properties\": {\"property1\": \"value1\", \"property2\": \"value2\"}}. The 'code' field is mandatory and must be unique among the values of the same DMS category (lookup type). The 'description' field is optional. The 'properties' field is optional and can contain any additional properties to associate to the DMS category value",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Id of the DMS category (lookup type)", required = true),
                    @Parameter(name = "lookupValueId", in = ParameterIn.PATH, description = "Id of the DMS category value to update", required = true)
            },
            requestBody = @RequestBody(description = "Data for the DMS category value to update", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid or the DMS category value code is not unique"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update DMS category values or the DMS category value does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            @PathParam("lookupValueId") Long lookupId,
            WsLookupValue wsLookupValue
    ) {
        return dmsCategoryValueWs_adm.update(lookupTypeId, lookupId, wsLookupValue);
    }

    @DELETE
    @Path("{lookupValueId}")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a DMS category value",
            description = "Delete a DMS category value",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Id of the DMS category (lookup type)", required = true),
                    @Parameter(name = "lookupValueId", in = ParameterIn.PATH, description = "Id of the DMS category value to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete DMS category values or the DMS category value does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            @PathParam("lookupValueId") Long lookupId
    ) {
        return dmsCategoryValueWs_adm.delete(lookupTypeId, lookupId);
    }

    @POST
    @Path("order")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Reorder DMS category values",
            description = "Reorder DMS category values for a specific DMS category (lookup type). The request body should contain a JSON array with the ids of the DMS category values in the desired order",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Id of the DMS category (lookup type)", required = true),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode header to determine the context of the operation (admin or management)", schema = @Schema(allowableValues = {"admin", "management"}))
            },
            requestBody = @RequestBody(description = "List of DMS category value ids in the desired order", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid or the list of DMS category value ids does not contain all existing values for the specified DMS category (lookup type)"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to reorder DMS category values or the specified DMS category (lookup type) does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object reorder(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            List<Long> lookupValueIds,
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode
    ) {
        if (isAdminViewMode(viewMode)) {
            return dmsCategoryValueWs_adm.reorder(lookupTypeId, lookupValueIds);
        }
        throw runtime("Operation blocked. You don't have the permissions");
    }
}
