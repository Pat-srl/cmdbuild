/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.auth.role.Role;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.service.rest.v4.command.RoleWsCommand;
import org.cmdbuild.service.rest.v4.model.WsRoleUsers;
import org.cmdbuild.service.rest.v4.serializationhelpers.UserSerializationHelper;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ROLES_VIEW_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeUtils.getAdminPermissionsDependencies;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.RoleSerializationHelper.serializeBasicRole;
import static org.cmdbuild.services.serialization.RoleSerializationHelper.serializeDetailedRole;

/**
 *
 * @author ldare
 */
@Path("administration/roles/")
@Tags({
        @Tag( name = "Roles", description = "APIs to manage roles." ),
        @Tag( name = "Administration" )
})
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class RoleWs_Administration {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OperationUserSupplier operationUserSupplier;
    private final ObjectTranslationService objectTranslationService;
    private final RoleWsCommand command;

    public RoleWs_Administration(
            UserRepository userRepository,
            RoleRepository roleRepository,
            OperationUserSupplier operationUserSupplier,
            ObjectTranslationService objectTranslationService,
            RoleWsCommand command) {
        this.userRepository = checkNotNull(userRepository);
        this.roleRepository = checkNotNull(roleRepository);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all roles",
            description = "Returns all roles",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
    public Object readMany(
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) @DefaultValue(FALSE) boolean detailed
    ) {
        List<Role> listRole = command.doReadMany(roleRepository::getAllGroups);
        return response(paged(listRole, offset, limit)
                .stream().map((t) -> {
                    if (detailed) {
                        return serializeDetailedRole(t, objectTranslationService, userRepository, operationUserSupplier);
                    } else {
                        return serializeBasicRole(t, objectTranslationService);
                    }
                }));
    }

    @GET
    @Path("{roleId}/")
    @Operation(
            summary = "Get a role",
            description = "Returns a role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "The role was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
    public Object readOne(
            @PathParam(ROLE_ID) @Parameter(description = "Id of the role to query", schema = @Schema(type = "string")) String roleId
    ) {
        return response(serializeDetailedRole(roleRepository.getByNameOrId(roleId), objectTranslationService, userRepository, operationUserSupplier));
    }

    @GET
    @Path("{roleId}/users")
    @Operation(
            summary = "Get users assigned to a role",
            description = "Returns users assigned to a role",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "404", description = "The role was not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
    public Object readRoleUsers(
            @PathParam(ROLE_ID) @Parameter(description = "Id of the role to query", schema = @Schema(type = "string")) String roleId,
            @QueryParam(FILTER) @Parameter(description = "Filter to apply to the query", schema = @Schema(type = "string")) String filterStr,
            @QueryParam(SORT) @Parameter(description = "How to order results", schema = @Schema(ref = "DefaultSortExample")) String sort,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam("assigned") @Parameter(description = "Filter to apply to the query", schema = @Schema(type = "boolean")) Boolean assigned
    ) {
        PagedElements<UserData> users = command.doReadRoleUsers(roleId, filterStr, sort, limit, offset, assigned);
        return response(users.stream().map(UserSerializationHelper::serializeMinimalUser), users.totalSize());
    }

    @GET
    @Path("admin/dependencies")
    @Operation(
            summary = "Get roles dependencies for administration",
            description = "Returns roles dependencies for administration",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ROLES_VIEW_AUTHORITY)
    public Object getAdminDependencies() {
        return response(getAdminPermissionsDependencies());
    }

    @POST
    @Path("{roleId}/users")
    @Operation(
            summary = "Update users assigned to a role",
            description = "Updates users assigned to a role",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "404", description = "The role was not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object updateUsers(
            @PathParam(ROLE_ID) @Parameter(description = "Id of the role to query", schema = @Schema(type = "string")) String roleId,
            @RequestBody(description = "Users to assign to the role", required = true, content = @Content(schema = @Schema(implementation = WsRoleUsers.class))) WsRoleUsers users
    ) {
        command.doUpdateUsers(roleId, users);
        return success();
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a role",
            description = "Creates a role",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation") ,
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Role data", required = true) String jsonData
    ) {
        Role role = command.doCreate(jsonData);
        return response(serializeDetailedRole(role, objectTranslationService, userRepository, operationUserSupplier));
    }

    @PUT
    @Path("{roleId}/")
    @Operation(
            summary = "Update a role",
            description = "Updates a role",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation") ,
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {}) }
    )
    @RolesAllowed(ADMIN_ROLES_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(ROLE_ID) @Parameter(description = "Id of the role to query", schema = @Schema(type = "string")) String roleId,
            @RequestBody(description = "Role data", required = true) String jsonData
    ) {
        Role role = command.doUpdate(roleId, jsonData);
        return response(serializeDetailedRole(role, objectTranslationService, userRepository, operationUserSupplier));
    }
}
