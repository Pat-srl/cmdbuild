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
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;


@Path("search")
@Produces(APPLICATION_JSON)
@Tag(name = "Search", description = "Operations related to search")
public class SearchWs {

    private final org.cmdbuild.service.rest.v4.endpoint.SearchWs searchWs_adm;

    public SearchWs(org.cmdbuild.service.rest.v4.endpoint.SearchWs searchWs_adm) {
        this.searchWs_adm = checkNotNull(searchWs_adm);
    }

    @GET
    @Path("/{itemType:.+}")
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    @Operation(
            summary = "Search items",
            description = "Search items",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode of the search, must be 'admin' to access this endpoint", required = true),
                    @Parameter(name = "itemType", in = ParameterIn.PATH, description = "Type of items to search for", required = true)
            },
            requestBody = @RequestBody(description = "Query options for the search", required = true, content = @Content(schema = @Schema(implementation = WsQueryOptions.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful search"),
                    @ApiResponse(responseCode = "400", description = "Invalid request, e.g. invalid view mode or item type"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to perform this search"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object search(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("itemType") String type,
            WsQueryOptions query
    ) {
        checkArgument(isAdminViewMode(viewMode), "this ws is only available for admin view mode");
        return searchWs_adm.search(type, query);
    }

    @GET
    @Path("/{itemType1}/{itemType2}")
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    @Operation(
            summary = "Search items of two types",
            description = "Search items of two types",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode of the search, must be 'admin' to access this endpoint", required = true),
                    @Parameter(name = "itemType1", in = ParameterIn.PATH, description = "Type of the first set of items to search for", required = true),
                    @Parameter(name = "itemType2", in = ParameterIn.PATH, description = "Type of the second set of items to search for", required = true)
            },
            requestBody = @RequestBody(description = "Query options for the search", required = true, content = @Content(schema = @Schema(implementation = WsQueryOptions.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful search"),
                    @ApiResponse(responseCode = "400", description = "Invalid request, e.g. invalid view mode or item types"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to perform this search"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object search2(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("itemType1") String type1,
            @PathParam("itemType2") String type2,
            WsQueryOptions query
    ) {
        checkArgument(isAdminViewMode(viewMode), "this ws is only available for admin view mode");
        return searchWs_adm.search2(type1, type2, query);
    }
}
