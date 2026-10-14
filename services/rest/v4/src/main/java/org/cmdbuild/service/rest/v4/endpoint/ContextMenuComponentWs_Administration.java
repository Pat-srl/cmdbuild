/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.v4.command.ContextMenuComponentWsCommand;
import org.cmdbuild.service.rest.v4.model.WsContextMenuComponentData;
import org.cmdbuild.service.rest.v4.serializationhelpers.UiComponentInfoSerializationHelper;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.springframework.stereotype.Component;

import java.util.List;

import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ID;
import static org.cmdbuild.service.rest.v4.serializationhelpers.UiComponentInfoSerializationHelper.serializeInfo;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Path("administration/components/contextmenu")
@Tag(name = "ContextMenuComponent", description = "ContextMenuComponent")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ContextMenuComponentWs_Administration {

    private final ContextMenuComponentWsCommand command;

    public ContextMenuComponentWs_Administration(ContextMenuComponentWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all context menu components",
            description = "Get all context menu components",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object list() {
        List<UiComponentInfo> uiComponentInfoList = command.doList();
        return response(uiComponentInfoList.stream().map(UiComponentInfoSerializationHelper::serializeInfo));
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get context menu component by id",
            description = "Get context menu component by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "ContextMenuComponent not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object get(
            @PathParam(ID) @Parameter(description = "Id of ContextMenuComponent to query") Long id
    ) {
        UiComponentInfo customMenuComponent = command.doGet(id);
        return response(serializeInfo(customMenuComponent));
    }

    @DELETE
    @Path("{id}")
    @Operation(
            summary = "Delete context menu component by id",
            description = "Delete context menu component by id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "ContextMenuComponent not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(ID) @Parameter(description = "Id of ContextMenuComponent to delete") Long id
    ) {
        command.doDelete(id);
        return success();
    }

    @DELETE
    @Path("{id}/{targetDevice}")
    @Operation(
            summary = "Delete context menu component for target device",
            description = "Delete context menu component for target device",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "ContextMenuComponent not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object deleteForTargetDevice(
            @PathParam(ID) @Parameter(description = "Id of ContextMenuComponent to delete") Long id,
            @PathParam("targetDevice") @Parameter(description = "Target device") TargetDevice targetDevice
    ) {
        command.doDeleteForTargetDevice(id, targetDevice);
        return success();
    }

    @GET
    @Path("{id}/{targetDevice}/{file}|{id}/{targetDevice}")
    @Operation(
            summary = "Download context menu component for target device",
            description = "Download context menu component for target device",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "ContextMenuComponent not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public DataHandler download(
            @PathParam(ID) @Parameter(description = "Id of ContextMenuComponent to download") Long id,
            @PathParam("targetDevice") @Parameter(description = "Target device") TargetDevice targetDevice
    ) {
        return command.doDownload(id, targetDevice);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create new context menu component",
            description = "Create new context menu component",
            requestBody = @RequestBody(description = "Multipart body: the 'data' part holds the component data (json), any other part is a component file", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsContextMenuComponentData.class)),
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
            })),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(
            @Multipart("-data") List<DataHandler> files,
            @QueryParam("merge") @DefaultValue(FALSE) @Parameter(description = "Merge existing component with the same name") Boolean merge,
            @Multipart(value = "data|DEFAULT", required = false) WsContextMenuComponentData data
    ) {
        UiComponentInfo uiComponentInfo = command.doCreate(files, merge, data);
        return response(serializeInfo(uiComponentInfo));
    }

    @PUT
    @Path("{id}")
    @Operation(
            summary = "Update context menu component by id",
            description = "Update context menu component by id",
            requestBody = @RequestBody(description = "Multipart body: the 'data' part holds the component data (json), any other part is a component file", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsContextMenuComponentData.class)),
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
            })),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "ContextMenuComponent not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(ID) @Parameter(description = "Id of ContextMenuComponent to update") Long id,
            @Multipart("-data") List<DataHandler> files,
            @Multipart(value = "data|DEFAULT", required = false) WsContextMenuComponentData data
    ) {
        UiComponentInfo contextMenuComponent = command.doUpdate(id, files, data);
        return response(serializeInfo(contextMenuComponent));
    }
}
