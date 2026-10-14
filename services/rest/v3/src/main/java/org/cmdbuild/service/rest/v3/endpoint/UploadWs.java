package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import org.cmdbuild.service.rest.v4.endpoint.UploadWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.UploadWs_Management;
import org.cmdbuild.service.rest.v4.model.WsUploadData;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.SYSTEM_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;

@Path("{procedure:uploads|downloads}/")
@Tag( name = "Upload or Download", description = "Upload or download files")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class UploadWs {

    private final UploadWs_Administration uploadWs_adm;
    private final UploadWs_Management uploadWs_mng;

    public UploadWs(UploadWs_Administration uploadWs_adm, UploadWs_Management uploadWs_mng) {
        this.uploadWs_adm = checkNotNull(uploadWs_adm);
        this.uploadWs_mng = checkNotNull(uploadWs_mng);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get uploads",
            description = "Get uploads",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"})),
                    @Parameter(name = "path", in = ParameterIn.QUERY, description = "Path to filter uploads. If not specified, all uploads will be returned", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of uploads data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object readMany(
            @QueryParam("path") @Nullable String dir
    ) {
        return uploadWs_adm.readMany(dir);
    }

    @GET
    @Path("{fileId}")
    @Operation(
            summary = "Get upload",
            description = "Get upload",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"})),
                    @Parameter(name = "fileId", in = ParameterIn.PATH, description = "Id of the upload to query", required = true, example = "1234567890")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of upload data"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readFile(
            @PathParam("fileId") String fileId
    ) {
        return uploadWs_mng.readFile(fileId);
    }

    @GET
    @Path("{fileId}/{file}")
    @Operation(
            summary = "Download file",
            description = "Download file",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"})),
                    @Parameter(name = "fileId", in = ParameterIn.PATH, description = "Id of the upload to query", required = true, example = "1234567890")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of upload data"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadFile(
            @PathParam("fileId") String fileId
    ) {
        return uploadWs_mng.downloadFile(fileId);
    }

    @GET
    @Path("_MANY/{file:.+.zip}")
    @Operation(
            summary = "Download multiple files",
            description = "Download multiple files as a zip file",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"})),
                    @Parameter(name = "path", in = ParameterIn.QUERY, description = "Path to filter uploads. If not specified, all uploads will be returned", required = false)
            },
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
            @QueryParam("path") String dir
    ) {
        return uploadWs_adm.downloadManyFiles(dir);
    }

    @GET
    @Path("_ALL/{file:.+.zip}")
    @Operation(
            summary = "Download all files",
            description = "Download all files as a zip file",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"}))
            },
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
        return uploadWs_adm.downloadAllFiles();
    }

    @POST
    @Path("_TEMP")
    @Operation(
            summary = "Create temporary file",
            description = "Create temporary file",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"}))
            },
            requestBody = @RequestBody(description = "File to upload", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of temporary file"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object createTempFile(
            @Multipart("file") DataHandler dataHandler
    ) {
        return uploadWs_mng.createTempFile(dataHandler);
    }

    @DELETE
    @Path("_TEMP/{tempId}")
    @Operation(
            summary = "Delete temporary file",
            description = "Delete temporary file",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"})),
                    @Parameter(name = "tempId", in = ParameterIn.PATH, description = "Id of the temporary file to delete", required = true, example = "1234567890")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of temporary file"),
                    @ApiResponse(responseCode = "404", description = "Temporary file not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteTempFile(
            @PathParam("tempId") String tempId
    ) {
        return uploadWs_mng.deleteTempFile(tempId);
    }

    @POST
    @Path("")
    @Operation(
            summary = "Create upload",
            description = "Create upload",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"})),
                    @Parameter(name = "path", in = ParameterIn.QUERY, description = "Path to save the uploaded file. If not specified, the file will be saved in the root directory", required = false),
                    @Parameter(name = "data", in = ParameterIn.QUERY, description = "Data to associate with the uploaded file", required = false),
                    @Parameter(name = "temp", in = ParameterIn.QUERY, description = "Whether to create a temporary file", required = false),
                    @Parameter(name = "overwrite_existing", in = ParameterIn.QUERY, description = "Whether to overwrite an existing file with the same name", required = false)
            },
            requestBody = @RequestBody(description = "File to upload", required = true),
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
            @QueryParam("path") String pathFromQuery,
            @Multipart(value = "data", required = false) @Nullable WsUploadData uploadData,
            @Multipart(value = "path", required = false) String pathFromMultipart,
            @QueryParam("temp") @DefaultValue(FALSE) Boolean temp,
            @QueryParam("overwrite_existing") @DefaultValue(FALSE) Boolean overwriteExisting
    ) {
        return uploadWs_adm.create(dataHandler, pathFromQuery, uploadData, pathFromMultipart, temp, overwriteExisting);
    }

    @POST
    @Path("_MANY")
    @Operation(
            summary = "Upload multiple files",
            description = "Upload multiple files",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"}))
            },
            requestBody = @RequestBody(description = "Files to upload", required = true),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful upload of multiple files"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_ACCESS_AUTHORITY)
    public Object loadZipFile(
            @Multipart("file") DataHandler dataHandler
    ) {
        return uploadWs_adm.loadZipFile(dataHandler);
    }

    @PUT
    @Path("{fileId}")
    @Operation(
            summary = "Update upload",
            description = "Update upload",
            requestBody = @RequestBody(description = "File to upload", required = false),
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"})),
                    @Parameter(name = "fileId", in = ParameterIn.PATH, description = "Id of the upload to update", required = true, example = "1234567890")},
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
            @PathParam("fileId") Long fileId,
            @Multipart(value = "file", required = false) @Nullable DataHandler dataHandler,
            @Multipart(value = "data", required = false) @Nullable WsUploadData uploadData
    ) {
        return uploadWs_adm.update(fileId, dataHandler, uploadData);
    }

    @DELETE
    @Path("{fileId}")
    @Operation(
            summary = "Delete upload",
            description = "Delete upload",
            parameters = {
                    @Parameter(name = "procedure", in = ParameterIn.PATH, description = "Whether to access uploads or downloads endpoint", required = true, example = "uploads", schema = @Schema(allowableValues = {"uploads", "downloads"})),
                    @Parameter(name = "fileId", in = ParameterIn.PATH, description = "Id of the upload to delete", required = true, example = "1234567890")},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of upload data"),
                    @ApiResponse(responseCode = "404", description = "Upload not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(SYSTEM_ACCESS_AUTHORITY)
    public Object deleteFile(
            @PathParam("fileId") String fileId
    ) {
        return uploadWs_mng.deleteFile(fileId);
    }
}
