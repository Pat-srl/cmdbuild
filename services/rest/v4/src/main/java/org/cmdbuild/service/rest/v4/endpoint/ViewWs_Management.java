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
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ViewSerializer;
import org.cmdbuild.service.rest.v4.command.ViewWsCommand;
import org.cmdbuild.service.rest.v4.model.WsViewData;
import org.cmdbuild.view.View;
import org.cmdbuild.view.ViewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 *
 * @author ldare
 */
@Path("views/")
@Produces(APPLICATION_JSON)
@Tag(name = "Views", description = "Views")
@Component
public class ViewWs_Management {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ViewService viewService;
    private final ViewSerializer viewSerializer;
    private final AttributeTypeConversionService attributeTypeConversionService;
    private final ViewWsCommand command;

    public ViewWs_Management(
            ViewService viewService,
            ViewSerializer viewSerializer,
            AttributeTypeConversionService attributeTypeConversionService,
            ViewWsCommand command) {
        this.viewService = checkNotNull(viewService);
        this.viewSerializer = checkNotNull(viewSerializer);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all views",
            description = "Get all views",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of views"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getMany(
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(DETAILED) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam("shared") @Parameter(description = "Only Shared (true) or all views", schema = @Schema(type = "boolean")) @DefaultValue(FALSE) Boolean sharedOnly
    ) {
        logger.debug("list all views");
        List<View> views = command.doGetMany(viewService::getActiveViewsForCurrentUser);
        logger.trace("processing views = {}", views);
        return response(views.stream().map(v -> detailed ? viewSerializer.serializeDetailedView(v) : viewSerializer.serializeView(v)).collect(toList()));
    }

    @GET
    @Path("{viewId}")
    @Operation(
            summary = "Get a specific view",
            description = "Obtain a specific view",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of view data"),
                    @ApiResponse(responseCode = "404", description = "The view was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getOne(
            @PathParam("viewId") @Parameter(description = "Id of the view to query", schema = @Schema(type = "string")) String viewId
    ) {
        View view = command.doGetOne(viewId, viewService::getForCurrentUserByNameOrId);
        return response(viewSerializer.serializeDetailedView(view));
    }

    @GET
    @Path("{viewId}/attributes")
    @Operation(
            summary = "Get attributes for a specific view",
            description = "Obtain attributes for a specific view",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of view data"),
                    @ApiResponse(responseCode = "404", description = "The view was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAttributes(
            @PathParam("viewId") @Parameter(description = "Id of the view to query", schema = @Schema(type = "string")) String viewId
    ) {
        Collection<Attribute> attributesForView = command.doGetAttributes(viewId, viewService::getForCurrentUserByNameOrId);
        return response(attributeTypeConversionService.serialize(attributesForView));
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create a new view",
            description = "Create a new view",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of view"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @RequestBody(description = "View data", required = true) WsViewData data
    ) {
        View view = command.doCreate(data);
        return response(viewSerializer.serializeDetailedView(view));
    }

    @PUT
    @Path("{viewId}")
    @Operation(
            summary = "Update an existing view",
            description = "Update an existing view",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful update of view"),
                    @ApiResponse( responseCode = "400", description = "Invalid input data"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("viewId") @Parameter(description = "Id of the view to update", schema = @Schema(type = "string")) String viewId,
            @RequestBody(description = "View data", required = true) WsViewData data
    ) {
        View view = command.doUpdate(viewId, data);
        return response(viewSerializer.serializeDetailedView(view));
    }

    @DELETE
    @Path("{viewId}")
    @Operation(
            summary = "Delete a view",
            description = "Delete a specific view",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful deletion of view"),
                    @ApiResponse( responseCode = "404", description = "The view was not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("viewId") @Parameter(description = "Id of the view to delete", schema = @Schema(type = "string")) String viewId
    ) {
        command.doDelete(viewId, viewService::getForCurrentUserByNameOrId);
        return success();
    }

    @GET
    @Path("{viewId}/print/{file}")
    @Operation(
            summary = "Print a view report",
            description = "Generate and download a report for a specific view",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of view report"),
                    @ApiResponse(responseCode = "404", description = "The view was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printView(
            @PathParam("viewId") @Parameter(description = "Id of the view to query", schema = @Schema(type = "string")) String viewId,
            @QueryParam(FILTER) @Parameter(description = "Filter to apply to the view", schema = @Schema(type = "string")) String filterStr,
            @QueryParam(SORT) @Parameter(description = "How to order results", schema = @Schema(ref = "DefaultSortExample")) String sort,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Long limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Long offset,
            @QueryParam(EXTENSION) @Parameter(description = "Extension of the report to generate", schema = @Schema(type = "string")) String extension,
            @QueryParam("attributes") @Parameter(description = "Attributes to include in the report", schema = @Schema(type = "string")) String attributes
    ) {
        return command.doPrintView(viewId, filterStr, sort, limit, offset, extension, attributes);
    }
}
