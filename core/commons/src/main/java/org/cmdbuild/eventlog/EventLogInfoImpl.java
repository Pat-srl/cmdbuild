/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.eventlog;

import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class EventLogInfoImpl implements EventLogInfo {

    private final Long card;
    private final String code;
    private final Map<String, Object> data;

    private EventLogInfoImpl(EventLogInfoImplBuilder builder) {
        this.card = builder.card;
        this.code = checkNotBlank(builder.code);
        this.data = map(firstNotNull(builder.data, emptyMap())).immutable();
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    @Nullable
    public Long getCard() {
        return card;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "EventLogInfoImpl{" + "code=" + code + '}';
    }

    public static EventLogInfoImplBuilder builder() {
        return new EventLogInfoImplBuilder();
    }

    public static EventLogInfoImplBuilder copyOf(EventLogInfo source) {
        return new EventLogInfoImplBuilder()
                .withCard(source.getCard())
                .withCode(source.getCode())
                .withData(source.getData());
    }

    public static class EventLogInfoImplBuilder implements Builder<EventLogInfo, EventLogInfoImplBuilder> {

        private Long card;
        private String code;
        private Map<String, Object> data;

        public EventLogInfoImplBuilder withCard(Long card) {
            this.card = card;
            return this;
        }

        public EventLogInfoImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public EventLogInfoImplBuilder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        @Override
        public EventLogInfoImpl build() {
            return new EventLogInfoImpl(this);
        }

    }
}
