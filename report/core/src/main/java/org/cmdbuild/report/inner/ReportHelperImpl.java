/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayOutputStream;
import static java.lang.String.format;
import jakarta.activation.DataHandler;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import org.apache.commons.io.FilenameUtils;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportFormat;
import static org.cmdbuild.report.ReportFormat.ODT;
import static org.cmdbuild.report.inner.utils.ReportUtils.getFileExtensionForReportFormat;
import org.cmdbuild.userconfig.UserPreferencesService;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.setCharsetInContentType;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.springframework.stereotype.Component;

@Component
public class ReportHelperImpl implements ReportHelper {

    private final UserPreferencesService userPreferencesService;
    private final JasperReportContextService contextService;

    public ReportHelperImpl(UserPreferencesService userPreferencesService, JasperReportContextService contextService) {
        this.userPreferencesService = checkNotNull(userPreferencesService);
        this.contextService = checkNotNull(contextService);
    }

    @Override
    public String getContentTypeForReportFormat(ReportFormat reportExtension) {
        return switch (reportExtension) {
            case PDF ->
                "application/pdf";
            case CSV ->
                switch (userPreferencesService.getUserPreferences().getPreferredOfficeSuite()) {
                    case POS_MSOFFICE ->
                        "application/vnd.ms-excel";//set charset ??
                    default ->
                        setCharsetInContentType("text/csv", userPreferencesService.getUserPreferences().getPreferredFileCharset());
                };
            case ODT ->
                "application/vnd";
            case DOCX ->
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case RTF ->
                "application/rtf";
            case ZIP ->
                "application/zip";
            default ->
                throw unsupported("unsupported report extension = %s", reportExtension);
        };
    }

    @Override
    public DataHandler exportReport(JasperPrint reportOutput, String basename, ReportFormat format) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Exporter exporter = createExporter(format);
            exporter.setExporterInput(new SimpleExporterInput(reportOutput));
            switch (format) {
                case CSV, RTF ->
                    exporter.setExporterOutput(new SimpleWriterExporterOutput(out, userPreferencesService.getUserPreferences().getPreferredFileCharset()));
                default ->
                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            }
            exporter.exportReport();
            return newDataHandler(out.toByteArray(), getContentTypeForReportFormat(format), format("%s.%s", FilenameUtils.getBaseName(basename), getFileExtensionForReportFormat(format)));
        } catch (JRException ex) {
            throw new ReportException(ex, "error printing report result");
        }
    }

    private Exporter createExporter(ReportFormat format) {
        return switch (format) {
            case PDF ->
                new JRPdfExporter(contextService.getContext());
            case CSV -> {
                SimpleCsvExporterConfiguration csvConfiguration = new SimpleCsvExporterConfiguration();
                csvConfiguration.setFieldDelimiter(userPreferencesService.getUserPreferences().getPreferredCsvSeparator());
                JRCsvExporter csvExporter = new JRCsvExporter(contextService.getContext());
                csvExporter.setConfiguration(csvConfiguration);
                yield csvExporter;
            }
            case ODT ->
                new JROdtExporter(contextService.getContext());
            case DOCX ->
                new JRDocxExporter(contextService.getContext());
            case RTF ->
                new JRRtfExporter(contextService.getContext());
            default ->
                throw unsupported("unsupported report extension = %s", format);
        };
    }
}
