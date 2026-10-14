/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import com.google.common.base.Joiner;
import org.cmdbuild.etl.config.WaterwayDescriptorInfoExt;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.config.WaterwayItemInfo;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import org.cmdbuild.utils.lang.CmMapUtils;

import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.buildDescriptorDataAndParams;
import static org.cmdbuild.etl.config.utils.WaterwayDescriptorUtils.descriptorDataJsonToYaml;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.buildMessageReference;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.serializeHistoryRecord;
import static org.cmdbuild.services.serialization.RequestSerializer.serializeErrors;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class WaterwaySerializer {

    public static CmMapUtils.FluentMap<String, Object> serializeBasicConfigFile(WaterwayDescriptorInfoExt configFile) {
        return (CmMapUtils.FluentMap) map(
                "_id", configFile.getCode(),
                "code", configFile.getCode(),
                "description", configFile.getDescription(),
                "notes", configFile.getNotes(),
                "version", configFile.getVersion(),
                "enabled", configFile.isEnabled(),
                "valid", configFile.isValid(),
                "disabled", Joiner.on(",").join(configFile.getDisabledItems()),
                "params", configFile.getParams(),
                "tag", configFile.getTag()
        );
    }

    public static CmMapUtils.FluentMap<String, Object> serializeDetailedConfigFile(WaterwayDescriptorInfoExt configFile, boolean includeMeta, WaterwayDescriptorService waterwayDescriptorService) {
        WaterwayDescriptorRecord configRecord = configFile instanceof WaterwayDescriptorRecord ? ((WaterwayDescriptorRecord) configFile) : waterwayDescriptorService.getDescriptor(configFile.getCode());
        String data = descriptorDataJsonToYaml(configRecord.getData());
        if (includeMeta) {
            data = buildDescriptorDataAndParams(data, configRecord);
        }
        return serializeBasicConfigFile(configFile).with("data", data);
    }

    public static Object serializeItem(WaterwayItemInfo i) {
        return map(
                "_id", i.getCode(),
                "code", i.getCode(),
                "type", serializeEnum(i.getType()),
                "enabled", i.isEnabled(),
                "description", i.getDescription(),
                "notes", i.getNotes(),
                "descriptor", i.getDescriptorKey()).skipNullValues().with(
                "subtype", i.getSubtype());
    }

    public static Object serializeMessage(WaterwayMessage message, boolean detailed) {
        return map(
                "_id", buildMessageReference(message.getStorageCode(), message.getMessageId()),
                "messageId", message.getMessageId(),
                "status", serializeEnum(message.getStatus()),
                "queue", message.getQueue(),
                "storage", message.getStorage(),
                "_queueKey", message.getQueueKey(),
                "_storageKey", message.getStorageKey(),
                "_queueCode", message.getQueueCode(),
                "_storageCode", message.getStorageCode(),
                "nodeId", message.getNodeId(),
                "timestamp", toIsoDateTimeLocal(message.getTimestamp()),
                "transactionId", message.getTransactionId()
        ).accept(b -> {
            if (detailed) {
                b.put("meta", message.getMeta(),
                        "history", message.getHistory(),
                        "errors", serializeErrors(message.getErrors()),
                        "logs", message.getLogs());
                b.put("_historyRecords", list(message.getHistoryRecords()).map(h -> map(
                        "_id", h.getMessageKey(),
                        "_value", serializeHistoryRecord(h),
                        "messageId", h.getMessageId(),
                        "status", serializeEnum(h.getStatus()),
                        "queue", h.getQueueKey(),
                        "storage", h.getStorageKey(),
                        "_queueKey", h.getQueueKey(),
                        "_storageKey", h.getStorageKey(),
                        "_queueCode", h.getQueueCode(),
                        "_storageCode", h.getStorageCode(),
                        "nodeId", h.getNodeId(),
                        "timestamp", toIsoDateTimeLocal(h.getTimestamp()),
                        "transactionId", h.getTransactionId()
                )));
                b.put("attachments", list(message.getAttachments()).sorted(WaterwayMessageAttachment::getName).map(a -> map(
                                "_id", a.getName(),
                                "name", a.getName(),
                                "type", serializeEnum(a.getType()),
                                "storage", serializeEnum(a.getStorage()),
                                "_contentType", a.getContentType(),
                                "_byteSize", a.getByteSize(),
                                "meta", a.getMeta()).accept(m -> {
                            switch (a.getStorage()) {
                                case WMAS_REFERENCE -> {
                                    m.put("value", a.getText());
                                }
                            }
                        })
                ));
            }
        });
    }
}
