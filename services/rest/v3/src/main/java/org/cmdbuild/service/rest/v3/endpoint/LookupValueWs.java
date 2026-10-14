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
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.cmdbuild.service.rest.v4.endpoint.LookupValueWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.LookupValueWs_Management;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOOKUPS_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;


@Path("lookup_types/{" + LOOKUP_TYPE_ID + "}/values/")
@Produces(APPLICATION_JSON)
@Tag(name = "Lookup Values", description = "Operations related to lookup values")
public class LookupValueWs {

    private final LookupValueWs_Administration lookupValueWs_adm;
    private final LookupValueWs_Management lookupValueWs_mng;

    public LookupValueWs(LookupValueWs_Administration lookupValueWs_adm, LookupValueWs_Management lookupValueWs_mng) {
        this.lookupValueWs_adm = lookupValueWs_adm;
        this.lookupValueWs_mng = lookupValueWs_mng;
    }

    @GET
    @Path("{" + LOOKUP_VALUE_ID + "}/")
    @Operation(
            summary = "Get lookup value by id",
            description = "Get lookup value by id",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Identifier of the lookup type to which the value belongs", required = true),
                    @Parameter(name = LOOKUP_VALUE_ID, in = ParameterIn.PATH, description = "Identifier of the lookup value to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup value data"),
                    @ApiResponse(responseCode = "404", description = "Lookup value not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            @PathParam(LOOKUP_VALUE_ID) Long lookupValueId
    ) {
        return lookupValueWs_mng.read(lookupTypeId, lookupValueId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all lookup values for a lookup type",
            description = "Get all lookup values for a lookup type",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Identifier of the lookup type to which the values belong", required = true),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of records to return"),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results"),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = "forClass", in = ParameterIn.QUERY, description = "Class name to filter lookup values for attributes of that class"),
                    @Parameter(name = "forAttr", in = ParameterIn.QUERY, description = "Attribute name to filter lookup values for attributes of that class"),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup values data"),
                    @ApiResponse(responseCode = "404", description = "Lookup type not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(FILTER) String filterStr,
            @QueryParam("forClass") String forClass,
            @QueryParam("forAttr") String forAttr,
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode
    ) {
        if (isAdminViewMode(viewMode)) {
            return lookupValueWs_adm.readAll(lookupTypeId, limit, offset, filterStr, forClass, forAttr);
        }
        return lookupValueWs_mng.readAll(lookupTypeId, limit, offset, filterStr, forClass, forAttr);
    }

    @POST
    @Path("")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create lookup value",
            description = "Create lookup value",
            parameters = {@Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Identifier of the lookup type to which the value belongs", required = true)},
            requestBody = @RequestBody(description = "Lookup value to create", required = true, content = @Content(schema = @Schema(implementation = WsLookupValue.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of lookup value data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            WsLookupValue wsLookupValue
    ) {
        return lookupValueWs_adm.create(lookupTypeId, wsLookupValue);
    }

    @PUT
    @Path("{lookupValueId}")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update lookup value",
            description = "Update lookup value",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Identifier of the lookup type to which the value belongs", required = true),
                    @Parameter(name = "lookupValueId", in = ParameterIn.PATH, description = "Identifier of the lookup value to update", required = true)
            },
            requestBody = @RequestBody(description = "Lookup value data to update", required = true, content = @Content(schema = @Schema(implementation = WsLookupValue.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of lookup value data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Lookup value not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            @PathParam("lookupValueId") Long lookupId,
            WsLookupValue wsLookupValue
    ) {
        return lookupValueWs_adm.update(lookupTypeId, lookupId, wsLookupValue);
    }

    @DELETE
    @Path("{lookupValueId}")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete lookup value",
            description = "Delete lookup value",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Identifier of the lookup type to which the value belongs", required = true),
                    @Parameter(name = "lookupValueId", in = ParameterIn.PATH, description = "Identifier of the lookup value to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of lookup value"),
                    @ApiResponse(responseCode = "404", description = "Lookup value not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            @PathParam("lookupValueId") Long lookupId
    ) {
        return lookupValueWs_adm.delete(lookupTypeId, lookupId);
    }

    @POST
    @Path("order")
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Reorder lookup values",
            description = "Reorder lookup values. The request body should contain a list of lookup value identifiers in the desired order",
            parameters = {
                    @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Identifier of the lookup type to which the values belong", required = true),
                    @Parameter( name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions")
            },
            requestBody = @RequestBody(description = "List of lookup value identifiers in the desired order", required = true, content = @Content(schema = @Schema(implementation = Long.class, type = "array"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful reordering of lookup values"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Lookup type or lookup values not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object reorder(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId,
            List<Long> lookupValueIds,
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode
    ) {
        return lookupValueWs_adm.reorder(lookupTypeId, lookupValueIds);
    }

}
