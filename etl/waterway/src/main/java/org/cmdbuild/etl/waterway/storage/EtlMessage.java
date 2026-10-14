/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.storage;

import jakarta.annotation.Nullable;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;

public interface EtlMessage extends WaterwayMessage {

    final String ETL_MESSAGE_ATTR_MESSAGE_STATUS = "MessageStatus",
            ETL_MESSAGE_ATTR_TRANSACTION_ID = "TransactionId",
            ETL_MESSAGE_ATTR_STORAGE_CODE = "Storage",
            ETL_MESSAGE_ATTR_NODE_ID = "NodeId",
            ETL_MESSAGE_ATTR_ATTACHMENTS = "Attachments",
            ETL_MESSAGE_ATTR_QUEUE_CODE = "Queue";

    @Nullable
    Long getId();

}
