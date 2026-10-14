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
import org.cmdbuild.auth.grant.GrantData;
import org.cmdbuild.auth.grant.GrantService;
import org.cmdbuild.auth.grant.PrivilegedObjectType;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.service.rest.v4.model.WsGrantData;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_VIEW_AUTHORITY;
import static org.cmdbuild.auth.role.RoleType.ADMIN;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.GrantSerializer.serializeGrant;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;

@Path("roles/{roleId}/grants/")
@Tag(name = "Grants", description = "Grants management")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
public class GrantWs {

    private final org.cmdbuild.service.rest.v4.endpoint.GrantWs grantWs;
    private final RoleRepository roleRepository;
    private final GrantService grantService;

    public GrantWs(org.cmdbuild.service.rest.v4.endpoint.GrantWs grantWs, RoleRepository roleRepository, GrantService grantService) {
        this.grantWs = checkNotNull(grantWs);
        this.roleRepository = checkNotNull(roleRepository);
        this.grantService = checkNotNull(grantService);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all grants for role",
            description = "Returns all grants for role. If role is `_ALL`, returns all groups with grants.",
            parameters = {
                    @Parameter(name = ROLE_ID, description = "Role ID or name", required = true, in = ParameterIn.PATH),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = "includeObjectDescription", in = ParameterIn.QUERY, description = "Whether to include object description in the response", schema = @Schema(type = "boolean", defaultValue = FALSE)),
                    @Parameter(name = "includeRecordsWithoutGrant", in = ParameterIn.QUERY, description = "Whether to include records without grant in the response", schema = @Schema(type = "boolean", defaultValue = FALSE))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of grants list"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam(ROLE_ID) String roleId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam("includeObjectDescription") @DefaultValue(FALSE) Boolean includeObjectDescription,
            @QueryParam("includeRecordsWithoutGrant") @DefaultValue(FALSE) Boolean includeRecordsWithoutGrant
    ) {
        return grantWs.readMany(roleId, filterStr, limit, offset, includeObjectDescription, includeRecordsWithoutGrant);
    }

    @GET
    @Path("/by-target/{objectType}/{objectTypeName}")
    @Operation(
            summary = "Get grant for role by target object",
            description = "Returns grant for role by target object. If role is `_ALL`, returns all groups with grants for the target object.",
            parameters = {
                    @Parameter(name = ROLE_ID, description = "Role ID or name", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "objectType", description = "Type of the privileged object", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "objectTypeName", description = "Name/ID of the privileged object", required = true, in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of grant data"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    // This endpoint doesn't map to the respective v4 because in the new version we updated the format of the value returned by the second return
    // In this version the content of the field "data" of the map returned differs between the two returns, in the first it contains a list, while
    // in the second it contains a single element (not inside a list)
    public Object readOneByObject(
            @PathParam(ROLE_ID) String roleId,
            @PathParam("objectType") String objectTypeStr,
            @PathParam("objectTypeName") String objectTypeName
    ) {
        if (equal(roleId, "_ALL")) {
            return response(list(roleRepository.getAllGroups()).filter(g -> !equal(g.getType(), ADMIN)).map(g -> {
                Role role = roleRepository.getByNameOrId(g.getName());
                PrivilegedObjectType objectType = parseEnum(objectTypeStr, PrivilegedObjectType.class);
                GrantData grant = grantService.getGrantDataByRoleAndTypeAndName(role.getId(), objectType, objectTypeName);
                return serializeGrant(grant, grantService);
            }));
        } else {
            Role role = roleRepository.getByNameOrId(roleId);
            PrivilegedObjectType objectType = parseEnum(objectTypeStr, PrivilegedObjectType.class);
            GrantData grant = grantService.getGrantDataByRoleAndTypeAndName(role.getId(), objectType, objectTypeName);
            return response(serializeGrant(grant, grantService));
        }
    }

    @POST
    @Path("_ANY")
    @Operation(
            summary = "Update grants for role",
            description = "Updates grants for role. If role is `_ALL`, updates grants for multiple roles.",
            parameters = {
                @Parameter(name = ROLE_ID, description = "Role ID or name", required = true, in = ParameterIn.PATH)
            },
            requestBody = @RequestBody(description = "List of grants to update. The content of the list depends on the role specified in the path parameter. If the role is `_ALL`, each item in the list must contain a `roleId` field specifying the role to update grants for, and the rest of the fields specify the grant to update for that role. If the role is not `_ALL`, each item in the list specifies a grant to update for the role specified in the path parameter", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of grants"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(ROLE_ID) String roleId,
            List<WsGrantData> data
    ) {
        return grantWs.update(roleId, data);
    }
}
