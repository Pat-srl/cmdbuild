/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.command.ContextMenuComponentWsCommand;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import static org.cmdbuild.service.rest.v4.serializationhelpers.UiComponentInfoSerializationHelper.serializeInfo;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Path("components/contextmenu/")
@Tag(name = "ContextMenuComponent", description = "ContextMenuComponent")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ContextMenuComponentWs_Management {

    private final ContextMenuComponentWsCommand command;

    public ContextMenuComponentWs_Management(ContextMenuComponentWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get context menu component by id",
            description = "Get context menu component by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Component not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object get(
            @PathParam("id") @Parameter(description = "Id of component to query") Long id
    ) {
        UiComponentInfo customMenuComponent = command.doGet(id);
        return response(serializeInfo(customMenuComponent));
    }

    @GET
    @Path("{id}/{targetDevice}/{file}|{id}/{targetDevice}")
    @Operation(
            summary = "Download context menu component for target device",
            description = "Download context menu component for target device",
            responses = {@ApiResponse(responseCode = "200", description = "Successful operation")},
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(
            @PathParam("id") @Parameter(description = "Id of the context menu component") Long id,
            @PathParam("targetDevice") @Parameter(description = "Target device of the component (default, mobile)") TargetDevice targetDevice
    ) {
        return command.doDownload(id, targetDevice);
    }
}
