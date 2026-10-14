package org.cmdbuild.etl.bus;

import java.util.List;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;

public interface EtlBusSubscriberService {

    final String WY_SUBSCRIBE_CONFIG = "subscribe";

    List<EtlBusSubscriber> getSubscribersForBus(String code);

    EtlBusSubscriber getSubscriber(String busCode, String subscriberId);

    void deliverMessage(String busCode, String subscriberId, WaterwayMessage message);

//    interface DeliverResult{
//        boolean deliveredToAllSubscribers();
//    }
    enum EtlBusSubscriberType {
        ST_CONSUMER //TODO dynamic subscribers, ST_LISTENER, ST_
    }

    interface EtlBusSubscriber {

        String getSubscriberId();

    }

}
