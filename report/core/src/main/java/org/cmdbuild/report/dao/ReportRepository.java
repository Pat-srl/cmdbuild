package org.cmdbuild.report.dao;

import org.cmdbuild.report.ReportData;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.report.inner.ReportDataExt;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public interface ReportRepository {

    List<ReportDataExt> getAllReports();

    @Nullable
    ReportDataExt getByCodeOrNull(String code);

    @Nullable
    ReportDataExt getByIdOrNull(long id);

    void deleteReportById(long id);

    ReportData createReport(ReportData report);

    ReportData updateReport(ReportData report);

    default List<ReportDataExt> getAllActiveReports() {
        return list(getAllReports()).withOnly(ReportData::isActive);
    }

    default ReportDataExt getById(long id) {
        return checkNotNull(getByIdOrNull(id), "report not found for id = %s", id);
    }

    default ReportDataExt getReportByCode(String code) {
        return checkNotNull(getByCodeOrNull(code), "report not found for code = %s", code);
    }
}
