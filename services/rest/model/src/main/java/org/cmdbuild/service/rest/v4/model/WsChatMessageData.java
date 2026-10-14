/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.chat.ChatMessageData;
import org.cmdbuild.chat.ChatMessageDataImpl;
import org.cmdbuild.chat.ChatMessageStatus;

import java.util.Map;

import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

/**
 * @author ldare
 */
public class WsChatMessageData {

    private final String target, subject, content, thread;
    private final Map<String, String> meta;
    private final ChatMessageStatus status;

    public WsChatMessageData(
            @JsonProperty("status") String status,
            @JsonProperty("target") String target,
            @JsonProperty("subject") String subject,
            @JsonProperty("content") String content,
            @JsonProperty("thread") String thread,
            @JsonProperty("meta") Map<String, String> meta
    ) {
        this.status = parseEnumOrNull(status, ChatMessageStatus.class);
        this.target = target;
        this.subject = subject;
        this.content = content;
        this.thread = thread;
        this.meta = meta;
    }

    public ChatMessageData toChatMessageData() {
        return ChatMessageDataImpl.builder().withTarget(target).withSubject(subject).withThread(thread).withMeta(meta).withContent(content).build();
    }

    public ChatMessageStatus getStatus() {
        return this.status;
    }
}
