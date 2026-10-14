/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.chat;

import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ChatMessageDataImpl implements ChatMessageData {

    private final String target, subject, content, thread;
    private final Map<String, String> meta;

    private ChatMessageDataImpl(ChatMessageDataImplBuilder builder) {
        this.target = checkNotBlank(builder.target, "missing `target` attribute");
        this.subject = nullToEmpty(builder.subject);
        this.content = nullToEmpty(builder.content);
        this.thread = builder.thread;
        this.meta = map(builder.meta).immutable();
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    @Nullable
    public String getSubject() {
        return subject;
    }

    @Override
    @Nullable
    public String getContent() {
        return content;
    }

    @Override
    @Nullable
    public String getThread() {
        return thread;
    }

    @Override
    public Map<String, String> getMeta() {
        return meta;
    }

    public static ChatMessageDataImplBuilder builder() {
        return new ChatMessageDataImplBuilder();
    }

    public static ChatMessageDataImplBuilder copyOf(ChatMessageData source) {
        return new ChatMessageDataImplBuilder()
                .withTarget(source.getTarget())
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withThread(source.getThread())
                .withMeta(source.getMeta());
    }

    public static class ChatMessageDataImplBuilder implements Builder<ChatMessageDataImpl, ChatMessageDataImplBuilder> {

        private String target;
        private String subject;
        private String content;
        private String thread;
        private final Map<String, String> meta = map();

        public ChatMessageDataImplBuilder withTarget(String target) {
            this.target = target;
            return this;
        }

        public ChatMessageDataImplBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public ChatMessageDataImplBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public ChatMessageDataImplBuilder withThread(String thread) {
            this.thread = thread;
            return this;
        }

        public ChatMessageDataImplBuilder withMeta(Map<String, String> meta) {
            this.meta.putAll(nullToEmpty(meta));
            return this;
        }

        @Override
        public ChatMessageDataImpl build() {
            return new ChatMessageDataImpl(this);
        }

    }
}
