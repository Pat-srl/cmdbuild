package org.cmdbuild.report;

import java.util.Map;

import jakarta.activation.DataHandler;

import static java.util.Collections.emptyMap;
import java.util.List;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface ReportService {

    List<ReportInfo> getAll();

    List<ReportInfo> getForCurrentUser();

    ReportInfo getByCode(String code);

    ReportData getReportData(long reportId);

    ReportInfo getById(long reportId);

    PrivilegeSubjectWithInfo getReportAsPrivilegeSubjectById(long reportId);

    List<Attribute> getParamsById(long id);

    DataHandler executeReportAndDownload(long reportId, ReportFormat extension, Map<String, Object> parameters);

    BatchReportInfo executeBatchReport(long reportId, ReportFormat extension, Map<String, Object> parameters);

    ReportData createReport(ReportInfo data, Map<String, byte[]> files);

    ReportData updateReportInfo(ReportInfo data);

    ReportData updateReportTemplate(long reportId, Map<String, byte[]> reportFiles);

    ReportData updateReport(ReportInfo data, Map<String, byte[]> files);

    void deleteReport(long reportId);

    boolean isActiveAndAccessibleByCode(String reportCode);

    ReportInfo getForUserByIdOrCode(String idorCode);

    default ReportInfo getByIdOrCode(String reportId) {
        checkNotBlank(reportId);
        if (NumberUtils.isCreatable(reportId)) {
            return getById(toLong(reportId));
        } else {
            return getByCode(reportId);
        }
    }

    default DataHandler executeReportAndDownload(String reportIdOrCode, ReportFormat ext) {
        return executeReportAndDownload(reportIdOrCode, ext, emptyMap());
    }

    default DataHandler executeReportAndDownload(String reportIdOrCode, ReportFormat ext, Map<String, Object> parameters) {
        return executeReportAndDownload(getByIdOrCode(reportIdOrCode).getId(), ext, parameters);
    }

    default void deleteReport(String reportCode) {
        deleteReport(getByCode(reportCode).getId());
    }

}
