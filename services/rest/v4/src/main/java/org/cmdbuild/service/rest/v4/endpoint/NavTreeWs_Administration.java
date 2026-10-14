/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
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
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_NAVTREES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_NAVTREES_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeService;
import static org.cmdbuild.service.rest.common.serializationhelpers.NavTreeSerializationHelper.*;
import org.cmdbuild.service.rest.common.serializationhelpers.NavTreeSerializationHelper.TreeMode;
import static org.cmdbuild.service.rest.common.serializationhelpers.NavTreeSerializationHelper.TreeMode.TREE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import org.cmdbuild.service.rest.v4.command.NavTreeWsCommand;
import org.cmdbuild.service.rest.v4.model.WsTreeData;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import org.springframework.stereotype.Component;

/**
 *
 * @author schursin
 */
@Path("administration/domainTrees")
@Tags({
    @Tag(name = "Nav Trees", description = "APIs to manage navigation trees."),
    @Tag(name = "Administration")
})
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class NavTreeWs_Administration {

    private final NavTreeService navTreeService;
    private final ObjectTranslationService translationService;
    private final NavTreeWsCommand command;

    public NavTreeWs_Administration(NavTreeService navTreeService, ObjectTranslationService translationService, NavTreeWsCommand command) {
        this.navTreeService = checkNotNull(navTreeService);
        this.translationService = checkNotNull(translationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all navigation trees",
            description = "Get all navigation trees",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of navigation trees"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_NAVTREES_VIEW_AUTHORITY)
    public Object readAll(
            @QueryParam("treeMode") @DefaultValue("flat") @Parameter(description = "Tree mode (default = flat)") String treeMode,
            @QueryParam(FILTER) @Parameter(description = "Filter navigation trees by name") String filterStr,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0")) Integer offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "If true, include full details in the response") Boolean detailed
    ) {
        List<NavTree> listNavTree = command.doReadAll(navTreeService::getAll, filterStr);
        return response(paged(listNavTree, offset, limit).map((tree) -> detailed ? serializeTree(tree, parseEnum(treeMode, TreeMode.class), translationService) : serializeTreeSimple(tree, translationService)));
    }

    @GET
    @Path("{treeId}/")
    @Operation(
            summary = "Get a navigation tree",
            description = "Get a navigation tree by id",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of navigation tree"),
                @ApiResponse(responseCode = "404", description = "The navigation tree was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_NAVTREES_VIEW_AUTHORITY)
    public Object read(
            @PathParam("treeId") @Parameter(description = "Id of the navigation tree to query", schema = @Schema(type = "string")) String id,
            @QueryParam("treeMode") @Parameter(description = "Tree Mode: tree | flat") @DefaultValue("flat") String treeMode
    ) {
        NavTree root = command.doRead(navTreeService::getTree, id);
        return response(serializeTree(root, parseEnum(treeMode, TreeMode.class), translationService));
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create a new navigation tree",
            description = "Create a new navigation tree; note: json payload given has to be in `tree` mode",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful creation of navigation tree"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Tree data", required = true) WsTreeData data
    ) {
        NavTree tree = command.doCreate(data);
        return response(serializeTree(tree, TREE, translationService));
    }

    @PUT
    @Path("{treeId}")
    @Operation(
            summary = "Update a navigation tree",
            description = "Update a navigation tree",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful update of navigation tree"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("treeId") @Parameter(description = "Id of the navigation tree to update", schema = @Schema(type = "string")) String id,
            @RequestBody(description = "Tree data", required = true) WsTreeData data
    ) {
        NavTree tree = command.doUpdate(id, data);
        return response(serializeTree(tree, TREE, translationService));
    }

    @DELETE
    @Path("{treeId}")
    @Operation(
            summary = "Delete a navigation tree",
            description = "Delete a navigation tree",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful deletion of navigation tree"),
                @ApiResponse(responseCode = "404", description = "The navigation tree was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("treeId") @Parameter(description = "Id of the navigation tree to delete", schema = @Schema(type = "string")) String id
    ) {
        command.doDelete(id);
        return success();
    }

    @POST
    @Path("{treeId}/fixDirections")
    @Operation(
            summary = "Fix navigation tree directions",
            description = "Fix navigation tree directions",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful fix of navigation tree directions"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_NAVTREES_MODIFY_AUTHORITY)
    public Object fixNavtreeDirections(
            @PathParam("treeId") @Parameter(description = "Id of the navigation tree to fix", schema = @Schema(type = "string")) String id
    ) {
        command.doFixNavTreeDirections(id);
        return success();
    }
}
