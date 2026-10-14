/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.cardfilter.StoredFilter;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.service.rest.v4.command.ClassFilterWsCommand;
import org.cmdbuild.service.rest.v4.model.WsDefaultStoredFilter;
import org.cmdbuild.service.rest.v4.model.WsFilterData;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.ClassFilterSerializationHelper.serializeFilter;

/**
 *
 * @author schursin
 */
@Path("{" + TYPE + ":classes|processes|views}/{" + CLASS_ID + "}/filters/")
@Tag( name = "Class filters", description = "APIs to manage class filters.")
@Component
public class ClassFilterWs_Management {

    private final ObjectTranslationService objectTranslationService;
    private final ClassFilterWsCommand command;

    public ClassFilterWs_Management(ObjectTranslationService objectTranslationService, ClassFilterWsCommand command) {
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all filters for a class",
            description = "Obtain all filters for a class",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes", "views"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = StoredFilter.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object readAll(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")  Integer offset,
            @QueryParam(SHARED) @Parameter(description = "If true, only shared filters will be returned", schema = @Schema(type = "boolean", defaultValue = FALSE)) @DefaultValue(FALSE) Boolean sharedOnly
    ) {
        List<StoredFilter> listStoredFilter = command.doReadAll(classId, sharedOnly);
        listStoredFilter = listStoredFilter.stream().filter(f -> f.isActive()).toList();
        PagedElements<StoredFilter> res = paged(listStoredFilter, offset, limit);
        return response(res.stream().map(f -> serializeFilter(f, objectTranslationService)).toList(), res.totalSize());
    }

    @GET
    @Path("{" + FILTER_ID + "}/")
    @Operation(
            summary = "Get a specific filter",
            description = "Obtain a specific filter",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes", "views"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = StoredFilter.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object read(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query", required = true, schema = @Schema(type = "string")) String classId,
            @PathParam(FILTER_ID) @Parameter(description = "Name of the filter to query", required = true, schema = @Schema(type = "string")) Long filterId
    ) {
        StoredFilter storedFilter = command.doRead(filterId);
        return response(serializeFilter(storedFilter, objectTranslationService));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new filter",
            description = "Create a new filter",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes", "views"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsFilterData.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object create(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query", required = true, schema = @Schema(type = "string")) String classId,
            @RequestBody(description = "Filter definition", required = true) WsFilterData element
    ) {
        return response(serializeFilter(command.doCreate(element), objectTranslationService));
    }

    @PUT
    @Path("{" + FILTER_ID + "}/")
    @Operation(
            summary = "Update an existing filter",
            description = "Update an existing filter",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes", "views"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsFilterData.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object update(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query", required = true, schema = @Schema(type = "string")) String classId,
            @PathParam(FILTER_ID) @Parameter(description = "Name of the filter to query", required = true, schema = @Schema(type = "string")) Long filterId,
            @RequestBody(description = "Filter definition", required = true) WsFilterData element
    ) {
        return response(serializeFilter(command.doUpdate(filterId, element), objectTranslationService));
    }

    @DELETE
    @Path("{" + FILTER_ID + "}/")
    @Operation(
            summary = "Delete a filter",
            description = "Delete a filter",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes", "views"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query", required = true, schema = @Schema(type = "string")) String classId,
            @PathParam(FILTER_ID) @Parameter(description = "Name of the filter to query", required = true, schema = @Schema(type = "string")) Long filterId
    ) {
        command.doDelete(filterId);
        return success();
    }

    @GET
    @Path("{filterId}/defaultFor/")
    @Operation(
            summary = "Get the default roles for a filter",
            description = "Get the default roles for a filter",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes", "views"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APIDefaultFilterForRolesResponse"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getDefaultForRoles(
            @PathParam(FILTER_ID) @Parameter(description = "Name of the filter to query", required = true, schema = @Schema(type = "string")) Long filterId
    ) {
        return response(
                command.doGetDefaultForRoles(filterId)
                        .map(f -> map("_id", f.getDefaultForRole()))
        );
    }

    @POST
    @Path("{filterId}/defaultFor/")
    @Operation(
            summary = "Update the default roles for a filter",
            description = "Update the default roles for a filter",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes", "views"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APIDefaultFilterForRolesResponse"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object updateWithPost(
            @PathParam(FILTER_ID) @Parameter(description = "Id of the filter", required = true, schema = @Schema(type = "string")) Long filterId,
            @RequestBody(description = "Default roles", required = true, content = @Content(schema = @Schema(implementation = WsDefaultStoredFilter.class))) List<WsDefaultStoredFilter> roles
    ) {
        return response(
                command.doUpdateWithPost(filterId, roles)
                        .map(f -> map("_id", f.getDefaultForRole()))
        );
    }
}
