/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.report.BatchReportInfo;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ReportSerializationHelper;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import org.cmdbuild.service.rest.v4.command.ReportWsCommand;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.springframework.stereotype.Component;

/**
 *
 * @author ldare
 */
@Path("reports/")
@Tag(name = "Reports")
@Produces(APPLICATION_JSON)
@Component
public class ReportWs_Management {

    private final ReportService reportService;
    private final AttributeTypeConversionService attributeTypeConversionService;
    private final ReportSerializationHelper reportSerializationHelper;
    private final ReportWsCommand command;

    public ReportWs_Management(ReportService reportService, AttributeTypeConversionService attributeTypeConversionService, ReportSerializationHelper reportSerializationHelper, ReportWsCommand command) {
        this.reportService = checkNotNull(reportService);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
        this.reportSerializationHelper = checkNotNull(reportSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all reports for the current user",
            description = "Get all reports for the current user",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(FILTER) @Parameter(description = "Filter") String filterStr,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(DETAILED) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean")) Boolean detailed
    ) {
        List<ReportInfo> reportInfoList = command.doReadAll(reportService::getForCurrentUser, filterStr);

        return response(reportSerializationHelper.applySerializationAndPaging(reportInfoList, detailed, limit, offset));
    }

    @GET
    @Path("{" + REPORT_ID + "}/")
    @Operation(
            summary = "Get a specific report for the current user",
            description = "Get a specific report for the current user",
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
            @PathParam(REPORT_ID) @Parameter(description = "Id of the report to query") String reportId
    ) {
        ReportData report = command.doRead(reportId, reportService::getForUserByIdOrCode);

        return response(reportSerializationHelper.serializeDetailedReport(report));
    }

    @GET
    @Path("{" + REPORT_ID + "}/attributes/")
    @Operation(
            summary = "Get all attributes for a specific report",
            description = "Get all attributes for a specific report",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of report attributes"),
                @ApiResponse(responseCode = "404", description = "The report was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllAttributes(
            @PathParam(REPORT_ID) @Parameter(description = "Id of the report to query") String reportId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        ReportInfo report = reportService.getForUserByIdOrCode(reportId);
        Iterable<Attribute> attributeIterable = command.doReadAllAttributes(report.getId());
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
            @PathParam(REPORT_ID) @Parameter(description = "Id of the report to query") String reportId,
            @QueryParam(EXTENSION) @Parameter(description = "Extension of the report to execute", schema = @Schema(type = "string")) String extension,
            @QueryParam(PARAMETERS) @Parameter(description = "Parameters to pass to the report", schema = @Schema(type = "string")) String parametersStr
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
                @ApiResponse(responseCode = "404", description = "The report file was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler download(
            @PathParam(REPORT_ID) @Parameter(description = "Id of the report to query") String reportId,
            @QueryParam(EXTENSION) @Parameter(description = "Extension of the report to download", schema = @Schema(type = "string")) String extension,
            @QueryParam(PARAMETERS) @Parameter(description = "Parameters to pass to the report", schema = @Schema(type = "string")) String parametersStr
    ) {
        return command.doDownload(reportId, extension, parametersStr);
    }
}
