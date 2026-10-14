/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.utils;

import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.List;
import java.util.Map.Entry;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.fault.FaultEventImpl;
import org.cmdbuild.jobs.JobRunContext;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmExceptionUtils.serializeError;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class EtlResultUtils {

    public static String serializeEtlProcessingResult(EtlProcessingResult result) {
        return toJson(etlProcessingResultToJsonObject(result));
    }

    public static FluentMap<String, Object> etlProcessingResultToJsonObject(EtlProcessingResult result) {
        return etlProcessingResultToJsonObject(result, result.hasDetails());
    }

    public static FluentMap<String, Object> etlProcessingResultToJsonObject(EtlProcessingResult result, boolean detailedReport) {
        return (FluentMap) map(
                "hasErrors", result.hasErrors(),
                "created", result.getCreatedRecordCount(),
                "modified", result.getModifiedRecordCount(),
                "unmodified", result.getUnmodifiedRecordCount(),
                "deleted", result.getDeletedRecordCount(),
                "processed", result.getProcessedRecordCount(),
                "errors", result.getErrors().stream().map(e -> map(
                "recordNumber", e.getRecordIndex(),
                "lineNumber", e.getRecordLineNumber(),
                "record", e.getRecordData().stream().collect(toMap(Entry::getKey, Entry::getValue)),
                "message", e.getUserErrorMessage(),
                "techMessage", e.getTechErrorMessage()
        )).collect(toList())).accept(m -> {
            if (detailedReport) {
                m.put(
                        "created_records", result.getDetails().getCreatedRecords(),
                        "modified_records", result.getDetails().getModifiedRecords(),
                        "deleted_records", result.getDetails().getDeletedRecords()
                );
            }
        });
    }

    public static FluentMap<String, Object> prepareEmailData(@Nullable JobRunContext jobContext, @Nullable EtlProcessingResult operationResult) {
        return prepareEmailData(jobContext, null, null, operationResult);
    }

    public static FluentMap<String, Object> prepareEmailData(@Nullable JobRunContext jobContext, @Nullable Exception exception, @Nullable EtlProcessingResult operationResult) {
        return prepareEmailData(jobContext, exception, exception == null ? null : FaultEventImpl.error(exception), operationResult);
    }

    public static FluentMap<String, Object> prepareEmailData(@Nullable JobRunContext jobContext, @Nullable FaultEvent fault, @Nullable EtlProcessingResult operationResult) {
//        return prepareEmailData(jobContext, fault == null ? null : fault.getException(), fault, operationResult);
        return prepareEmailData(jobContext, null, fault, operationResult);
    }

    private static FluentMap<String, Object> prepareEmailData(@Nullable JobRunContext jobContext, @Nullable Throwable exception, @Nullable FaultEvent fault, @Nullable EtlProcessingResult operationResult) {
        return mapOf(String.class, Object.class).with().accept(m -> {
            if (jobContext != null) {
                m.put("cm_job_run", jobContext.getJobRunId(),
                        "cm_job_name", jobContext.getJob().getCode());
            }
            if (fault == null) {
                m.put("cm_import_failed", false);
                if (operationResult != null && operationResult.hasErrors()) {
                    m.put(
                            "cm_import_error_desc_tech", operationResult.getErrorsDescription(),
                            "cm_import_error_desc", operationResult.getErrorsDescription()
                    );
                }
            } else {
                m.put(
                        "cm_import_failed", true,
                        "cm_import_error", firstNotNull(exception, fault),
                        "cm_import_error_desc_tech", fault.getMessage(), //exceptionToMessage(exception),
                        "cm_import_error_desc", fault.getUserMessage()//exceptionToUserMessage(exception)
                );
            }
            if (operationResult != null) {
                m.put(
                        "cm_import_result", operationResult,//TODO data for template
                        "cm_import_result_description", operationResult.getResultDescription(),
                        "cm_import_errors_description", operationResult.getErrorsDescription(),
                        "cm_import_processed_count", operationResult.getProcessedRecordCount(),
                        "cm_import_created_count", operationResult.getCreatedRecordCount(),
                        "cm_import_modified_count", operationResult.getModifiedRecordCount(),
                        "cm_import_deleted_count", operationResult.getDeletedRecordCount(),
                        "cm_import_unmodified_count", operationResult.getUnmodifiedRecordCount(),
                        "cm_import_errors_count", operationResult.getErrors().size(),
                        "cm_import_has_errors", !operationResult.getErrors().isEmpty(),
                        "cm_import_errors", operationResult.getErrors().stream().map(e -> {
                            Pair<String, List<String>> error = serializeError(e.getUserErrorMessage());
                            return map(
                                    "index", e.getRecordIndex(),
                                    "row", e.getRecordLineNumber(),
                                    "record", e.getRecordData(),
                                    "message", format(error.getKey(), error.getValue().toArray()));
                        }).collect(toList()));
            }
        });

    }

}
