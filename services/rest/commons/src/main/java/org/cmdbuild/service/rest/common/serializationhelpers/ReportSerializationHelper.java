/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static com.google.common.collect.Lists.transform;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ldare
 */
@Component
public class ReportSerializationHelper {

    private final ObjectTranslationService objectTranslationService;
    private final ReportService reportService;

    public ReportSerializationHelper(ObjectTranslationService objectTranslationService, ReportService reportService) {
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.reportService = checkNotNull(reportService);
    }

    public FluentMap<String, Object> serializeMinimalReport(ReportInfo report) {
        return map(
                "_id", report.getId(),
                "code", report.getCode(),
                "description", report.getDescription(),
                "_description_translation", objectTranslationService.translateReportDescription(report.getCode(), report.getDescription()),
                "active", report.isActive(),
                "icon_font", report.getIconFont()
        );
    }

    public FluentMap<String, Object> serializeDetailedReport(ReportInfo report) {
        ReportData reportData = report instanceof ReportData rData ? rData : reportService.getReportData(report.getId());
        return serializeMinimalReport(report).with(
                "title", report.getCode(),
                "query", reportData.getQuery()
        ).with(flattenMaps(map("config", reportData.getConfig())));
    }

    public Object applySerializationAndPaging(List<ReportInfo> reportInfoList, Boolean detailed, Integer limit, Integer offset) {
        Function<ReportInfo, Object> serializer = defaultIfNull(detailed, false) ? this::serializeDetailedReport : this::serializeMinimalReport;
        if (isPaged(offset, limit)) {
            return paged(reportInfoList, offset, limit).map(serializer);
        } else {
            return list(transform(reportInfoList, serializer::apply));
        }
    }
}
