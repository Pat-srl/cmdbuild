/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 *
 * @author schursin
 */
@Path("domains/")
@Tag(name = "Domains")
@Produces(APPLICATION_JSON)
@Component
public class DomainWs_Management {

    private final UserDomainService userDomainService;
	private final DomainSerializationHelper domainSerializationHelper;
	private final DomainWsCommand command;

    public DomainWs_Management(UserDomainService userDomainService, DomainSerializationHelper domainSerializationHelper, DomainWsCommand command) {
        this.userDomainService = checkNotNull(userDomainService);
		this.domainSerializationHelper = checkNotNull(domainSerializationHelper);
        this.command = checkNotNull(command);
	}

	@GET
	@Path(EMPTY)
    @Operation(
            summary = "Get all active domains",
            description = "Get all active domains",
            responses = {
					@ApiResponse(responseCode = "200", description = "Successful retrieval of domains"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(FILTER) @Parameter(description = "Filter the domains by name") String filterStr,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(EXT) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) Boolean detailed
    ) {
        List<Domain> listDomain = command.doReadAll(userDomainService::getActiveUserDomains, filterStr);
		return response(paged(listDomain, offset, limit).stream().map(equal(detailed, Boolean.TRUE) ? domainSerializationHelper::serializeDetailedDomain : domainSerializationHelper::serializeBasicDomain).collect(toList()));
	}

	@GET
	@Path("{" + DOMAIN_ID + "}/")
	@Operation(
			summary = "Get a specific domain",
			description = "Get a specific domain",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful retrieval of domain"),
					@ApiResponse(responseCode = "404", description = "The domain was not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	public Object read(
			@PathParam(DOMAIN_ID) @Parameter(description = "Id of the domain to query") String domainId
	) {
		Domain domain = userDomainService.getUserDomain(domainId);
		return response(domainSerializationHelper.serializeDetailedDomain(domain));
	}
}
