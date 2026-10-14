/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.corecomponents.CoreComponent;
import org.cmdbuild.corecomponents.CoreComponentService;
import org.cmdbuild.service.rest.v4.command.CoreComponentWsCommand;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CODE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE;
import static org.cmdbuild.services.serialization.CoreComponentSerializationHelper.applySerializationToCoreComponent;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("components/core/{"+TYPE+"}/")
@Tag(name = "Core Components")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CoreComponentWs_Management {

    private final CoreComponentService coreComponentService;
    private final CoreComponentWsCommand command;

    public CoreComponentWs_Management(CoreComponentService coreComponentService, CoreComponentWsCommand command) {
        this.coreComponentService = checkNotNull(coreComponentService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{"+CODE+"}")
    @Operation(
            summary = "Get a core component",
            description = "Get a core component by its code",
            parameters = {
                    @Parameter(name = TYPE, description = "Type of the core component", schema = @Schema(type = "string")),
                    @Parameter(name = CODE, description = "Code of the core component", schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of core component data"),
                    @ApiResponse(responseCode = "404", description = "The core component was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object get(
            @PathParam(CODE) @Parameter(description = "Code of the core component", schema = @Schema(type = "string")) String code
    ) {
        CoreComponent coreComponent = command.doGet(code, coreComponentService::getActiveComponent);
        return response(applySerializationToCoreComponent(coreComponent));
    }
}
