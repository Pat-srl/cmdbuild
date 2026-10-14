/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification.mobileapp.beans;

import static com.google.common.base.Strings.nullToEmpty;
import static java.util.Arrays.asList;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class MobileAppMessageDataImpl implements MobileAppMessageData {

    private final String target, subject, content;
    private final Map<String, String> meta;

    private MobileAppMessageDataImpl(MobileAppMessageDataImplBuilder builder) {
        this.target = checkNotBlank(builder.target, "missing `target` attribute");
        this.subject = nullToEmpty(builder.subject);
        this.content = nullToEmpty(builder.content);
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
    public Map<String, String> getMeta() {
        return meta;
    }

    public static MobileAppMessageDataImplBuilder builder() {
        return new MobileAppMessageDataImplBuilder();
    }

    public static MobileAppMessageDataImplBuilder copyOf(MobileAppMessageData source) {
        final MobileAppMessageDataImplBuilder result = new MobileAppMessageDataImplBuilder()
                .withTarget(source.getTarget())
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withMeta(source.getMeta());

        return result;
    }

    public static MobileAppNotificationDataImpl.MobileAppNotificationDataImplBuilder copyNotificationDataOf(MobileAppMessageData source) {
        final MobileAppNotificationDataImpl.MobileAppNotificationDataImplBuilder result = new MobileAppNotificationDataImpl.MobileAppNotificationDataImplBuilder()
                .withTopics(asList(source.getTarget()))
                .withSubject(source.getSubject())
                .withContent(source.getContent())
                .withMeta(source.getMeta());

        if (source instanceof MobileAppMessage mobileAppMessage) {
            result.withStatus(mobileAppMessage.getStatus().toNotificationStatus());
        }

        return result;
    }

    public static class MobileAppMessageDataImplBuilder implements Builder<MobileAppMessageDataImpl, MobileAppMessageDataImplBuilder> {

        private String target;
        private String subject;
        private String content;
        private final Map<String, String> meta = map();

        public MobileAppMessageDataImplBuilder withTarget(String target) {
            this.target = target;
            return this;
        }

        public MobileAppMessageDataImplBuilder withSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public MobileAppMessageDataImplBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public MobileAppMessageDataImplBuilder withMeta(Map<String, String> meta) {
            this.meta.putAll(nullToEmpty(meta));
            return this;
        }

        @Override
        public MobileAppMessageDataImpl build() {
            return new MobileAppMessageDataImpl(this);
        }

    }
}
