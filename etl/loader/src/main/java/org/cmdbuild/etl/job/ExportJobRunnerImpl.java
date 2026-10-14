/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.job;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import jakarta.activation.DataSource;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.config.api.DirectoryService;
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
import static org.cmdbuild.etl.job.ExportJobRunnerImpl.ExportNotificationMode.EN_ALWAYS;
import static org.cmdbuild.etl.job.ExportJobRunnerImpl.ExportNotificationMode.EN_ATTACH_FILE;
import static org.cmdbuild.etl.job.ExportJobRunnerImpl.ExportNotificationMode.EN_NEVER;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.template.ExpressionInputData;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ExportJobRunnerImpl implements JobRunner {

    public static final String EXPORT_JOB_TYPE = "export_file";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EtlTemplateService importExportService;
    private final EmailTemplateService emailTemplateService;
    private final EmailAccountService emailAccountService;
    private final EmailTemplateProcessorService emailTemplateProcessorService;
    private final EmailService emailService;
    private final DirectoryService directoryService;

    public ExportJobRunnerImpl(EtlTemplateService importExportService, EmailTemplateService emailTemplateService, EmailAccountService emailAccountService, EmailTemplateProcessorService emailTemplateProcessorService, EmailService emailService, DirectoryService directoryService) {
        this.importExportService = checkNotNull(importExportService);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailAccountService = checkNotNull(emailAccountService);
        this.emailTemplateProcessorService = checkNotNull(emailTemplateProcessorService);
        this.emailService = checkNotNull(emailService);
        this.directoryService = checkNotNull(directoryService);
    }

    @Override
    public String getJobRunnerName() {
        return EXPORT_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        try {
            logger.debug("preparing export job = {}", jobData);
            new ExportJobHelper(jobData).runExportJob();
        } catch (Exception ex) {
            throw new EtlException(ex, "error executing import job = %s", jobData);
        }
    }

    private class ExportJobHelper {

        private final JobData jobData;
        private final EtlTemplate template;
        private final EmailTemplate emailTemplate;
        private final EmailAccount emailAccount;
        private final String directory, fileName;
        private final ExportNotificationMode notificationMode;

        public ExportJobHelper(JobData jobData) {
            this.jobData = checkNotNull(jobData);
            this.template = importExportService.getTemplateByName(jobData.getConfigNotBlank("template"));
            this.notificationMode = parseEnum(jobData.getConfigNotBlank("notificationMode"), ExportNotificationMode.class);
            directory = jobData.getConfigNotBlank("directory");
            fileName = toStringOrNull(jobData.getConfig().get("fileName"));
            String emailTemplateCode = toStringOrNull(jobData.getConfig().get("emailTemplate")),
                    emailAccountCode = toStringOrNull(jobData.getConfig().get("emailAccount"));
            emailTemplate = isBlank(emailTemplateCode) ? null : emailTemplateService.getByName(emailTemplateCode);
            emailAccount = isBlank(emailAccountCode) ? null : emailAccountService.getAccount(emailAccountCode);
            if (!equal(notificationMode, EN_NEVER)) {
                checkNotNull(emailTemplate, "missing email notification template");
            }
        }

        public void runExportJob() {
            logger.info("executing export job = {} with template = {}", jobData, template);
            File targetFile = null;
            EtlException ex = null;
            DataSource exportData = null;
            try {
                exportData = importExportService.exportDataWithTemplate(template);
                String actualFileName = fileName;
                if (actualFileName.contains("TIMESTAMP")) {
                    actualFileName = actualFileName.replace("TIMESTAMP", CmDateUtils.dateTimeFileSuffix());
                }
                //            if (isBlank(FilenameUtils.getExtension(actualFileName))) {
                //                actualFileName = format("%s.%s", FilenameUtils.getBaseName(actualFileName), checkNotBlank(FilenameUtils.getExtension(exportData.getName())));
                //            }
                File targetDir = directoryService.getFileRelativeToContainerDirectoryIfAvailableAndNotAbsolute(new File(directory));
                targetDir.mkdirs();
                checkArgument(targetDir.isDirectory(), "invalid target dir = %s", targetDir.getAbsolutePath());
                targetFile = new File(targetDir, actualFileName);
                logger.info(marker(), "exported data = {} {} to file = {}", FileUtils.byteCountToDisplaySize(countBytes(exportData)), exportData.getContentType(), targetFile.getAbsolutePath());
                writeToFile(exportData, targetFile);
            } catch (Exception e) {
                ex = new EtlException(e, "error executing export job = %s with template = %s", jobData, template);
            }
            if (emailTemplate != null && (ex != null || set(EN_ALWAYS, EN_ATTACH_FILE).contains(notificationMode))) {
                FluentMap<String, Object> data = map("cm_export_job", jobData, "cm_export_job_config", jobData.getConfig(), "cm_export_template", template);
                if (ex != null) {
                    data.put("cm_export_success", false, "cm_export_error", ex, "cm_export_error_description", ex.toString());
                } else {
                    data.put("cm_export_success", true, "cm_export_file", targetFile.getAbsolutePath());
                }
                Email email = emailTemplateProcessorService.processEmail(
                        null, // builds synthesized draft email
                        ExpressionInputData.builder()
                                .withTemplate(emailTemplate)
                                .withOtherData(data)
                                .build()
                );
                email = EmailImpl.copyOf(email).withStatus(ES_OUTGOING).accept(e -> {
                    if (emailAccount != null) {
                        e.withAccount(emailAccount.getId());
                    }
                }).build();
                if (equal(EN_ATTACH_FILE, notificationMode) && exportData != null) {
                    email = EmailImpl.copyOf(email).withAttachments(list(EmailAttachmentImpl.build(exportData))).build();
                }
                email = emailService.create(email);
                logger.debug("sent notification email = %s", email);
            }
            if (ex != null) {
                throw ex;
            }
        }
    }

    enum ExportNotificationMode {
        EN_ON_ERRORS, EN_ALWAYS, EN_ATTACH_FILE, EN_NEVER
    }
}
