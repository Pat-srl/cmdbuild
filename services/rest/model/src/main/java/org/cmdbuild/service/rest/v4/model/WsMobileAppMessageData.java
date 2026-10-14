/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageDataImpl;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus;

import java.util.Map;

import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

/**
 * @author ldare
 */
public class WsMobileAppMessageData {

    private final String target, subject, content;
    private final Map<String, String> meta;
    private final MobileAppMessageStatus status;

    public WsMobileAppMessageData(
            @JsonProperty("status") String status,
            @JsonProperty("target") String target,
            @JsonProperty("subject") String subject,
            @JsonProperty("content") String content,
            @JsonProperty("meta") Map<String, String> meta) {
        this.status = parseEnumOrNull(status, MobileAppMessageStatus.class);
        this.target = target;
        this.subject = subject;
        this.content = content;
        this.meta = meta;
    }

    public MobileAppMessageData toMobileAppMessageData() {
        return MobileAppMessageDataImpl.builder().withTarget(target).withSubject(subject).withMeta(meta).withContent(content).build();
    }

    public MobileAppMessageStatus getStatus(){
        return status;
    }

}
