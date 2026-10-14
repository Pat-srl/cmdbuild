/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.job;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailJobRunnerImpl implements JobRunner {

    public static final String EMAIL_JOB_TYPE = "emailService";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EmailJobHelperService emailReceiverService;

    public EmailJobRunnerImpl(EmailJobHelperService emailReaderService) {
        this.emailReceiverService = checkNotNull(emailReaderService);
    }

    @Override
    public String getJobRunnerName() {
        return EMAIL_JOB_TYPE;
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        EmailJobConfig config = new EmailJobConfigImpl(jobData);
        emailReceiverService.receiveEmailsWithConfig(config);
    }

}
