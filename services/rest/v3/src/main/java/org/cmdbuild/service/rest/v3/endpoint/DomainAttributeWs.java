/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
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
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.service.rest.v4.endpoint.DomainAttributeWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.DomainAttributeWs_Management;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DOMAINS_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("domains/{" + DOMAIN_ID + "}/attributes/")
@Produces(APPLICATION_JSON)
@Tag(name = "Domain attributes", description = "Operations related to attributes of domains")
public class DomainAttributeWs {

    private final DomainAttributeWs_Administration domainAttributeWs_adm;
    private final DomainAttributeWs_Management domainAttributeWs_mng;

    public DomainAttributeWs(DomainAttributeWs_Administration domainAttributeWs_adm, DomainAttributeWs_Management domainAttributeWs_mng) {
        this.domainAttributeWs_adm = checkNotNull(domainAttributeWs_adm);
        this.domainAttributeWs_mng = checkNotNull(domainAttributeWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all attributes of a domain",
            description = "Get all attributes of a domain. If the user has admin view permissions, all attributes will be returned. Otherwise, only attributes for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'. The view mode will be determined based on the user's permissions if the header is not set.",
            parameters = {
                    @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "ID of the domain to retrieve attributes for", required = true),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view attributes of the domain"),
                    @ApiResponse(responseCode = "404", description = "Not found - the specified domain does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @PathParam(DOMAIN_ID) String domainId,
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset
    ) {
        if (isAdminViewMode(viewMode)) {
            return domainAttributeWs_adm.readAll(domainId, limit, offset);
        }
        return domainAttributeWs_mng.readAll(domainId, limit, offset);
    }

    @GET
    @Path("{attrId}/")
    @Operation(
            summary = "Get a specific attribute of a domain",
            description = "Get a specific attribute of a domain. The user must have management permissions for the attribute to retrieve it.",
            parameters = {
                    @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "ID of the domain to which the attribute belongs", required = true),
                    @Parameter(name = "attrId", in = ParameterIn.PATH, description = "ID of the attribute to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the attribute of the domain"),
                    @ApiResponse(responseCode = "404", description = "Not found - the specified domain or attribute does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(DOMAIN_ID) String domainId,
            @PathParam("attrId") String attrId
    ) {
        return domainAttributeWs_mng.read(domainId, attrId);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new attribute for a domain",
            description = "Create a new attribute for a domain. The user must have admin permissions for the domain to create a new attribute.",
            parameters = { @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "ID of the domain to which the attribute will be added", required = true)},
            requestBody = @RequestBody(description = "Attribute data to create", required = true, content = @Content(schema = @Schema(implementation = WsAttributeData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid attribute data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create attributes for the domain"),
                    @ApiResponse(responseCode = "404", description = "Not found - the specified domain does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(DOMAIN_ID) String domainId,
            WsAttributeData data
    ) {
        return domainAttributeWs_adm.create(domainId, data);
    }

    @PUT
    @Path("{attrId}/")
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an attribute of a domain",
            description = "Update an attribute of a domain. The user must have admin permissions for the domain to update an attribute.",
            parameters = {
                    @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "ID of the domain to which the attribute belongs", required = true),
                    @Parameter(name = "attrId", in = ParameterIn.PATH, description = "ID of the attribute to update", required = true)
            },
            requestBody = @RequestBody(description = "Attribute data to update", required = true, content = @Content(schema = @Schema(implementation = WsAttributeData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid attribute data provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update attributes for the domain or the attribute does not exist"),
                    @ApiResponse(responseCode = "404", description = "Not found - the specified domain or attribute does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(DOMAIN_ID) String domainId,
            @PathParam("attrId") String attrId,
            WsAttributeData data
    ) {
        return domainAttributeWs_adm.update(domainId, attrId, data);
    }

    @DELETE
    @Path("{attrId}/")
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete an attribute of a domain",
            description = "Delete an attribute of a domain. The user must have admin permissions for the domain to delete an attribute.",
            parameters = {
                    @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "ID of the domain to which the attribute belongs", required = true),
                    @Parameter(name = "attrId", in = ParameterIn.PATH, description = "ID of the attribute to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete attributes for the domain or the attribute does not exist"),
                    @ApiResponse(responseCode = "404", description = "Not found - the specified domain or attribute does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(DOMAIN_ID) String domainId,
            @PathParam("attrId") String attrId
    ) {
        return domainAttributeWs_adm.delete(domainId, attrId);
    }

    @POST
    @Path("order")
    @RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Reorder attributes of a domain",
            description = "Reorder attributes of a domain. The user must have admin permissions for the domain to reorder its attributes.",
            parameters = { @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "ID of the domain to which the attributes belong", required = true)},
            requestBody = @RequestBody(description = "List of attribute IDs in the desired order", required = true, content = @Content(schema = @Schema(implementation = String.class, type = "array"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid attribute order provided"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to reorder attributes for the domain or the domain does not exist"),
                    @ApiResponse(responseCode = "404", description = "Not found - the specified domain does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object reorder(
            @PathParam(DOMAIN_ID) String domainId,
            List<String> attrOrder
    ) {
        return domainAttributeWs_adm.reorder(domainId, attrOrder);
    }

}
