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
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType;
import org.cmdbuild.service.rest.v4.endpoint.DmsCategoryWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.DmsCategoryWs_Management;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("dms/categories/")
@Tag( name = "DMS Categories", description = "DMS Categories")
@Produces(APPLICATION_JSON)
public class DmsCategoryWs {

    private final DmsCategoryWs_Administration dmsCategoryWs_adm;
    private final DmsCategoryWs_Management dmsCategoryWs_mng;

    public DmsCategoryWs(DmsCategoryWs_Administration dmsCategoryWs_adm, DmsCategoryWs_Management dmsCategoryWs_mng) {
        this.dmsCategoryWs_adm = checkNotNull(dmsCategoryWs_adm);
        this.dmsCategoryWs_mng = checkNotNull(dmsCategoryWs_mng);
    }

    @GET
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @Operation(
            summary = "Get a DMS category",
            description = "Get a DMS category",
            parameters = { @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "ID of the DMS category to retrieve", required = true)},
            responses = { @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS category data")},
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId
    ) {
        return dmsCategoryWs_mng.read(lookupTypeId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all DMS categories",
            description = "Get all DMS categories",
            parameters = {
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS categories data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(FILTER) String filter
    ) {
        return dmsCategoryWs_mng.readAll(limit, offset, filter);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create a new DMS category",
            description = "Create a new DMS category",
            requestBody = @RequestBody(description = "DMS category data to create", required = true, content = @Content(schema = @Schema(implementation = WsLookupType.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of DMS category data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid DMS category data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create DMS categories"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object createLookupType(
            WsLookupType wsLookupType
    ) {
        return dmsCategoryWs_adm.createLookupType(wsLookupType);
    }

    @DELETE
    @Path("{" + LOOKUP_TYPE_ID + "}/")
    @Operation(
            summary = "Delete a DMS category",
            description = "Delete a DMS category",
            parameters = { @Parameter(name = LOOKUP_TYPE_ID, in = ParameterIn.PATH, description = "ID of the DMS category to delete", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of DMS category"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete DMS categories"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    public Object deleteLookupType(
            @PathParam(LOOKUP_TYPE_ID) String lookupTypeId
    ) {
        return dmsCategoryWs_adm.deleteLookupType(lookupTypeId);
    }
}
