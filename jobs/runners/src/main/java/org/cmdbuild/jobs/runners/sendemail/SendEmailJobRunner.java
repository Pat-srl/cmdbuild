/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.sendemail;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.Optional;
import jakarta.activation.DataHandler;
import static org.apache.commons.lang3.StringUtils.trimToNull;
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
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.report.ReportConfig;
import org.cmdbuild.report.ReportConfigImpl;
import org.cmdbuild.report.ReportInfo;
import org.cmdbuild.report.ReportService;
import org.cmdbuild.template.ExpressionInputData;
import org.cmdbuild.template.SimpleExpressionInputData;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SendEmailJobRunner implements JobRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EmailTemplateService templateService;
    private final EmailAccountService accountService;
    private final ReportService reportService;
    private final EmailService emailService;
    private final EmailTemplateProcessorService templateProcessorService;

    public SendEmailJobRunner(EmailTemplateService templateService, EmailAccountService accountService, ReportService reportService, EmailService emailService, EmailTemplateProcessorService templateProcessorService) {
        this.templateService = checkNotNull(templateService);
        this.accountService = checkNotNull(accountService);
        this.reportService = checkNotNull(reportService);
        this.emailService = checkNotNull(emailService);
        this.templateProcessorService = checkNotNull(templateProcessorService);
    }

    @Override
    public String getJobRunnerName() {
        return "sendemail";
    }

    @Override
    public void vaildateJob(JobData jobData) {
        new SendEmailJobRunHelper(jobData);
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        new SendEmailJobRunHelper(jobData).run();
    }

    private class SendEmailJobRunHelper {

        private final JobData jobData;
        private final EmailTemplate template;
        private final EmailAccount account;
        private final Map<String, String> emailContext;
        private final boolean attachReport;
        private final ReportInfo report;
        private final ReportConfig reportConfig;

        public SendEmailJobRunHelper(JobData jobData) {
            this.jobData = checkNotNull(jobData);
            this.template = templateService.getByName(jobData.getConfigNotBlank("email_template"));
            this.account = Optional.ofNullable(trimToNull(jobData.getConfig("email_account"))).map(accountService::getAccount).orElse(null);
            emailContext = unflattenMap(jobData.getConfig(), "email_template_context");
            attachReport = toBooleanOrDefault(jobData.getConfig("attach_report_enabled"), false);
            if (attachReport) {
                reportConfig = ReportConfigImpl.fromConfig(map(jobData.getConfig()).filterMapKeys("attach_report_"));
                report = reportService.getByCode(reportConfig.getCode());
            } else {
                reportConfig = null;
                report = null;
            }
        }

        public void run() {
            logger.debug("send email with optional report, email template = {}", template);
            Email email = templateProcessorService.processEmail(
                    EmailImpl.builder().withStatus(ES_OUTGOING).build(),
                    ExpressionInputData.builder()
                            .withTemplate(template)
                            .withOtherData((Map) emailContext)
                            .build()
            );
            if (account != null) {
                email = EmailImpl.copyOf(email).withAccount(account.getId()).build();
            }
            if (attachReport) {
                Map<String, Object> params = templateProcessorService.processMultipleExpressions(
                        SimpleExpressionInputData.extendedBuilder()
                                .withExpressions(reportConfig.getParams())
                                .build()
                );
                logger.debug("build report = {} with params = {}", report, mapToLoggableStringInline(params));
                DataHandler reportAttachment = reportService.executeReportAndDownload(report.getId(), reportConfig.getFormat(), params);
                email = EmailImpl.copyOf(email).withAttachments(EmailAttachmentImpl.build(toDataSource(reportAttachment))).build();
            }
            emailService.create(email);
        }

    }

}
