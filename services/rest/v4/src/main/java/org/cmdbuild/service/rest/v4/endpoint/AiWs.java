/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.ai.core.api.AiResponse;
import org.cmdbuild.service.rest.v4.command.AiWsCommand;
import org.cmdbuild.service.rest.v4.model.WsAiRequest;
import org.cmdbuild.services.serialization.AiSerializationHelper;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

/**
 * @author ldare
 */
@Path("/ai/")
@Tag(
        name = "Ai",
        description = "Ai endpoints"
)
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class AiWs {

    private final AiWsCommand command;
    private final AiSerializationHelper aiSerializationHelper;

    public AiWs(AiWsCommand command, AiSerializationHelper aiSerializationHelper) {
        this.command = command;
        this.aiSerializationHelper = aiSerializationHelper;
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Query an AI model",
            description = "Query an AI model",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful Ai query"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth"), @SecurityRequirement(name = "BearerAuth")}
    )
    public Object query(
            @RequestBody(description = "Request data", required = true, content = @Content(schema = @Schema(implementation = WsAiRequest.class, name = "WsAiRequest"))) WsAiRequest wsAiRequest
    ) {
        AiResponse aiResponse = command.query(wsAiRequest.getQuery(), wsAiRequest.getContext());
        return response(aiSerializationHelper.serializeAiResponse(aiResponse));
    }
}
