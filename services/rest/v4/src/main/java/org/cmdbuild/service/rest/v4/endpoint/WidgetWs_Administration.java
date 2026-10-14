/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.v4.command.WidgetWsCommand;
import org.cmdbuild.service.rest.v4.model.WsWidgetComponentData;
import org.cmdbuild.service.rest.v4.serializationhelpers.UiComponentInfoSerializationHelper;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.widget.WidgetComponentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.v4.serializationhelpers.UiComponentInfoSerializationHelper.serializeInfo;

/**
 * @author ldare
 */
@Path("administration/components/widget")
@Tags({
        @Tag( name = "Widget Components", description = "APIs to manage widgets." ),
        @Tag( name = "Administration" )
})
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class WidgetWs_Administration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WidgetComponentService widgetComponentService;
    private final WidgetWsCommand command;

    public WidgetWs_Administration(WidgetComponentService widgetComponentService, WidgetWsCommand command) {
        this.widgetComponentService = checkNotNull(widgetComponentService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "List all widgets",
            description = "List all widgets",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of widgets"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object list() {//TODO fix this, make admin only (??); see ContextMenuComponentWs
        logger.debug("list all widget components for current user");
        List<UiComponentInfo> list = command.doList(widgetComponentService::getAll);
        return response(list.stream().map(UiComponentInfoSerializationHelper::serializeInfo));
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get a specific widget",
            description = "Get a specific widget",
            parameters = {
                    @Parameter(name = "id", description = "Id of the widget to query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of widget"),
                    @ApiResponse(responseCode = "404", description = "The widget was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object get(
            @PathParam("id") @Parameter(description = "Id of the widget") Long id
    ) {
        UiComponentInfo customMenuComponent = command.doGet(id);
        return response(serializeInfo(customMenuComponent));
    }

    @GET
    @Path("{id}/{version}/{file}|{id}/{version}")
    @Operation(
            summary = "Download a specific widget version",
            description = "Download a specific widget version",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful download of widget version"),
                    @ApiResponse(responseCode = "404", description = "The widget was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public DataHandler download(
            @PathParam("id") @Parameter(description = "Id of the widget to query") Long id,
            @PathParam("version") @Parameter(description = "Version of the widget to query", schema = @Schema(implementation = TargetDevice.class)) TargetDevice targetDevice
    ) {
        return command.doDownload(id, targetDevice);
    }

    @DELETE
    @Path("{id}")
    @Operation(
            summary = "Delete a specific widget",
            description = "Delete a specific widget",
            parameters = {
                    @Parameter(name = "id", description = "Id of the widget to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of widget"),
                    @ApiResponse(responseCode = "404", description = "The widget was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("id") @Parameter(description = "Id of the widget") Long id
    ) {
        widgetComponentService.delete(id);
        return success();
    }

    @DELETE
    @Path("{id}/{targetDevice}")
    @Operation(
            summary = "Delete a specific widget for a specific target device",
            description = "Delete a specific widget for a specific target device",
            parameters = {
                    @Parameter(name = "id", description = "Id of the widget to delete"),
                    @Parameter(name = "targetDevice", description = "Target device of the widget to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of widget"),
                    @ApiResponse(responseCode = "404", description = "The widget was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object deleteForTargetDevice(
            @PathParam("id") @Parameter(description = "Id of the widget") Long id,
            @PathParam("targetDevice") @Parameter(description = "Target device of the widget (default, mobile)", schema = @Schema(implementation = TargetDevice.class)) TargetDevice targetDevice
    ) {
        command.doDeleteForTargetDevice(id, targetDevice);
        return success();
    }

    @POST
    @Path(EMPTY)
    @Operation(
            requestBody = @RequestBody(description = "Widget definition (optional 'data' part, JSON) and the files composing the widget (all other parts)", required = false,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsWidgetComponentData.class)),
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
                    })),
            summary = "Create a new widget",
            description = "Create a new widget",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of widget"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(
            @Multipart("-data")  List<DataHandler> files,
            @Multipart(value = "data|DEFAULT", required = false) WsWidgetComponentData data,
            @QueryParam("merge") @Parameter(description = "Merge with existing widget if true, otherwise create a new one") @DefaultValue(FALSE) Boolean merge
    ) {
        UiComponentInfo info = command.doCreate(files, data, merge);
        return response(serializeInfo(widgetComponentService.update(info)));
    }

    @PUT
    @Path("{id}")
    @Operation(
            requestBody = @RequestBody(description = "Widget definition (optional 'data' part, JSON) and the files composing the widget (all other parts)", required = false,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsWidgetComponentData.class)),
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
                    })),
            summary = "Update an existing widget",
            description = "Update an existing widget",
            parameters = {
                    @Parameter(name = "id", description = "Id of the widget to update")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of widget"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("id") @Parameter(description = "Id of the widget") Long id,
            @Multipart("-data")  @Parameter(array = @ArraySchema(schema = @Schema(implementation = DataHandler.class))) List<DataHandler> files,
            @Multipart(value = "data|DEFAULT", required = false) @Parameter(schema = @Schema(implementation = WsWidgetComponentData.class)) WsWidgetComponentData data
    ) {
        UiComponentInfo component = command.doUpdate(id, files, data);
        return response(serializeInfo(component));
    }
}
