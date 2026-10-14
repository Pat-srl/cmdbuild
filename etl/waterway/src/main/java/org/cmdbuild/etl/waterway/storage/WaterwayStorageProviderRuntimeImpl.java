/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.storage;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.utils.FilterProcessor.predicateFromFilter;
import static org.cmdbuild.dao.utils.SorterProcessor.comparatorFromSorter;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_STORAGE;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.configItemKey;
import org.cmdbuild.etl.waterway.WaterwayMessagesStats;
import org.cmdbuild.etl.waterway.message.MessageReference;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import static org.cmdbuild.etl.waterway.storage.WaterwayStorageHandler.WY_STORAGE_TYPE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WaterwayStorageProviderRuntimeImpl implements WaterwayStorageProvider {

    private final static String RUNTIME_STORAGE_PROVIDER_TYPE = "runtime",
            DEFAULT_RUNTIME_STORAGE_PROVIDER_CODE = "runtime";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService config;

    private final Map<String, WaterwayMessage> messagesByKey = new ConcurrentHashMap<>();//TODO cleanup ??

    public WaterwayStorageProviderRuntimeImpl(WaterwayDescriptorService config) {
        this.config = checkNotNull(config);
    }

    @Override
    public List<WaterwayStorageHandler> getStorageHandlers() {
        logger.debug("load runtime storage providers");
        List<WaterwayStorageHandler> list = (List) config.getAllItems(WYCIT_STORAGE).stream().filter(i -> equal(i.getConfig(WY_STORAGE_TYPE), RUNTIME_STORAGE_PROVIDER_TYPE)).map(i -> new RuntimeStorageProvider(i)).collect(toImmutableList());
        if (!list.stream().anyMatch(c -> equal(c.getCode(), DEFAULT_RUNTIME_STORAGE_PROVIDER_CODE))) {
            logger.debug("add default runtime storage provider ( code =< {} > )", DEFAULT_RUNTIME_STORAGE_PROVIDER_CODE);
            list = list(list).with(new RuntimeStorageProvider(configItemKey("system", DEFAULT_RUNTIME_STORAGE_PROVIDER_CODE), map(WY_STORAGE_TYPE, RUNTIME_STORAGE_PROVIDER_TYPE)));
        }
        logger.debug("loaded {} runtime storage providers", list.size());
        return list;
    }

    @Override
    public PagedElements<WaterwayMessage> getMessages(DaoQueryOptions query) {
        List<WaterwayMessage> messages = messagesByKey.values().stream()
                .filter(predicateFromFilter(query.getFilter()))//TODO map query attr names !!
                .sorted(comparatorFromSorter(query.getSorter()))//TODO map query attr names !!
                .collect(toImmutableList());
        return paged(messages, query);
    }

    private class RuntimeStorageProvider implements WaterwayStorageHandler {

        private final String key;

        public RuntimeStorageProvider(WaterwayItem config) {
            this(config.getKey(), config.getConfig());
        }

        public RuntimeStorageProvider(String key, Map<String, String> config) {
            this.key = checkNotBlank(key);
            checkArgument(equal(config.get(WY_STORAGE_TYPE), RUNTIME_STORAGE_PROVIDER_TYPE), "invalid config storage type");
            //TODO
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public WaterwayMessage createMessage(WaterwayMessage message) {
            checkArgument(getMessageOrNull(message.getMessageId()) == null);
            logger.debug("create message = {}", message);
            return setMessage(message);
        }

        @Override
        public WaterwayMessage updateMessage(WaterwayMessage message) {
            checkArgument(getMessageOrNull(message.getMessageId()) != null);
            logger.debug("update message = {}", message);
            return setMessage(message);
        }

        @Override
        public void deleteMessage(String messageId) {
            logger.debug("delete message =< {} >", messageId);
            checkArgument(messagesByKey.remove(key(getCode(), checkNotBlank(messageId))) != null, "failed to remove message with id =< %s >", messageId);
        }

        @Override
        public WaterwayMessage getMessageOrNull(MessageReference messageReference) {
            WaterwayMessage message = messagesByKey.get(key(getCode(), checkNotBlank(messageReference.getMessageId())));
            return (message != null && (!messageReference.hasTransactionId() || equal(message.getTransactionId(), messageReference.getTransactionId()))) ? message : null;//TODO store prev transactions ??
        }

        @Override
        public WaterwayMessageAttachment getMessageAttachmentLoadData(MessageReference messageReference, String attachmentName) {
            WaterwayMessageAttachment attachment = getMessage(messageReference).getAttachment(attachmentName);
            //TODO load attachment data, if necessary
            return attachment;
        }

        @Override
        public String toString() {
            return "RuntimeStorageProvider{" + "key=" + key + '}';
        }

        private WaterwayMessage setMessage(WaterwayMessage message) {
//            message = WaterwayMessageImpl.copyOf(message).withStorage(getKey()).build();
            message.checkHasStorageKey(key);
            messagesByKey.put(key(getCode(), message.getMessageId()), message);
            return message;
        }

//        @Override
//        public List<WaterwayMessage> getDelayedMessages(ZonedDateTime expiredBeforeTimestamp) {//TODO handle message conversion etc
//            return list(messagesById.values()).filter(m -> m.hasStatus(WMS_STANDBY) && m.hasMetaNotBlank(WY_MESSAGE_RETRY_TIMESTAMP) && toDateTime(m.getMeta(WY_MESSAGE_RETRY_TIMESTAMP)).isBefore(expiredBeforeTimestamp));
//        }
        @Override
        public WaterwayMessagesStats getMessagesStats() {
            return new WaterwayMessagesStatsImpl(list(messagesByKey.values()).stream().filter(m -> equal(m.getStorageCode(), getCode()))
                    .reduce(map(new WaterwayMessagesStatsImpl().getMessageCountByStatus()), (s, m) -> map(s).with(m.getStatus(), s.get(m.getStatus()) + 1), (a, b) -> map(a).mapValues((k, v) -> b.get(k) + v)));
        }
    }

}
