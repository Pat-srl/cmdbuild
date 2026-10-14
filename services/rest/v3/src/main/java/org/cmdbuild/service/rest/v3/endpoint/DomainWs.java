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
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper.WsDomainData;
import org.cmdbuild.service.rest.v4.endpoint.DomainWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.DomainWs_Management;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DOMAINS_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("domains/")
@Produces(APPLICATION_JSON)
@Tag(name = "Domains", description = "Operations related to domains")
public class DomainWs {

    private final DomainWs_Administration domainWs_adm;
    private final DomainWs_Management domainWs_mng;

    public DomainWs(DomainWs_Administration domainWs_adm, DomainWs_Management domainWs_mng) {
        this.domainWs_adm = checkNotNull(domainWs_adm);
        this.domainWs_mng = checkNotNull(domainWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all domains",
            description = "Get all domains. If the user has admin view permissions, all domains will be returned. Otherwise, only domains for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = EXT, in = ParameterIn.QUERY, description = "Whether to include full details of domains in the response, which may include additional information such as the list of values for each domain")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of domains data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view domains"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(EXT) Boolean includeFullDetails
    ) {
        if (isAdminViewMode(viewMode)) {
            return domainWs_adm.readAll(filterStr, limit, offset, includeFullDetails);
        }
        return domainWs_mng.readAll(filterStr, limit, offset, includeFullDetails);
    }

    @GET
    @Path("{" + DOMAIN_ID + "}/")
    @Operation(
            summary = "Get a domain",
            description = "Get a domain by its ID. If the user has admin view permissions, the full details of the domain will be returned. Otherwise, only basic information about the domain will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "ID of the domain to retrieve", required = true),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of domain data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the domain or the domain does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(DOMAIN_ID) String domainId
    ) {
        if (isAdminViewMode(viewMode)) {
            return domainWs_adm.read(domainId);
        }
        return domainWs_mng.read(domainId);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new domain",
            description = "Create a new domain. The user must have admin permissions for domains to perform this operation.",
            requestBody = @RequestBody(description = "Domain data to create", required = true, content = @Content(schema = @Schema(implementation = WsDomainData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of domain"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid domain data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create domains"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsDomainData data
    ) {
        return domainWs_adm.create(data);
    }

    @PUT
    @Path("{domainId}/")
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update a domain",
            description = "Update a domain. The user must have admin permissions for domains to perform this operation.",
            parameters = { @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "ID of the domain to update", required = true)},
            requestBody = @RequestBody(description = "Domain data to update", required = true, content = @Content(schema = @Schema(implementation = WsDomainData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of domain"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid domain data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update domains or the domain does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(DOMAIN_ID) String domainId,
            WsDomainData data
    ) {
        return domainWs_adm.update(domainId, data);
    }

    @DELETE
    @Path("{domainId}/")
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a domain",
            description = "Delete a domain. The user must have admin permissions for domains to perform this operation.",
            parameters = { @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "ID of the domain to delete", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of domain"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete domains or the domain does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(DOMAIN_ID) String domainId
    ) {
        return domainWs_adm.delete(domainId);
    }
}
