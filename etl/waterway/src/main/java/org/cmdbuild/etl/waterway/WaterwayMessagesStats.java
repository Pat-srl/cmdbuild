package org.cmdbuild.etl.waterway;

import java.util.Map;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;

public interface WaterwayMessagesStats {

    Map<WaterwayMessageStatus, Long> getMessageCountByStatus();

}
