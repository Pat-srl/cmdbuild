package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

import javax.annotation.security.RolesAllowed;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;

@Path("globalsearch")
@Tag(name = "Global Search", description = "Global search for all classes and attributes")
@Produces(APPLICATION_JSON)
public class GlobalSearchWs {

    private final org.cmdbuild.service.rest.v4.endpoint.GlobalSearchWs globalSearchWs;

    private static final String EMAIL_CLASS = "Email";

    public GlobalSearchWs(org.cmdbuild.service.rest.v4.endpoint.GlobalSearchWs globalSearchWs) {
        this.globalSearchWs = checkNotNull(globalSearchWs);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Global search",
            description = "Global search for all classes and attributes",
            parameters = {
                    @Parameter( name = VIEW_MODE_HEADER_PARAM, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions")
            },
            requestBody = @RequestBody(description = "Query options for the global search"),
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful retrieval of global search data"),
                    @ApiResponse( responseCode = "401", description = "User not authenticated"),
                    @ApiResponse( responseCode = "403", description = "Access denied to the requested global search data"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object globalSearch(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            WsQueryOptions wsQueryOptions
    ) {
        return globalSearchWs.globalSearch(wsQueryOptions);
    }
}
