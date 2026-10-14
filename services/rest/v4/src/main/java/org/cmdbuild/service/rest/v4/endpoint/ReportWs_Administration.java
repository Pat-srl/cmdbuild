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
import jakarta.ws.rs.core.MediaType;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_REPORTS_MODIFY_AUTHORITY;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.report.BatchReportInfo;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ReportSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import org.cmdbuild.service.rest.v4.command.ReportWsCommand;
import org.cmdbuild.service.rest.v4.model.WsReportData;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.springframework.stereotype.Component;

/**
 *
 * @author ldare
 */
@Path("administration/reports")
@Tags({
    @Tag(name = "Reports", description = "APIs to manage reports."),
    @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
@Component
public class ReportWs_Administration {

    private final ReportService reportService;
    private final AttributeTypeConversionService attributeTypeConversionService;
    private final ReportSerializationHelper reportSerializationHelper;
    private final ReportWsCommand command;

    public ReportWs_Administration(ReportService reportService, AttributeTypeConversionService attributeTypeConversionService, ReportSerializationHelper reportSerializationHelper, ReportWsCommand command) {
        this.reportService = checkNotNull(reportService);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
        this.reportSerializationHelper = checkNotNull(reportSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all reports",
            description = "Get all reports",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of reports"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(FILTER) @Parameter(description = "Filter to apply to the query") String filterStr,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(DETAILED) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) Boolean detailed
    ) {
        List<ReportInfo> reportInfoList = command.doReadAll(reportService::getAll, filterStr);

        return response(reportSerializationHelper.applySerializationAndPaging(reportInfoList, detailed, limit, offset));
    }

    @GET
    @Path("{" + REPORT_ID + "}/")
    @Operation(
            summary = "Get a specific report",
            description = "Obtain a specific report",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of report"),
                @ApiResponse(responseCode = "404", description = "The report was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(REPORT_ID) @Parameter(description = "Id of report", schema = @Schema(type = "long")) String reportId
    ) {
        ReportData report = command.doRead(reportId, reportService::getByIdOrCode);

        return response(reportSerializationHelper.serializeDetailedReport(report));
    }

    @GET
    @Path("{" + REPORT_ID + "}/attributes/")
    @Operation(
            summary = "Get all attributes of a report",
            description = "Get all attributes of a report",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of attributes of a report"),
                @ApiResponse(responseCode = "404", description = "The report was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllAttributes(
            @PathParam(REPORT_ID) @Parameter(description = "Id of report", schema = @Schema(type = "long")) String reportId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        ReportInfo report = reportService.getByIdOrCode(reportId);
        List<Attribute> attributeIterable = command.doReadAllAttributes(report.getId());
        return response(attributeTypeConversionService.serializeReportAttributeIterable(attributeIterable, report.getCode(), limit, offset));
    }

    @POST
    @Path("{" + REPORT_ID + "}/execute")
    @Operation(
            summary = "Execute a report",
            description = "Execute a report",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful execution of report"),
                @ApiResponse(responseCode = "404", description = "The report was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object executeBatchReport(
            @PathParam(REPORT_ID) @Parameter(description = "Id of report", schema = @Schema(type = "long")) String reportId,
            @QueryParam(EXTENSION) @Parameter(description = "Extension of report", schema = @Schema(type = "string")) String extension,
            @QueryParam(PARAMETERS) @Parameter(description = "Parameters of report", schema = @Schema(type = "string")) String parametersStr
    ) {
        BatchReportInfo info = command.doExecuteBatchReport(reportId, extension, parametersStr);
        return response(map("batchId", info.getBatchId()));
    }

    @GET
    @Path("{" + REPORT_ID + "}/{file}")
    @Operation(
            summary = "Download a report file",
            description = "Download a report file",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful download of report file"),
                @ApiResponse(responseCode = "404", description = "The report was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(
            @PathParam(REPORT_ID) @Parameter(description = "Id of report", schema = @Schema(type = "long")) String reportId,
            @QueryParam(EXTENSION) @Parameter(description = "Extension of report", schema = @Schema(type = "string")) String extension,
            @QueryParam(PARAMETERS)  @Parameter(description = "Parameters of report", schema = @Schema(type = "string")) String parametersStr
    ) {
        return command.doDownload(reportId, extension, parametersStr);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            requestBody = @RequestBody(description = "Report definition (JSON) and the report template files", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsReportData.class, requiredMode = Schema.RequiredMode.REQUIRED)),
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
                    })),
            summary = "Create a new report",
            description = "Create a new report",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful creation of report"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    public Object createReport(
            WsReportData data,
            List<Attachment> attachmentList
    ) {
        ReportData reportData = command.doCreateReport(data, attachmentList);
        return response(reportSerializationHelper.serializeDetailedReport(reportData));
    }

    @PUT
    @Path("{" + REPORT_ID + "}/")
    @Operation(
            requestBody = @RequestBody(description = "Report definition (part 'data', JSON; optional) and the report template files (all other parts)", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "data", schema = @Schema(implementation = WsReportData.class)),
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
                    })),
            summary = "Update an existing report",
            description = "Update an existing report",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful update of report"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    public Object updateReport(
            @PathParam(REPORT_ID) @Parameter(description = "Id of report", schema = @Schema(type = "long")) String reportId,
            List<Attachment> attachmentList
    ) {
        ReportData reportData = command.doUpdateReport(reportId, attachmentList);
        return response(reportSerializationHelper.serializeDetailedReport(reportData));
    }

    @PUT
    @Path("{" + REPORT_ID + "}/template")
    @Operation(
            requestBody = @RequestBody(description = "Report template files (parts may have any name)", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "files", array = @ArraySchema(schema = @Schema(type = "string", format = "binary")))
                    })),
            summary = "Update report template",
            description = "Update report template",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful update of report template"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    public Object updateReportTemplate(
            @PathParam(REPORT_ID) @Parameter(description = "Id of report", schema = @Schema(type = "long")) String reportId,
            List<Attachment> attachmentList
    ) {
        ReportData reportData = command.doUpdateReportTemplate(reportId, attachmentList);
        return response(reportSerializationHelper.serializeDetailedReport(reportData));
    }

    @GET
    @Path("{" + REPORT_ID + "}/template")
    @Operation(
            summary = "Download report template",
            description = "Download report template",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful download of report template"),
                @ApiResponse(responseCode = "404", description = "The report was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    public DataHandler downloadTemplateFiles(
            @PathParam(REPORT_ID) @Parameter(description = "Id of report", schema = @Schema(type = "long")) Long reportId
    ) {
        return downloadTemplateFilesWithFilename(reportId);
    }

    @GET
    @Path("{" + REPORT_ID + "}/template/{fileName}")
    @Operation(
            summary = "Download report template with filename",
            description = "Download report template with filename",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful download of report template with filename"),
                @ApiResponse(responseCode = "404", description = "The report was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    public DataHandler downloadTemplateFilesWithFilename(
            @PathParam(REPORT_ID) @Parameter(description = "Id of report", schema = @Schema(type = "long")) Long reportId
    ) {
        return command.doDownloadTemplateFilesWithFilename(reportId);
    }

    @DELETE
    @Path("{" + REPORT_ID + "}/")
    @Operation(
            summary = "Delete a report",
            description = "Delete a report",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful deletion of report"),
                @ApiResponse(responseCode = "404", description = "The report was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_REPORTS_MODIFY_AUTHORITY)
    public Object deleteReport(
            @PathParam(REPORT_ID) @Parameter(description = "Id of report", schema = @Schema(type = "long")) Long reportId
    ) {
        command.doDelete(reportId);
        return success();
    }

}
