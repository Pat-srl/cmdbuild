package org.cmdbuild.etl.job;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.etl.waterway.event.EtlAfterProcessingEvent;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;

public class EtlAfterProcessingEventImpl implements EtlAfterProcessingEvent {

    private final WaterwayMessage message;

    public EtlAfterProcessingEventImpl(WaterwayMessage message) {
        this.message = checkNotNull(message);
    }

    @Override
    public WaterwayMessage getMessage() {
        return message;
    }

}
