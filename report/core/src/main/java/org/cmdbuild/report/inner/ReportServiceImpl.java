package org.cmdbuild.report.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
import jakarta.activation.DataHandler;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_REPORTS_MODIFY;
import static org.cmdbuild.auth.role.RolePrivilege.RP_REPORT_ALL_READ;
import org.cmdbuild.auth.user.OperationUserStore;
import org.cmdbuild.customclassloader.CustomClassloaderService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.report.BatchReportInfo;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.report.dao.ReportDataImpl;
import org.cmdbuild.report.dao.ReportDataImpl.ReportDataImplBuilder;
import org.cmdbuild.report.dao.ReportRepository;
import org.cmdbuild.report.inner.utils.ReportUtils;
import static org.cmdbuild.report.inner.utils.ReportUtils.getReportParameters;
import static org.cmdbuild.report.inner.utils.ReportUtils.loadReport;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReportServiceImpl implements ReportService {//TODO localization of report (description, other)

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ReportRepository reportRepository;
    private final OperationUserStore operationUser;
    private final ReportProcessorService processor;
    private final CustomClassloaderService customClassloaderService;
    private final WaterwayService service;

    public ReportServiceImpl(WaterwayService service, ReportRepository reportRepository, OperationUserStore operationUser, ReportProcessorService processor, CustomClassloaderService customClassloaderService) {
        this.reportRepository = checkNotNull(reportRepository);
        this.operationUser = checkNotNull(operationUser);
        this.processor = checkNotNull(processor);
        this.service = checkNotNull(service);
        this.customClassloaderService = checkNotNull(customClassloaderService);
    }

    @Override
    public List<ReportInfo> getAll() {
        return (List) reportRepository.getAllReports();
    }

    @Override
    public List<ReportInfo> getForCurrentUser() {
        return reportRepository.getAllActiveReports().stream().filter(this::canRead).map(ReportInfo.class::cast).collect(toList());
    }

    @Override
    public ReportInfo getById(long reportId) {
        return reportRepository.getById(reportId);
    }

    @Override
    public PrivilegeSubjectWithInfo getReportAsPrivilegeSubjectById(long reportId) {
        return reportRepository.getById(reportId);
    }

    @Override
    public boolean isActiveAndAccessibleByCode(String reportCode) {
        ReportDataExt report = reportRepository.getReportByCode(reportCode);
        return report.isActive() && canRead(report);
    }

    @Override
    public ReportInfo getByCode(String code) {
        return reportRepository.getReportByCode(code);
    }

    @Override
    public ReportData getReportData(long reportId) {
        return reportRepository.getById(reportId);
    }

    @Override
    public ReportData updateReportTemplate(long reportId, Map<String, byte[]> files) {
        ReportData reportData = checkCanWrite(getReportData(reportId));
        reportData = ReportDataImpl.copyOf(reportData).accept(updateReportData(reportData, files)).build();
        reportData = ReportDataImpl.copyOf(loadReport(reportData)).build();
        return reportRepository.updateReport(reportData);
    }

    @Override
    public ReportData createReport(ReportInfo info, Map<String, byte[]> files) {
        checkCanCreate();
        ReportData reportData = ReportDataImpl.copyOf(info).accept(updateReportData(info, files)).build();
        reportData = ReportDataImpl.copyOf(loadReport(reportData)).build();
        return reportRepository.createReport(reportData);
    }

    @Override
    public ReportData updateReportInfo(ReportInfo info) {
        ReportData reportData = getReportData(info.getId());
        reportData = ReportDataImpl.copyOf(reportData).withInfo(info).build();
        return reportRepository.updateReport(reportData);
    }

    @Override
    public ReportData updateReport(ReportInfo info, Map<String, byte[]> files) {
        ReportData reportData = checkCanWrite(getReportData(info.getId()));
        reportData = ReportDataImpl.copyOf(reportData).withInfo(info).accept(updateReportData(info, files)).build();
        reportData = ReportDataImpl.copyOf(loadReport(reportData)).build();
        return reportRepository.updateReport(reportData);
    }

    @Override
    public void deleteReport(long reportId) {
        ReportInfo report = checkCanWrite(getReportData(reportId));
        reportRepository.deleteReportById(report.getId());
    }

    @Override
    public ReportInfo getForUserByIdOrCode(String idOrCode) {
        ReportInfo report = getByIdOrCode(idOrCode);
        checkArgument(canRead(report), "CM: access denied: you are not allowed to access this report");
        return report;
    }

    private Consumer<ReportDataImplBuilder> updateReportData(ReportInfo info, Map<String, byte[]> files) {
        return b -> {
            if (info.hasCustomClasspath()) {
                customClassloaderService.doWithCustomClassLoader(info.getCustomClasspath(), () -> b.accept(ReportUtils.updateReportData(files)));
            } else {
                b.accept(ReportUtils.updateReportData(files));
            }
        };
    }

    private boolean canRead(ReportInfo report) {
        return operationUser.hasPrivileges((p) -> p.hasPrivileges(RP_REPORT_ALL_READ) || p.hasReadAccess(report));//TODO auto grant read from reports view
    }

    private <T extends ReportInfo> T checkCanWrite(T report) {
        checkArgument(operationUser.getPrivileges().hasPrivileges(RP_ADMIN_REPORTS_MODIFY), "CM: permission denied: you are not allowed to modify this report");
        return report;
    }

    private void checkCanCreate() {
        checkArgument(operationUser.getPrivileges().hasPrivileges(RP_ADMIN_REPORTS_MODIFY), "CM: permission denied: you are not allowed to create reports");
    }

    @Override
    public List<Attribute> getParamsById(long id) {
        ReportDataExt reportData = reportRepository.getById(id);
        return getReportParameters(reportData).stream().map(a -> a.toCardAttribute(id)).collect(toList());
    }

    @Override
    public DataHandler executeReportAndDownload(long reportId, ReportFormat reportExtension, Map<String, Object> parameters) {
        ReportDataExt report = reportRepository.getById(reportId);
        return processor.executeReport(report, reportExtension, mergeParams(report, parameters));
    }

    @Override
    public BatchReportInfo executeBatchReport(long reportId, ReportFormat extension, Map<String, Object> parameters) {
        String batchId = randomId();
        ReportDataExt report = reportRepository.getById(reportId);
        service.newRequest("SystemReportBatchService").withMeta(
                "username", operationUser.getUsername(),
                "group", operationUser.getCurrentGroup(),
                "reportId", toStringNotBlank(report.getId()),
                "extension", serializeEnum(extension),
                "parameters", toJson(mergeParams(report, parameters)),
                "batchId", batchId).submit();
        return new BatchReportInfo() {
            @Override
            public String getBatchId() {
                return batchId;
            }

            @Override
            public String toString() {
                return "BatchReportInfo{" + "batchId=" + batchId + '}';
            }

        };
    }

    /**
     * Merge of report (persisted) params and dynamic given ones
     * 
     * @param report
     * @param parameters
     * @return 
     */
    private Map<String, Object> mergeParams(ReportDataExt report, Map<String, Object> parameters) {
        Map<String, Object> params = map();                
        // Start with persisted params...
        params.putAll(report.getConfig());
        // ...and overwrite with template params
        params.putAll(parameters);
        
        logger.debug("using report =< {} > with merged params = {}", report.getCode(), mapToLoggableStringInline(params));
        
        return params;
    }

}
