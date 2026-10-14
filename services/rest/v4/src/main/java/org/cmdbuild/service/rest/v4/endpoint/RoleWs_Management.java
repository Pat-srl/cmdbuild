/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.service.rest.v4.command.RoleWsCommand;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.RoleSerializationHelper.serializeRoleInfo;

/**
 *
 * @author ldare
 */
@Path("roles/")
@Tag(name = "Roles")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class RoleWs_Management {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final OperationUserSupplier operationUserSupplier;
    private final ObjectTranslationService objectTranslationService;
    private final RoleWsCommand command;

    public RoleWs_Management(
            UserRepository userRepository,
            RoleRepository roleRepository,
            OperationUserSupplier operationUserSupplier,
            ObjectTranslationService objectTranslationService,
            RoleWsCommand command) {
        this.roleRepository = checkNotNull(roleRepository);
        this.userRepository = checkNotNull(userRepository);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = checkNotNull(command);
    }

    // The original entrypoint has a hasPrivileges control that doesn't depend on viewMode
    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all roles",
            description = "Get all roles",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset
    ) {
        return response(paged(operationUserSupplier.getUser().getLoginUser().getRoleInfos(), offset, limit).stream().map(t -> serializeRoleInfo(t, objectTranslationService)));
    }

    // The original entrypoint has a hasPrivileges control that doesn't depend on viewMode
    @GET
    @Path("{roleId}/")
    @Operation(
            summary = "Get a role by its identifier",
            description = "Get a role by its identifier",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "The role was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam(ROLE_ID) @Parameter(description = "Id of the role to query", schema = @Schema(type = "string")) String roleId
    ) {
        return response(serializeRoleInfo(operationUserSupplier.getUser().getLoginUser().getRoleInfoByNameOrId(roleId), objectTranslationService));
    }
}
