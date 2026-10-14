/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import jakarta.activation.DataHandler;
import javax.sql.DataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.cmdbuild.api.CmApiService;
import static org.cmdbuild.api.CmApiService.CMDB_API_PARAM;
import org.cmdbuild.customclassloader.CustomClassloaderService;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportFormat;
import static org.cmdbuild.report.inner.utils.ReportUtils.exportReportTemplatesAsZip;
import static org.cmdbuild.report.inner.utils.ReportUtils.getBaseFileName;
import static org.cmdbuild.report.inner.utils.ReportUtils.getReportImageName;
import static org.cmdbuild.report.inner.utils.ReportUtils.getReportParameters;
import static org.cmdbuild.report.inner.utils.ReportUtils.getSubreportName;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ReportProcessorServiceImpl implements ReportProcessorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ReportHelper reportHelper;
    private final DataSource dataSource;
    private final ReportPreferencesHelperService preferencesHelper;
    private final JasperReportContextService contextService;
    private final CmApiService apiService;
    private final CustomClassloaderService customClassloaderService;

    public ReportProcessorServiceImpl(ReportHelper reportHelper, DataSource dataSource, ReportPreferencesHelperService preferencesHelper, JasperReportContextService contextService, CmApiService apiService, CustomClassloaderService customClassloaderService) {
        this.reportHelper = checkNotNull(reportHelper);
        this.dataSource = checkNotNull(dataSource);
        this.preferencesHelper = checkNotNull(preferencesHelper);
        this.contextService = checkNotNull(contextService);
        this.apiService = checkNotNull(apiService);
        this.customClassloaderService = checkNotNull(customClassloaderService);
    }

    @Override
    public DataHandler executeReport(ReportDataExt report, ReportFormat reportExtension, Map<String, Object> parameters) {
        try {
            return new DbReportProcessorHelper(report, reportExtension, parameters).executeReport();
        } catch (Exception e) {
            throw new ReportException(e, "error processing report = %s", report);
        }
    }

    private class DbReportProcessorHelper {

        private final ReportDataExt reportData;
        private final ReportFormat reportExtension;
        private final Map<String, Object> params;

        public DbReportProcessorHelper(ReportDataExt reportData, ReportFormat reportExtension, Map<String, Object> params) {
            this.reportData = checkNotNull(reportData);
            this.reportExtension = checkNotNull(reportExtension);
            this.params = map(checkNotNull(params)).immutable();
        }

        public DataHandler executeReport() {
            if (equal(reportExtension, ReportFormat.ZIP)) {
                return new DataHandler(exportReportTemplatesAsZip(reportData));
            } else {
                if (reportData.hasCustomClasspath()) {
                    logger.info("execute report with custom classpath =< {} >", reportData.getCustomClasspath());
                    return customClassloaderService.doWithCustomClassLoader(reportData.getCustomClasspath(), this::doExecuteReportToDataHandler);
                } else {
                    return doExecuteReportToDataHandler();
                }
            }
        }

        private DataHandler doExecuteReportToDataHandler() {
            return reportHelper.exportReport(doExecuteReport(), getBaseFileName(reportData), reportExtension);
        }

        private JasperPrint doExecuteReport() {
            try {
                List<JasperReport> subReports = reportData.getSubJasperReports();
                List<InputStream> images = reportData.getImages().stream().map(ByteArrayInputStream::new).collect(toList());
                JasperReport jasperReport = reportData.getMasterJasperReport();

                checkNotNull(params, "must set parameters for report");

//                if (jasperReport.getStyles() == null || jasperReport.getStyles().length == 0) {
//                    JasperDesign jasperDesign = ReportUtils.toJasperDesign(reportData.getSourceMasterReport());
//                    JRDesignStyle defaultStyle = new JRDesignStyle();
//                    defaultStyle.setName("my_default_style");
//                    defaultStyle.setDefault(true);
//                    defaultStyle.setFontName(reportConfiguration.getDefaultPdfFont());
//                    jasperDesign.addStyle(defaultStyle);
//                    jasperReport = JasperCompileManager.compileReport(jasperDesign);
//                }
                Map<String, Object> userParams = getReportParameters(jasperReport).stream().collect(toMap(ReportParameter::getName, (parameter) -> {
                    String key = parameter.getName();
                    Object rawValue = params.get(key);
                    Object value = parameter.parseValue(rawValue);
                    checkArgument(parameter.isOptional() || value != null, "missing report param value for key = %s", key);
                    return value;
                }));

                logger.trace("user report params =\n\n{}\n", mapToLoggableString(userParams));

                Map<String, Object> resourcesParams = map((m) -> {
                    for (int i = 0; i < subReports.size(); i++) {
                        m.put(getSubreportName(i), subReports.get(i));
                    }
                    for (int i = 0; i < images.size(); i++) {
                        m.put(getReportImageName(i), images.get(i));
                    }
                });

                logger.trace("report resources =\n\n{}\n", mapToLoggableString(resourcesParams));

                Map<String, Object> paramsForReport = map(userParams).with(resourcesParams).with(preferencesHelper.getUserPreferencesReportParams()).with(CMDB_API_PARAM, apiService.getCmApi());

                logger.trace("complete report params =\n\n{}\n", mapToLoggableString(paramsForReport));

                logger.trace("report properties =\n\n{}\n", mapToLoggableString(map(list(jasperReport.getPropertiesMap().getPropertyNames()), identity(), jasperReport.getPropertiesMap()::getProperty)));

                logger.debug("executing report = {}", reportData);
                Stopwatch stopwatch = Stopwatch.createStarted();
                JasperPrint jasperPrint;
                try (Connection connection = dataSource.getConnection()) {
                    jasperPrint = JasperFillManager.getInstance(contextService.getContext()).fill(jasperReport, paramsForReport, connection);
                }
                logger.debug("executed report = {} in {} secs", reportData, stopwatch.elapsed(TimeUnit.MILLISECONDS) / 1000d);
                return jasperPrint;

            } catch (Exception exception) {
                throw new ReportException(exception, "error processing report = %s", reportData);
            }
        }

        public String getContentType() {
            return reportHelper.getContentTypeForReportFormat(reportExtension);
        }

    }

}
