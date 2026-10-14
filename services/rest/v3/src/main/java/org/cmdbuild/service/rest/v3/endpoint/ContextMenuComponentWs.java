package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.service.rest.v4.endpoint.ContextMenuComponentWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ContextMenuComponentWs_Management;
import org.cmdbuild.service.rest.v4.model.WsContextMenuComponentData;
import org.cmdbuild.ui.TargetDevice;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_UICOMPONENTS_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;

@Path("components/contextmenu")
@Tag(name = "ContextMenuComponent", description = "ContextMenuComponent")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ContextMenuComponentWs {

    private final ContextMenuComponentWs_Administration contextMenuComponentWs_adm;
    private final ContextMenuComponentWs_Management contextMenuComponentWs_mng;

    public ContextMenuComponentWs(ContextMenuComponentWs_Administration contextMenuComponentWs_adm, ContextMenuComponentWs_Management contextMenuComponentWs_mng) {
        this.contextMenuComponentWs_adm = checkNotNull(contextMenuComponentWs_adm);
        this.contextMenuComponentWs_mng = checkNotNull(contextMenuComponentWs_mng);
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get context menu component by id",
            description = "Get context menu component by id",
            responses = {@ApiResponse(responseCode = "200", description = "Successful operation")},
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object get(@PathParam("id") Long id) {
        return contextMenuComponentWs_mng.get(id);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all context menu components",
            description = "Get all context menu components",
            responses = {@ApiResponse(responseCode = "200", description = "Successful operation")},
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_VIEW_AUTHORITY)
    public Object list() {
        return contextMenuComponentWs_adm.list();
    }

    @DELETE
    @Path("{id}")
    @Operation(
            summary = "Delete context menu component by id",
            description = "Delete context menu component by id",
            responses = {@ApiResponse(responseCode = "200", description = "Successful operation")},
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object delete(@PathParam("id") Long id) {
        return contextMenuComponentWs_adm.delete(id);
    }

    @DELETE
    @Path("{id}/{targetDevice}")
    @Operation(
            summary = "Delete context menu component for target device",
            description = "Delete context menu component for target device",
            parameters = {
                    @Parameter( name = "id", in = ParameterIn.PATH, description = "ID of the context menu component to delete" ),
                    @Parameter( name = "targetDevice", in = ParameterIn.PATH, description = "Target device for which to delete the context menu component" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Context menu component not found for the specified target device"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object deleteForTargetDevice(
            @PathParam("id") Long id,
            @PathParam("targetDevice") TargetDevice targetDevice
    ) {
        return contextMenuComponentWs_adm.deleteForTargetDevice(id, targetDevice);
    }

    @GET
    @Path("{id}/{targetDevice}/{file}|{id}/{targetDevice}")
    @Operation(
            summary = "Download context menu component for target device",
            description = "Download context menu component for target device",
            parameters = {
                    @Parameter( name = "id", in = ParameterIn.PATH, description = "ID of the context menu component to download" ),
                    @Parameter( name = "targetDevice", in = ParameterIn.PATH, description = "Target device for which to download the context menu component" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Context menu component not found for the specified target device"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(
            @PathParam("id") Long id,
            @PathParam("targetDevice") TargetDevice targetDevice
    ) {
        return contextMenuComponentWs_adm.download(id, targetDevice);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create new context menu component",
            description = "Create new context menu component",
            parameters = {
                    @Parameter(name = "-data", description = "Files to upload for the context menu component"),
                    @Parameter(name = "merge", in = ParameterIn.QUERY, description = "Whether to merge the uploaded files with existing files"),
                    @Parameter(name = "data", in = ParameterIn.QUERY, description = "Data for the context menu component to create")
            },
            responses = {@ApiResponse(responseCode = "200", description = "Successful operation")},
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object create(
            @Multipart("-data") List<DataHandler> files,
            @QueryParam("merge") @DefaultValue(FALSE) Boolean merge,
            @Multipart(value = "data|DEFAULT", required = false) WsContextMenuComponentData data
    ) {
        return contextMenuComponentWs_adm.create(files, merge, data);
    }

    @PUT
    @Path("{id}")
    @Operation(
            summary = "Update context menu component by id",
            description = "Update context menu component by id",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the context menu component to update"),
                    @Parameter(name = "-data", description = "Files to upload for the context menu component"),
                    @Parameter(name = "data", in = ParameterIn.QUERY, description = "Data for the context menu component to update")
            },
            responses = {@ApiResponse(responseCode = "200", description = "Successful operation")},
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_UICOMPONENTS_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("id") Long id,
            @Multipart("-data") List<DataHandler> files,
            @Multipart(value = "data|DEFAULT", required = false) WsContextMenuComponentData data
    ) {
        return contextMenuComponentWs_adm.update(id, files, data);
    }
}
