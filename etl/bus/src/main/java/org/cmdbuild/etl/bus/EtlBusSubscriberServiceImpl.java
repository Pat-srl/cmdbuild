package org.cmdbuild.etl.bus;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_BUS;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_COLLECTOR;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_GATE;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EtlBusSubscriberServiceImpl implements EtlBusSubscriberService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService configService;
    private final WaterwayService service;

//    private final MinionHandler minionHandler;
    public EtlBusSubscriberServiceImpl(WaterwayDescriptorService configService, WaterwayService service) {
        this.configService = checkNotNull(configService);
        this.service = checkNotNull(service);
//        minionHandler=MinionHandlerImpl.builder() TODO
//                .withName("Waterway_ Bus")
//                .reloadOnConfigs(WaterwayConfig.class)
//                .build();
    }

    @Override
    public List<EtlBusSubscriber> getSubscribersForBus(String code) {
        checkNotBlank(code);
        return configService.getAllItemsEnabled(WYCIT_GATE, WYCIT_BUS, WYCIT_COLLECTOR).stream().filter(i -> equal(i.getConfig(WY_SUBSCRIBE_CONFIG), code))
                .map(EtlBusSubscriberImpl::new)
                .collect(toImmutableList());
    }

    @Override
    public EtlBusSubscriber getSubscriber(String busCode, String subscriberId) {
        checkNotBlank(subscriberId);
        return getSubscribersForBus(busCode).stream().filter(s -> equal(s.getSubscriberId(), subscriberId)).collect(onlyElement("subscriber not found for bus =< %s > subscriberId =< %s >", busCode, subscriberId));
    }

    @Override
    public void deliverMessage(String busCode, String subscriberId, WaterwayMessage message) {
        EtlBusSubscriber subscriber = getSubscriber(busCode, subscriberId);
        logger.debug("deliver message = {} to subscriber = {}", message, subscriber);
        service.newRequest()
                .withMessageIdAndHistory(message.getMessageId(), message.getHistory())
                .withTarget(((EtlBusSubscriberImpl) subscriber).item.getCode())//TODO improve this
                .withMeta(message.getMeta())
                .withPayload(message.getAttachments())
                .submit();
        logger.debug("delivered message = {} to subscriber = {}", message, subscriber);
    }

    private class EtlBusSubscriberImpl implements EtlBusSubscriber {

        private final WaterwayItem item;

        public EtlBusSubscriberImpl(WaterwayItem item) {
            this.item = checkNotNull(item);
        }

        @Override
        public String getSubscriberId() {
            return "wy_%s_%s".formatted(serializeEnum(item.getType()), item.getCode());
        }

        @Override
        public String toString() {
            return "EtlBusSubscriber{" + "subscriberId=" + getSubscriberId() + ",item=" + item + '}';
        }

    }

}
