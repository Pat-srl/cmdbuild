package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;

@Path("classes/{" + CLASS_ID + "}/")
@Tag(name = "Class stats", description = "Operations related to stats of classes")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ClassStatsWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ClassStatsWs classStatsWs;

    public ClassStatsWs(org.cmdbuild.service.rest.v4.endpoint.ClassStatsWs classStatsWs) {
        this.classStatsWs = checkNotNull(classStatsWs);
    }

    @GET
    @Path("{b:stats}")
    @Operation(
            summary = "Get stats for a class",
            description = "Get stats for a class",
            parameters = {
                    @Parameter(name = CLASS_ID, description = "Name of the class to query"),
                    @Parameter(name = "select", description = "Attribute to select in the response")
            },
            requestBody = @RequestBody(description = "Query options"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of stats data"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object stats(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            WsQueryOptions wsQueryOptions,
            @QueryParam("select") String select
    ) {
        return classStatsWs.stats(classId, wsQueryOptions, select);
    }

    @GET
    @Path("{b:relations}")
    @Operation(
            summary = "Get relations stats for a class",
            description = "Get relations stats for a class",
            parameters = {
                    @Parameter(name = CLASS_ID, description = "Name of the class to query")
            },
            requestBody = @RequestBody(description = "Query options"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of relations stats data"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object relations(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            WsQueryOptions wsQueryOptions
    ) {
        return classStatsWs.relations(classId, wsQueryOptions);
    }

}
