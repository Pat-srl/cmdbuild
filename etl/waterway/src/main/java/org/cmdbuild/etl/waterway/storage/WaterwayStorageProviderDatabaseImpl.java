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
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_A;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_U;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItem;
import static org.cmdbuild.etl.config.WaterwayItemType.WYCIT_STORAGE;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.configItemKey;
import org.cmdbuild.etl.waterway.WaterwayMessagesStats;
import org.cmdbuild.etl.waterway.message.MessageReference;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.etl.waterway.message.WaterwayMessageImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import static org.cmdbuild.etl.waterway.storage.EtlMessage.ETL_MESSAGE_ATTR_MESSAGE_STATUS;
import static org.cmdbuild.etl.waterway.storage.EtlMessage.ETL_MESSAGE_ATTR_NODE_ID;
import static org.cmdbuild.etl.waterway.storage.EtlMessage.ETL_MESSAGE_ATTR_QUEUE_CODE;
import static org.cmdbuild.etl.waterway.storage.EtlMessage.ETL_MESSAGE_ATTR_STORAGE_CODE;
import static org.cmdbuild.etl.waterway.storage.EtlMessage.ETL_MESSAGE_ATTR_TRANSACTION_ID;
import static org.cmdbuild.etl.waterway.storage.WaterwayStorageHandler.WY_STORAGE_TYPE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.etl.waterway.message.MessageReference.MESSAGE_SKIP_STORAGE;

@Component
public class WaterwayStorageProviderDatabaseImpl implements WaterwayStorageProvider {

    private final static String DATABASE_STORAGE_PROVIDER_TYPE = "database",
            DEFAULT_STORAGE_PROVIDER_CODE = "default";

    public static final Map<String, String> WATERWAY_MESSAGE_ATTR_MAPPING = ImmutableMap.copyOf(map(
            "timestamp", ATTR_BEGINDATE,
            "status", ETL_MESSAGE_ATTR_MESSAGE_STATUS,
            "queue", ETL_MESSAGE_ATTR_QUEUE_CODE,
            "messageId", ATTR_CODE,
            "nodeId", ETL_MESSAGE_ATTR_NODE_ID
    ));

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WaterwayDescriptorService config;
    private final DaoService dao;

    public WaterwayStorageProviderDatabaseImpl(WaterwayDescriptorService config, DaoService dao) {
        this.config = checkNotNull(config);
        this.dao = checkNotNull(dao);
    }

    @Override
    public List<WaterwayStorageHandler> getStorageHandlers() {
        logger.debug("load database storage providers");
        List<WaterwayStorageHandler> list = config.getAllItems(WYCIT_STORAGE).stream().filter(i -> equal(firstNotBlank(i.getConfig(WY_STORAGE_TYPE), DATABASE_STORAGE_PROVIDER_TYPE), DATABASE_STORAGE_PROVIDER_TYPE)).map(DatabaseStorageProvider::new).collect(toImmutableList());
        if (!list.stream().anyMatch(c -> equal(c.getCode(), DEFAULT_STORAGE_PROVIDER_CODE))) {
            logger.debug("add default storage provider ( code =< {} > )", DEFAULT_STORAGE_PROVIDER_CODE);
            list = list(list).with(new DatabaseStorageProvider(configItemKey("system", DEFAULT_STORAGE_PROVIDER_CODE), map(
                    WY_STORAGE_TYPE, DATABASE_STORAGE_PROVIDER_TYPE
            )));
        }
        logger.debug("loaded {} database storage providers", list.size());
        return list;
    }

    @Override
    public PagedElements<WaterwayMessage> getMessages(DaoQueryOptions query) {
        List<WaterwayMessage> messages = list(
                dao.select(query).from(EtlMessage.class).withOptions(query.mapAttrNames(WATERWAY_MESSAGE_ATTR_MAPPING)).asList(EtlMessage.class)
        ).map(this::toMessage);
        long total;
        if (query.isPagedAndHasFullPage(messages.size())) {
            total = dao.selectCount().from(EtlMessage.class).where(query.getFilter().mapNames(WATERWAY_MESSAGE_ATTR_MAPPING)).getCount();
        } else {
            total = messages.size() + query.getOffset();
        }
        return paged(messages, total);
    }

    private WaterwayMessage toMessage(EtlMessage message) {
        return WaterwayMessageImpl.copyOf(message).build();
    }

    private class DatabaseStorageProvider implements WaterwayStorageHandler {

        private final String key;

        public DatabaseStorageProvider(WaterwayItem config) {
            this(config.getKey(), config.getConfig());
        }

        public DatabaseStorageProvider(String key, Map<String, String> config) {
            this.key = checkNotBlank(key);
            checkArgument(equal(firstNotBlank(config.get(WY_STORAGE_TYPE), DATABASE_STORAGE_PROVIDER_TYPE), DATABASE_STORAGE_PROVIDER_TYPE), "invalid config storage type");
            //TODO
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public WaterwayMessage createMessage(WaterwayMessage message) {
            message.checkHasStorageKey(key);
            dao.createOnly(EtlMessageImpl.copyOf(message).build());
            return WaterwayMessageImpl.copyOf(message).build();
        }

        @Override
        public WaterwayMessageAttachment getMessageAttachmentLoadData(MessageReference messageReference, String attachmentName) {
            WaterwayMessageAttachment attachment = getMessage(messageReference).getAttachment(attachmentName);
            //TODO load attachment data, if necessary
            return attachment;
        }

        @Override
        public WaterwayMessage updateMessage(WaterwayMessage message) {
            message.checkHasStorageKey(key);
            long cardId = dao.select(ATTR_ID).from(EtlMessage.class).where(ATTR_CODE, EQ, message.getMessageId()).where(ETL_MESSAGE_ATTR_STORAGE_CODE, EQ, getCode()).getCard().getId();
            dao.updateOnly(EtlMessageImpl.copyOf(message).withId(cardId).build());
            return message;
        }

        @Override
        public void deleteMessage(String messageId) {
            checkArgument(dao.getJdbcTemplate().update("UPDATE \"_EtlMessage\" SET \"Status\" = 'N' WHERE \"Status\" = 'A' AND \"Code\" = ?", checkNotBlank(messageId)) > 0, "unable to delete message with messageId =< %s >", messageId);
        }

        @Override
        public WaterwayMessage getMessageOrNull(MessageReference messageReference) {
            EtlMessage message;
            if (messageReference.hasTransactionId()) {
                message = dao.selectAll().from(EtlMessage.class)
                        .includeHistory().where(ATTR_STATUS, IN, set(ATTR_STATUS_A, ATTR_STATUS_U))
                        .where(ATTR_CODE, EQ, checkNotBlank(messageReference.getMessageId())).where(ETL_MESSAGE_ATTR_TRANSACTION_ID, EQ, messageReference.getTransactionId())
                        .where(ETL_MESSAGE_ATTR_STORAGE_CODE, EQ, getCode())
                        .getOneOrNull();
            } else {
                message = dao.selectAll().from(EtlMessage.class)
                        .where(ATTR_CODE, EQ, checkNotBlank(messageReference.getMessageId()))
                        .where(ETL_MESSAGE_ATTR_STORAGE_CODE, IN, set(getCode(), MESSAGE_SKIP_STORAGE))
                        .getOneOrNull();
            }
            return applyOrNull(message, WaterwayStorageProviderDatabaseImpl.this::toMessage);
        }

        @Override
        public WaterwayMessagesStats getMessagesStats() {
            return new WaterwayMessagesStatsImpl(dao.getJdbcTemplate().queryForList("SELECT count(\"Id\") _count,\"MessageStatus\" FROM \"_EtlMessage\" WHERE \"Status\" = 'A' AND \"Storage\" = ? GROUP BY \"MessageStatus\"", getCode()).stream()
                    .collect(toMap(r -> parseEnum(toStringNotBlank(r.get("MessageStatus")), WaterwayMessageStatus.class), r -> toLong(r.get("_count")))));
        }

        @Override
        public String toString() {
            return "DatabaseStorageProvider{" + "key=" + key + '}';
        }

//
//        @Override
//        @Nullable
//        public WaterwayMessage getMessageOrNull(String messageId) {
//            EtlMessage message = dao.selectAll().from(EtlMessage.class).where(ATTR_CODE, EQ, checkNotBlank(messageId)).getOneOrNull();
//            return message == null ? null : toMessage(message);
//        }
//        @Override
//        public List<WaterwayMessage> getDelayedMessages(ZonedDateTime expiringAfterTimestamp) {
//            return dao.selectAll().from(EtlMessage.class).where(ETL_MESSAGE_ATTR_MESSAGE_STATUS, EQ, serializeEnum(WMS_STANDBY))
//                    .whereExpr("_cm3_utils_is_not_blank(\"Meta\"->>'wy_retry_timestamp') AND (\"Meta\"->>'wy_retry_timestamp')::timestamptz < ?", checkNotNull(expiringAfterTimestamp)).asList();
//        }
    }
}
