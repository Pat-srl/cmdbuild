/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.entrytype.FkDomain;
import org.cmdbuild.service.rest.v4.command.FkDomainWsCommand;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.services.serialization.FkDomainSerializationHelper.serializeFkDomain;

/**
 *
 * @author schursin
 */
@Path("fkdomains/")
@Tag(name = "FK Domains", description = "APIs to manage FK domains")
@Produces(APPLICATION_JSON)
@Component
public class FkDomainWs {

    private final FkDomainWsCommand command;
    private final ObjectTranslationService objectTranslationService;
    private final UserClassService userClassService;

    public FkDomainWs(UserClassService userClassService, ObjectTranslationService objectTranslationService, FkDomainWsCommand command) {
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.userClassService = checkNotNull(userClassService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all active FK domains",
            description = "Get all active FK domains",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of FK domains"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(FILTER) @Parameter(description = "How to filter results", schema = @Schema(ref = "DefaultFkDomainFilter")) String filterStr,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        List<FkDomain> listFkDomain = command.fetchFkDomains(filterStr);
        List<CmMapUtils.FluentMap<String, Object>> fkDomainSerializations = paged(listFkDomain, offset, limit).stream().map(d -> serializeFkDomain(d, objectTranslationService, userClassService)).collect(toList());
        return response(fkDomainSerializations, listFkDomain.size());
    }
}
