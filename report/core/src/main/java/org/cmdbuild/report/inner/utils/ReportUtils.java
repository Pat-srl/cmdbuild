/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.MoreCollectors.onlyElement;
import com.google.javascript.jscomp.jarjar.com.google.re2j.Matcher;
import com.google.javascript.jscomp.jarjar.com.google.re2j.Pattern;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.sf.jasperreports.engine.JRBand;
import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.JRElementGroup;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRGroup;
import net.sf.jasperreports.engine.JRImage;
import net.sf.jasperreports.engine.JRSubreport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.base.JRBaseReport;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignParameter;
import net.sf.jasperreports.engine.design.JRDesignSubreport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.util.JRSaver;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.xml.JRXmlWriter;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.api.CmApiService.CMDB_API_PARAM;
import org.cmdbuild.api.ExtendedApi;
import org.cmdbuild.report.ReportData;
import org.cmdbuild.report.ReportException;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportProcessor;
import org.cmdbuild.report.dao.ReportDataImpl;
import org.cmdbuild.report.dao.ReportDataImpl.ReportDataImplBuilder;
import org.cmdbuild.report.inner.ReportDataExt;
import org.cmdbuild.report.inner.ReportDataExtImpl;
import org.cmdbuild.report.inner.ReportParameter;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.deserializeObject;
import static org.cmdbuild.utils.io.CmIoUtils.isUrl;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmZipUtils.isZipFile;
import static org.cmdbuild.utils.io.CmZipUtils.unzipDataAsMap;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmExceptionUtils.lazyString;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    public static final String P_REPORT_PARAMETERS_MAP_GET_S = "$P{REPORT_PARAMETERS_MAP}.get(\"%s\")";

    public static String getFileExtensionForReportFormat(ReportFormat reportExtension) {
        return reportExtension.name().toLowerCase();//TODO
    }

    public static JasperDesign jasperReportToJasperDesign(JasperReport masterReport, JasperReportsContext contextService) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JRXmlWriter.writeReport(masterReport, out, "UTF-8");
        byte[] data = out.toByteArray();
        JasperDesign jasperDesign = JRXmlLoader.load(contextService, new ByteArrayInputStream(data));
        return jasperDesign;
    }

    public static Map<String, Object> loadReportImageParamsFromResourcesAndFixReport(JasperDesign jasperDesign) throws JRException {
        return loadReportImageParamsFromResourcesAndFixReport(getChildren(jasperDesign));
//        return loadSubreportParamsFromResourcesAndFixReport(jasperDesign);
    }

    public static Map<String, Object> loadReportImageParamsFromResourcesAndFixReport(List<JRChild> elements) throws JRException {
        Map<String, Object> imageParams = map();
        List<JRDesignImage> designImages = getImages(elements);
        designImages.forEach(i -> {
            JRDesignExpression expression = (JRDesignExpression) i.getExpression();
            String imageFileName = expression.getText().replaceFirst("^[\"](.+)\"$", "$1"),
                    imageId = format("image_%s_%s", normalize(imageFileName), randomId(6));
            imageParams.put(imageId, new ByteArrayInputStream(CmIoUtils.toByteArray(checkNotNull(ReportUtils.class.getResourceAsStream("/org/cmdbuild/report/files/" + imageFileName), "report image not found for name =< %s >", imageFileName))));
            expression.setText(format(P_REPORT_PARAMETERS_MAP_GET_S, imageId));
        });
        return imageParams;
    }

    public static Map<String, Object> loadSubreportParamsFromResourcesAndFixReport(JasperDesign jasperDesign) throws JRException {
        Map<String, Object> subreportParams = map();
        List<JRDesignSubreport> designSubreports = getSubreports(jasperDesign);
        designSubreports.forEach(rethrowConsumer(i -> {
            JRDesignExpression expression = (JRDesignExpression) i.getExpression();
            String subreportFileName = expression.getText().replaceFirst("^[\"](.+)[.]jasper\"$", "$1"),
                    subreportId = format("subreport_%s_%s", normalize(subreportFileName), randomId(6));

            LOGGER.info("processing subreport expr =< {} > filename =< {} > id =< {} >", expression.getText(), subreportFileName, subreportId);

            expression.setText(format(P_REPORT_PARAMETERS_MAP_GET_S, subreportId));
            if (i.getParametersMapExpression() == null) {
                i.setParametersMapExpression(new JRDesignExpression("$P{REPORT_PARAMETERS_MAP}"));
            }

            LOGGER.info("report expression =< {} > parmapexpr =< {} >", expression.getText(), i.getParametersMapExpression().getText());

            byte[] data = CmIoUtils.toByteArray(checkNotNull(ReportUtils.class.getResourceAsStream("/org/cmdbuild/report/files/%s.jrxml".formatted(subreportFileName)), "subreport not found for name =< %s >", subreportFileName));
            JasperDesign subreportJasperDesign = toJasperDesign(data);

            LOGGER.info("BEGIN: fix nested items within subreport =< {} >", subreportFileName);

            subreportParams.putAll(loadReportImageParamsFromResourcesAndFixReport(subreportJasperDesign));
            subreportParams.putAll(loadSubreportParamsFromResourcesAndFixReport(subreportJasperDesign));
            data = toByteArray(JasperCompileManager.compileReport(subreportJasperDesign));

            LOGGER.info("END:   fix nested items within subreport =< {} >", subreportFileName);

            subreportParams.put(subreportId, new ByteArrayInputStream(data));
        }));
        return subreportParams;
    }

    public static Object loadSubreportFromResources(String subreportFileName) throws JRException {
        return new ByteArrayInputStream(CmIoUtils.toByteArray(checkNotNull(ReportUtils.class.getResourceAsStream("/org/cmdbuild/report/files/" + subreportFileName), "subreport not found for name =< %s >", subreportFileName)));
    }

    public static String getSubreportName(JRSubreport jrSubreport) {//TODO improve this
        String rawExpr = jrSubreport.getExpression().getText();
        LOGGER.trace("get subreport name, raw expr = {}", rawExpr);
        String subreportPath = rawExpr.replaceAll("\\$P\\{SUBREPORT_DIR\\}", "").replaceAll("\\+", "").replaceAll("[ \"]", "");
        LOGGER.trace("got subreport name, raw expr = {}, name = {}", rawExpr, subreportPath);
        return subreportPath;
    }

    public static List<JRDesignSubreport> getSubreports(JasperDesign jasperDesign) {
        return getBands(jasperDesign).stream().filter((band) -> (band != null && band.getChildren() != null)).map((band) -> searchSubreports(band.getChildren())).flatMap(List::stream).collect(toList());
    }

    private static List<JRDesignSubreport> searchSubreports(List<JRChild> elements) {
        List<JRDesignSubreport> subreportsList = list();
        Iterator<JRChild> i = elements.listIterator();
        while (i.hasNext()) {
            final Object jreg = i.next();
            if (jreg instanceof JRDesignSubreport jrDesignSubreport) {
                subreportsList.add(jrDesignSubreport);
            } else if (jreg instanceof JRElementGroup jrElementGroup) {
                subreportsList.addAll(searchSubreports(jrElementGroup.getChildren()));
            }
        }
        return subreportsList;
    }

    public static List<JRDesignImage> getImages(JRBaseReport report) {
        return getImages(getChildren(report));
    }

    public static List<JRChild> getChildren(JRBaseReport report) {
        return getBands(report).stream().filter((b) -> (b != null && b.getChildren() != null)).map((b) -> b.getChildren()).flatMap(List::stream).collect(toList());
    }

    private static List<JRDesignImage> getImages(List<JRChild> elements) {
        Iterator<JRChild> i = elements.listIterator();
        List<JRDesignImage> designImagesList = list();
        while (i.hasNext()) {
            Object jreg = i.next();
            if (jreg instanceof JRDesignImage jrDesignImage) {
                designImagesList.add(jrDesignImage);
            } else if (jreg instanceof JRElementGroup jrElementGroup) {
                designImagesList.addAll(getImages(jrElementGroup.getChildren()));
            }
        }
        return designImagesList;
    }

    public static List<JRBand> getBands(JRBaseReport jasperDesign) {
        List<JRBand> bands = list();
        bands.add(jasperDesign.getTitle());
        bands.add(jasperDesign.getPageHeader());
        bands.add(jasperDesign.getColumnHeader());
        bands.addAll(list(jasperDesign.getDetailSection().getBands()));
        bands.add(jasperDesign.getColumnFooter());
        bands.add(jasperDesign.getPageFooter());
        bands.add(jasperDesign.getLastPageFooter());
        bands.add(jasperDesign.getSummary());
        for (JRGroup group : jasperDesign.getGroups()) {
            bands.addAll(list(group.getGroupFooterSection().getBands()));
            bands.addAll(list(group.getGroupHeaderSection().getBands()));
        }
        return list(bands).filter(Objects::nonNull).immutable();
    }

    public static String getImageFormatName(InputStream is) throws IOException { //TODO improve this (use tika?)
        String format = "";
        try (ImageInputStream iis = ImageIO.createImageInputStream(is)) {
            Iterator<ImageReader> readerIterator = ImageIO.getImageReaders(iis);
            if (readerIterator.hasNext()) {
                final ImageReader reader = readerIterator.next();
                format = reader.getFormatName();
            }
        }
        is.reset();
        return format;
    }

    public static DataHandler doExecuteReportAndDownload(ReportProcessor reportFactory) {
        try {
            return new DataHandler(reportFactory.executeReport());
        } catch (Exception e) {
            throw new ReportException(e, "error processing report = %s", reportFactory);
        }
    }

    public static void setImageFilename(JRImage jrImage, String newValue) {
        JRDesignExpression newImageExpr = new JRDesignExpression();
        newImageExpr.setText(newValue);
        ((JRDesignImage) jrImage).setExpression(newImageExpr);
    }

    public static List<ReportParameter> getReportParameters(ReportDataExt reportCard) {
        return getReportParameters(reportCard.getMasterJasperReport());
    }

    public static List<ReportParameter> getReportParameters(JasperReport masterReport) {
        return stream(masterReport.getParameters()).filter(p -> (p.isForPrompting() && !p.isSystemDefined())).map(ReportParameter::parseJrParameter).collect(toList());
    }

    public static jakarta.activation.DataSource exportReportTemplatesAsZip(ReportDataExt reportData) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(out)) {

            JasperDesign jasperDesign = toJasperDesign(reportData.getSourceMasterReport());

            List<String> imageNames = reportData.getImageNames();
            List<byte[]> images = reportData.getImages();

            Map<String, String> imageNameMapping = IntStream.range(0, images.size()).boxed().collect(toMap(i -> getReportImageName(i), i -> imageNames.get(i)));

            prepareDesignImagesForZipExport(getImages(jasperDesign), imageNameMapping);
            prepareDesignSubreportsForZipExport(getSubreports(jasperDesign), reportData.getSubJasperReports());

            zip.putNextEntry(new ZipEntry(format("%s.jrxml", jasperDesign.getName())));
            JRXmlWriter.writeReport(JasperCompileManager.compileReport(jasperDesign), zip, StandardCharsets.UTF_8.name());
            zip.closeEntry();

            Set<String> alreadyIncludedFiles = set();

            for (int i = 0; i < imageNames.size(); i++) {
                byte[] imageData = images.get(i);
                String imageFilename = imageNames.get(i);
                if (alreadyIncludedFiles.add(imageFilename)) {
                    zip.putNextEntry(new ZipEntry(imageFilename));
                    zip.write(imageData);
                    zip.closeEntry();
                } else {
                    LOGGER.warn("image file =< {} > already included in zip file, skipping", imageFilename);
                }
            }

            reportData.getSourceSubReports().stream().map(ReportUtils::toJasperDesign).forEach(rethrowConsumer(subreport -> {
                prepareDesignImagesForZipExport(getImages(subreport), imageNameMapping);
                String subreportFileName = format("%s.jrxml", subreport.getName());
                if (alreadyIncludedFiles.add(subreportFileName)) {
                    zip.putNextEntry(new ZipEntry(subreportFileName));
                    JRXmlWriter.writeReport(subreport, zip, StandardCharsets.UTF_8.name());
                    zip.closeEntry();
                } else {
                    LOGGER.warn("subreport file =< {} > already included in zip file, skipping", subreportFileName);
                }
            }));

        } catch (Exception ex) {
            throw new ReportException(ex, "error writing zip report for report = %s", reportData);
        }
        return newDataSource(out.toByteArray(), "application/zip", FilenameUtils.getBaseName(getBaseFileName(reportData)) + ".zip");
    }

    private static void prepareDesignImagesForZipExport(List<JRDesignImage> designImagesList, Map<String, String> imageNameMapping) {
        designImagesList.forEach(i -> {
            if (i.getExpression() != null && !isBlank(i.getExpression().getText())) {
                Matcher matcher = Pattern.compile("\\$P\\{REPORT_PARAMETERS_MAP\\}.get\\(\"(IMAGE[0-9]+)\"\\)").matcher(i.getExpression().getText());
                if (matcher.matches()) {
                    String mapped = imageNameMapping.get(checkNotBlank(matcher.group(1)));
                    if (isNotBlank(mapped)) {
                        i.setExpression(new JRDesignExpression(format("\"%s\"", mapped)));
                    }
                }
            }
        });
    }

    private static void prepareDesignSubreportsForZipExport(List<JRDesignSubreport> designSubreports, List<JasperReport> jasperSubreports) {
        designSubreports.forEach(subreport -> {
            if (subreport.getExpression() != null && !isBlank(subreport.getExpression().getText())) {
                Matcher matcher = Pattern.compile("\\$P\\{REPORT_PARAMETERS_MAP\\}.get\\(\"SUBREPORT([0-9]+)\"\\)").matcher(subreport.getExpression().getText());
                if (matcher.matches()) {
                    int index = toInt(matcher.group(1)) - 1;
                    subreport.setExpression(new JRDesignExpression(format("\"%s.jasper\"", jasperSubreports.get(index).getName())));
                }
            }
        });
    }

    public static String getBaseFileName(ReportData reportData) {
        return reportData.getCode().replace(" ", "");
    }

    public static JasperDesign toJasperDesign(byte[] data, JasperReportsContext context) {
        try {
            return JRXmlLoader.load(context, new ByteArrayInputStream(data));
        } catch (JRException ex) {
            throw new ReportException(ex, "error deserializing jrxml file");
        }
    }

    public static JasperDesign toJasperDesign(byte[] data) {
        try {
            return JRXmlLoader.load(new ByteArrayInputStream(data));
        } catch (JRException ex) {
            throw new ReportException(ex, "error deserializing jrxml file");
        }
    }

    public static byte[] toByteArray(JasperDesign jasperDesign, JasperReportsContext context) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JasperCompileManager.getInstance(context).compileToStream(jasperDesign, out);
            return checkNotBlank(out.toByteArray());
        } catch (JRException ex) {
            throw new ReportException(ex);
        }
    }

    public static byte[] toByteArray(JasperDesign jasperDesign) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JasperCompileManager.compileReportToStream(jasperDesign, out);
            return checkNotBlank(out.toByteArray());
        } catch (JRException ex) {
            throw new ReportException(ex);
        }
    }

    public static byte[] toByteArray(JasperReport jasperReport) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            JRSaver.saveObject(jasperReport, out);
            return checkNotBlank(out.toByteArray());
        } catch (JRException ex) {
            throw new ReportException(ex);
        }
    }

    public static String toJrxmlString(JasperDesign jasperDesign) {
        try {
            JasperReport compiled = JasperCompileManager.compileReport(jasperDesign);
            return JRXmlWriter.writeReport(compiled, "UTF-8");
        } catch (JRException ex) {
            throw new ReportException(ex, "error compiling report to jrxml");
        }
    }

    public static ReportDataExt loadReport(ReportData reportData) {
        try {
            LOGGER.debug("load report = {}", reportData);
            List<String> sourceReports;
            List<JasperReport> jasperReports;
            List<byte[]> compiledReports;
            if (reportData.hasCompiledReports()) {
                compiledReports = reportData.getCompiledReports();
                jasperReports = list(compiledReports).map(r -> (JasperReport) deserializeObject(r));
            } else {
                List<JasperDesign> jasperDesigns = list(checkNotEmpty(reportData.getSourceReports())).map(ReportUtils::toJasperDesign);
                jasperReports = list(jasperDesigns).map(rethrowFunction(JasperCompileManager::compileReport));
                compiledReports = list(jasperReports).map(ReportUtils::toByteArray);
            }
            if (reportData.hasSourceReports()) {
                sourceReports = reportData.getSourceReports();
            } else {
                sourceReports = list(checkNotEmpty(jasperReports)).map(rethrowFunction(r -> JRXmlWriter.writeReport(r, StandardCharsets.UTF_8.name())));
            }
            return ReportDataExtImpl.builder().withInner(ReportDataImpl.copyOf(reportData).withCompiledReports(compiledReports).withSourceReports(sourceReports).build()).withJasperReports(jasperReports).build();
        } catch (JRException ex) {
            throw new ReportException(ex, "error loading report");
        }
    }

    public static JasperDesign toJasperDesign(String xml) {
        try {
            return JRXmlLoader.load(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
        } catch (JRException ex) {
            throw new ReportException(ex);
        }
    }

    public static String getReportImageName(int pos) {
        return format("IMAGE%s", pos);
    }

    public static String getSubreportName(int pos) {
        return format("SUBREPORT%s", pos + 1);//subreports index begin with 1 (and not 0) for legacy reasons
    }

    public static Consumer<ReportDataImplBuilder> updateReportData(byte[] zipFile) {
        return updateReportData(map("file.zip", checkNotNull(zipFile)));
    }

    public static Consumer<ReportDataImplBuilder> updateReportData(Map<String, byte[]> files) {
        return (builder) -> {
            LOGGER.debug("load report data from files =\n\n{}\n", mapToLoggableStringLazy(files));
            Map<String, byte[]> sourceFiles;
            if (files.size() == 1 && isZipFile(getOnlyElement(files.keySet()))) {
                sourceFiles = map(unzipDataAsMap(getOnlyElement(files.values())));
            } else {
                sourceFiles = map(files);
            }
            sourceFiles.forEach((k, v) -> checkArgument(isNotBlank(k) && v != null && v.length > 0, "invalid file = %s", k));

            Map<String, JasperDesign> allCompiledReports = sourceFiles.keySet().stream().filter((p) -> p.endsWith(".jrxml")).sorted().collect(toMap(identity(), k -> {
                byte[] data = checkNotNull(sourceFiles.get(k));
                try {
                    return toJasperDesign(data);
                } catch (Exception ex) {
                    throw new ReportException(ex, "error processing report from file = %s (%s)", k, byteCountToDisplaySize(data.length));
                }
            }));

            Entry<String, JasperDesign> masterReportEntry;
            try {
                checkArgument(!allCompiledReports.isEmpty());
                if (allCompiledReports.size() == 1) {
                    masterReportEntry = getOnlyElement(allCompiledReports.entrySet());
                } else {
                    Set<String> subreports = set();
                    allCompiledReports.values().forEach(r -> getSubreports(r).forEach(sr -> subreports.add(FilenameUtils.getBaseName(getSubreportName(sr)))));
                    masterReportEntry = allCompiledReports.entrySet().stream().filter((r) -> !subreports.contains(FilenameUtils.getBaseName(r.getKey()))).collect(onlyElement());
                }
            } catch (Exception ex) {
                throw new ReportException(ex, "unable to find master report; expected one and only one <file>.jrxml master report file");
            }

            LOGGER.debug("processing master report from file = {}", masterReportEntry.getKey());

            JasperDesign masterReport = masterReportEntry.getValue();

            ImageHelper imageHelper = new ImageHelper();

            getImages(masterReport).forEach(imageHelper::prepareDesignImageForUpload);
            addSysParams(masterReport);

            List<JRDesignSubreport> subreports = list(allCompiledReports.values()).flatMap(ReportUtils::getSubreports);
            List<String> subreportFileNames = list(subreports).map(ReportUtils::getSubreportName);
            prepareDesignSubreportsForUpload(subreports);

            map(allCompiledReports).withoutKey(masterReportEntry.getKey()).forEach((fileName, subreport) -> {
                String compiledReportName = format("%s.jasper", FilenameUtils.getBaseName(fileName));
                LOGGER.debug("compiling sub report source file =< {} > to file =< {} >", fileName, compiledReportName);
                try {
                    getImages(subreport).forEach(imageHelper::prepareDesignImageForUpload);
                    addSysParams(subreport);
                    sourceFiles.remove(fileName);
                    LOGGER.trace("processed subreport =< {} > to jrxml =\n\n{}\n", fileName, lazyString(() -> toJrxmlString(subreport)));
                    sourceFiles.put(compiledReportName, toByteArray(subreport));
                } catch (Exception ex) {
                    throw new ReportException(ex, "error compiling sub report file =< %s >", fileName);
                }
            });

            List<String> missingFiles = list(imageHelper.getImageFileNames()).with(subreportFileNames).stream().distinct().filter(not(sourceFiles.keySet()::contains)).sorted().collect(toList());
            checkArgument(missingFiles.isEmpty(), "missing required files = %s", missingFiles.stream().collect((joining(","))));

            list(sourceFiles.keySet()).without(imageHelper.getImageFileNames()).without(subreportFileNames).without(masterReportEntry.getKey()).sorted().forEach((superflousFile) -> {
                LOGGER.warn(marker(), "found unnecessary file = {} (this file will be ignored and discarded)", superflousFile);
            });

            LOGGER.debug("import report files =\n\n{}\n", mapToLoggableStringLazy(sourceFiles));

            List<byte[]> images = list(imageHelper.getImageFileNames()).map(sourceFiles::get),
                    compiledReports = listOf(byte[].class).with(toByteArray(masterReport)).with(list(subreportFileNames).map(sourceFiles::get));

            String query = masterReport.getQuery() == null ? null : masterReport.getQuery().getText().replace("\"", "\\\"");

            builder
                    .withImageNames(imageHelper.getImageFileNames())
                    .withImages(images)
                    .withCompiledReports(compiledReports)
                    .withSourceReports(emptyList())
                    .withQuery(query);
        };
    }

    private static void prepareDesignSubreportsForUpload(List<JRDesignSubreport> subreportsList) {
        for (int i = 0; i < subreportsList.size(); i++) {
            JRDesignSubreport subreport = subreportsList.get(i);
            subreport.setExpression(new JRDesignExpression(format(P_REPORT_PARAMETERS_MAP_GET_S, getSubreportName(i))));
            if (subreport.getParametersMapExpression() == null) {
                subreport.setParametersMapExpression(new JRDesignExpression("$P{REPORT_PARAMETERS_MAP}"));
            }
        }
    }

    private static void addSysParams(JasperDesign report) {
        try {
            report.removeParameter(CMDB_API_PARAM);
            JRDesignParameter cmdb = new JRDesignParameter();
            cmdb.setName(CMDB_API_PARAM);
            cmdb.setValueClassName(ExtendedApi.class.getName());
            cmdb.setForPrompting(false);
            report.addParameter(cmdb);
        } catch (JRException ex) {
            throw new ReportException(ex);
        }
    }

    private static class ImageHelper {

        private final List<String> imageNames = list();
        private final Map<String, String> imageMapping = map();

        public void prepareDesignImageForUpload(JRDesignImage image) {
            String fileName = getImageFileNameOrNull(image);
            if (isNotBlank(fileName)) {
                String mappedName = getMappedName(fileName);
                JRDesignExpression newImageExpr = new JRDesignExpression();
                newImageExpr.setText(format(P_REPORT_PARAMETERS_MAP_GET_S, mappedName));
                image.setExpression(newImageExpr);
                image.setUsingCache((Boolean) true);
                image.setOnErrorType(OnErrorTypeEnum.BLANK);
            }
        }

        @Nullable
        private String getImageFileNameOrNull(JRImage jrImage) {
            String rawExpr = jrImage.getExpression().getText();
            LOGGER.trace("processing report image expr =< {} >", rawExpr);
            Matcher matcher = Pattern.compile("\"([^\"]+)\"").matcher(rawExpr);
            if (matcher.matches()) {
                String value = checkNotBlank(matcher.group(1)).replaceAll("[\\\\]", "/");
                if (isUrl(value)) {
                    LOGGER.trace("expr is url, skipping");
                    return null;
                } else {
                    File file = new File(value);
                    if (file.isAbsolute()) {
                        LOGGER.trace("expr is absolute file path, skipping");
                        return null;
                    } else {
                        String filename = file.getName();
                        LOGGER.trace("extracted report image file name from expr =< {} >, image file name =< {} >", rawExpr, filename);
                        return filename;
                    }
                }
            } else {
                LOGGER.trace("expr does not match filename pattern, skipping");
                return null;
            }
        }

        private String getMappedName(String fileName) {
            checkNotBlank(fileName);
            if (!imageMapping.containsKey(fileName)) {
                String mappedName = getReportImageName(imageNames.size());
                LOGGER.debug("map image =< {} > as =< {} >", fileName, mappedName);
                imageNames.add(fileName);
                imageMapping.put(fileName, mappedName);
            }
            return imageMapping.get(fileName);
        }

        public List<String> getImageFileNames() {
            return imageNames;
        }

    }

}
