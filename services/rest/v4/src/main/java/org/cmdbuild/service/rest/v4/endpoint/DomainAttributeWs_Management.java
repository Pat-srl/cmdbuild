/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.v4.command.DomainAttributeWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 *
 * @author schursin
 */
@Path("domains/{" + DOMAIN_ID + "}/attributes/")
@Tag(name = "Domain Attributes")
@Produces(APPLICATION_JSON)
@Component
public class DomainAttributeWs_Management {

    private final AttributeTypeConversionService attributeTypeConversionService;
	private final DomainAttributeWsCommand command;

    public DomainAttributeWs_Management(AttributeTypeConversionService attributeTypeConversionService, DomainAttributeWsCommand command) {
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
        this.command = checkNotNull(command);
	}

	@GET
	@Path(EMPTY)
    @Operation(
            summary = "Get all active domain attributes",
            description = "Get all active domain attributes",
            responses = {
					@ApiResponse(responseCode = "200", description = "Successful retrieval of domain attributes"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
	public Object readAll(
            @PathParam(DOMAIN_ID) @Parameter(description = "Name of the domain to query") String domainId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        List<Attribute> listAttribute = command.doReadAll(domainId, true);
        return response(PagedElements.paged(listAttribute, offset, limit).stream().map(attributeTypeConversionService::serializeAttributeType).collect(toList()), listAttribute.size());
    }

	@GET
	@Path("{attrId}/")
	@Operation(
			summary = "Get a specific domain attribute",
			description = "Obtain details of a specific domain attribute",
			responses = {
					@ApiResponse(responseCode = "200", description = "Successful retrieval of domain attribute"),
					@ApiResponse(responseCode = "404", description = "The domain attribute was not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			},
			security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
	)
	public Object read(
            @PathParam(DOMAIN_ID) @Parameter(description = "Name of the domain to query") String domainId,
            @PathParam("attrId") @Parameter(description = "Id of the domain attribute to query") String attrId
    ) {
        Attribute attribute = command.doRead(domainId, attrId);
        return response(attributeTypeConversionService.serializeAttributeType(attribute));
	}
}
