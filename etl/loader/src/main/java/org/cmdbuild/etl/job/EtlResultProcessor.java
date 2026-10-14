package org.cmdbuild.etl.job;

import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import org.cmdbuild.jobs.JobRunContext;

public interface EtlResultProcessor {

    WaterwayMessage handleProcessingResult(JobRunContext jobContext, WaterwayMessage message, WaterwayMessageData result);

}
