package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("domains/{domainId}/relations")
@Tag(name = "History", description = "Operations related to history of relations")
@Produces(APPLICATION_JSON)
public class RelationHistoryWs {

    private final org.cmdbuild.service.rest.v4.endpoint.RelationHistoryWs relationHistoryWs;

    public RelationHistoryWs(org.cmdbuild.service.rest.v4.endpoint.RelationHistoryWs relationHistoryWs) {
        this.relationHistoryWs = checkNotNull(relationHistoryWs);
    }

    @GET
    @Path("history/{relationId}")
    @Operation(
            summary = "Get relation history record",
            description = "Get relation history record",
            parameters = {
                    @Parameter(name = DOMAIN_ID, in = ParameterIn.PATH, description = "Domain ID"),
                    @Parameter(name = CARD_ID, in = ParameterIn.PATH, description = "Card ID"),
                    @Parameter(name = RELATION_ID, in = ParameterIn.PATH, description = "Relation history record ID")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of relation history record"),
                    @ApiResponse(responseCode = "404", description = "Relation history record not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getRelationHistoryRecord(
            @PathParam(DOMAIN_ID) String domainId,
            @PathParam(CARD_ID) Long id,
            @PathParam(RELATION_ID) Long relationId
    ) {
        return relationHistoryWs.getRelationHistoryRecord(domainId, id, relationId);
    }

}
