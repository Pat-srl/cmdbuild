package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClass;
import org.cmdbuild.service.rest.v4.command.RoleClassFilterWsCommand;
import org.cmdbuild.service.rest.v4.model.WsDefaultStoredFilterForClass;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_SEARCHFILTERS_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ROLE_ID;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("administration/roles/{roleId}/filters")
@Tag(name = "Search Filters", description = "Operations related to search filters")
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_SEARCHFILTERS_VIEW_AUTHORITY)
@Component
public class RoleClassFilterWs {

    private final RoleClassFilterWsCommand command;

    public RoleClassFilterWs(RoleClassFilterWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get default filters for a role",
            description = "Get default filters for a role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of default filters data"),
                    @ApiResponse(responseCode = "404", description = "Role not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(ROLE_ID) @Parameter(description = "Id of role", required = true) String roleId
    ) {
        List<CardFilterAsDefaultForClass> filters = command.doRead(roleId);
        return response(filters.stream().map(f -> map(
                "_id", f.getFilter().getId(),
                "_defaultFor", f.getDefaultForClass()
        )));
    }

    @POST
    @Path("")
    @Operation(
            summary = "Update default filters for a role",
            description = "Update default filters for a role",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of default filters data"),
                    @ApiResponse(responseCode = "404", description = "Role not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object updateWithPost(
            @PathParam(ROLE_ID) @Parameter(description = "Id of role", required = true) String roleId,
            @RequestBody(description = "Default filters data", required = true, content = @Content(array = @ArraySchema(schema = @Schema(implementation = WsDefaultStoredFilterForClass.class)))) List<WsDefaultStoredFilterForClass> filters
    ) {
        command.doUpdateWithPost(roleId, filters);
        return read(roleId);
    }
}
