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
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.menu.*;
import org.cmdbuild.service.rest.common.serializationhelpers.MenuSerializationHelper;
import org.cmdbuild.service.rest.v4.model.MenuNodeWsBean;
import org.cmdbuild.service.rest.v4.model.MenuRootNodeWsBean;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_MENUS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_MENUS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

/**
 * @author ldare
 */
@Path("administration/menu/")
@Tags({
        @Tag(name = "Menu", description = "Menu"),
        @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class MenuWs_Administration {

    private final MenuService menuService;
    private final MenuSerializationHelper helper;

    public MenuWs_Administration(MenuService menuService, MenuSerializationHelper helper) {
        this.menuService = checkNotNull(menuService);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all menus",
            description = "Get all menus",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of menu data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_VIEW_AUTHORITY)
    public Object readAll(
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter( description = "If true, return detailed menu data") Boolean detailed,
            @QueryParam(TYPE) @Nullable @Parameter(description = "Type of menu to return") String type
    ) {
        List<MenuInfo> allMenuInfos = menuService.getAllMenuInfos();
        if (isNotBlank(type)) {
            MenuType menuType = parseEnumOrNull(type, MenuType.class);
            allMenuInfos = allMenuInfos.stream().filter(m -> m.getType().equals(menuType)).collect(toList());
        }
        return response(allMenuInfos.stream().map(detailed ? (m) -> helper.serializeDetailedMenu(menuService.getMenuById(m.getId())) : helper::serializeBasicMenu).collect(toList()));
    }

    @GET
    @Path("{menuId}")
    @Operation(
            summary = "Get menu by id",
            description = "Get menu by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of menu data"),
                    @ApiResponse(responseCode = "404", description = "Menu not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_VIEW_AUTHORITY)
    public Object read(
            @PathParam("menuId") @Parameter(description = "Id of menu to return", required = true, example = "1") Long menuId
    ) {
        Menu menu = menuService.getMenuById(menuId);
        return helper.menuResponse(menu);
    }

    @GET
    @Path("gismenu")
    @Operation(
            summary = "Get GIS menu",
            description = "Get GIS menu",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of GIS menu data"),
                    @ApiResponse(responseCode = "404", description = "GIS menu not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_VIEW_AUTHORITY)
    public Object getGeoMenu() {
        Menu gisMenu = menuService.getGisMenu();
        if (gisMenu != null) {
            return helper.menuResponse(gisMenu);
        } else {
            return success();
        }
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create menu",
            description = "Create menu",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of menu data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Menu data (root node with its child nodes)", required = true) MenuRootNodeWsBean data,
            @QueryParam("regenerateNodeCodes") @Parameter(name = "regenerateNodeCodes", description = "If true, regenerate node codes") @DefaultValue(TRUE) Boolean regenerateNodeCodes
    ) {
        Menu menu = menuService.create(data.groupName, toMenuTreeNode(data, regenerateNodeCodes), data.targetDevice, data.type);
        return helper.menuResponse(menu);
    }

    @PUT
    @Path("{menuId}")
    @Operation(
            summary = "Update menu",
            description = "Update menu",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of menu data"),
                    @ApiResponse(responseCode = "404", description = "Menu not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("menuId")  @Parameter(description = "Id of menu to update", required = true, example = "1") Long menuId,
            @RequestBody(description = "Menu data (root node with its child nodes)", required = true) MenuRootNodeWsBean data
    ) {
        Menu menu = menuService.update(menuId, toMenuTreeNode(data, false), data.targetDevice);
        return helper.menuResponse(menu);
    }

    @DELETE
    @Path("{menuId}")
    @Operation(
            summary = "Delete menu",
            description = "Delete menu",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of menu"),
                    @ApiResponse(responseCode = "404", description = "Menu not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_MENUS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("menuId") @Parameter(description = "Id of menu to delete", required = true, example = "1") Long menuId
    ) {
        menuService.delete(menuId);
        return success();
    }

    private MenuTreeNode toMenuTreeNode(MenuRootNodeWsBean data, boolean regenerateNodeCodes) {
        return MenuTreeNodeImpl.buildRoot(data.children.stream().map((n) -> toMenuTreeNode(n, regenerateNodeCodes)).collect(toList()));
    }

    private MenuTreeNode toMenuTreeNode(MenuNodeWsBean data, boolean regenerateNodeCodes) {
        return MenuTreeNodeImpl.builder()
                .withCode(regenerateNodeCodes ? randomId() : data.code)
                .withDescription(data.objectDescription)
                .withTarget(data.target)
                .withType(data.menuType)
                .withChildren(data.children.stream().map((n) -> toMenuTreeNode(n, regenerateNodeCodes)).collect(toList()))
                .build();
    }
}
