/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;


import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import org.cmdbuild.utils.lang.CmMapUtils;

import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeLocal;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class MobileAppMessageSerializer {

    public static CmMapUtils.FluentMap<String, Object> serializeBasicMessage(MobileAppMessage message) {
        return map(
                "_id", message.getId(),
                "messageId", message.getMessageId(),
                "subject", message.getSubject(),
                "target", message.getTarget(),
                "sourceType", serializeEnum(message.getSourceType()),
                "sourceName", message.getSourceName(),
                "sourceDescription", message.getSourceDescription(),
                "timestamp", toIsoDateTimeLocal(message.getTimestamp()),
                "status", serializeEnum(message.getStatus()),
                "_isNew", message.isNewMessage()
        );
    }

    public static CmMapUtils.FluentMap<String, Object> serializeDetailedMessage(MobileAppMessage message) {
        return map(serializeBasicMessage(message)).with(
                "content", message.getContent(),
                "meta", message.getMeta()
        );
    }
}
