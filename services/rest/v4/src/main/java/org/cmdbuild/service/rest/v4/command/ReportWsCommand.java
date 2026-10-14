/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Function;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.core.MediaType;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.report.BatchReportInfo;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import org.cmdbuild.report.utils.ReportFilesUtils;
import org.cmdbuild.service.rest.v4.model.WsReportData;
import static org.cmdbuild.service.rest.v4.model.WsReportData.getData;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.springframework.stereotype.Component;

/**
 *
 * @author ldare
 */
@Component
public class ReportWsCommand {

    private final ReportService reportService;

    public ReportWsCommand(ReportService reportService) {
        this.reportService = checkNotNull(reportService);
    }

    public List<ReportInfo> doReadAll(Supplier<List<ReportInfo>> function, String filterStr) {
        List<ReportInfo> reportInfoList = function.get();
        return filterReportInfo(reportInfoList, filterStr);
    }

    public ReportData doRead(String reportId, Function<String, ReportInfo> function) {
        ReportInfo report = function.apply(reportId);
        return report instanceof ReportData reportData ? reportData : reportService.getReportData(report.getId());
    }

    public List<Attribute> doReadAllAttributes(Long reportId) {
        return reportService.getParamsById(reportId);
    }

    public BatchReportInfo doExecuteBatchReport(String reportId, String extension, String parametersStr) {
        ReportInfo report = reportService.getForUserByIdOrCode(reportId);
        Map<String, Object> parameters = isBlank(parametersStr) ? emptyMap() : fromJson(parametersStr, MAP_OF_OBJECTS);
        return reportService.executeBatchReport(report.getId(), reportExtFromString(extension), parameters);//TODO handle special report codes
    }

    public DataHandler doDownload(String reportId, String extension, String parametersStr) {
        ReportInfo report = reportService.getForUserByIdOrCode(reportId);
        Map<String, Object> parameters = isBlank(parametersStr) ? emptyMap() : fromJson(parametersStr, MAP_OF_OBJECTS);
        return reportService.executeReportAndDownload(report.getId(), reportExtFromString(extension), parameters);//TODO handle special report codes
    }

    public ReportData doCreateReport(WsReportData data, List<Attachment> attachmentList) {
        checkNotNull(attachmentList);
        Map<String, byte[]> files = getFiles(attachmentList);
        return reportService.createReport(data.toReportInfo().build(), files);
    }

    public ReportData doUpdateReport(String reportId, List<Attachment> attachmentList) {
        WsReportData wsData = getData(attachmentList);
        Map<String, byte[]> files = getFiles(attachmentList);
        ReportInfo info = wsData.toReportInfo().withId(reportService.getByIdOrCode(reportId).getId()).build();
        ReportData reportData;
        if (files.isEmpty()) {
            reportData = reportService.updateReportInfo(info);
        } else {
            reportData = reportService.updateReport(info, files);
        }
        return reportData;
    }

    public ReportData doUpdateReportTemplate(String reportId, List<Attachment> attachmentList) {
        checkNotNull(attachmentList);
        Map<String, byte[]> files = getFiles(attachmentList);
        return reportService.updateReportTemplate(reportService.getByIdOrCode(reportId).getId(), files);
    }

    public DataHandler doDownloadTemplateFilesWithFilename(Long reportId) {
        return reportService.executeReportAndDownload(reportId.toString(), ReportFormat.ZIP);
    }

    public void doDelete(Long reportId) {
        reportService.deleteReport(reportId);
    }

    private List<ReportInfo> filterReportInfo(List<ReportInfo> reportInfoList, String filterStr) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            reportInfoList = AttributeFilterProcessor.<ReportInfo>builder()
                    .withKeyToValueFunction((key, report)
                            -> switch (checkNotBlank(key)) {
                case "title", "code" ->
                    report.getCode();
                case "description" ->
                    report.getDescription();
                default ->
                    throw new IllegalArgumentException("invalid attribute filter key = " + key);
            })
                    .withFilter(filter.getAttributeFilter()).build().filter(reportInfoList);
        }
        return reportInfoList;
    }

    private static Map<String, byte[]> getFiles(List<Attachment> attachments) {
        return ReportFilesUtils.unpackReportFiles(attachments.stream()
                .filter((a) -> !a.getContentType().isCompatible(MediaType.APPLICATION_JSON_TYPE))
                .collect(toMap(e -> e.getContentDisposition().getFilename(), e -> toByteArray(e.getDataHandler()))));
    }
}
