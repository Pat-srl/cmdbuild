package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlMessageWsCommand;
import org.cmdbuild.utils.lang.CmConvertUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_VIEW_AUTHORITY;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.toDataSource;
import static org.cmdbuild.service.rest.common.serializationhelpers.WaterwaySerializer.serializeMessage;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT_ID;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("administration/etl/messages/")
@Tag(name = "ETL Messages", description = "ETL Messages")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
@Component
public class EtlMessageWs {

    private final EtlMessageWsCommand command;

    public EtlMessageWs(EtlMessageWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get ETL messages",
            description = "Get ETL messages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL messages"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMessages(
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        PagedElements<WaterwayMessage> messages = command.doReadMessages(wsQueryOptions);
        return response(messages.map(m -> serializeMessage(m, wsQueryOptions.isDetailed())));
    }

    @GET
    @Path("{messageReference}/")
    @Operation(
            summary = "Get ETL message",
            description = "Get ETL message",
            parameters = {
                    @Parameter(name = "messageReference", description = "Id of the ETL message", schema = @Schema(type = "string")),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL message"),
                    @ApiResponse(responseCode = "404", description = "ETL message not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("messageReference") @Parameter(description = "Reference of the message") String messageReference
    ) {
        WaterwayMessage message = command.doRead(messageReference);
        return response(serializeMessage(message, true));
    }

    @GET
    @Path("{messageReference}/attachments/{" + ATTACHMENT_ID + "}")
    @Operation(
            summary = "Get ETL message attachment",
            description = "Get ETL message attachment",
            parameters = {
                    @Parameter(name = "messageReference", description = "Id of the ETL message", schema = @Schema(type = "string")),
                    @Parameter(name = ATTACHMENT_ID, description = "Id of the ETL message attachment", schema = @Schema(type = "string")),
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL message attachment"),
                    @ApiResponse(responseCode = "404", description = "ETL message attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAttachment(
            @PathParam("messageReference") @Parameter(description = "Reference of the message") String messageReference,
            @PathParam(ATTACHMENT_ID) @Parameter(description = "Id of the attachment") String attachmentId
    ) {
        WaterwayMessageAttachment attachment = command.doReadAttachment(messageReference, attachmentId);
        return toDataSource(attachment);
    }

    @GET
    @Path("stats")
    @Operation(
            summary = "Get ETL messages statistics",
            description = "Get ETL messages statistics",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL messages statistics"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMessagesStats() {
        Map<WaterwayMessageStatus, Long> stats = command.doReadMessagesStats();
        return response(map(stats).mapKeys(CmConvertUtils::serializeEnum));
    }
}
