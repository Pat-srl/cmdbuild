package org.cmdbuild.etl.waterway.event;

import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;

public interface EtlEvent {

    WaterwayMessage getMessage();
    
    default WaterwayMessageStatus getStatus(){
        return getMessage().getStatus();
    }

    default String getQueue() {
        return getMessage().getQueue();
    }

}
