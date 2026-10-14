/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.etl.loader.EtlNotificationImpl.EtlNotificationImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@JsonDeserialize(builder = EtlNotificationImplBuilder.class)
@JsonInclude(Include.NON_NULL)
public class EtlNotificationImpl implements EtlNotification {

    private final String template, account;
    private final EtlNotificationEvent event;

    private EtlNotificationImpl(EtlNotificationImplBuilder builder) {
        this.template = checkNotBlank(builder.template);
        this.account = builder.account;
        this.event = checkNotNull(builder.event);
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Nullable
    @Override
    public String getAccount() {
        return account;
    }

    @Override
    @JsonIgnore
    public EtlNotificationEvent getEvent() {
        return event;
    }

    @JsonProperty("event")
    public String getEventAsString() {
        return serializeEnum(event);
    }

    @Override
    public String toString() {
        return "EtlNotification{" + "template=" + template + ", account=" + account + ", event=" + event + '}';
    }

    public static EtlNotificationImplBuilder builder() {
        return new EtlNotificationImplBuilder();
    }

    public static EtlNotificationImplBuilder copyOf(EtlNotification source) {
        return new EtlNotificationImplBuilder()
                .withTemplate(source.getTemplate())
                .withAccount(source.getAccount())
                .withEvent(source.getEvent());
    }

    public static class EtlNotificationImplBuilder implements Builder<EtlNotificationImpl, EtlNotificationImplBuilder> {

        private String template;
        private String account;
        private EtlNotificationEvent event;

        public EtlNotificationImplBuilder withTemplate(String template) {
            this.template = template;
            return this;
        }

        public EtlNotificationImplBuilder withAccount(String account) {
            this.account = account;
            return this;
        }

        public EtlNotificationImplBuilder withEvent(EtlNotificationEvent event) {
            this.event = event;
            return this;
        }

        public EtlNotificationImplBuilder withEvent(String event) {
            this.event = parseEnum(event, EtlNotificationEvent.class);
            return this;
        }

        @Override
        public EtlNotificationImpl build() {
            return new EtlNotificationImpl(this);
        }

    }
}
