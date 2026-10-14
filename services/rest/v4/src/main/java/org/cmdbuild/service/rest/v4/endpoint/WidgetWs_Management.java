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
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.command.WidgetWsCommand;
import org.cmdbuild.service.rest.v4.serializationhelpers.UiComponentInfoSerializationHelper;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.widget.WidgetComponentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.v4.serializationhelpers.UiComponentInfoSerializationHelper.serializeInfo;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Path("components/widget")
@Tag(name = "Widget Components")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class WidgetWs_Management {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WidgetComponentService widgetComponentService;
    private final WidgetWsCommand command;

    public WidgetWs_Management(WidgetComponentService widgetComponentService, WidgetWsCommand command) {
        this.widgetComponentService = checkNotNull(widgetComponentService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "List all widget components for current user",
            description = "Obtain a list of all widget components available for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of widget components"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object list() {//TODO fix this, make admin only (??); see ContextMenuComponentWs
        logger.debug("list all widget components for current user");
        List<UiComponentInfo> list = command.doList(widgetComponentService::getActiveForCurrentUserAndDevice);
        return response(list.stream().map(UiComponentInfoSerializationHelper::serializeInfo));
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get widget component by id",
            description = "Get a specific widget component by its id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of the widget component"),
                    @ApiResponse(responseCode = "404", description = "The widget component was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object get(
            @PathParam("id") @Parameter(description = "Id of the widget component to query") Long id
    ) {
        UiComponentInfo customMenuComponent = command.doGet(id);
        return response(serializeInfo(customMenuComponent));
    }

    @GET
    @Path("{id}/{version}/{file}|{id}/{version}")
    @Operation(
            summary = "Download widget component file",
            description = "Download a specific widget component file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful download of the widget component file"),
                    @ApiResponse(responseCode = "404", description = "The widget component file was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(
            @PathParam("id") @Parameter(description = "Id of the widget component to query") Long id,
            @PathParam("version") @Parameter(description = "Version of the widget component to query", schema = @Schema(implementation = TargetDevice.class)) TargetDevice targetDevice
    ) {
        return command.doDownload(id, targetDevice);
    }
}
