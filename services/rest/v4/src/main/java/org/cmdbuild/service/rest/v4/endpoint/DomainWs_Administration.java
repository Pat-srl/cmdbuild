/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper;
import org.cmdbuild.service.rest.v4.command.DomainWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DOMAINS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DOMAINS_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 *
 * @author schursin
 */
@Path("administration/domains/")
@Tags({
		@Tag( name = "Domains", description = "APIs to manage domains."),
		@Tag( name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class DomainWs_Administration {

    private final UserDomainService userDomainService;
	private final DomainSerializationHelper domainSerializationHelper;
	private final DomainWsCommand command;

    public DomainWs_Administration(UserDomainService userDomainService, DomainSerializationHelper domainSerializationHelper, DomainWsCommand command) {
        this.userDomainService = checkNotNull(userDomainService);
		this.domainSerializationHelper = checkNotNull(domainSerializationHelper);
        this.command = checkNotNull(command);
	}

	@GET
	@Path(EMPTY)
	@Operation(
			summary = "Get all domains",
			description = "Get all domains",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful operation"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_VIEW_AUTHORITY)
	public Object readAll(
            @QueryParam(FILTER) @Parameter(description = "Filter the results by a string") String filterStr,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(EXT) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) Boolean detailed
    ) {
		List<Domain> listDomain = command.doReadAll(userDomainService::getUserDomainsAsAdmin, filterStr);
		return response(paged(listDomain, offset, limit).stream().map(equal(detailed, Boolean.TRUE) ? domainSerializationHelper::serializeDetailedDomain : domainSerializationHelper::serializeBasicDomain).collect(toList()));
	}

	@GET
	@Path("{" + DOMAIN_ID + "}/")
	@Operation(
			summary = "Get a specific domain by ID",
			description = "Obtain details of a specific domain",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful retrieval of domain data"),
					@ApiResponse(responseCode = "404", description = "The domain was not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {}) }
	)
	@RolesAllowed(ADMIN_DOMAINS_VIEW_AUTHORITY)
	public Object read(
			@PathParam(DOMAIN_ID) @Parameter(description = "Id of domain to get") String domainId
	) {
		Domain domain = userDomainService.getDomain(domainId);
		return response(domainSerializationHelper.serializeDetailedDomain(domain));
	}

	@POST
	@Path(EMPTY)
	@Operation(
			summary = "Create a new domain",
			description = "Create a new domain",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful creation of domain"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
	public Object create(
			@RequestBody(description = "Domain data", required = true) DomainSerializationHelper.WsDomainData data
	) {
        Domain domain = command.doCreate(data);
		return response(domainSerializationHelper.serializeDetailedDomain(domain));
	}

	@PUT
	@Path("{domainId}/")
	@Operation(
			summary = "Update an existing domain",
			description = "Update an existing domain",
			parameters = {
					@Parameter(name = DOMAIN_ID, description = "Id of the domain", schema = @Schema(type = "long"))
			},
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful update of domain"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
	public Object update(
			@PathParam(DOMAIN_ID) @Parameter(description = "Name of the domain") String domainId,
			@RequestBody(description = "Domain data", required = true) DomainSerializationHelper.WsDomainData data
	) {
        Domain domain = command.doUpdate(domainId, data);
		return response(domainSerializationHelper.serializeDetailedDomain(domain));
	}

	@DELETE
	@Path("{domainId}/")
	@Operation(
			summary = "Delete a domain",
			description = "Delete a domain",
			parameters = {
					@Parameter(name = DOMAIN_ID, description = "Id of the domain", schema = @Schema(type = "long"))
			},
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful deletion of domain"),
					@ApiResponse(responseCode = "404", description = "The domain was not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
	public Object delete(
			@PathParam(DOMAIN_ID) @Parameter(description = "Name of the domain") String domainId
	) {
        command.doDelete(domainId);
		return success();
	}
}
