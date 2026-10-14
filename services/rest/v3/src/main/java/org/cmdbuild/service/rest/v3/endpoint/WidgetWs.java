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
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.v4.endpoint.WidgetWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.WidgetWs_Management;
import org.cmdbuild.service.rest.v4.model.WsWidgetComponentData;
import org.cmdbuild.ui.TargetDevice;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;


@Path("components/widget")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Tag(name = "Widget", description = "Operations related to widgets")
public class WidgetWs {

    private final WidgetWs_Administration widgetWs_adm;
    private final WidgetWs_Management widgetWs_mng;

    public WidgetWs(WidgetWs_Administration widgetWs_adm, WidgetWs_Management widgetWs_mng) {
        this.widgetWs_adm = checkNotNull(widgetWs_adm);
        this.widgetWs_mng = checkNotNull(widgetWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all widgets",
            description = "Get all widgets. If the 'View-Mode' header is set to 'admin', all widgets will be returned, otherwise only widgets for the current target device will be returned",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode of the request, if set to 'admin' all widgets will be returned, otherwise only widgets for the current target device will be returned", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of widgets"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid view mode provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object list(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode
    ) {//TODO fix this, make admin only (??); see ContextMenuComponentWs
        if (isAdminViewMode(viewMode)) {
            return widgetWs_adm.list();
        }
        return widgetWs_mng.list();
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get a widget",
            description = "Get a widget by id",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the widget to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of widget data"),
                    @ApiResponse(responseCode = "404", description = "Widget not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object get(@PathParam("id") Long id) {
        return widgetWs_mng.get(id);
    }

    @DELETE
    @Path("{id}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a widget",
            description = "Delete a widget by id. This will delete the widget for all target devices",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the widget to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of widget"),
                    @ApiResponse(responseCode = "404", description = "Widget not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("id") Long id
    ) {
        return widgetWs_adm.delete(id);
    }

    @DELETE
    @Path("{id}/{targetDevice}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a widget for a target device",
            description = "Delete a widget for a target device. If the widget is not assigned to any other target device, the widget will be deleted completely",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the widget to delete", required = true),
                    @Parameter(name = "targetDevice", in = ParameterIn.PATH, description = "Target device for which the widget should be deleted", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of widget for target device"),
                    @ApiResponse(responseCode = "404", description = "Widget not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteForTargetDevice(
            @PathParam("id") Long id,
            @PathParam("targetDevice") TargetDevice targetDevice
    ) {
        return widgetWs_adm.deleteForTargetDevice(id, targetDevice);
    }

    @GET
    @Path("{id}/{version}/{file}|{id}/{version}")
    @Produces(APPLICATION_OCTET_STREAM)
    @Operation(
            summary = "Download a widget file",
            description = "Download a widget file for a target device. If the file parameter is not provided, the widget will be downloaded as a zip file containing all files for the widget and target device",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the widget to download", required = true),
                    @Parameter(name = "version", in = ParameterIn.PATH, description = "Target device for which the widget should be downloaded", required = true),
                    @Parameter(name = "file", in = ParameterIn.PATH, description = "Name of the file to download, if not provided the whole widget will be downloaded as a zip file", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful download of widget file"),
                    @ApiResponse(responseCode = "404", description = "Widget or file not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public DataHandler download(
            @PathParam("id") Long id,
            @PathParam("version") TargetDevice targetDevice
    ) {
        return widgetWs_mng.download(id, targetDevice);
    }

    @POST
    @Path(EMPTY)
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a widget",
            description = "Create a widget. The widget data should be provided as a multipart form data with the widget files and a JSON part named 'data' containing the widget data. If the 'merge' query parameter is set to true, the widget will be merged with an existing widget with the same name and target device, otherwise a new widget will be created. If a widget with the same name and target device already exists and 'merge' is not set to true, an error will be returned",
            parameters = {
                    @Parameter(name = "merge", in = ParameterIn.QUERY, description = "Whether to merge the widget with an existing widget with the same name and target device, if not set or set to false a new widget will be created and an error will be returned if a widget with the same name and target device already exists", required = false)
            },
            requestBody = @RequestBody(description = "Multipart form data containing the widget files and a JSON part named 'data' containing the widget data", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schema = @Schema(type = "object"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of widget"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid multipart data provided or widget with the same name and target device already exists and merge is not set to true"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @Multipart("-data") List<DataHandler> files,
            @Multipart(value = "data|DEFAULT", required = false) WsWidgetComponentData data,
            @QueryParam("merge") @DefaultValue(FALSE) Boolean merge
    ) {
        return widgetWs_adm.create(files, data, merge);
    }

    @PUT
    @Path("{id}")
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update a widget",
            description = "Update a widget. The widget data should be provided as a multipart form data with the widget files and a JSON part named 'data' containing the widget data. The widget with the specified id will be updated with the provided data and files. If a file with the same name as an existing file for the widget is provided, the existing file will be overwritten, otherwise the provided file will be added to the widget. If the 'data' part is not provided, only the files will be updated for the widget, otherwise both the widget data and files will be updated. If no widget with the specified id exists, an error will be returned",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id of the widget to update", required = true)
            },
            requestBody = @RequestBody(description = "Multipart form data containing the widget files and a JSON part named 'data' containing the widget data", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schema = @Schema(type = "object"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of widget"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid multipart data provided or widget with the same name and target device already exists when updating the widget name and target device"),
                    @ApiResponse(responseCode = "404", description = "Widget not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("id") Long id,
            @Multipart("-data") List<DataHandler> files,
            @Multipart(value = "data|DEFAULT", required = false) WsWidgetComponentData data
    ) {
        return widgetWs_adm.update(id, files, data);
    }
}
