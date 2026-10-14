package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{type:classes|processes}/_ANY/{typeName:cards|instances}/_ANY/geovalues/")
@Tag(name = "Geo values", description = "Operations related to geo values attached to cards")
@Produces(APPLICATION_JSON)
public class GeoValueWs {

    private final org.cmdbuild.service.rest.v4.endpoint.GeoValueWs geoValueWs;

    public GeoValueWs(org.cmdbuild.service.rest.v4.endpoint.GeoValueWs geoValueWs) {
        this.geoValueWs = checkNotNull(geoValueWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get geo values for a card",
            description = "Get geo values for a card",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = ATTRIBUTE, in = ParameterIn.QUERY, description = "Attribute ids", schema = @Schema(type = "Set", implementation = Long.class, example = "1,2,3")),
                    @Parameter(name = AREA, in = ParameterIn.QUERY, description = "Area to query geo values for"),
                    @Parameter(name = "attach_nav_tree", in = ParameterIn.QUERY, description = "Attach navigation tree to geo values", schema = @Schema(type = "boolean", defaultValue = FALSE)),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = "forOwner", in = ParameterIn.QUERY, description = "Filter to apply to the query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geo value data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Geo value not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object query(
            @QueryParam(ATTRIBUTE) Set<Long> attrs,
            @QueryParam(AREA) String area,
            @QueryParam("attach_nav_tree") @DefaultValue(FALSE) Boolean attachNavTree,
            @QueryParam(FILTER) String filterStr,
            @QueryParam("forOwner") String forOwner
    ) {
        return geoValueWs.query(attrs, area, attachNavTree, filterStr, forOwner);
    }

    @GET
    @Path("area")
    @Operation(
            summary = "Get area for geo values",
            description = "Get area for geo values",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = ATTRIBUTE, in = ParameterIn.QUERY, description = "Attribute ids", schema = @Schema(type = "Set", implementation = Long.class, example = "1,2,3")),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = "forOwner", in = ParameterIn.QUERY, description = "Filter to apply to the query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geo value area data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object queryArea(
            @QueryParam(ATTRIBUTE) Set<Long> attrs,
            @QueryParam(FILTER) String filterStr,
            @QueryParam("forOwner") String forOwner
    ) {
        return geoValueWs.queryArea(attrs, filterStr, forOwner);
    }

    @GET
    @Path("center")
    @Operation(
            summary = "Get center for geo values",
            description = "Get center for geo values",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "typeName", in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = ATTRIBUTE, in = ParameterIn.QUERY, description = "Attribute ids", schema = @Schema(type = "Set", implementation = Long.class, example = "1,2,3")),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = "forOwner", in = ParameterIn.QUERY, description = "Filter to apply to the query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of geo value center data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object queryCenter(
            @QueryParam(ATTRIBUTE) Set<Long> attrs,
            @QueryParam(FILTER) String filterStr,
            @QueryParam("forOwner") String forOwner
    ) {
        return geoValueWs.queryCenter(attrs, filterStr, forOwner);
    }
}
