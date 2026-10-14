/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.job;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.net.URI;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccount;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailAttachmentImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.etl.EtlException;
import org.cmdbuild.etl.handler.FileReaderHelperService;
import static org.cmdbuild.etl.handler.FileReaderHelperService.FAIL_ON_MISSING_SOURCE_DATA_CONFIG;
import org.cmdbuild.etl.handler.FileReaderHelperService.FileReaderHelper;
import static org.cmdbuild.etl.job.ImportJobRunnerImpl.ImportJobSource.IS_FILE;
import static org.cmdbuild.etl.job.ImportJobRunnerImpl.ImportNotificationMode.IN_ALWAYS;
import static org.cmdbuild.etl.job.ImportJobRunnerImpl.ImportNotificationMode.IN_NEVER;
import static org.cmdbuild.etl.job.ImportJobRunnerImpl.ImportNotificationMode.IN_ON_ERRORS;
import org.cmdbuild.etl.loader.EtlNotification;
import org.cmdbuild.etl.loader.EtlNotification.EtlNotificationEvent;
import static org.cmdbuild.etl.loader.EtlNotification.EtlNotificationEvent.EN_ALWAYS;
import static org.cmdbuild.etl.loader.EtlNotification.EtlNotificationEvent.EN_ERROR;
import static org.cmdbuild.etl.loader.EtlNotification.EtlNotificationEvent.EN_SUCCESS;
import org.cmdbuild.etl.loader.EtlNotificationImpl;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateService;
import static org.cmdbuild.etl.utils.EtlResultUtils.prepareEmailData;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.template.ExpressionInputData;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ImportJobRunnerImpl implements JobRunner {

    public static final String IMPORT_JOB_TYPE = "import_file";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EtlTemplateService importExportService;
    private final EmailTemplateService emailTemplateService;
    private final EmailAccountService emailAccountService;
    private final EmailTemplateProcessorService emailTemplateProcessorService;
    private final EmailService emailService;
    private final FileReaderHelperService helperService;

    public ImportJobRunnerImpl(EtlTemplateService importExportService, EmailTemplateService emailTemplateService, EmailAccountService emailAccountService, EmailTemplateProcessorService emailTemplateProcessorService, EmailService emailService, FileReaderHelperService helperService) {
        this.importExportService = checkNotNull(importExportService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailAccountService = checkNotNull(emailAccountService);
        this.emailTemplateProcessorService = checkNotNull(emailTemplateProcessorService);
        this.emailService = checkNotNull(emailService);
        this.helperService = checkNotNull(helperService);
    }

    @Override
    public String getJobRunnerName() {
        return IMPORT_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        try {
            logger.debug("preparing import job = {}", jobData);
            new ImportJobHelper(jobData, jobContext).runImportJob();
        } catch (Exception ex) {
            throw new EtlException(ex, "error executing import job = %s", jobData);
        }
    }

    @Override
    public void vaildateJob(JobData jobData) {
        new ImportJobConfig(jobData);
    }

    private class ImportJobConfig {

        private final JobData jobData;
        private final EtlTemplate template;
        private final String url;
        private final ImportJobSource source;
        private final boolean failOnMissingSourceData, attachImportReport;
        private final FileReaderHelper helper;
        private final List<EtlNotification> notifications;

        public ImportJobConfig(JobData jobData) {
            this.jobData = checkNotNull(jobData);
            this.template = importExportService.getTemplateByName(jobData.getConfigNotBlank("template"));
            this.source = parseEnum(jobData.getConfigNotBlank("source"), ImportJobSource.class);
            switch (source) {
                case IS_FILE -> {
                    helper = helperService.newHelper(jobData.getConfig());
                    url = null;
                }
                case IS_URL -> {
                    helper = null;
                    url = jobData.getConfigNotBlank("url");
                }
                default ->
                    throw new EtlException("unsupported source = %s", source);
            }

            EmailAccount errorAccount = Optional.ofNullable(firstNotBlankOrNull(toStringOrNull(jobData.getConfig("errorAccount")), template.getErrorAccount())).map(emailAccountService::getAccount).orElse(null),
                    notificationAccount = Optional.ofNullable(firstNotBlankOrNull(toStringOrNull(jobData.getConfig("notificationAccount")), template.getNotificationAccount())).map(emailAccountService::getAccount).orElse(null);
            EmailTemplate errorTemplate = Optional.ofNullable(firstNotBlankOrNull(toStringOrNull(jobData.getConfig("errorTemplate")), template.getErrorTemplate())).map(emailTemplateService::getByName).orElse(null),
                    notificationTemplate = Optional.ofNullable(firstNotBlankOrNull(toStringOrNull(jobData.getConfig("notificationTemplate")), template.getNotificationTemplate())).map(emailTemplateService::getByName).orElse(null);

            ImportNotificationMode notificationMode = parseEnumOrDefault(jobData.getConfig("notificationMode"), errorTemplate == null ? IN_NEVER : IN_ON_ERRORS);

            notifications = (List) list().accept(l -> {
                switch (notificationMode) {
                    case IN_ALWAYS -> {
                        checkNotNull(notificationTemplate, "notification mode is `always`, but notification template is missing");
                        if (errorTemplate != null) {
                            l.add(buildNotification(errorTemplate, errorAccount, EN_ERROR));
                            l.add(buildNotification(notificationTemplate, notificationAccount, EN_SUCCESS));
                        } else {
                            l.add(buildNotification(notificationTemplate, notificationAccount, EN_ALWAYS));
                        }
                    }
                    case IN_ON_ERRORS -> {
                        checkNotNull(errorTemplate, "notification mode is `on_errors`, but error template is missing");
                        l.add(buildNotification(errorTemplate, errorAccount, EN_ERROR));
                    }
                }
            }).immutableCopy();
            failOnMissingSourceData = toBooleanOrDefault(jobData.getConfig(FAIL_ON_MISSING_SOURCE_DATA_CONFIG), true);
            attachImportReport = toBooleanOrDefault(jobData.getConfig("attachImportReport"), false);
        }

        public boolean failForMissingFile() {
            return failOnMissingSourceData;
        }

        public boolean failForMissingDir() {
            return failOnMissingSourceData;
        }

    }

    private static EtlNotification buildNotification(EmailTemplate template, @Nullable EmailAccount account, EtlNotificationEvent event) {
        return EtlNotificationImpl.builder().withTemplate(template.getCode()).withAccount(account == null ? null : account.getName()).withEvent(event).build();
    }

    private class ImportJobHelper {

        private final ImportJobConfig config;
        private final JobRunContext context;

        private Optional<File> sourceFile;

        public ImportJobHelper(JobData jobData, JobRunContext jobContext) {
            this.config = new ImportJobConfig(jobData);
            this.context = checkNotNull(jobContext);
        }

        public void runImportJob() {
            logger.info("executing import job = {}", config.jobData);
            try {
                byte[] data = loadDataForImport();
                if (data == null) {
                    logger.info(marker(), "skip import operation: no data available for import");
                } else {
                    EtlProcessingResult operationResult = importExportService.importDataWithTemplate(newDataSource(data, "application/octet-stream"), config.template);
                    handlePostImportAction();//TODO safe post import execution
                    sendMailIfConfigured(prepareEmailData(context, operationResult), operationResult.hasErrors(), operationResult);
                }
            } catch (Exception ex) {
                logger.error(marker(), "import operation failed with error", ex);
                sendMailIfConfiguredSafe(prepareEmailData(context, ex, null), true, null);
                throw new EtlException(ex);
            }
        }

        private void sendMailIfConfiguredSafe(Map<String, Object> mailData, boolean hasError, @Nullable EtlProcessingResult result) {
            try {
                sendMailIfConfigured(mailData, hasError, result);
            } catch (Exception ex) {
                logger.error(marker(), "email processing error", ex);
            }
        }

        private void sendMailIfConfigured(Map<String, Object> mailData, boolean hasError, @Nullable EtlProcessingResult result) {
            Set<EtlNotificationEvent> events = hasError ? EnumSet.of(EN_ALWAYS, EN_ERROR) : EnumSet.of(EN_ALWAYS, EN_SUCCESS);
            config.notifications.stream().filter(n -> n.hasEvent(events)).forEach(n -> {
                Email email = emailTemplateProcessorService.processEmail(
                        null, // builds synthesized draft email
                        ExpressionInputData.builder()
                                .withTemplate(emailTemplateService.getByName(n.getTemplate()))
                                .withOtherData((Map) map(
                                        "cm_import_source", getSourceDebugInfoStr()
                                ).with(mailData))
                                .build()
                );
                email = EmailImpl.copyOf(email).withStatus(ES_OUTGOING).accept(e -> {
                    if (n.hasAccount()) {
                        e.withAccount(emailAccountService.getAccount(n.getAccount()).getId());
                    }
                    if (config.attachImportReport && result != null) {
                        try {
                            DataSource dataSource = importExportService.buildImportResultReport(result, config.template);
                            e.withAttachments(EmailAttachmentImpl.build(dataSource));
                        } catch (Exception ex) {
                            logger.error(marker(), "error building result report", ex);
                        }
                    }
                }).build();
                email = emailService.create(email);
                logger.debug("sent notification email = {}", email);
            });
        }

        @Nullable
        private String getSourceDebugInfoStr() {
            return switch (config.source) {
                case IS_FILE ->
                    getFileForImportSafe() == null ? null : getFileForImportSafe().getAbsolutePath();
                case IS_URL ->
                    config.url;
            };
        }

        @Nullable
        private byte[] loadDataForImport() {
            return switch (config.source) {
                case IS_FILE ->
                    loadFileDataForImport();
                case IS_URL ->
                    loadUrlDataForImport();
            };
        }

        private byte[] loadUrlDataForImport() {
            try {
                logger.debug("load data from url = {}", config.url);
                URI uri = URI.create(config.url);
                byte[] data = toByteArray(uri.toURL().openStream());
                logger.debug("processing {} bytes from url = {}", FileUtils.byteCountToDisplaySize(data.length), config.url);
                return data;
            } catch (Exception ex) {
                throw new EtlException(ex, "error loading data from url = %s", config.url);
            }
        }

        @Nullable
        private byte[] loadFileDataForImport() {
            File fileToImport = getFileForImport();
            if (fileToImport == null) {
                return null;
            } else {
                logger.debug("found file for import = {}", fileToImport.getAbsolutePath());
                byte[] data = toByteArray(fileToImport);
                logger.debug("processing {} bytes from file = {}", FileUtils.byteCountToDisplaySize(data.length), fileToImport.getAbsolutePath());
                return data;
            }
        }

        @Nullable
        private File getFileForImport() {
            if (sourceFile == null) {
                sourceFile = Optional.ofNullable(config.helper.getFileForImport());
            }
            return sourceFile.orElse(null);
        }

        @Nullable
        private File getFileForImportSafe() {
            try {
                return getFileForImport();
            } catch (Exception ex) {
                logger.warn("source file not found", ex);
                return null;
            }
        }

        private void handlePostImportAction() {
            logger.debug("handle post import action for source = {}", config.source);
            if (equal(config.source, IS_FILE)) {
                config.helper.handlePostImportAction();
            }
        }

    }

    public static enum ImportJobSource {
        IS_FILE, IS_URL
    }

    enum ImportNotificationMode {
        IN_ON_ERRORS, IN_ALWAYS, IN_NEVER
    }

}
