package org.cmdbuild.report.dao;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.report.ReportData;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.List;
import java.util.Optional;
import jakarta.annotation.Nullable;
import org.cmdbuild.cache.CacheConfig;

import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.Holder;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.report.inner.ReportDataExt;
import org.cmdbuild.report.inner.utils.ReportUtils;

@Component
public class ReportRepositoryImpl implements ReportRepository {

    private final DaoService dao;
    private final CmCache<Optional<ReportDataExt>> reportsByCode;
    private final CmCache<Optional<ReportDataExt>> reportsById;
    private final Holder<List<ReportData>> reportDataCache;

    public ReportRepositoryImpl(CacheService cacheService, DaoService dao) {
        this.dao = checkNotNull(dao);
        reportsById = cacheService.newCache("reports_by_id", CacheConfig.SYSTEM_OBJECTS);
        reportsByCode = cacheService.newCache("reports_by_code", CacheConfig.SYSTEM_OBJECTS);
        reportDataCache = cacheService.newHolder("reports_all", CacheConfig.SYSTEM_OBJECTS);
    }

    private void invalidateCache() {
        reportsById.invalidateAll();
        reportsByCode.invalidateAll();
        reportDataCache.invalidate();
    }

    @Override
    public List<ReportDataExt> getAllReports() {
        return getRawReportsData().stream().map(r -> getById(r.getId())).collect(toImmutableList());
    }

    @Override
    @Nullable
    public ReportDataExt getByCodeOrNull(String code) {
        checkNotBlank(code);
        return reportsByCode.get(code, () -> getRawReportsData().stream().filter(r -> equal(r.getCode(), code)).collect(toOptional()).map(r -> getById(r.getId()))).orElse(null);
    }

    @Override
    @Nullable
    public ReportDataExt getByIdOrNull(long id) {
        return reportsById.get(id, () -> getRawReportsData().stream().filter(r -> equal(r.getId(), id)).collect(toOptional()).map(r -> ReportUtils.loadReport(r))).orElse(null);
    }

    @Override
    public void deleteReportById(long id) {
        dao.delete(ReportDataImpl.class, id);
        invalidateCache();
    }

    @Override
    public ReportData createReport(ReportData report) {
        dao.createOnly(report);
        invalidateCache();
        return getReportByCode(report.getCode());
    }

    @Override
    public ReportData updateReport(ReportData report) {
        dao.updateOnly(report);
        invalidateCache();
        return getReportByCode(report.getCode());
    }

    private List<ReportData> getRawReportsData() {
        return reportDataCache.get(() -> dao.selectAll().from(ReportDataImpl.class).asList());
    }

}
