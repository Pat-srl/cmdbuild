package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("fkdomains/")
@Produces(APPLICATION_JSON)
@Tag(name = "Foreign keys domains", description = "Operations related to foreign keys domains")
public class FkDomainWs {

    private final org.cmdbuild.service.rest.v4.endpoint.FkDomainWs fkDomainWs;

    public FkDomainWs(org.cmdbuild.service.rest.v4.endpoint.FkDomainWs fkDomainWs) {
        this.fkDomainWs = checkNotNull(fkDomainWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all foreign keys domains",
            description = "Get all foreign keys domains. If the user has admin view permissions, all foreign keys domains will be returned. Otherwise, only foreign keys domains for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of foreign keys domains data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view foreign keys domains"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset
    ) {
        return fkDomainWs.readAll(filterStr, limit, offset);
    }
}
