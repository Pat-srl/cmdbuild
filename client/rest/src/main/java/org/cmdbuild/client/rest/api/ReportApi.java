/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import static com.google.common.collect.ImmutableList.toImmutableList;
import java.io.File;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfo;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;

public interface ReportApi {

    ReportData executeAndDownload(String reportId, ReportFormat ext, Map<String, Object> params);

    ReportInfo createReport(ReportInfo reportInfo, List<Pair<String, byte[]>> files);

    void uploadReportTemplate(String reportId, List<Pair<String, byte[]>> files);

    boolean reportExists(String reportCode);

    default ReportInfo createReport(ReportInfo reportInfo, Map<String, byte[]> files) {
        return createReport(reportInfo, files.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())).collect(toImmutableList()));
    }

    default void uploadReportTemplate(String reportId, Collection<File> files) {
        uploadReportTemplate(reportId, files.stream().map((f) -> Pair.of(f.getName(), toByteArray(f))).collect(toList()));
    }

    default ReportInfo createReport(ReportInfo reportInfo, Collection<File> files) {
        return createReport(reportInfo, files.stream().map((f) -> Pair.of(f.getName(), toByteArray(f))).collect(toList()));
    }

    default ReportData executeAndDownload(String reportId, ReportFormat ext) {
        return executeAndDownload(reportId, ext, emptyMap());
    }

    interface ReportData {

        byte[] toByteArray();
    }

}
