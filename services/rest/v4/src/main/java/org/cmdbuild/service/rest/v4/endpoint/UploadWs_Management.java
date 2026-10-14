/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


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
import org.cmdbuild.common.CmContentInfo;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.service.rest.v4.command.UploadWsCommand;
import org.cmdbuild.temp.TempInfo;
import org.cmdbuild.temp.TempService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.serializationhelpers.EasyUploadItemSerializer.serializeItem;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.services.serialization.TempInfoSerializer.serializeTempInfo;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Path("{a:uploads|downloads}/")
@Tag( name = "Upload or Download", description = "Upload or download files")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class UploadWs_Management {

    private final TempService tempService;
    private final UploadWsCommand command;

    public UploadWs_Management(TempService tempService, UploadWsCommand command) {
        this.tempService = checkNotNull(tempService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{fileId}")
    @Operation(
            summary = "Get upload",
            description = "Get upload",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of upload data"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readFile(
            @PathParam("fileId") @Parameter(description = "Id of upload to query") String fileId
    ) {
        CmContentInfo cmContentInfo = command.doReadFile(fileId);

        if (cmContentInfo instanceof TempInfo tempInfo) {
            return response(map("_id", fileId).with(serializeTempInfo(tempInfo)));
        } else {
            EasyuploadItem easyuploadItem = (EasyuploadItem) cmContentInfo;
            return response(serializeItem(easyuploadItem));
        }
    }

    @GET
    @Path("{fileId}/{file}")
    @Operation(
            summary = "Download file",
            description = "Download file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of upload data"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadFile(
            @PathParam("fileId") @Parameter(description = "Id of upload to query") String fileId
    ) {
        return command.doDownloadFile(fileId);
    }

    @POST
    @Path("_TEMP")
    @Operation(
            summary = "Create temporary file",
            description = "Create temporary file",
            requestBody = @RequestBody(description = "Multipart body: the file to upload as temporary file", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "file", schema = @Schema(type = "string", format = "binary"))
            })),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of temporary file"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object createTempFile(
            @Multipart("file") DataHandler dataHandler
    ) {
        String tempId = tempService.putTempData(dataHandler);
        TempInfo tempInfo = tempService.getTempInfo(tempId);
        return response(map("_id", tempId).with(serializeTempInfo(tempInfo)));
    }

    @DELETE
    @Path("_TEMP/{tempId}")
    @Operation(
            summary = "Delete temporary file",
            description = "Delete temporary file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of temporary file"),
                    @ApiResponse(responseCode = "404", description = "Temporary file not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteTempFile(@PathParam("tempId") @Parameter(description = "Id of temporary file to delete") String tempId) {
        command.doDeleteTempFile(tempId);
        return success();
    }

    @DELETE
    @Path("{fileId}")
    @Operation(
            summary = "Delete upload",
            description = "Delete upload",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of upload data"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object deleteFile(@PathParam("fileId") @Parameter(description = "Id of upload to delete") String fileId) {
        command.doDeleteFile(fileId);
        return success();
    }
}
