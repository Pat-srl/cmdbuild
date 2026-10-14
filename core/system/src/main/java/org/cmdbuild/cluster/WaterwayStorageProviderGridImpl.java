package org.cmdbuild.cluster;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.binary.BinaryReader;
import org.apache.ignite.binary.BinaryWriter;
import org.apache.ignite.binary.Binarylizable;
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
import org.cmdbuild.etl.waterway.message.WaterwayMessageImpl;
import org.cmdbuild.etl.waterway.storage.EtlMessageAttachmentsData;
import org.cmdbuild.etl.waterway.storage.WaterwayMessagesStatsImpl;
import org.cmdbuild.etl.waterway.storage.WaterwayStorageHandler;
import static org.cmdbuild.etl.waterway.storage.WaterwayStorageHandler.WY_STORAGE_TYPE;
import org.cmdbuild.etl.waterway.storage.WaterwayStorageProvider;
import org.cmdbuild.fault.FaultEvent;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WaterwayStorageProviderGridImpl implements WaterwayStorageProvider {

    private final static String GRID_STORAGE_PROVIDER_TYPE = "grid",
            DEFAULT_GRID_STORAGE_PROVIDER_CODE = "grid";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService config;
    private final IgniteService clusterService;

    public WaterwayStorageProviderGridImpl(WaterwayDescriptorService config, IgniteService clusterService) {
        this.config = checkNotNull(config);
        this.clusterService = checkNotNull(clusterService);
    }

    @Override
    public List<WaterwayStorageHandler> getStorageHandlers() {
        logger.debug("load grid storage handlers");
        List<WaterwayStorageHandler> list = config.getAllItems(WYCIT_STORAGE).stream().filter(i -> equal(i.getConfig(WY_STORAGE_TYPE), GRID_STORAGE_PROVIDER_TYPE)).map(ClusterStorageHandler::new).collect(toImmutableList());
        if (!list.stream().anyMatch(c -> equal(c.getCode(), DEFAULT_GRID_STORAGE_PROVIDER_CODE))) {
            logger.debug("add default grid storage provider ( code =< {} > )", DEFAULT_GRID_STORAGE_PROVIDER_CODE);
            list = list(list).with(new ClusterStorageHandler(configItemKey("system", DEFAULT_GRID_STORAGE_PROVIDER_CODE), map(WY_STORAGE_TYPE, GRID_STORAGE_PROVIDER_TYPE)));
        }
        logger.debug("loaded {} grid storage handlers", list.size());
        return list;
    }

    @Override
    public PagedElements<WaterwayMessage> getMessages(DaoQueryOptions query) {//TODO
        if (clusterService.isEnabled()) {
            List<WaterwayMessage> messages = getStorageHandlers().stream().map(ClusterStorageHandler.class::cast).flatMap(h -> stream(h.getCache())).map(e -> e.getValue().getMessage())
                    .filter(predicateFromFilter(query.getFilter()))//TODO map query attr names !!
                    .sorted(comparatorFromSorter(query.getSorter()))//TODO map query attr names !!
                    .collect(toImmutableList());
            return paged(messages, query);
        } else {
            return paged(emptyList());
        }
    }

    private class ClusterStorageHandler implements WaterwayStorageHandler {

        private final String key;

        public ClusterStorageHandler(WaterwayItem config) {
            this(config.getKey(), config.getConfig());
        }

        public ClusterStorageHandler(String key, Map<String, String> config) {
            this.key = checkNotBlank(key);
            checkArgument(equal(config.get(WY_STORAGE_TYPE), GRID_STORAGE_PROVIDER_TYPE), "invalid config storage type");
            //TODO
        }

        public IgniteCache<String, StorableMessage> getCache() {
            resetCacheLostPartition();
            return clusterService.getIgnite().getOrCreateCache(format("etl_storage_%s", getCode()));
        }

        private void resetCacheLostPartition() {
            if (clusterService.getIgnite().cache(format("etl_storage_%s", getCode())) != null) {
                logger.info("cache partition lost {}", clusterService.getIgnite().cache(format("etl_storage_%s", getCode())).lostPartitions());
                clusterService.getIgnite().resetLostPartitions(list(format("etl_storage_%s", getCode())));
            }
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public WaterwayMessage createMessage(WaterwayMessage message) {
            message.checkHasStorageKey(key);
            checkArgument(getCache().putIfAbsent(message.getMessageId(), new StorableMessage(message)), "duplicate messageId =< %s >", message.getMessageId());
            return message;
        }

        @Override
        public WaterwayMessage updateMessage(WaterwayMessage message) {
            message.checkHasStorageKey(key);
            checkNotNull(getCache().getAndReplace(message.getMessageId(), new StorableMessage(message)), "message not found for messageId =< %s >", message.getMessageId());
            return message;
        }

        @Override
        public void deleteMessage(String messageId) {
            checkNotNull(getCache().remove(messageId), "message not found for messageId =< %s >", messageId);
        }

        @Nullable
        @Override
        public WaterwayMessage getMessageOrNull(MessageReference messageReference) {
            if (clusterService.isEnabled()) {
                WaterwayMessage message = Optional.ofNullable(getCache().get(messageReference.getMessageId())).map(StorableMessage::getMessage).orElse(null);
                return (message != null && (!messageReference.hasTransactionId() || equal(message.getTransactionId(), messageReference.getTransactionId()))) ? message : null;//TODO store prev transactions ??
            } else {
                return null;
            }
        }

        @Override
        public WaterwayMessageAttachment getMessageAttachmentLoadData(MessageReference messageReference, String attachmentName) {
            WaterwayMessageAttachment attachment = getMessage(messageReference).getAttachment(attachmentName);
            //TODO load attachment data, if necessary
            return attachment;
        }

        @Override
        public WaterwayMessagesStats getMessagesStats() {
            return clusterService.isRunning() ? new WaterwayMessagesStatsImpl(map(new WaterwayMessagesStatsImpl().getMessageCountByStatus()).accept(s -> getCache().iterator().forEachRemaining(m -> {
                if (equal(m.getValue().getMessage().getStorageCode(), getCode())) {
                    s.put(m.getValue().getMessage().getStatus(), s.get(m.getValue().getMessage().getStatus()) + 1);
                }
            }))) : new WaterwayMessagesStatsImpl();
        }
    }

    private static class StorableMessage implements Binarylizable {

        private final static Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

        private WaterwayMessage message;

        public StorableMessage() {
        }

        public StorableMessage(WaterwayMessage message) {
            this.message = checkNotNull(message);
        }

        public WaterwayMessage getMessage() {
            return checkNotNull(message);
        }

        @Override
        public void writeBinary(BinaryWriter writer) {
            LOGGER.trace("store on cluster etl message = {}", message);
            writer.writeString("messageId", message.getMessageId());
            writer.writeString("queue", message.getQueue());
            writer.writeString("nodeId", message.getNodeId());
            writer.writeString("logs", message.getLogs());
            writer.writeString("storage", message.getStorage());
            writer.writeInt("transactionId", message.getTransactionId());
            writer.writeTimestamp("timestamp", new java.sql.Timestamp(message.getTimestamp().toInstant().toEpochMilli()));
            writer.writeEnum("status", message.getStatus());
            writer.writeMap("meta", message.getMeta());
            writer.writeStringArray("history", message.getHistory().toArray(String[]::new));
            writer.writeStringArray("errors", list(message.getErrors()).map(CmJsonUtils::toJson).toArray(String[]::new));
            writer.writeStringArray("faultTolerantErrors", list(message.getFaultTolerantErrors()).map(CmJsonUtils::toJson).toArray(String[]::new));
            writer.writeMap("attachments", map(message.getAttachmentMap()).mapValues(EtlMessageAttachmentsData::new).mapValues(CmJsonUtils::toJson));//TODO store attachments
        }

        @Override
        public void readBinary(BinaryReader reader) {
            message = WaterwayMessageImpl.builder()
                    .withMessageId(reader.readString("messageId"))
                    .withQueue(reader.readString("queue"))
                    .withNodeId(reader.readString("nodeId"))
                    .withLogs(reader.readString("logs"))
                    .withStorage(reader.readString("storage"))
                    .withTransactionId(reader.readInt("transactionId"))
                    .withTimestamp(toDateTime(reader.readTimestamp("timestamp")))
                    .withStatus(reader.readEnum("status"))
                    .withMeta(reader.readMap("meta"))
                    .withHistory(list(reader.readStringArray("history")))
                    .withErrors(list(reader.readStringArray("errors")).map(e -> fromJson(e, FaultEvent.class)))
                    .withFaultTolerantErrors(list(reader.readStringArray("faultTolerantErrors")).map(e -> fromJson(e, FaultEvent.class)))
                    .withAttachments((Map) map(reader.readMap("attachments")).mapValues(e -> fromJson((String) e, EtlMessageAttachmentsData.class).toAttachment()))
                    .build();
            LOGGER.trace("read from cluster etl message = {}", message);
        }
    }
}
