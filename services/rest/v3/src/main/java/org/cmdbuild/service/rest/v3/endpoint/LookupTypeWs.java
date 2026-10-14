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
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType;
import org.cmdbuild.service.rest.v4.endpoint.LookupTypeWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.LookupTypeWs_Management;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOOKUPS_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("lookup_types/")
@Tag( name = "Lookup types", description = "Lookup types")
@Produces(APPLICATION_JSON)
public class LookupTypeWs {

    private final LookupTypeWs_Administration lookupTypeWs_adm;
    private final LookupTypeWs_Management lookupTypeWs_mng;

    public LookupTypeWs(LookupTypeWs_Administration lookupTypeWs_adm, LookupTypeWs_Management lookupTypeWs_mng) {
        this.lookupTypeWs_adm = checkNotNull(lookupTypeWs_adm);
        this.lookupTypeWs_mng = checkNotNull(lookupTypeWs_mng);
    }

    @GET
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @Operation(
            summary = "Get lookup type by id",
            description = "Get lookup type by id",
            parameters = {@Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Identifier of the lookup type to retrieve", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup type data"),
                    @ApiResponse(responseCode = "404", description = "Lookup type not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId
    ) {
        return lookupTypeWs_mng.read(lookupTypeId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all lookup types",
            description = "Get all lookup types",
            parameters = {
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lookup types data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(FILTER) String filter
    ) {
        return lookupTypeWs_mng.readAll(limit, offset, filter);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create lookup type",
            description = "Create lookup type",
            requestBody = @RequestBody(description = "Lookup type to create", required = true, content = @io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = WsLookupType.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of lookup type data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object createLookupType(
            WsLookupType wsLookupType
    ) {
        return lookupTypeWs_adm.createLookupType(wsLookupType);
    }

    @DELETE
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @Operation(
            summary = "Delete lookup type by id",
            description = "Delete lookup type by id",
            parameters = {@Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "Identifier of the lookup type to delete", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of lookup type"),
                    @ApiResponse(responseCode = "404", description = "Lookup type not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOOKUPS_MODIFY_AUTHORITY)
    public Object deleteLookupType(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId
    ) {
        return lookupTypeWs_adm.deleteLookupType(lookupTypeId);
    }
}
