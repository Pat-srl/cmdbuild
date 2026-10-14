/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.menu.Menu;
import org.cmdbuild.menu.MenuService;
import org.cmdbuild.service.rest.common.serializationhelpers.MenuSerializationHelper;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;

/**
 * @author ldare
 */
@Path("menu/")
@Tag( name = "Menu", description = "Menu")
@Produces(APPLICATION_JSON)
@Component
public class MenuWs_Management {

    private final MenuService menuService;
    private final MenuSerializationHelper helper;

    public MenuWs_Management(MenuService menuService, MenuSerializationHelper helper) {
        this.menuService = checkNotNull(menuService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path("gismenu")
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
        Menu gisMenu = menuService.getGisMenu();
        if (gisMenu != null) {
            return helper.menuResponse(gisMenu);
        } else {
            return success();
        }
    }
}
