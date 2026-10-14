package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.RelationHistoryWsCommand;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CARD_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DOMAIN_ID;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;

@Path("domains/{" + DOMAIN_ID + "}/relations")
@Tag(name = "History", description = "Operations related to history of relations")
@Produces(APPLICATION_JSON)
@Component
public class RelationHistoryWs {

    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;
    private final RelationHistoryWsCommand command;

    public RelationHistoryWs(CardWsSerializationHelperv3 cardWsSerializationHelperv3, RelationHistoryWsCommand command) {
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("history/{" + RELATION_ID + "}")
    @Operation(
            summary = "Get relation history record",
            description = "Get relation history record",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of relation history record"),
                    @ApiResponse(responseCode = "404", description = "Relation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getRelationHistoryRecord(
            @PathParam(DOMAIN_ID) @Parameter(description = "Domain id") String domainId,
            @PathParam(CARD_ID) @Parameter(description = "Card id") Long id,
            @PathParam(RELATION_ID) @Parameter(description = "Relation id") Long relationId
    ) {
        CMRelation rel = command.doGetRelationHistoryRecord(domainId, relationId);
        return response(cardWsSerializationHelperv3.serializeDetailedRelation(rel).with(
                "_endDate", toIsoDateTime(rel.getEndDate()),
                "_status", rel.getCardStatus().name()));
    }

}
