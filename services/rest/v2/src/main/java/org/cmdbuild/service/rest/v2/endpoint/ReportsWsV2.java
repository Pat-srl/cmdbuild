package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.ArrayList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.FilterType;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.DETAILED;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.EXTENSION;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PARAMETERS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.service.rest.v2.serializationhelpers.AttributeTypeConversionServicev2;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("reports/")
@Produces(APPLICATION_JSON)
public class ReportsWsV2 {

    private final ReportService reportService;
    private final AttributeTypeConversionServicev2 toAttributeDetail;
    private final ObjectTranslationService translationService;

    public ReportsWsV2(ReportService reportService, AttributeTypeConversionServicev2 toAttributeDetail, ObjectTranslationService translationService) {
        this.reportService = checkNotNull(reportService);
        this.toAttributeDetail = checkNotNull(toAttributeDetail);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@QueryParam(FILTER) String filterStr, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(DETAILED) Boolean detailed) {
        List<ReportInfo> list = reportService.getForCurrentUser();
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (filter.hasFilter()) {
            filter.checkHasOnlySupportedFilterTypes(FilterType.ATTRIBUTE);
            list = AttributeFilterProcessor.<ReportInfo>builder()
                    .withKeyToValueFunction((key, report) -> {
                        switch (checkNotBlank(key)) {
                            case "title":
                            case "code":
                                return report.getCode();
                            case "description":
                                return report.getDescription();
                            default:
                                throw new IllegalArgumentException("invalid attribute filter key = " + key);
                        }
                    })
                    .withFilter(filter.getAttributeFilter()).build().filter(list);
        }
        return map("data", paged(list, offset, limit).map(this::serializeMinimalReport).elements(), "meta", map("total", list.size()));
    }

    @GET
    @Path("{reportId}/")
    public Object readOne(@PathParam("reportId") String reportId) {
        ReportInfo report = reportService.getByIdOrCode(reportId);
        return map("data", serializeDetailedReport(report), "meta", map());
    }

    @GET
    @Path("{reportId}/attributes/")
    public Object readAllAttributes(@PathParam("reportId") String reportId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        List<Attribute> result = new ArrayList<>();
        Iterable<Attribute> elements = reportService.getParamsById(reportService.getByIdOrCode(reportId).getId());
        elements.forEach(result::add);
        return map("data", result.stream().map(toAttributeDetail::serializeAttributeType).collect(toList()), "meta", map("total", result.size()));
    }

    @GET
    @Path("{reportId}/{file: [^/]+}")
    @Produces(APPLICATION_OCTET_STREAM)
    public Object download(@PathParam("reportId") String reportId, @QueryParam(EXTENSION) String extension, @QueryParam(PARAMETERS) String parametersStr) {
        Map<String, Object> parameters = isBlank(parametersStr) ? emptyMap() : fromJson(parametersStr, MAP_OF_OBJECTS);
        return reportService.executeReportAndDownload(reportId, parseEnum(extension, ReportFormat.class), parameters);
    }

    private Object serializeDetailedReport(ReportInfo report) {
        ReportData reportData = report instanceof ReportData ? ((ReportData) report) : reportService.getReportData(report.getId());
        return serializeMinimalReport(report).with(
                "title", report.getCode(),
                "query", reportData.getQuery()
        );
    }

    private FluentMap<String, Object> serializeMinimalReport(ReportInfo report) {
        return map(
                "title", report.getCode(),
                "description", translationService.translateReportDescription(report.getCode(), report.getDescription()),
                "_id", report.getId()
        );
    }

}
