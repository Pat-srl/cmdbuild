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
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.ViewWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ViewWs_Management;
import org.cmdbuild.service.rest.v4.model.WsViewData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;


@Path("views/")
@Produces(APPLICATION_JSON)
@Tag(name = "Views", description = "Operations related to views")
public class ViewWs {

    private final ViewWs_Administration viewWs_adm;
    private final ViewWs_Management viewWs_mng;

    public ViewWs(ViewWs_Administration viewWs_adm, ViewWs_Management viewWs_mng) {
        this.viewWs_adm = checkNotNull(viewWs_adm);
        this.viewWs_mng = checkNotNull(viewWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all views",
            description = "Get all views",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode, either 'admin' or 'management'. If not specified, 'management' mode will be used", required = false),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of views to return", required = false),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Number of views to skip before starting to collect the result set", required = false),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information in the response", required = false),
                    @Parameter(name = "shared", in = ParameterIn.QUERY, description = "Whether to return only shared views. If not specified, both shared and non-shared views will be returned", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of views list"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getMany(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam("shared") @DefaultValue(FALSE) Boolean sharedOnly
    ) {
        if (isAdminViewMode(viewMode)) {
            return viewWs_adm.getMany(limit, offset, detailed, sharedOnly);
        }
        return viewWs_mng.getMany(limit, offset, detailed, sharedOnly);
    }

    @GET
    @Path("{viewId}")
    @Operation(
            summary = "Get view by id",
            description = "Get view by id",
            parameters = {
                    @Parameter(name = "viewId", in = ParameterIn.PATH, description = "Id of the view to retrieve", required = true),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode, either 'admin' or 'management'. If not specified, 'management' mode will be used", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of view data"),
                    @ApiResponse(responseCode = "400", description = "Invalid view ID"),
                    @ApiResponse(responseCode = "404", description = "View not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getOne(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("viewId") String viewId
    ) {
        if (isAdminViewMode(viewMode)) {
            return viewWs_adm.getOne(viewId);
        }
        return viewWs_mng.getOne(viewId);
    }

    @GET
    @Path("{viewId}/attributes")
    @Operation(
            summary = "Get view attributes",
            description = "Get view attributes by view id",
            parameters = {
                    @Parameter(name = "viewId", in = ParameterIn.PATH, description = "Id of the view to retrieve attributes for", required = true),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode, either 'admin' or 'management'. If not specified, 'management' mode will be used", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of view attributes"),
                    @ApiResponse(responseCode = "400", description = "Invalid view ID"),
                    @ApiResponse(responseCode = "404", description = "View not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getAttributes(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("viewId") String viewId
    ) {
        if (isAdminViewMode(viewMode)) {
            return viewWs_adm.getAttributes(viewId);
        }
        return viewWs_mng.getAttributes(viewId);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create a new view",
            description = "Create a new view",
            requestBody = @RequestBody(description = "View data to create", required = true, content = @Content(schema = @Schema(implementation = WsViewData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of view"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsViewData data
    ) {
        return viewWs_mng.create(data);
    }

    @PUT
    @Path("{viewId}")
    @Operation(
            summary = "Update a view",
            description = "Update a view",
            parameters = {
                    @Parameter(name = "viewId", in = ParameterIn.PATH, description = "Id of the view to update", required = true)
            },
            requestBody = @RequestBody(description = "View data to update", required = true, content = @Content(schema = @Schema(implementation = WsViewData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of view"),
                    @ApiResponse(responseCode = "400", description = "Invalid view ID or input data"),
                    @ApiResponse(responseCode = "404", description = "View not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("viewId") String viewId,
            WsViewData data
    ) {
        return viewWs_mng.update(viewId, data);
    }

    @DELETE
    @Path("{viewId}")
    @Operation(
            summary = "Delete a view",
            description = "Delete a view",
            parameters = {
                    @Parameter(name = "viewId", in = ParameterIn.PATH, description = "Id of the view to delete", required = true),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode, either 'admin' or 'management'. If not specified, 'management' mode will be used", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of view"),
                    @ApiResponse(responseCode = "400", description = "Invalid view ID"),
                    @ApiResponse(responseCode = "404", description = "View not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("viewId") String viewId
    ) {
        if (isAdminViewMode(viewMode)) {
            return viewWs_adm.delete(viewId);
        }
        return viewWs_mng.delete(viewId);
    }

    @GET
    @Path("{viewId}/print/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    @Operation(
            summary = "Print a view",
            description = "Print a view",
            parameters = {
                    @Parameter(name = "viewId", in = ParameterIn.PATH, description = "Id of the view to print", required = true),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the view", required = false),
                    @Parameter(name = SORT, in = ParameterIn.QUERY, description = "Sort order to apply to the view", required = false),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of records to print", required = false),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Number of records to skip before starting to print", required = false),
                    @Parameter(name = EXTENSION, in = ParameterIn.QUERY, description = "File extension to use for the printed file", required = false),
                    @Parameter(name = "attributes", in = ParameterIn.QUERY, description = "Comma-separated list of attributes to include in the printed file", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful print of view"),
                    @ApiResponse(responseCode = "400", description = "Invalid view ID or file extension"),
                    @ApiResponse(responseCode = "404", description = "View not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public DataHandler printView(
            @PathParam("viewId") String viewId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(EXTENSION) String extension,
            @QueryParam("attributes") String attributes
    ) {
        return viewWs_mng.printView(viewId, filterStr, sort, limit, offset, extension, attributes);
    }
}
