/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.service.rest.v4.command.DomainAttributeWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DOMAINS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DOMAINS_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 *
 * @author schursin
 */
@Path("administration/domains/{" + DOMAIN_ID + "}/attributes/")
@Tags({
		@Tag(name = "Domain attributes", description = "APIs to manage domain attributes."),
		@Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class DomainAttributeWs_Administration {

    private final AttributeTypeConversionService attributeTypeConversionService;
	private final DomainAttributeWsCommand command;

    public DomainAttributeWs_Administration(AttributeTypeConversionService attributeTypeConversionService, DomainAttributeWsCommand command) {
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
        this.command = checkNotNull(command);
	}

	@GET
	@Path(EMPTY)
	@Operation(
			summary = "Get all domain attributes",
			description = "Get all domain attributes",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful retrieval of domain attributes"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_VIEW_AUTHORITY)
	public Object readAll(
            @PathParam(DOMAIN_ID) @Parameter(description = "Id of the domain to query") String domainId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        List<Attribute> listAttribute = command.doReadAll(domainId, false);
        return response(PagedElements.paged(listAttribute, offset, limit).stream().map(attributeTypeConversionService::serializeAttributeType).collect(toList()), listAttribute.size());
	}

	@GET
	@Path("{attrId}/")
	@Operation(
			summary = "Get a domain attribute",
			description = "Get a domain attribute by id",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful retrieval of domain attribute"),
					@ApiResponse(responseCode = "404", description = "The domain attribute was not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_VIEW_AUTHORITY)
	public Object read(
			@PathParam(DOMAIN_ID) @Parameter(description = "Id of the domain to query") String domainId,
			@PathParam("attrId") @Parameter(description = "Id of the attribute to query") String attrId
	) {
        Attribute attribute = command.doRead(domainId, attrId);
        return response(attributeTypeConversionService.serializeAttributeType(attribute));
	}

	@POST
	@Path(EMPTY)
	@Operation(
			summary = "Create a new domain attribute",
			description = "Create a new domain attribute",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful creation of domain attribute"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
	public Object create(
			@PathParam(DOMAIN_ID) @Parameter(description = "Id of the domain to query") String domainId,
			@RequestBody(description = "Attribute data", required = true) WsAttributeData data
	) {
        Attribute attribute = command.doCreate(domainId, data);
        return response(attributeTypeConversionService.serializeAttributeType(attribute));
	}

	@PUT
	@Path("{attrId}/")
	@Operation(
			summary = "Update an existing domain attribute",
			description = "Update an existing domain attribute",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful update of domain attribute"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
	public Object update(
			@PathParam(DOMAIN_ID) @Parameter(description = "Id of the domain to query") String domainId,
			@PathParam("attrId") @Parameter(description = "Id of the attribute to update") String attrId,
			@RequestBody(description = "Attribute data", required = true) WsAttributeData data
	) {
        Attribute attribute = command.doUpdate(domainId, attrId, data);
        return response(attributeTypeConversionService.serializeAttributeType(attribute));
	}

	@DELETE
	@Path("{attrId}/")
	@Operation(
			summary = "Delete a domain attribute",
			description = "Delete a domain attribute",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful deletion of domain attribute"),
					@ApiResponse(responseCode = "404", description = "The domain attribute was not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
	public Object delete(
			@PathParam(DOMAIN_ID) @Parameter(description = "Id of the domain to query") String domainId,
			@PathParam("attrId") @Parameter(description = "Id of the attribute to delete") String attrId
	) {
        command.doDelete(domainId, attrId);
		return success();
	}

	@POST
	@Path("order")
	@Operation(
			summary = "Reorder domain attributes",
			description = "Reorder domain attributes",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful reordering of domain attributes"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	@RolesAllowed(ADMIN_DOMAINS_MODIFY_AUTHORITY)
	public Object reorder(
			@PathParam(DOMAIN_ID) @Parameter(description = "Id of the domain to query") String domainId,
			@RequestBody(description = "Ordered list of attribute names, defining the new attribute order", required = true) List<String> attrOrder
	) {
        Domain domain = command.doReorder(domainId, attrOrder);
        return response(attrOrder.stream().map(domain::getAttribute).map(attributeTypeConversionService::serializeAttributeType).collect(toList()), attrOrder.size());
	}
}
