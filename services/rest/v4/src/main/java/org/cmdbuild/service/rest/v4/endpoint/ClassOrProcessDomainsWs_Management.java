/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ClassOrProcessDomainsWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 *
 * @author schursin
 */
@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/domains")
@Tag( name = "Class or process domains")
@Produces(APPLICATION_JSON)
@Component
public class ClassOrProcessDomainsWs_Management {

    private final UserDomainService userDomainService;
    private final UserClassService userClassService;
    private final DomainSerializationHelper domainSerializationHelper;
    private final ClassOrProcessDomainsWsCommand command;

    public ClassOrProcessDomainsWs_Management(UserClassService userClassService, UserDomainService userDomainService, DomainSerializationHelper domainSerializationHelper, ClassOrProcessDomainsWsCommand command) {
        this.userDomainService = checkNotNull(userDomainService);
        this.userClassService = checkNotNull(userClassService);
        this.domainSerializationHelper = checkNotNull(domainSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get domains for a class",
            description = "Obtain domains for a class",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Domain.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getDomains(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query", required = true)  String classId,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details of the domains in the response", schema = @Schema(type = "boolean", defaultValue = FALSE)) Boolean includeFullDetails
    ) {
        List<Domain> listDomain = command.doGetDomains(classId, userDomainService::getActiveUserDomainsForClasse);
        return response(domainSerializationHelper.serializeDomainList(listDomain, classId, includeFullDetails, userClassService));
    }
}
