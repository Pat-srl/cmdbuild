/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway;

import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.storage.WaterwayStorageCommons;
import org.cmdbuild.minions.MinionComponent;

public interface WaterwayService extends MinionComponent, WaterwayStorageCommons {

    final String WATERWAY_SERVICE_MINION = "Waterway Engine";

    PagedElements<WaterwayMessage> getMessages(DaoQueryOptions query);

    WaterwayServiceRequestHelper newRequest();

    WaterwayMessageUpdateHelper updateMessage(WaterwayMessage message);

    WaterwayMessagesStats getMessagesStats();

    default WaterwayServiceRequestHelper newRequest(String target) {
        return newRequest().withTarget(target);
    }

    default WaterwayMessage submitRequest(String code, Object payload) {
        return newRequest(code).withPayload(payload).submit();
    }

    default WaterwayMessage submitRequest(String code, Object payload, Map<String, String> meta) {
        return newRequest(code).withPayload(payload).withMeta(meta).submit();
    }

    default WaterwayMessage submitRequest(String code, Map<String, String> meta) {
        return newRequest(code).withMeta(meta).submit();
    }

    default WaterwayMessage submitRequest(String code) {
        return newRequest(code).submit();
    }

    default WaterwayMessage submitRequest(String code, @Nullable Object payload, String... meta) {
        return newRequest(code).withPayload(payload).withMeta(meta).submit();
    }

}
