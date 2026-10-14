/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.storage;

import java.util.List;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.etl.waterway.WaterwayMessagesStats;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;

public interface WaterwayStorageService extends WaterwayStorageCommons {

    WaterwayStorageHandler getStorage(String code);

    WaterwayStorageHandler getStorageOrDefaultForQueueCode(String code);

    List<WaterwayStorageHandler> getAllStorageHandlers();

    PagedElements<WaterwayMessage> getMessages(DaoQueryOptions query);

    WaterwayMessagesStats getMessagesStats();

}
