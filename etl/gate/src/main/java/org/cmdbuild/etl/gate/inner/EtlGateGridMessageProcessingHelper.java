package org.cmdbuild.etl.gate.inner;

import org.cmdbuild.etl.waterway.message.WaterwayMessage;

public interface EtlGateGridMessageProcessingHelper {

    WaterwayMessage queueGridMessageForLocalProcessing(String messageReference);
}
