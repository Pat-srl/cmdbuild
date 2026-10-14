/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message;

import java.time.ZonedDateTime;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.getItemCodeFromKeyOrCode;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.buildMessageKey;

public interface WaterwayMessageHistoryRecord {

    ZonedDateTime getTimestamp();

    WaterwayMessageStatus getStatus();

    int getTransactionId();

    String getMessageId();

    String getQueueKey();

    String getStorageKey();

    String getNodeId();

    default String getQueueCode() {
        return getItemCodeFromKeyOrCode(getQueueKey());
    }

    default String getStorageCode() {
        return getItemCodeFromKeyOrCode(getStorageKey());
    }

    default String getMessageKey() {
        return buildMessageKey(getMessageId(), getTransactionId());
    }
}
