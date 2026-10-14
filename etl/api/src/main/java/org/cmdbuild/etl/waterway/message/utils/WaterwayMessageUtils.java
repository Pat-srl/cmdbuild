/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message.utils;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.activation.DataSource;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.singletonList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.function.Function.identity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.checkIsValidItemCode;
import org.cmdbuild.etl.job.PayloadUtils;
import org.cmdbuild.etl.waterway.message.MessageReference;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment.WMA_CONTENT_TYPE;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment.WMA_FILE_NAME;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentImpl;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentStorage.WMAS_EMBEDDED;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_BYTES;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_JSON;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageAttachmentType.WMAT_TEXT;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_PROCESSING_REPORT;
import org.cmdbuild.etl.waterway.message.WaterwayMessageDataImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageHistoryRecord;
import org.cmdbuild.etl.waterway.message.WaterwayMessageHistoryRecordImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import static org.cmdbuild.jobs.JobRun.JOB_OUTPUT;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.urlToDataSource;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMultimap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.parseListOfStrings;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.serializeListOfStrings;
import static org.cmdbuild.utils.url.CmUrlUtils.isDataUrl;

public class WaterwayMessageUtils {

    public static String checkIsValidMessageId(String messageId) {
        checkArgument(nullToEmpty(messageId).matches("[a-z0-9.]+"), "invalid message id =< %s >", messageId);
        return messageId;
    }

    public static String buildMessageKey(String messageId, int transactionId) {
        return format("%s-%s", checkIsValidMessageId(messageId), transactionId);
    }

    public static String buildMessageReference(String storageCode, String messageIdOrKey) {
        return format("%s:%s", checkIsValidItemCode(storageCode), checkNotBlank(messageIdOrKey));
    }

    public static MessageReference toMessageReference(String storageCode, String messageIdOrKey) {
        return toMessageReference(buildMessageReference(storageCode, messageIdOrKey));
    }

    public static MessageReference toMessageReference(String messageId, int transactionId) {
        return toMessageReference(buildMessageKey(messageId, transactionId));
    }

    public static MessageReference toMessageReference(String value) {
        checkNotBlank(value);
        Matcher matcher = Pattern.compile("^(([^:-]+):)?([^:-]+)(-([0-9]+))?$").matcher(value);
        checkArgument(matcher.matches(), "invalid pattern for message reference =< %s >", value);
        return new MessageReferenceImpl(matcher.group(2), matcher.group(3), toIntegerOrNull(matcher.group(5)));
    }

    public static boolean hasMessageParent(String messageId) {
        return checkNotBlank(messageId).matches("^[^.]+([.][0-9]+)+");
    }

    public static String getMessageParent(String messageId) {
        Matcher matcher = Pattern.compile("^(.+)[.][0-9]+$").matcher(checkNotBlank(messageId));
        checkArgument(matcher.matches(), "message parent not found for message id =< {} > (invalid messageId pattern)", messageId);
        return checkNotBlank(matcher.group(1));
    }

    public static String buildMessageChildId(String parentMessageId, int index) {
        return format("%s.%s", checkNotBlank(parentMessageId), index);
    }

    public static WaterwayMessage addHistoryRecord(WaterwayMessage message) {
        return WaterwayMessageImpl.copyOf(message).withHistory(appendHistory(message.getHistory(), message)).build();
    }

    public static List<String> appendHistory(List<String> curHistory, WaterwayMessage newMessage) {
        List<WaterwayMessageHistoryRecord> history = parseHistoryRecords(curHistory);
        WaterwayMessageHistoryRecord newRecord = WaterwayMessageHistoryRecordImpl.copyOf(newMessage).build();
        return list(curHistory).with(serializeHistoryRecord(newRecord, !history.isEmpty()
                && equal(getLast(history).getMessageId(), newRecord.getMessageId())
                && equal(getLast(history).getQueueKey(), newRecord.getQueueKey())
                && equal(getLast(history).getStorageKey(), newRecord.getStorageKey())
                && equal(getLast(history).getNodeId(), newRecord.getNodeId())));
    }

    public static List<WaterwayMessageHistoryRecord> parseHistoryRecords(List<String> history) {
        return listOf(WaterwayMessageHistoryRecord.class).accept(l -> history.forEach(h -> l.add(l.isEmpty() ? parseHistoryRecord(null, h) : parseHistoryRecord(getLast(l), h))));
    }

    public static String serializeHistoryRecord(WaterwayMessageHistoryRecord record) {
        return serializeHistoryRecord(record, false);
    }

    public static String serializeHistoryRecord(WaterwayMessageHistoryRecord record, boolean lean) {
        return serializeListOfStrings("|", lean
                ? list(toIsoDateTimeUtc(record.getTimestamp()), serializeEnum(record.getStatus()), record.getTransactionId())
                : list(toIsoDateTimeUtc(record.getTimestamp()), serializeEnum(record.getStatus()), record.getMessageId(), record.getTransactionId(), record.getQueueKey(), record.getStorageKey(), record.getNodeId()));
    }

    public static WaterwayMessageHistoryRecord parseHistoryRecord(@Nullable WaterwayMessageHistoryRecord prev, String value) {
        List<String> list = parseListOfStrings("|", value);
        switch (list.size()) {
            case 3 -> {
                checkNotNull(prev, "found lean history record without previous record");
                return WaterwayMessageHistoryRecordImpl.copyOf(prev)
                        .withTimestamp(toDateTime(list.get(0)))
                        .withStatus(parseEnum(list.get(1), WaterwayMessageStatus.class))
                        .withTransactionId(toInt(list.get(2)))
                        .build();
            }
            case 7 -> {
                return WaterwayMessageHistoryRecordImpl.builder()
                        .withTimestamp(toDateTime(list.get(0)))
                        .withStatus(parseEnum(list.get(1), WaterwayMessageStatus.class))
                        .withMessageId(list.get(2))
                        .withTransactionId(toInt(list.get(3)))
                        .withQueueKey(list.get(4))
                        .withStorageKey(list.get(5))
                        .withNodeId(list.get(6))
                        .build();
            }
            default ->
                throw runtime("invalid history record format for value =< %s >", value);
        }
    }

    public static DataSource toDataSource(WaterwayMessageAttachment attachment) {
        return payloadToDataSource(attachment.getObject());//TODO improve this, use metadata/contenttype etc
    }

    public static boolean isValidPayload(Object attachmentPayload) {
        return PayloadUtils.isValidPayload(attachmentPayload);
    }

    public static DataSource payloadToDataSource(Object attachmentPayload) {
        return PayloadUtils.payloadToDataSource(attachmentPayload);
    }

    public static List<Map<String, ?>> payloadToRecords(Object attachmentPayload) {
        return PayloadUtils.payloadToRecords(attachmentPayload);
    }

    @Nullable
    public static DataSource getOutputDataFromMessage(WaterwayMessage message) {
        if (message.hasAttachment(JOB_OUTPUT)) {
            return attachmentToDataSource(message.getAttachment(JOB_OUTPUT));
        }
        String output = message.getMeta(JOB_OUTPUT);
        if (isNotBlank(output)) {
            return isDataUrl(output) ? urlToDataSource(output) : newDataSource(output); //TODO improve this (allow data urls without auto decoding; output from attachment; etc
        }
        if (message.hasAttachment(WY_PROCESSING_REPORT)) {
            return attachmentToDataSource(message.getAttachment(WY_PROCESSING_REPORT));
        }
        return newDataSource(toJson(map("success", true, "message", "processing completed, no primary output payload detected", "data", map("meta", message.getMeta(), "attachments", list(message.getAttachmentMap().values()).map(a -> map(
                "_id", a.getName(),
                "type", serializeEnum(a.getType()),
                "size", (a.hasStorage(WMAS_EMBEDDED) && a.isOfType(WMAT_BYTES, WMAT_TEXT, WMAT_JSON) ? countBytes(toDataSource(a)) : null)).accept(m -> {
            if (a.hasStorage(WMAS_EMBEDDED) && a.isOfType(WMAT_BYTES, WMAT_TEXT)) {
                DataSource dataSource = toDataSource(a);
                m.put("size", countBytes(dataSource), "contentType", dataSource.getContentType());
            }
            if (a.hasStorage(WMAS_EMBEDDED) && a.isOfType(WMAT_JSON)) {
                m.put("data", fromJson(readToString(toDataSource(a)), JsonNode.class));
            }
        }))))), "application/json");
    }

    public static List<WaterwayMessageData> splitDataForAttachments(WaterwayMessageData data) {
        if (data.hasMultipleAttachments()) {
            return list(data.getAttachmentMap().values()).map(a -> WaterwayMessageDataImpl.copyOf(data).clearAttachments().withAttachments(a).build());
        } else {
            return singletonList(data);
        }
    }

    public static WaterwayMessageData mergeDataForAttachments(Collection<WaterwayMessageData> records) {
        Set<String> duplicates = list(records).flatMap(WaterwayMessageData::getAttachments).map(WaterwayMessageAttachment::getName).duplicates();
        AtomicInteger index = new AtomicInteger(1);
        return WaterwayMessageDataImpl.builder().withAttachments(list(records).flatMap(a -> list(a.getAttachmentMap().values())).accept(list -> {
            list(duplicates).forEach(d -> {
                List<WaterwayMessageAttachment> items = list(list).filter(a -> equal(a.getName(), d));
                WaterwayMessageAttachment first = items.get(0);
                if (items.stream().skip(1).allMatch(item -> equal(first.getType(), item.getType()) && equal(first.getStorage(), item.getStorage()) && equal(first.getMeta(), item.getMeta()) && equal(first.getObject(), item.getObject()))) {
                    duplicates.remove(d);
                    list.removeIf(a -> equal(a.getName(), d));
                    list.add(first);
                }
            });
        }).map(a -> WaterwayMessageAttachmentImpl.copyOf(a).accept(b -> {
            String name = a.getName();
            while (duplicates.contains(name)) {
                name = format("%s_%s", a.getName(), index.getAndIncrement());
            }
            duplicates.add(name);
            b.withName(name);
        }).build())).withMeta((Map) map().accept(m -> list(records).forEach(a -> m.putAll(a.getMeta()))))
                .withLogs(getLast(records).getLogs())
                .withErrors(getLast(records).getErrors())
                .withFaultTolerantErrors(getLast(records).getFaultTolerantErrors())
                .build();
        //TODO improve log/error merge !!
    }

    public static WaterwayMessageData appendDataForAttachments(Collection<WaterwayMessageData> records) {
        return WaterwayMessageDataImpl.builder().accept(b
                -> list(records).flatMap(WaterwayMessageData::getAttachments).collect(toMultimap(WaterwayMessageAttachment::getName, identity()))
                        .asMap().values().stream().map(WaterwayMessageUtils::appendAttachments).forEach(b::withAttachments))
                .withMeta((Map) map().accept(m -> list(records).forEach(a -> m.putAll(a.getMeta()))))
                .withLogs(getLast(records).getLogs())
                .withErrors(getLast(records).getErrors())
                .withFaultTolerantErrors(getLast(records).getFaultTolerantErrors())
                .build();
        //TODO improve log/error merge !!
    }

//    private static WaterwayMessageData doAppendDataForAttachments(Collection<WaterwayMessageData> records) {
//        Iterator<WaterwayMessageData> iterator = records.iterator();
//        WaterwayMessageData data = iterator.next();
//        while (iterator.hasNext()) {
//            WaterwayMessageData next = iterator.next();
//            if (data.hasAttachments()) {
//                data = WaterwayMessageDataImpl.builder().withMeta(next.getMeta())
//                        .withAttachments(appendAttachments(list(data.getAttachments()).with(next.getAttachments()))).build();
//            } else {
//                data = next;
//            }
//        }
//        return data;
//    }
    public static WaterwayMessageAttachment appendAttachments(Collection<WaterwayMessageAttachment> attachments) {
        if (attachments.size() == 1) {
            return getOnlyElement(attachments);
        } else {
            checkArgument(attachments.stream().allMatch(a -> a.hasStorage(WMAS_EMBEDDED)), "cannot append non-embedded attachments");
            Iterator<WaterwayMessageAttachment> iterator = attachments.iterator();
            WaterwayMessageAttachment attachment = iterator.next();
            while (iterator.hasNext()) {
                WaterwayMessageAttachment next = iterator.next();
                checkArgument(equal(attachment.getType(), next.getType()), "cannot append attachments with different types = %s <> %s", attachment.getType(), next.getType());
                attachment = WaterwayMessageAttachmentImpl.copyOf(attachment).withMeta(next.getMeta()).withObject(switch (attachment.getType()) {
                    case WMAT_BYTES ->
                        ArrayUtils.addAll(attachment.getBytes(), next.getBytes());
                    case WMAT_TEXT ->
                        attachment.getText() + next.getText();
                    case WMAT_OBJECT ->
                        list((List) attachment.getObject()).with((List) next.getObject()).immutable();
                    case WMAT_JSON ->
                        toJson(list(fromJson(attachment.getText(), List.class)).with(fromJson(next.getText(), List.class)));
                    default ->
                        throw new IllegalArgumentException();
                }).build();
            }
            return attachment;
        }
    }

    public static DataSource attachmentToDataSource(WaterwayMessageAttachment attachment) {
        return switch (attachment.getType()) {
            case WMAT_BYTES ->
                newDataSource(attachment.getBytes(), attachment.getMeta(WMA_CONTENT_TYPE), attachment.getMeta(WMA_FILE_NAME));
            case WMAT_JSON, WMAT_TEXT ->
                newDataSource(attachment.getText(), attachment.getMeta(WMA_CONTENT_TYPE), attachment.getMeta(WMA_FILE_NAME));
            case WMAT_OBJECT ->
                (DataSource) attachment.getObject();//TODO
            default ->
                throw runtime("invalid attachment for datasource = %s", attachment);
        };
    }

    private static class MessageReferenceImpl implements MessageReference {

        private final String storage, messageId;
        private final Integer transactionId;

        public MessageReferenceImpl(@Nullable String storage, String messageId) {
            this(storage, messageId, null);
        }

        public MessageReferenceImpl(@Nullable String storage, String messageId, @Nullable Integer transactionId) {
            this.storage = isBlank(storage) ? null : checkIsValidItemCode(storage);
            this.messageId = checkIsValidMessageId(messageId);
            this.transactionId = transactionId;
        }

        @Nullable
        @Override
        public String getStorage() {
            return storage;
        }

        @Override
        public String getMessageId() {
            return messageId;
        }

        @Nullable
        @Override
        public Integer getTransactionId() {
            return transactionId;
        }

        @Override
        public String toString() {
            return "MessageReference{" + "storage=" + storage + ", messageId=" + messageId + ", transactionId=" + transactionId + '}';
        }

    }
}
