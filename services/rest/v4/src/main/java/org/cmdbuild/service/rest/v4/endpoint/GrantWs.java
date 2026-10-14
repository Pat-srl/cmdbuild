package org.cmdbuild.service.rest.v4.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
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
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.auth.grant.*;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import org.cmdbuild.service.rest.v4.command.GrantWsCommand;
import org.cmdbuild.service.rest.v4.model.WsGrantData;
import static org.cmdbuild.services.serialization.GrantSerializer.serializeGrant;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.stereotype.Component;

@Path("administration/roles/{roleId}/grants/")
@Tag(name = "Grants", description = "Grants management")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
@Component
public class GrantWs {

    private final GrantService grantService;
    private final GrantWsCommand command;

    public GrantWs(GrantService grantService, GrantWsCommand command) {
        this.grantService = checkNotNull(grantService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all grants for role",
            description = "Returns all grants for role. If role is `_ALL`, returns all groups with grants.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of grants list"),
                @ApiResponse(responseCode = "404", description = "No grants found for the role"),
                @ApiResponse(responseCode = "400", description = "Invalid filter"),
                @ApiResponse(responseCode = "403", description = "Access denied"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "409", description = "Conflict"),
                @ApiResponse(responseCode = "422", description = "Unprocessable Entity"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam(ROLE_ID) @Parameter(description = "Role ID or name", required = true) String roleId,
            @QueryParam(FILTER) @Parameter(description = "Filter to apply to the resultset") String filterStr,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam("includeObjectDescription") @Parameter(description = "Include object description in the response") @DefaultValue(FALSE) Boolean includeObjectDescription,
            @QueryParam("includeRecordsWithoutGrant") @Parameter(description = "Include records without grant in the response") @DefaultValue(FALSE) Boolean includeRecordsWithoutGrant
    ) {
        List<GrantData> grants = command.doReadMany(roleId, filterStr, includeRecordsWithoutGrant);
        return response(paged(grants, offset, limit).map((g) -> serializeGrant(g, includeObjectDescription, grantService)));
    }

    @GET
    @Path("/by-target/{objectType}/{objectTypeName}")
    @Operation(
            summary = "Get grant for role by target object",
            description = "Returns grant for role by target object. If role is `_ALL`, returns all groups with grants for the target object.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of grant data"),
                @ApiResponse(responseCode = "404", description = "No grant found for the role and object"),
                @ApiResponse(responseCode = "400", description = "Invalid object type or name"),
                @ApiResponse(responseCode = "403", description = "Access denied"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "409", description = "Conflict"),
                @ApiResponse(responseCode = "422", description = "Unprocessable Entity"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )

    // The type of value returned by this endpoint has slightly changed its structure in v4, now the content of the field "data" of the map
    // returned is always a list
    public Object readOneByObject(
            @PathParam(ROLE_ID) @Parameter(description = "Role ID or name", required = true) String roleId,
            @PathParam("objectType") @Parameter(description = "Type of the privileged object", required = true) String objectTypeStr,
            @PathParam("objectTypeName") @Parameter(description = "Name/ID of the privileged object", required = true) String objectTypeName
    ) {
        List<GrantData> grantDataList = command.doReadOneByObject(roleId, objectTypeStr, objectTypeName);
        return response(list(grantDataList).map(g -> serializeGrant(g, grantService)));
    }

    @POST
    @Path("_ANY")
    @Operation(
            summary = "Update grants for role",
            description = "Updates grants for role. If role is `_ALL`, updates grants for multiple roles.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful update of grants"),
                @ApiResponse(responseCode = "400", description = "Invalid request body"),
                @ApiResponse(responseCode = "404", description = "Role not found"),
                @ApiResponse(responseCode = "403", description = "Access denied"),
                @ApiResponse(responseCode = "401", description = "Unauthorized"),
                @ApiResponse(responseCode = "409", description = "Conflict"),
                @ApiResponse(responseCode = "422", description = "Unprocessable Entity"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(ROLE_ID) @Parameter(description = "Role ID or name", required = true) String roleId,
            @RequestBody(description = "List of grants to update", required = true, content = @Content(schema = @Schema(implementation = WsGrantData.class))) List<WsGrantData> data
    ) {
        return response(list(command.doUpdate(roleId, data)).map(m -> serializeGrant(m, grantService)));
    }
}
