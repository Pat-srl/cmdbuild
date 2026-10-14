package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ETL_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ATTACHMENT_ID;

@Path("etl/messages/")
@Tag(name = "ETL Messages", description = "ETL Messages")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ETL_VIEW_AUTHORITY)
public class EtlMessageWs {

    private final org.cmdbuild.service.rest.v4.endpoint.EtlMessageWs etlMessageWs;

    public EtlMessageWs(org.cmdbuild.service.rest.v4.endpoint.EtlMessageWs etlMessageWs) {
        this.etlMessageWs = checkNotNull(etlMessageWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get ETL messages",
            description = "Get ETL messages",
            requestBody = @RequestBody(description = "Query options", content = @Content(schema = @Schema(implementation = WsQueryOptions.class))),
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of ETL messages")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMessages(
            WsQueryOptions wsQueryOptions
    ) {
        return etlMessageWs.readMessages(wsQueryOptions);
    }

    @GET
    @Path("{messageReference}/")
    @Operation(
            summary = "Get ETL message",
            description = "Get ETL message",
            parameters = { @Parameter(name = "messageReference", in = ParameterIn.PATH, description = "Reference of the ETL message to retrieve", required = true)},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL message"),
                    @ApiResponse(responseCode = "404", description = "ETL message not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam("messageReference") String messageReference
    ) {
        return etlMessageWs.read(messageReference);
    }

    @GET
    @Path("{messageReference}/attachments/{" + ATTACHMENT_ID + "}")
    @Operation(
            summary = "Get ETL message attachment",
            description = "Get ETL message attachment",
            parameters = {
                    @Parameter(name = "messageReference", in = ParameterIn.PATH, description = "Reference of the ETL message", required = true),
                    @Parameter(name = ATTACHMENT_ID, in = ParameterIn.PATH, description = "ID of the attachment to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL message attachment"),
                    @ApiResponse(responseCode = "404", description = "ETL message or attachment not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAttachment(
            @PathParam("messageReference") String messageReference,
            @PathParam(ATTACHMENT_ID) String attachmentId
    ) {
        return etlMessageWs.readAttachment(messageReference, attachmentId);
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
        return etlMessageWs.readMessagesStats();
    }
}
