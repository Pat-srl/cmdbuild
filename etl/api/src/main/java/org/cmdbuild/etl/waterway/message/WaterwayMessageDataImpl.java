/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.waterway.message;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.utils.lang.CmStringUtils;

public class WaterwayMessageDataImpl implements WaterwayMessageData {

    private final Map<String, String> meta;
    private final Map<String, WaterwayMessageAttachment> attachments;
    private final List<FaultEvent> errors, faultTolerantErrors;
    private final String logs;

    private WaterwayMessageDataImpl(WaterwayMessageDataImplBuilder builder) {
        this.meta = map(builder.meta).mapValues(CmStringUtils::toStringOrNull).immutable();
        this.logs = nullToEmpty(builder.logs);
        this.errors = ImmutableList.copyOf(firstNotNull(builder.errors, emptyList()));
        this.faultTolerantErrors = ImmutableList.copyOf(firstNotNull(builder.faultTolerantErrors, emptyList()));
        this.attachments = map(builder.attachments.values(), WaterwayMessageAttachment::getName).immutable();
        attachments.forEach((k, v) -> {
            checkNotBlank(k);
            checkNotNull(v);
        });
    }

    @Override
    public Map<String, String> getMeta() {
        return meta;
    }

    @Override
    public Map<String, WaterwayMessageAttachment> getAttachmentMap() {
        return attachments;
    }

    @Override
    public List<FaultEvent> getErrors() {
        return errors;
    }

    @Override
    public List<FaultEvent> getFaultTolerantErrors() {
        return faultTolerantErrors;
    }    
    
    @Nullable
    @Override
    public String getLogs() {
        return logs;
    }

    @Override
    public String toString() {
        return "WaterwayMessageData{" + "attachments=" + Joiner.on(",").join(attachments.keySet()) + '}';
    }

    public static WaterwayMessageData build(String name, Object data, Map<String, String> meta) {
        return builder().withAttachment(name, data).withMeta(meta).build();
    }

    public static WaterwayMessageDataImplBuilder builder() {
        return new WaterwayMessageDataImplBuilder();
    }

    public static WaterwayMessageDataImplBuilder copyOf(WaterwayMessageData source) {
        return builder().withData(source);
    }

    public static class WaterwayMessageDataImplBuilder implements Builder<WaterwayMessageDataImpl, WaterwayMessageDataImplBuilder> {

        private final FluentMap<String, Object> meta = map();
        private final Map<String, WaterwayMessageAttachment> attachments = map();
        private List<FaultEvent> errors, faultTolerantErrors;
        private String logs;

        public WaterwayMessageDataImplBuilder withData(WaterwayMessageData source) {
            return this.clearAttachments().clearMeta()
                    .withMeta(source.getMeta())
                    .withAttachments(source.getAttachmentMap())
                    .withErrors(source.getErrors())
                    .withFaultTolerantErrors(source.getFaultTolerantErrors())
                    .withLogs(source.getLogs());
        }

        public WaterwayMessageDataImplBuilder withMeta(Map<String, ?> meta) {
            this.meta.putAll(meta);
            return this;
        }

        public WaterwayMessageDataImplBuilder withMeta(String key, Object value) {
            this.meta.put(key, value);
            return this;
        }

        public WaterwayMessageDataImplBuilder withMeta(Object... items) {
            this.meta.put(items);
            return this;
        }

        public WaterwayMessageDataImplBuilder withoutMeta(String... keys) {
            list(keys).forEach(this.meta::remove);
            return this;
        }

        public WaterwayMessageDataImplBuilder withAttachments(Map<String, WaterwayMessageAttachment> attachments) {
            this.attachments.putAll(attachments);
            return this;
        }

        public WaterwayMessageDataImplBuilder withAttachments(WaterwayMessageAttachment... attachments) {
            return this.withAttachments(list(attachments));
        }

        public WaterwayMessageDataImplBuilder withAttachment(String name, Object data) {
            return this.withAttachments(WaterwayMessageAttachmentImpl.builder().fromObject(data).withName(name).build());
        }

        public WaterwayMessageDataImplBuilder withAttachment(String name, Object data, Map<String, String> meta) {
            return this.withAttachments(WaterwayMessageAttachmentImpl.builder().fromObject(data).withName(name).withMeta(meta).build());
        }

        public WaterwayMessageDataImplBuilder withAttachment(String name, Object data, String... meta) {
            return this.withAttachments(WaterwayMessageAttachmentImpl.builder().fromObject(data).withName(name).withMeta(map(meta)).build());
        }

        public WaterwayMessageDataImplBuilder withAttachments(Iterable<WaterwayMessageAttachment> attachments) {
            this.attachments.putAll(map(attachments, WaterwayMessageAttachment::getName));
            return this;
        }

        public WaterwayMessageDataImplBuilder withErrors(List<FaultEvent> errors) {
            this.errors = errors;
            return this;
        }

        public WaterwayMessageDataImplBuilder withFaultTolerantErrors(List<FaultEvent> faultTolerantErrors) {
            this.faultTolerantErrors = faultTolerantErrors;
            return this;
        }        
        
        public WaterwayMessageDataImplBuilder withLogs(String logs) {
            this.logs = logs;
            return this;
        }

        public WaterwayMessageDataImplBuilder clearAttachments() {
            this.attachments.clear();
            return this;
        }

        public WaterwayMessageDataImplBuilder clearMeta() {
            this.meta.clear();
            return this;
        }

        @Override
        public WaterwayMessageDataImpl build() {
            return new WaterwayMessageDataImpl(this);
        }

    }
}
