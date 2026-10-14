/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.bim.BimObject;
import org.cmdbuild.bim.BimProjectExt;
import org.cmdbuild.service.rest.v4.endpoint.BimProjectWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.BimProjectWs_Management;
import org.cmdbuild.service.rest.v4.model.WsProjectData;

import java.io.IOException;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_BIM_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILE;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("bim/projects/")
@Tag( name = "BIM", description = "BIM Projects" )
@Produces(APPLICATION_JSON)
public class BimProjectWs {

    private final BimProjectWs_Administration bimProjectWs_adm;
    private final BimProjectWs_Management bimProjectWs_mng;

    public BimProjectWs(BimProjectWs_Administration bimProjectWs_adm, BimProjectWs_Management bimProjectWs_mng) {
        this.bimProjectWs_adm = checkNotNull(bimProjectWs_adm);
        this.bimProjectWs_mng = checkNotNull(bimProjectWs_mng);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get projects and objects",
            description = "Returns sorted serialized projects and objects",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = BimProjectExt.class)))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} ) }
    )
    public Object getAll() {
        return bimProjectWs_mng.getAll();
    }

    /**
     * Gets BIM object by project and global ID
     */
    @GET
    @Path("{projectId}/values/{globalId}")
    @Operation(
            summary = "Gets BIM object by project and global ID",
            description = "Gets BIM object by project and global ID",
            parameters = {
                    @Parameter(name = "projectId", description = "Id of Project", required = true),
                    @Parameter(name = "globalId", description = "Global Id", required = true),
                    @Parameter(name = "if_exists", description = "If exists", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = BimObject.class))),
                    @ApiResponse(responseCode = "404", description = "BIM object not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getValue(
            @PathParam("projectId") @Parameter(description = "Id of Project") Long projectId,
            @PathParam("globalId") @Parameter(description = "Global Id") String globalId,
            @QueryParam("if_exists") @DefaultValue(FALSE) Boolean ifExists
    ) {
        return bimProjectWs_mng.getValue(projectId, globalId, ifExists);
    }

    @GET
    @Path("{id}")
    @Operation(
            summary = "Get project by id",
            description = "Returns serialized project by id",
            parameters = {
                    @Parameter(name = "id", description = "Id of Project to query", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = BimProjectExt.class))),
                    @ApiResponse(responseCode = "404", description = "Project not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public Object getOne(
            @PathParam("id") Long id
    ) {
        return bimProjectWs_mng.getOne(id);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create new project",
            description = "Create new project",
            requestBody = @RequestBody(description = "Project data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsProjectData.class))),
            parameters = {
                    @Parameter(name = "file", description = "File to upload", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = BimProjectExt.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_BIM_MODIFY_AUTHORITY)
    public Object createProjectWithFile(
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            WsProjectData data
    ) {
        return bimProjectWs_adm.createProjectWithFile(dataHandler, data);
    }

    @PUT
    @Path("{id}")
    @Operation(
            summary = "Update project",
            description = "Update project",
            parameters = {
                    @Parameter(name = "id", description = "Id of Project to update", required = true),
                    @Parameter(name = "file", description = "File to upload", required = false)
            },
            requestBody = @RequestBody(description = "Project data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = WsProjectData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful Operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = BimProjectExt.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_BIM_MODIFY_AUTHORITY)
    public Object update(
            @PathParam("id") Long id,
            @Multipart(value = FILE, required = false) DataHandler dataHandler,
            WsProjectData data
    ) {
        return bimProjectWs_adm.update(id, dataHandler, data);
    }

    @GET
    @Path("{id}/file")
    @Operation(
            summary = "Download project file",
            description = "Download project file",
            parameters = {
                    @Parameter(name = "id", description = "Id of Project to query", required = true),
                    @Parameter(name = "ifcFormat", description = "Ifc format", required = false),
                    @Parameter(name = "bimFormat", description = "Bim format", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = "application/octet-stream")),
                    @ApiResponse(responseCode = "404", description = "The project file was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    public DataHandler downloadIfcFile(
            @PathParam("id") Long id,
            @QueryParam("ifcFormat") @Nullable String ifcFormat,
            @QueryParam("bimFormat") String bimFormat
    ) {
        return bimProjectWs_mng.downloadIfcFile(id, ifcFormat, bimFormat);
    }

    @POST
    @Path("{id}/file")
    @Operation(
            summary = "Upload project file",
            description = "Upload project file",
            parameters = {
                    @Parameter(name = "id", description = "Id of Project to query", required = true),
                    @Parameter(name = "file", description = "File to upload", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON)),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_BIM_MODIFY_AUTHORITY)
    public Object uploadIfcFile(
            @PathParam("id") Long id,
            @Multipart(FILE) DataHandler dataHandler
    ) throws IOException {
        return bimProjectWs_adm.uploadIfcFile(id, dataHandler);
    }

    @DELETE
    @Path("{id}")
    @Operation(
            summary = "Delete project",
            description = "Delete project",
            parameters = {
                    @Parameter(name = "id", description = "Id of Project to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON)),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @RolesAllowed(ADMIN_BIM_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam("id") @Parameter(description = "Id of Project to delete") Long id
    ) {
        return bimProjectWs_adm.delete(id);
    }
}
