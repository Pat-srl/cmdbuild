/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.jobs;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_MESSAGE_RETRY_DELAY;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_MESSAGE_RETRY_TIMESTAMP;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageStatus.WMS_QUEUED;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WaterwayDelayedMessageJobRunner implements JobRunner {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public final static String WY_DELAYED_MESSAGES_JOB_TYPE = "etl_delayed",
            WY_DELAYED_MESSAGES_JOB_ATTR_MESSAGE_STORAGE = "etl_job_storage",
            WY_DELAYED_MESSAGES_JOB_ATTR_MESSAGE_ID = "etl_job_messageId";

    private final WaterwayService service;

    public WaterwayDelayedMessageJobRunner(WaterwayService service) {
        this.service = checkNotNull(service);
    }

    @Override
    public String getJobRunnerName() {
        return WY_DELAYED_MESSAGES_JOB_TYPE;
    }

    @Override
    public void runJob(JobData job, JobRunContext jobContext) {
        WaterwayMessage message = service.getMessage(job.getConfigNotBlank(WY_DELAYED_MESSAGES_JOB_ATTR_MESSAGE_STORAGE), job.getConfigNotBlank(WY_DELAYED_MESSAGES_JOB_ATTR_MESSAGE_ID));
        logger.debug("requeue delayed message = {}", message);
        service.updateMessage(message).withStatus(WMS_QUEUED).withoutMeta(WY_MESSAGE_RETRY_DELAY, WY_MESSAGE_RETRY_TIMESTAMP).update();//TODO error management etc
    }

}
