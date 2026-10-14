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
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import org.cmdbuild.service.rest.v4.endpoint.ProcessWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ProcessWs_Management;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_PROCESSES_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;


@Path("processes/")
@Produces(APPLICATION_JSON)
@Tag(name = "Processes", description = "Operations related to processes")
public class ProcessWs {

    private final ProcessWs_Administration processWs_adm;
    private final ProcessWs_Management processWs_mng;

    public ProcessWs(ProcessWs_Administration processWs_adm, ProcessWs_Management processWs_mng) {
        this.processWs_adm = checkNotNull(processWs_adm);
        this.processWs_mng = checkNotNull(processWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all processes",
            description = "Get all processes. If the user has admin view permissions, all processes will be returned. Otherwise, only processes for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about processes, such as the configuration of associated task definitions"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view processes"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed
    ) {
        if (isAdminViewMode(viewMode)) {
            return processWs_adm.readAll(limit, offset, detailed);
        }
        return processWs_mng.readAll(limit, offset, detailed);
    }

    @GET
    @Path("{processId}/")
    @Operation(
            summary = "Get a specific process",
            description = "Get a specific process. If the user has admin view permissions, the process will be returned even if the user does not have management permissions for it. Otherwise, the process will only be returned if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH, description = "Id of the process to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the process or the process does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(PROCESS_ID) String processId
    ) {
        if (isAdminViewMode(viewMode)) {
            return processWs_adm.read(processId);
        }
        return processWs_mng.read(processId);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_PROCESSES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new process",
            description = "Create a new process. Requires admin permissions for processes.",
            requestBody = @RequestBody(description = "Data for the new process to create", required = true, content = @Content(schema = @Schema(implementation = WsClassData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of process"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create processes"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsClassData data
    ) {
        return processWs_adm.create(data);
    }

    @PUT
    @Path("{processId}/")
    @RolesAllowed(ADMIN_PROCESSES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an existing process",
            description = "Update an existing process. Requires admin permissions for processes.",
            parameters = {
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH, description = "Id of the process to update")
            },
            requestBody = @RequestBody(description = "Data to update the process with", required = true, content = @Content(schema = @Schema(implementation = WsClassData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of process"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update processes or the process does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(PROCESS_ID) String classId,
            WsClassData data
    ) {
        return processWs_adm.update(classId, data);
    }

    @DELETE
    @Path("{processId}/")
    @RolesAllowed(ADMIN_PROCESSES_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a process",
            description = "Delete a process. Requires admin permissions for processes.",
            parameters = {
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH, description = "Id of the process to delete")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of process"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete processes or the process does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam(PROCESS_ID) String classId
    ) {
        return processWs_adm.delete(classId);
    }

    @POST
    @Path("{processId}/versions")
    @Consumes(MULTIPART_FORM_DATA)
    @Operation(
            summary = "Upload a new version of a process",
            description = "Upload a new version of a process by providing the XPDL file. If the 'replace' query parameter is set to true, the uploaded version will replace the current version of the process. Otherwise, it will be added as a new version and the current version will be kept. Requires management permissions for the process.",
            parameters = {
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH, description = "Id of the process to upload a new version for"),
                    @Parameter(name = "file", in = ParameterIn.QUERY, description = "XPDL file to upload as the new version of the process", required = true, content = @Content(schema = @Schema(type = "string", format = "binary"))),
                    @Parameter(name = "replace", in = ParameterIn.QUERY, description = "Whether to replace the current version of the process with the uploaded version or to keep both versions", required = false)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful upload of new process version"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid input data or missing file"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to upload a new version for the process or the process does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object uploadNewXpdlVersion(
            @PathParam(PROCESS_ID) String processId,
            @Multipart(FILE) DataHandler dataHandler,
            @QueryParam("replace") @DefaultValue(FALSE) Boolean replace
    ) {
        return processWs_mng.uploadNewXpdlVersion(processId, dataHandler, replace);
    }

    @GET
    @Path("{processId}/versions")
    @Operation(
            summary = "Get all versions of a process",
            description = "Get all versions of a process. Requires management permissions for the process.",
            parameters = {
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH, description = "Id of the process to retrieve versions for")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process versions"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view versions for the process or the process does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object getAllXpdlVersions(
            @PathParam(PROCESS_ID) String processId
    ) {
        return processWs_mng.getAllXpdlVersions(processId);
    }

    @GET
    @Path("{processId}/versions/{planId}/file")
    @Produces(APPLICATION_OCTET_STREAM)
    @Operation(
            summary = "Download the XPDL file of a specific version of a process",
            description = "Download the XPDL file of a specific version of a process. Requires management permissions for the process.",
            parameters = {
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH, description = "Id of the process to retrieve the version file for"),
                    @Parameter(name = "planId", in = ParameterIn.PATH, description = "Id of the process version to retrieve the file for")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of XPDL file"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view versions for the process or the process/version does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler getXpdlVersionFile(
            @PathParam(PROCESS_ID) String processId,
            @PathParam("planId") String planId
    ) {
        return processWs_mng.getXpdlVersionFile(processId, planId);
    }

    @GET
    @Path("{processId}/template")
    @Produces(APPLICATION_OCTET_STREAM)
    @Operation(
            summary = "Download the XPDL template file for a process",
            description = "Download the XPDL template file for a process. The template file can be used as a starting point for creating new versions of the process. Requires management permissions for the process.",
            parameters = {
                    @Parameter(name = PROCESS_ID, in = ParameterIn.PATH, description = "Id of the process to retrieve the template file for")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of XPDL template file"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view versions for the process or the process does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler getXpdlTemplateFile(
            @PathParam(PROCESS_ID) String processId
    ) {
        return processWs_mng.getXpdlTemplateFile(processId);
    }
}
