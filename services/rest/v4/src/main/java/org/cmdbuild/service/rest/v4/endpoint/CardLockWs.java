/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.service.rest.v4.command.CardLockWsCommand;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.lock.LockType.ILT_CARD;
import static org.cmdbuild.lock.LockTypeUtils.itemIdWithLockType;
import static org.cmdbuild.service.rest.common.serializationhelpers.ItemLockSerializationHelper.serializeLock;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.*;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/{" + TYPE_NAME + ":cards|instances}/{" + CARD_ID + "}/lock")
@Tag(name = "Card lock", description = "Operations related to card locks")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class CardLockWs {

    private final LockService lockService;
    private final SessionService sessionService;
    private final CoreConfiguration coreConfig;
    private final CardLockWsCommand command;

    public CardLockWs(LockService lockService, SessionService sessionService, CoreConfiguration coreConfig, CardLockWsCommand command) {
        this.lockService = checkNotNull(lockService);
        this.sessionService = checkNotNull(sessionService);
        this.coreConfig = checkNotNull(coreConfig);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get lock for a card",
            description = "Get lock for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of lock data"),
                    @ApiResponse(responseCode = "404", description = "Lock not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getLock(
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to query") Long cardId
    ) {
        //TODO authorize card lock access
        ItemLock lock = command.doGetLock(cardId);
        if (lock == null) {
            return success().with("found", false);
        } else {
            return response(serializeLock(lock, sessionService)).with("found", true);
        }
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create lock for a card",
            description = "Create lock for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"})),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of lock data"),
                    @ApiResponse(responseCode = "409", description = "Lock already exists"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object createLock(
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to lock") Long cardId
    ) {
        //TODO authorize card lock create
        LockResponse lockResponse = command.doCreateLock(cardId);
        if (lockResponse.isAquired()) {
            return response(serializeLock(lockResponse.getLock(), sessionService));
        } else {
            if (coreConfig.getCardlockShowUser()) {
                String username = sessionService.getSessionById(lockService.getLock(itemIdWithLockType(ILT_CARD, cardId)).getSessionId()).getOperationUser().getUsername();
                return failure().with("user", username);
            } else {
                return failure();
            }
        }
    }

    @DELETE
    @Path("")
    @Operation(
            summary = "Release lock for a card",
            description = "Release lock for a card",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = TYPE_NAME, in = ParameterIn.PATH, description = "instances or cards", schema = @Schema(allowableValues = {"instances", "cards"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful release of lock data"),
                    @ApiResponse(responseCode = "404", description = "Lock not found"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object releaseLock(
            @PathParam(CARD_ID) @Parameter(description = "Id of the card to release the lock") Long cardId
    ) {
        //TODO authorize card lock delete
        command.doReleaseLock(cardId);
        return success();
    }
}
