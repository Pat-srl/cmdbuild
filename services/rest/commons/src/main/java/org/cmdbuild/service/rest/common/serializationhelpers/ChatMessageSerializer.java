/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.cmdbuild.chat.ChatMessage;
import org.cmdbuild.utils.lang.CmMapUtils;

import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class ChatMessageSerializer {

    public static CmMapUtils.FluentMap<String, Object> serializeBasicMessage(ChatMessage message) {
        return map(
                "_id", message.getId(),
                "messageId", message.getMessageId(),
                "subject", message.getSubject(),
                "target", message.getTarget(),
                "thread", message.getThread(),
                "sourceType", serializeEnum(message.getSourceType()),
                "sourceName", message.getSourceName(),
                "sourceDescription", message.getSourceDescription(),
                "timestamp", toIsoDateTimeLocal(message.getTimestamp()),
                "type", serializeEnum(message.getType()),
                "status", serializeEnum(message.getStatus()),
                "_isNew", message.isNewMessage()
        );
    }

    public static CmMapUtils.FluentMap<String, Object> serializeDetailedMessage(ChatMessage message) {
        return map(serializeBasicMessage(message)).with(
                "content", message.getContent(),
                "meta", message.getMeta()
        );
    }
}
