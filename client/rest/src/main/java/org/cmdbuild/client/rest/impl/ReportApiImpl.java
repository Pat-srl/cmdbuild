/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayInputStream;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.client.rest.api.ReportApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportInfoImpl;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class ReportApiImpl extends AbstractServiceClientImpl implements ReportApi {

    public ReportApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public ReportData executeAndDownload(String reportId, ReportFormat ext, Map<String, Object> params) {
        byte[] data = getBytes(format("reports/%s/report_%s.%s?extension=%s&parameters=%s",
                encodeUrlPath(trimAndCheckNotBlank(reportId)),
                encodeUrlPath(trimAndCheckNotBlank(reportId)),
                ext.name().toLowerCase(),//TODO ext to string utility
                ext.name(),
                encodeUrlQuery(toJson(checkNotNull(params)))));
        return new ReportData() {
            @Override
            public byte[] toByteArray() {
                return data;
            }
        };
    }

    @Override
    public ReportInfo createReport(ReportInfo reportInfo, List<Pair<String, byte[]>> files) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        files.forEach((f) -> {
            builder.addBinaryBody("file_" + randomId(), listenUpload(format("report file %s upload", f.getKey()), new ByteArrayInputStream(f.getValue())), ContentType.APPLICATION_OCTET_STREAM, f.getKey());
        });
        builder.addTextBody("data", toJson(map(
                "code", reportInfo.getCode(),
                "description", reportInfo.getDescription(),
                "config", reportInfo.getConfig(),
                "active", reportInfo.isActive()
        )), ContentType.APPLICATION_JSON);
        HttpEntity multipart = builder
                .setContentType(ContentType.MULTIPART_FORM_DATA)
                .build();
        long id = post("administration/reports", multipart).asJackson().get("data").get("_id").asLong();
        return ReportInfoImpl.copyOf(reportInfo).withId(id).build();//TODO read full report info from response
    }

    @Override
    public boolean reportExists(String reportCode) {
        return (get("reports?filter=%7B%22attribute%22%3A%7B%22simple%22%3A%7B%22attribute%22%3A%22code%22%2C%22operator%22%3A%22equal%22%2C%22value%22%3A%5B%22" + reportCode + "%22%5D%7D%7D%7D")
                .asJackson().get("meta").get("total").asInt() == 1);
    }

    @Override
    public void uploadReportTemplate(String reportId, List<Pair<String, byte[]>> files) {
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        files.forEach((f) -> {
            builder.addBinaryBody("file_" + randomId(), listenUpload(format("report file %s upload", f.getKey()), new ByteArrayInputStream(f.getValue())), ContentType.APPLICATION_OCTET_STREAM, f.getKey());
        });
        HttpEntity multipart = builder
                .setContentType(ContentType.MULTIPART_FORM_DATA)
                .build();
        put(format("administration/reports/%s/template", reportId), multipart);
    }

}
