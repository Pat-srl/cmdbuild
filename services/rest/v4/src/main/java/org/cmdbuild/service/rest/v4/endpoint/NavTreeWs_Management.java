/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.domain.tree.DomainPathFinder;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeService;
import org.cmdbuild.service.rest.common.serializationhelpers.NavTreeSerializationHelper.TreeMode;
import static org.cmdbuild.service.rest.common.serializationhelpers.NavTreeSerializationHelper.serializeTree;
import static org.cmdbuild.service.rest.common.serializationhelpers.NavTreeSerializationHelper.serializeTreeSimple;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.failure;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import org.cmdbuild.service.rest.v4.command.NavTreeWsCommand;
import org.cmdbuild.services.serialization.domain.tree.CardData;
import org.cmdbuild.services.serialization.domain.tree.DomainTreeData;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import org.springframework.stereotype.Component;

/**
 *
 * @author schursin
 */
@Path("domainTrees/")
@Tag(name = "Navigation Trees")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class NavTreeWs_Management {

    private final NavTreeService navTreeService;
    private final ObjectTranslationService translationService;
    private final NavTreeWsCommand command;
    private final DomainPathFinder domainPathFinder;
    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;

    public NavTreeWs_Management(NavTreeService navTreeService, ObjectTranslationService translationService, DomainPathFinder domainPathFinder, CardWsSerializationHelperv3 cardWsSerializationHelperv3, NavTreeWsCommand command) {
        this.navTreeService = checkNotNull(navTreeService);
        this.translationService = checkNotNull(translationService);
        this.command = checkNotNull(command);
        this.domainPathFinder = checkNotNull(domainPathFinder);
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all active navigation trees",
            description = "Get all active navigation trees",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of navigation trees"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam("treeMode") @DefaultValue("flat") @Parameter(description = "Tree mode (default = flat)") String treeMode,
            @QueryParam(FILTER) @Parameter(description = "Filter navigation trees by name") String filterStr,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "If true, include full details in the response") Boolean detailed
    ) {
        List<NavTree> listNavTree = command.doReadAll(navTreeService::getUserAll, filterStr);
        return response(paged(listNavTree, offset, limit).map((tree) -> detailed ? serializeTree(tree, parseEnum(treeMode, TreeMode.class), translationService) : serializeTreeSimple(tree, translationService)));
    }

    @GET
    @Path("{treeId}/")
    @Operation(
            summary = "Get a specific navigation tree",
            description = "Get a specific navigation tree",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of navigation tree"),
                @ApiResponse(responseCode = "404", description = "The navigation tree was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("treeId") @Parameter(description = "Id of the navigation tree", schema = @Schema(type = "string")) String id,
            @QueryParam("treeMode") @Parameter(description = "Tree mode", schema = @Schema(type = "string")) @DefaultValue("flat") String treeMode
    ) {
        NavTree root = command.doRead(navTreeService::getUserTree, id);
        return response(serializeTree(root, parseEnum(treeMode, TreeMode.class), translationService));
    }

    @POST
    @Path("{treeId}/")
    @Operation(
            summary = "Find a path, if any, to the root of given navigation tree",
            description = "Find a root path in given navigation tree",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of path to root in navigation tree; an empty list if no path found"),
                @ApiResponse(responseCode = "404", description = "Tree non found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object findRootPath(
            @PathParam("treeId") @Parameter(description = "Id of the navigation tree", schema = @Schema(type = "string")) String id,
            @RequestBody(description = "Card data used to resolve the path to the root of the navigation tree", required = true) CardData data
    ) {
        NavTree root = command.doRead(navTreeService::getUserTree, id);

        if (root == null) {
            return notFoundWith("tree with id =< %s > not found".formatted(id));
        }

        Map navTreeSerializationAsMap = serializeTree(root, TreeMode.FLAT, translationService);
        // Use json serialization to build needed tree data
        DomainTreeData domainTreeData = fromJson(toJson(navTreeSerializationAsMap), DomainTreeData.class);

        List<Card> resultPath = domainPathFinder.findPathToRoot(domainTreeData, data);
        return response(list(resultPath).map(cardWsSerializationHelperv3::serializeCard));
    }

    private Response notFoundWith(String msg) {
        return Response.status(Response.Status.NOT_FOUND).entity(failure().with("messages", msg)).build();
    }
}
