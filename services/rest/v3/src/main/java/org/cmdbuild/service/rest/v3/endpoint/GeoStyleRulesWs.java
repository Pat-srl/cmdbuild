package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.model.WsRulesetData;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/geostylerules/")
@Tag(name = "Geo style rules", description = "Operations related to geo style rules")
@Produces(APPLICATION_JSON)
public class GeoStyleRulesWs {

    private final org.cmdbuild.service.rest.v4.endpoint.GeoStyleRulesWs geoStyleRulesWs;

    public GeoStyleRulesWs(org.cmdbuild.service.rest.v4.endpoint.GeoStyleRulesWs geoStyleRulesWs) {
        this.geoStyleRulesWs = checkNotNull(geoStyleRulesWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get geo style rules for a class",
            description = "Get geo style rules for a class",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geo style rules data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested geo style rules"),
                    @ApiResponse(responseCode = "404", description = "No geo style rules found matching the query parameters"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam(CLASS_ID) String classId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset
    ) {
        return geoStyleRulesWs.readAll(classId, filterStr, limit, offset);
    }

    @GET
    @Path("{rulesetId}/")
    @Operation(
            summary = "Get geo style rule details",
            description = "Get geo style rule details",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = "rulesetId", in = ParameterIn.PATH, description = "Identifier of the geo style rule to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geo style rule data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested geo style rule"),
                    @ApiResponse(responseCode = "404", description = "No geo style rule found matching the query parameters"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(CLASS_ID) String classId,
            @PathParam("rulesetId") Long rulesetId
    ) {
        return geoStyleRulesWs.read(classId, rulesetId);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create geo style ruleset",
            description = "Create geo style ruleset",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
            },
            requestBody = @RequestBody(description = "Data for the new geo style ruleset", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of geo style ruleset data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input data provided"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested geo"),
                    @ApiResponse(responseCode = "404", description = "No geo style ruleset found matching the query parameters"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(@PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId, WsRulesetData data) {
        return geoStyleRulesWs.create(data);
    }

    @PUT
    @Path("{rulesetId}/")
    @Operation(
            summary = "Update geo style ruleset",
            description = "Update geo style ruleset",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "rulesetId", in = ParameterIn.PATH, description = "Identifier of the geo style ruleset to update")
            },
            requestBody = @RequestBody(description = "Data for the updated geo style ruleset", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of geo style ruleset data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input data provided"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested geo style ruleset"),
                    @ApiResponse(responseCode = "404", description = "No geo style ruleset found matching the query parameters"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("rulesetId") Long rulesetId,
            WsRulesetData data
    ) {
        return geoStyleRulesWs.update(rulesetId, data);
    }

    @DELETE
    @Path("{rulesetId}/")
    @Operation(
            summary = "Delete geo style ruleset",
            description = "Delete geo style ruleset",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "rulesetId", in = ParameterIn.PATH, description = "Identifier of the geo style ruleset to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of geo style ruleset data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested geo style ruleset"),
                    @ApiResponse(responseCode = "404", description = "No geo style ruleset found matching the query parameters"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("rulesetId") Long rulesetId
    ) {
        return geoStyleRulesWs.delete(rulesetId);
    }

    @POST
    @Path("visibility")
    @Operation(
            summary = "Bulk update visibility of geostyle rules",
            description = "Bulk update visibility from public to private and viceversa",
            parameters = {
                @Parameter(name = "type", in = ParameterIn.PATH, required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"})),
                @Parameter(name = CLASS_ID, in = ParameterIn.PATH, required = true, description = "Name of the class to query, must be _ANY")
            },
            requestBody = @RequestBody(description = "Map of ruleset IDs and their visibility status", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of visibility of geostyle rules"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input data provided"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested geo style ruleset"),
                    @ApiResponse(responseCode = "404", description = "No geo style ruleset found matching the query parameters"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public Object updateVisibility(
            @PathParam(CLASS_ID) String classId,
            Map<Long, Boolean> rulesets
    ) {
        return geoStyleRulesWs.updateVisibility(classId, rulesets);
    }

    @GET
    @Path("{rulesetId}/result")
    @Operation(
            summary = "Apply geo style rules to cards",
            description = "Apply geo style rules to cards",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "rulesetId", in = ParameterIn.PATH, description = "Identifier of the geo style ruleset to apply"),
                    @Parameter(name = "cards", in = ParameterIn.QUERY, description = "Comma-separated list of card IDs to apply the ruleset to")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geo style rules application result data"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Access denied to the requested geo style ruleset"),
                    @ApiResponse(responseCode = "404", description = "No geo style ruleset found matching the query parameters"),
                    @ApiResponse(responseCode = "406", description = "Requested pagination parameters are invalid"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object applyRules(
            @PathParam("rulesetId") Long rulesetId,
            @QueryParam("cards") @Nullable String cards
    ) {
        return geoStyleRulesWs.applyRules(rulesetId, cards);
    }

    @POST
    @Path("tryRules")
    @Operation(
            summary = "Test geo style rules on cards",
            description = "Test geo style rules on cards",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(type = "string", allowableValues = {"processes", "classes"})),
                    @Parameter(name = "cards", in = ParameterIn.QUERY, description = "Comma-separated list of card IDs to test the rules on")
            },
            requestBody = @RequestBody(description = "Data for the geo style rules to test", required = true),
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of geo style rules test result data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object testRules(WsRulesetData data, @QueryParam("cards") @Nullable String cards) {
        return geoStyleRulesWs.testRules(data, cards);
    }
}
