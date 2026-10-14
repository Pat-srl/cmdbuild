package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.MenuWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.MenuWs_Management;
import org.cmdbuild.service.rest.v4.model.MenuRootNodeWsBean;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_MENUS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_MENUS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE;

@Path("menu/")
@Tag( name = "Menu", description = "Menu")
@Produces(APPLICATION_JSON)
public class MenuWs {

    private final MenuWs_Administration menuWs_adm;
    private final MenuWs_Management menuWs_mng;

    public MenuWs(MenuWs_Administration menuWs_adm, MenuWs_Management menuWs_mng) {
        this.menuWs_adm = checkNotNull(menuWs_adm);
        this.menuWs_mng = checkNotNull(menuWs_mng);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all menus",
            description = "Get all menus",
            parameters = {
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Include or not full details in the response", required = false),
                    @Parameter(name = TYPE, in = ParameterIn.QUERY, description = "Filter menus by type. If not specified, all types will be returned", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of menu data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_VIEW_AUTHORITY)
    public Object readAll(
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam(TYPE) @Nullable String type
    ) {
        return menuWs_adm.readAll(detailed, type);
    }

    @GET
    @Path("/{menuId}")
    @Operation(
            summary = "Get menu by id",
            description = "Get menu by id",
            parameters = {@Parameter(name = "menuId", description = "Id of the menu to query")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of menu data"),
                    @ApiResponse(responseCode = "404", description = "Menu not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_VIEW_AUTHORITY)
    public Object read(
            @PathParam("menuId") Long menuId
    ) {
        return menuWs_adm.read(menuId);
    }

    @GET
    @Path("/gismenu")
    @Operation(
            summary = "Get GIS menu",
            description = "Get GIS menu",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of GIS menu data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getGeoMenu() {
        return menuWs_mng.getGeoMenu();
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create menu",
            description = "Create menu",
            parameters = {@Parameter(name = "regenerateNodeCodes", description = "Whether to regenerate node codes for the menu")},
            requestBody = @RequestBody(description = "Menu data to create"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of menu data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_MODIFY_AUTHORITY)
    public Object create(
            MenuRootNodeWsBean data,
            @QueryParam("regenerateNodeCodes") @DefaultValue(TRUE) Boolean regenerateNodeCodes
    ) {
        return menuWs_adm.create(data, regenerateNodeCodes);
    }

    @PUT
    @Path("/{menuId}")
    @Operation(
            summary = "Update menu",
            description = "Update menu",
            parameters = {@Parameter(name = "menuId", description = "Id of the menu to update")},
            requestBody = @RequestBody(description = "Menu data to update"),
            responses = {@ApiResponse(responseCode = "200", description = "Successful update of menu data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("menuId") Long menuId,
            MenuRootNodeWsBean data
    ) {
        return menuWs_adm.update(menuId, data);
    }

    @DELETE
    @Path("/{menuId}")
    @Operation(
            summary = "Delete menu",
            description = "Delete menu",
            parameters = {@Parameter(name = "menuId", description = "Id of the menu to delete")},
            responses = {@ApiResponse(responseCode = "200", description = "Successful deletion of menu")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("menuId") Long menuId
    ) {
        return menuWs_adm.delete(menuId);
    }
}
