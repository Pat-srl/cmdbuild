/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import static java.lang.Math.max;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import jakarta.activation.DataSource;
import org.apache.poi.ss.usermodel.Cell;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.Row;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateDynamicProcessor;
import org.cmdbuild.etl.loader.EtlTemplateImpl;
import org.cmdbuild.etl.loader.EtlTemplateProcessorService;
import org.cmdbuild.etl.loader.EtlTemplateReference;
import org.cmdbuild.etl.loader.EtlTemplateRepository;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.etl.loader.EtlTemplateTarget;
import org.cmdbuild.etl.loader.EtlTemplateWithData;
import static org.cmdbuild.etl.utils.EtlTemplateUtils.buildWorkbook;
import static org.cmdbuild.etl.utils.EtlTemplateUtils.getCsvPreference;
import org.cmdbuild.etl.utils.WorkbookInfo;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.springframework.stereotype.Component;
import org.supercsv.io.CsvListWriter;

@Component
public class EtlTemplateServiceImpl implements EtlTemplateService {

    private final EtlTemplateRepository templateService;
    private final EtlTemplateProcessorService processorService;
    private final OperationUserSupplier userSupplier;
    private final EtlTemplateDynamicProcessor templateDynamicProcessor;

    public EtlTemplateServiceImpl(EtlTemplateRepository templateService, EtlTemplateProcessorService processorService, OperationUserSupplier userSupplier, EtlTemplateDynamicProcessor templateDynamicProcessor) {
        this.templateService = checkNotNull(templateService);
        this.processorService = checkNotNull(processorService);
        this.userSupplier = checkNotNull(userSupplier);
        this.templateDynamicProcessor = checkNotNull(templateDynamicProcessor);
    }

    @Override
    public EtlProcessingResult importDataWithTemplate(Object data, EtlTemplate template) {
        return processorService.importDataWithTemplate(data, template);
    }

    @Override
    public EtlProcessingResult importDataWithTemplates(List<EtlTemplateWithData> templatesWithData) {
        return processorService.importDataWithTemplates(templatesWithData);
    }

    @Override
    public List<EtlTemplate> getAllForUser() {
        return getTemplates().stream().filter(t -> userSupplier.hasPrivileges(p -> p.hasReadAccess(t))).collect(toList());
    }

    @Override
    public EtlTemplate getForUserByCode(String code) {
        EtlTemplate template = getTemplateByName(code);
        userSupplier.checkPrivileges(p -> p.hasReadAccess(template), "user not authorized to access template = %s", code);
        return template;
    }

    @Override
    public EtlTemplate getForUserByCodeWithFilter(String code, String filter) {
        EtlTemplate template = getTemplateByName(code);
        userSupplier.checkPrivileges(p -> p.hasReadAccess(template), "user not authorized to access template = %s", code);
        return EtlTemplateImpl.copyOf(template).withFilter(template.getFilter().and(CmFilterUtils.parseFilter(filter))).build();
    }

    @Override
    public EtlProcessingResult importForUserDataWithTemplate(DataSource data, EtlTemplate template) {
        userSupplier.checkPrivileges(p -> p.hasReadAccess(template), "user not authorized to import with template = %s", template.getCode());
        return importDataWithTemplate(data, template);
    }

    @Override
    public List<EtlTemplate> getForUserForTargetClassAndRelatedDomains(String classId) {
        return getAllForTargetClassAndRelatedDomains(classId).stream().filter(t -> userSupplier.hasPrivileges(p -> p.hasReadAccess(t))).collect(toList());
    }

    @Override
    public List<EtlTemplate> getForUserForTarget(EtlTemplateTarget target, String classId) {
        return getAllForTarget(target, classId).stream().filter(t -> userSupplier.hasPrivileges(p -> p.hasReadAccess(t))).collect(toList());
    }

    @Override
    public List<EtlTemplate> getTemplates() {
        return templateService.getTemplates();
    }

    @Override
    public List<EtlTemplateReference> getAll() {
        return templateService.getAll();
    }

    @Override
    public EtlTemplateReference getByName(String templateName) {
        return templateService.getByName(templateName);
    }

    @Override
    public List<EtlTemplate> getAllForTarget(EtlTemplateTarget type, String name) {
        return templateService.getAllForTarget(type, name);
    }

    @Override
    public List<EtlTemplate> getAllForTargetClassAndRelatedDomains(String classId) {
        return templateService.getAllForTargetClassAndRelatedDomains(classId);
    }

    @Override
    public EtlTemplate create(EtlTemplate template) {
        return templateService.create(template);
    }

    @Override
    public EtlTemplate update(EtlTemplate template) {
        return templateService.update(template);
    }

    @Override
    public void delete(String templateId) {
        templateService.delete(templateId);
    }

    @Override
    public DataSource exportDataWithTemplate(EtlTemplate template) {
        return processorService.exportDataWithTemplate(template);
    }

    @Override
    public EtlTemplate getTemplateByName(String templateName) {
        return templateService.getTemplateByName(templateName);
    }

    @Override
    public EtlTemplate prepareTemplate(EtlTemplateReference dynamicTemplate, Map<String, Object> context) {
        return templateDynamicProcessor.prepareTemplate(dynamicTemplate, context);
    }

    @Override
    public DataSource buildImportResultReport(EtlProcessingResult result, EtlTemplate template) {
        try {
            List<List<?>> reportData = list(
                    list(""),
                    list("processed", result.getProcessedRecordCount()),
                    list("created", result.getCreatedRecordCount()),
                    list("modified", result.getModifiedRecordCount()),
                    list("deleted", result.getDeletedRecordCount()),
                    list("unmodified", result.getUnmodifiedRecordCount()),
                    list("errors", result.getErrors().size()),
                    list(""),
                    list("record", "line", "error", "detail", "data")
            );
            result.getErrors().stream().map(e -> list(e.getRecordIndex(), e.getRecordLineNumber(), e.getUserErrorMessage(), e.getTechErrorMessage(), e.getRecordData().stream().map(r -> format("%s = %s", r.getKey(), r.getValue())).collect(joining(", ")))).forEach(reportData::add);
            return switch (template.getFileFormat()) {
                case EFF_CSV -> {
                    StringWriter writer = new StringWriter();
                    try (CsvListWriter csv = new CsvListWriter(writer, getCsvPreference(template))) {
                        reportData.forEach(rethrowConsumer(e -> csv.write(e)));
                    }
                    yield newDataSource(writer.toString().getBytes(StandardCharsets.UTF_8), "text/csv", "export.csv");
                }
                case EFF_XLS, EFF_XLSX -> {
                    WorkbookInfo workbookInfo = buildWorkbook(template, "report");
                    reportData.forEach(r -> {
                        Row row = workbookInfo.getSheet().createRow(max(workbookInfo.getSheet().getLastRowNum(), 0) + 1);
                        r.forEach(c -> {
                            Cell cell = row.createCell(max(row.getLastCellNum(), 0), STRING);
                            cell.setCellValue(toStringOrEmpty(c));
                        });
                    });
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    workbookInfo.getWorkbook().write(out);
                    yield newDataSource(out.toByteArray(), workbookInfo.getContentType(), "report." + workbookInfo.getFileExt());
                }
                default ->
                    throw new EtlException("unsupported template file format = %s", template.getFileFormat());
            };
        } catch (IOException ex) {
            throw new EtlException(ex);
        }
    }

}
