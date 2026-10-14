/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import com.google.common.collect.Ordering;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.common.CmContentInfo;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadItemInfo;
import org.cmdbuild.service.rest.common.serializationhelpers.EasyUploadItemSerializer;
import org.cmdbuild.service.rest.v4.command.UploadWsCommand;
import org.cmdbuild.service.rest.v4.model.WsUploadData;
import org.cmdbuild.temp.TempInfo;
import org.cmdbuild.temp.TempService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.serializationhelpers.EasyUploadItemSerializer.serializeItem;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.services.serialization.TempInfoSerializer.serializeTempInfo;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

/**
 * @author ldare
 */
@Path("administration/{a:uploads|downloads}/")
@Tag( name = "Upload or Download", description = "Upload or download files")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class UploadWs_Administration {

    private final TempService tempService;
    private final UploadWsCommand command;

    public UploadWs_Administration(TempService tempService, UploadWsCommand command) {
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
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
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
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public DataHandler downloadFile(
            @PathParam("fileId") @Parameter(description = "Id of upload to query") String fileId
    ) {
        return command.doDownloadFile(fileId);
    }

    @POST
    @Path("_TEMP")
    @Operation(
            requestBody = @RequestBody(description = "The file to upload as temporary file", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "file", schema = @Schema(type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED))
                    })),
            summary = "Create temporary file",
            description = "Create temporary file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of temporary file"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
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
    public Object deleteTempFile(
            @PathParam("tempId") @Parameter(description = "Id of temporary file to delete") String tempId
    ) {
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
    public Object deleteFile(
            @PathParam("fileId") @Parameter(description = "Id of upload to delete") String fileId
    ) {
        command.doDeleteFile(fileId);
        return success();
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get uploads",
            description = "Get uploads",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of uploads data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object readMany(
            @QueryParam("path") @Parameter(description = "Path of the folder to query") @Nullable String dir
    ) {
        List<EasyuploadItemInfo> list = command.doReadMany(dir);
        return response(list.stream()
                .sorted(Ordering.natural().onResultOf(EasyuploadItemInfo::getPath))
                .map(EasyUploadItemSerializer::serializeItem));
    }

    @GET
    @Path("_MANY/{file:.+.zip}")
    @Operation(
            summary = "Download multiple files",
            description = "Download multiple files as a zip file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of upload data"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public DataHandler downloadManyFiles(
            @QueryParam("path") @Parameter(description = "Path of the folder to query") String dir
    ) {
        return command.doDownloadManyFiles(dir);
    }

    @GET
    @Path("_ALL/{file:.+.zip}")
    @Operation(
            summary = "Download all files",
            description = "Download all files as a zip file",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of upload data"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public DataHandler downloadAllFiles() {
        return command.doDownloadAllFiles();
    }

    @POST
    @Path("")
    @Operation(
            requestBody = @RequestBody(description = "The file to upload, with optional upload metadata and destination path", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "file", schema = @Schema(type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED)),
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsUploadData.class)),
                    @SchemaProperty(name = "path", schema = @Schema(type = "string"))
                    })),
            summary = "Create upload",
            description = "Create upload",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of upload data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object create(
            @Multipart("file") DataHandler dataHandler,
            @QueryParam("path") @Parameter(description = "Path of the folder to query") String pathFromQuery,
            @Multipart(value = "data", required = false) @Nullable WsUploadData uploadData,
            @Multipart(value = "path", required = false) String pathFromMultipart,
            @QueryParam("temp") @Parameter(description = "Create a temporary file") @DefaultValue(FALSE) Boolean temp,
            @QueryParam("overwrite_existing") @Parameter(description = "Overwrite existing file") @DefaultValue(FALSE) Boolean overwriteExisting
    ) {
        if (temp == true) {
            return createTempFile(dataHandler);
        } else {
            EasyuploadItem item = command.doCreate(dataHandler, pathFromQuery, uploadData, pathFromMultipart, overwriteExisting);
            return response(serializeItem(item));
        }
    }

    @POST
    @Path("_MANY")
    @Operation(
            requestBody = @RequestBody(description = "Zip file to load", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "file", schema = @Schema(type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED))
                    })),
            summary = "Upload multiple files",
            description = "Upload multiple files",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful upload of multiple files"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object loadZipFile(@Multipart("file") DataHandler dataHandler) {
        command.doLoadZipFile(dataHandler);
        return success();
    }

    @PUT
    @Path("{fileId}")
    @Operation(
            requestBody = @RequestBody(description = "Replacement file (optional) and updated upload metadata (optional)", required = false,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "file", schema = @Schema(type = "string", format = "binary")),
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsUploadData.class))
                    })),
            summary = "Update upload",
            description = "Update upload",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of upload data"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object update(
            @PathParam("fileId") @Parameter(description = "Id of upload to update") Long fileId,
            @Multipart(value = "file", required = false) @Nullable DataHandler dataHandler,
            @Multipart(value = "data", required = false) @Nullable WsUploadData uploadData
    ) {
        EasyuploadItem item = command.doUpdate(fileId, dataHandler, uploadData);
        return response(serializeItem(item));
    }
}