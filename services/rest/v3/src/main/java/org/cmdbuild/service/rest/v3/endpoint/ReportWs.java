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
import jakarta.ws.rs.core.MediaType;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.service.rest.v4.endpoint.ReportWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ReportWs_Management;
import org.cmdbuild.service.rest.v4.model.WsReportData;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_REPORTS_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;


@Path("reports/")
@Produces(APPLICATION_JSON)
@Tag(name = "Reports", description = "Operations related to reports")
public class ReportWs {

    private final ReportWs_Administration reportWs_adm;
    private final ReportWs_Management reportWs_mng;

    public ReportWs(ReportWs_Administration reportWs_adm, ReportWs_Management reportWs_mng) {
        this.reportWs_adm = checkNotNull(reportWs_adm);
        this.reportWs_mng = checkNotNull(reportWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all reports",
            description = "Get all reports. If the user has admin view permissions, all reports will be returned. Otherwise, only reports for which the user has management permissions will be returned. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter string to filter reports by name or description"),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about reports, such as the configuration of attributes contained in the report"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view reports"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(DETAILED) Boolean detailed
    ) {
        if (isAdminViewMode(viewMode)) {
            return reportWs_adm.readAll(filterStr, limit, offset, detailed);
        }
        return reportWs_mng.readAll(filterStr, limit, offset, detailed);
    }

    @GET
    @Path("{" + REPORT_ID + "}/")
    @Operation(
            summary = "Get report by id or code",
            description = "Get report by id or code. If the user has admin view permissions, the report will be returned if it exists, regardless of the user's management permissions for that report. Otherwise, the report will only be returned if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "id", in = ParameterIn.PATH, description = "Id or code of the report to retrieve " )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the report or the report does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error") },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(REPORT_ID) String reportId
    ) {
        if (isAdminViewMode(viewMode)) {
            return reportWs_adm.read(reportId);
        }
        return reportWs_mng.read(reportId);
    }

    @GET
    @Path("{" + REPORT_ID + "}/attributes/")
    @Operation(
            summary = "Get attributes of a report",
            description = "Get attributes of a report. If the user has admin view permissions, the report attributes will be returned if the report exists, regardless of the user's management permissions for that report. Otherwise, the report attributes will only be returned if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = REPORT_ID, in = ParameterIn.PATH, description = "Id or code of the report to retrieve attributes for " ),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return"),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the report or the report does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllAttributes(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(REPORT_ID) String reportId,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset
    ) {
        if (isAdminViewMode(viewMode)) {
            return reportWs_adm.readAllAttributes(reportId, limit, offset);
        }
        return reportWs_mng.readAllAttributes(reportId, limit, offset);
    }

    @POST
    @Path("{" + REPORT_ID + "}/execute")
    @Operation(
            summary = "Execute a report",
            description = "Execute a report and return the result. If the user has admin view permissions, the report will be executed if it exists, regardless of the user's management permissions for that report. Otherwise, the report will only be executed if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = REPORT_ID, in = ParameterIn.PATH, description = "Id or code of the report to execute" ),
                    @Parameter(name = EXTENSION, in = ParameterIn.QUERY, description = "Extension of the report output file"),
                    @Parameter(name = PARAMETERS, in = ParameterIn.QUERY, description = "Report parameters as JSON string" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to execute the report or the report does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object executeBatchReport(
            @PathParam(REPORT_ID) String reportId,
            @QueryParam(EXTENSION) String extension,
            @QueryParam(PARAMETERS) String parametersStr
    ) {
        return reportWs_mng.executeBatchReport(reportId, extension, parametersStr);
    }

    @GET
    @Path("{" + REPORT_ID + "}/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    @Operation(
            summary = "Download report output file",
            description = "Download report output file. If the user has admin view permissions, the report output file will be downloaded if the report exists, regardless of the user's management permissions for that report. Otherwise, the report output file will only be downloaded if the user has management permissions for it. The view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'",
            parameters = {
                    @Parameter(name = REPORT_ID, in = ParameterIn.PATH, description = "Id or code of the report to download output file for" ),
                    @Parameter(name = EXTENSION, in = ParameterIn.QUERY, description = "Extension of the report output file"),
                    @Parameter(name = PARAMETERS, in = ParameterIn.QUERY, description = "Report parameters as JSON string" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to download the report output file or the report does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler download(
            @PathParam(REPORT_ID) String reportId,
            @QueryParam(EXTENSION) String extension,
            @QueryParam(PARAMETERS) String parametersStr
    ) {
        return reportWs_mng.download(reportId, extension, parametersStr);
    }

    @POST
    @Path("")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new report",
            description = "Create a new report with the provided data and template file. The report data should be sent as a JSON string in a form field named 'data', and the template file should be sent as a file attachment in a form field named 'template'.",
            requestBody = @RequestBody(description = "Report data and template file", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(type = "object" ))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create a report"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}

    )
    public Object createReport(
            WsReportData data,
            List<Attachment> attachments
    ) {
        return reportWs_adm.createReport(data, attachments);
    }

    @PUT
    @Path("{" + REPORT_ID + "}/")
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Operation(
            summary = "Update a report",
            description = "Update a report with the provided data and template file. The report data should be sent as a JSON string in a form field named 'data', and the template file should be sent as a file attachment in a form field named 'template'.",
            parameters = {
                    @Parameter(name = REPORT_ID, in = ParameterIn.PATH, description = "Id or code of the report to update" )
            },
            requestBody = @RequestBody(description = "Report data and template file for updating the report", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(type = "object" ))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update the report or the report does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object updateReport(
            @PathParam(REPORT_ID) String reportId,
            List<Attachment> attachments
    ) {
        return reportWs_adm.updateReport(reportId, attachments);
    }

    @PUT
    @Path("{" + REPORT_ID + "}/template")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update a report template",
            description = "Update a report template file. The template file should be sent as a file attachment in a form field named 'template'.",
            parameters = {
                    @Parameter(name = REPORT_ID, in = ParameterIn.PATH, description = "Id or code of the report to update template for" )
            },
            requestBody = @RequestBody(description = "Template file for updating the report", required = true, content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA, schema = @Schema(type = "object" ))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update the report template or the report does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object updateReportTemplate(
            @PathParam(REPORT_ID) String reportId,
            List<Attachment> attachments
    ) {
        return reportWs_adm.updateReportTemplate(reportId, attachments);
    }

    @GET
    @Path("{" + REPORT_ID + "}/template")
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Download report template file",
            description = "Download report template file. The template file will be downloaded if the report exists and the user has permissions to modify the report. Otherwise, a forbidden error will be returned.",
            parameters = {
                    @Parameter(name = REPORT_ID, in = ParameterIn.PATH, description = "Id or code of the report to download template for" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to download the report template or the report does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler downloadTemplateFiles(
            @PathParam(REPORT_ID) Long reportId
    ) {
        return reportWs_adm.downloadTemplateFiles(reportId);
    }

    @GET
    @Path("{" + REPORT_ID + "}/template/{fileName}")
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Download report template file with original filename",
            description = "Download report template file with original filename. The template file will be downloaded if the report exists and the user has permissions to modify the report. Otherwise, a forbidden error will be returned.",
            parameters = {
                    @Parameter(name = REPORT_ID, in = ParameterIn.PATH, description = "Id or code of the report to download template for" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to download the report template or the report does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler downloadTemplateFilesWithFilename(
            @PathParam(REPORT_ID) Long reportId
    ) {
        return reportWs_adm.downloadTemplateFilesWithFilename(reportId);
    }

    @DELETE
    @Path("{" + REPORT_ID + "}/")
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a report",
            description = "Delete a report. The report will be deleted if it exists and the user has permissions to modify it. Otherwise, a forbidden error will be returned.",
            parameters = {
                    @Parameter(name = REPORT_ID, in = ParameterIn.PATH, description = "Id or code of the report to delete" )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "User not authenticated"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete the report or the report does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object deleteReport(
            @PathParam(REPORT_ID) Long reportId
    ) {
        return reportWs_adm.deleteReport(reportId);
    }
}
