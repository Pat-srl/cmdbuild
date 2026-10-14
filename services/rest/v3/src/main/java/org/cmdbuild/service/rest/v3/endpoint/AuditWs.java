package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.audit.RequestData;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;

@Path("system/audit/")
@Tags({
        @Tag( name = "Administration"),
        @Tag( name = "Audit", description = "Auditing and logging of requests and errors")
})
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
public class AuditWs {

    private final org.cmdbuild.service.rest.v4.endpoint.AuditWs auditWs;

    public AuditWs(org.cmdbuild.service.rest.v4.endpoint.AuditWs auditWs) {
        this.auditWs = auditWs;
    }

    @GET
    @Path("mark")
    @Operation(
            summary = "Get current audit mark",
            description = "Returns a timestamp mark representing the current point in time. This mark can be used to retrieve audit records (requests and errors) that occurred after this point.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Current audit mark", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema( ref = "APIAuditMark"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object mark() {
        return auditWs.mark();
    }

    @GET
    @Path("requests")
    @Operation(
            summary = "Get audit requests",
            description = "Returns a list of audit requests that occurred after the specified mark, or last limit requests if mark is not provided.",
            parameters = {
                    @Parameter(name = "since", description = "Date and time from which to return all requests expressed as epoch milliseconds", in = ParameterIn.QUERY),
                    @Parameter(name = "limit", description = "Amount of audit requests to return", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Audit data retrieved successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = RequestData.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request mark"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getRequests(
            @QueryParam("since") @Nullable String mark,
            @QueryParam("limit") @Nullable Long limit
    ) {
        return auditWs.getRequests(mark, limit);
    }

    @GET
    @Path("errors")
    @Operation(
            summary = "Get request errors",
            description = "Returns a list of error requests that occurred after the specified mark, or last limit error requests if mark is not provided.",
            parameters = {
                    @Parameter(name = "since", description = "Date and time from which to return all requests expressed as epoch milliseconds", in = ParameterIn.QUERY),
                    @Parameter(name = "limit", description = "Amount of error requests to return", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Audit data retrieved successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = RequestData.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request mark"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getErrors(
            @QueryParam("since") @Nullable String mark,
            @QueryParam("limit") @Nullable Long limit
    ) {
        return auditWs.getErrors(mark, limit);
    }

    @GET
    @Path("requests/{id}")
    @Operation(
            summary = "Get audit request details",
            description = "Returns details of a specific audit request.",
            parameters = {
                    @Parameter(name = "id", description = "Id of audit request", in = ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Audit data retrieved successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = RequestData.class))),
                    @ApiResponse(responseCode = "404", description = "Audit request not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getRequest(
            @PathParam("id") String id
    ) {
        return auditWs.getRequest(id);
    }
}
