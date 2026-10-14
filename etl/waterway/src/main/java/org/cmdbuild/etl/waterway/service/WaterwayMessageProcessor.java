/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.service;

import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.parseItemKey;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;

public interface WaterwayMessageProcessor {

    String getKey();

    WaterwayMessage queueMessage(WaterwayMessage message);

    default String getCode() {
        return parseItemKey(getKey()).getCode();
    }
}
