/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.String.format;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.IOUtils;
import static org.apache.commons.io.IOUtils.copyLarge;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.cmdbuild.common.log.LoggerConfigService;
import org.cmdbuild.config.BugreportConfiguration;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.dao.postgres.services.DumpService;
import org.cmdbuild.utils.crypto.CmDataCryptoUtils;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import org.cmdbuild.utils.io.BigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.io.CmNetUtils.getHostname;
import static org.cmdbuild.utils.io.CmPropertyUtils.serializeMapAsProperties;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BugReportServiceImpl implements BugReportService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BugreportConfiguration config;
    private final LoggerConfigService loggerService;
    private final DumpService dumpService;
    private final GlobalConfigService configService;

    public BugReportServiceImpl(BugreportConfiguration config, LoggerConfigService loggerService, DumpService dumpService, GlobalConfigService configService) {
        this.config = checkNotNull(config);
        this.loggerService = checkNotNull(loggerService);
        this.dumpService = checkNotNull(dumpService);
        this.configService = checkNotNull(configService);
    }

    @Override
    public DataSource generateBugReport(@Nullable String password) {
        File zip = generateBugReportFile(null, password);
        BigByteArray zipData = toBigByteArray(zip);
        deleteQuietly(zip);
        String zipName = generateZipName();
        logger.info("return debug info zip {} {}", zipName, byteCountToDisplaySize(zipData.length()));
        return newDataSource(zipData, "application/octet-stream", zipName);
    }

    @Override
    public BugReportInfo sendBugReport(@Nullable String message, @Nullable String password) {
        File zip = generateBugReportFile(message, password);
        String zipName = generateZipName();
        try {
            logger.info("begin upload of bug report zip file {} {}", zipName, byteCountToDisplaySize(zip.length()));
            uploadBugreport(zip, zipName);//TODO get report server via config
            logger.info("completed upload of bug report zip file {} {}", zipName, byteCountToDisplaySize(zip.length()));
            return new DebugInfoImpl(zipName);
        } finally {
            deleteQuietly(zip);
        }
    }

    private File generateBugReportFile(@Nullable String message, @Nullable String password) {
        try {

            logger.info("building debug info zip file");
            List<Pair<String, Supplier<InputStream>>> list = list();

            loggerService.getActiveLogFiles().forEach(rethrowConsumer((file) -> {
                logger.debug("load log file = {}", file.getAbsolutePath());
                list.add(Pair.of(file.getName(), rethrowSupplier(() -> new FileInputStream(file))));
            }));

//        list.add(toPair(reportService.executeReportFromFile("system_status_log_report", ReportFormat.PDF))); TODO
//        list.add(toPair(reportService.executeReportFromFile("system_status_log_report", ReportFormat.CSV))); TODO
            list.add(Pair.of("system.conf", () -> new ByteArrayInputStream(serializeMapAsProperties(configService.getConfigAsMap()).getBytes())));//TODO improve config export format (see cli editconfig)

            if (isNotBlank(message)) {
                list.add(Pair.of("message.txt", () -> new ByteArrayInputStream(message.getBytes())));
            }

            File dump = tempFile();
            dumpService.dumpDatabaseToFile(dump);
            list.add(Pair.of("database.backup", rethrowSupplier(() -> new FileInputStream(dump))));

            File zip = tempFile();

            try (ZipOutputStream out = new ZipOutputStream(Optional.of((OutputStream) new FileOutputStream(zip))
                    .map(o -> isBlank(password) ? o : CmDataCryptoUtils.withPassword(password).encrypt(o)).get())) {
                for (Pair<String, Supplier<InputStream>> record : list) {
                    logger.debug("add zip entry = {}", record.getLeft());
                    ZipEntry zipEntry = new ZipEntry(checkNotBlank(record.getLeft()));
                    out.putNextEntry(zipEntry);
                    try (InputStream in = record.getValue().get()) {
                        copyLarge(in, out);
                    }
                    out.closeEntry();
                }
            } finally {
                deleteQuietly(dump);
            }

            return zip;
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    private String generateZipName() {
        return format("cmdbuild_bugreport_%s_%s.zip", dateTimeFileSuffix(), getHostname().toLowerCase().replaceAll("[^a-z0-9]", ""));
    }

    private void uploadBugreport(File reportfile, String filename) {
        logger.debug("send bug report to url = {}", config.getBugreportEndpoint());
        checkNotNull(reportfile);
        checkArgument(reportfile.exists() && reportfile.length() > 0, "reportfile is not valid");
        CloseableHttpClient client = HttpClientBuilder.create().build();
        try {
            HttpEntity payload = MultipartEntityBuilder.create()
                    .addBinaryBody("file", reportfile, ContentType.APPLICATION_OCTET_STREAM, filename)
                    .build();
            HttpPost request = new HttpPost(config.getBugreportEndpoint());
            request.setEntity(payload);
            String response = IOUtils.toString(client.execute(request).getEntity().getContent(), Charset.defaultCharset());
            checkArgument(fromJson(response, JsonNode.class).get("success").asBoolean() == true, "bug report upload error");//TODO improve this
        } catch (IOException ex) {
            throw runtime(ex);
        } finally {
            try {
                client.close();
            } catch (IOException ex) {
            }
        }
    }
}
