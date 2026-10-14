/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_DATE;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_INSTANT;
import org.cmdbuild.calendar.beans.CalendarSequenceConfigImpl.CalendarSequenceConfigImplBuilder;
import static org.cmdbuild.calendar.beans.CalendarTrigger.TriggerScope.TS_INTERACTIVE_ONLY;
import org.cmdbuild.common.beans.LookupValue;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.dao.utils.LookupUtils.checkLookupNotNull;
import org.cmdbuild.email.beans.EmailTemplateInlineData;
import static org.cmdbuild.participant.ParticipantUtils.checkParticipants;
import org.cmdbuild.utils.date.Interval;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_CalendarTrigger")
public class CalendarTriggerImpl implements CalendarTrigger {

    private final String description, content, ownerClass, ownerAttr, code;
    private final Long id;
    private final CalendarEventType type;
    private final String timeZone;
    private final LookupValue category, priority;
    private final List<String> participants;
    private final List<EmailTemplateInlineData> notifications;
    private final CalendarSequenceConfig config;
    private final boolean isActive;
    private final LocalTime eventTime;
    private final Interval delay;
    private final LocalDate lastEvent;
    private final TriggerScope triggerScope;

    private CalendarTriggerImpl(CalendarTriggerImplBuilder builder) {
        this.code = checkNotNull(builder.code);
        this.ownerClass = checkNotBlank(builder.ownerClass);
        this.ownerAttr = checkNotBlank(builder.ownerAttr);
        this.id = builder.id;
        this.isActive = firstNotNull(builder.isActive, true);
        this.description = nullToEmpty(builder.description);
        this.content = nullToEmpty(builder.content);
        this.type = firstNotNull(builder.type, CT_INSTANT);
        this.triggerScope = firstNotNull(builder.triggerScope, TS_INTERACTIVE_ONLY);
        this.timeZone = isBlank(builder.timeZone) ? null : ZoneId.of(builder.timeZone).getId();
        this.category = checkLookupNotNull(builder.category);
        this.priority = checkLookupNotNull(builder.priority);
        this.participants = checkParticipants(firstNotNull(builder.participants, emptyList()));
        this.notifications = ImmutableList.copyOf(firstNotNull(builder.notifications, emptyList()));
        this.config = firstNotNull(builder.config, CalendarSequenceConfigImpl.builder().build());
        this.eventTime = firstNotNull(builder.eventTime, LocalTime.of(0, 0));
        this.delay = firstNotNull(builder.delay, Interval.ZERO);
        this.lastEvent = switch (config.getEndType()) {
            case SET_DATE ->
                builder.lastEvent;
            default ->
                null;
        };
        switch (type) {
            case CT_DATE, CT_INSTANT -> {
            }
            default ->
                throw new IllegalArgumentException("unsupported event type = " + type);
        }
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getCode() {
        return code;
    }

    @Nullable
    @Override
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @Nullable
    @CardAttr
    public LocalDate getLastEvent() {
        return lastEvent;
    }

    @Override
    @CardAttr("Time")
    public LocalTime getEventTime() {
        return eventTime;
    }

    @Override
    @CardAttr
    public Interval getDelay() {
        return delay;
    }

    @Override
    @CardAttr
    public boolean isActive() {
        return isActive;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr
    public String getContent() {
        return content;
    }

    @Override
    @CardAttr(CAL_TRIGGER_ATTR_OWNERCLASS)
    public String getOwnerClass() {
        return ownerClass;
    }

    @Override
    @CardAttr(CAL_TRIGGER_ATTR_OWNERATTR)
    public String getOwnerAttr() {
        return ownerAttr;
    }

    @Override
    @CardAttr("EventType")
    public CalendarEventType getType() {
        return type;
    }

    @Override
    @CardAttr
    @Nullable
    public String getTimeZone() {
        return timeZone;
    }

    @Override
    @CardAttr
    public LookupValue getCategory() {
        return category;
    }

    @Override
    @CardAttr
    public LookupValue getPriority() {
        return priority;
    }

    @Override
    @CardAttr
    public List<String> getParticipants() {
        return participants;
    }

    @Override
    @CardAttr
    public List<EmailTemplateInlineData> getNotifications() {
        return notifications;
    }

    @Override
    @CardAttr
    public CalendarSequenceConfig getConfig() {
        return config;
    }

    @Override
    @CardAttr("TriggerScope")
    public TriggerScope getScope() {
        return triggerScope;
    }

    @Override
    public String toString() {
        return "CalendarTrigger{" + "description=" + description + ", ownerClass=" + ownerClass + ", ownerAttr=" + ownerAttr + ", id=" + id + '}';
    }

    public static CalendarTriggerImplBuilder builder() {
        return new CalendarTriggerImplBuilder();
    }

    public static CalendarTriggerImplBuilder copyOf(CalendarTrigger source) {
        return new CalendarTriggerImplBuilder()
                .withActive(source.isActive())
                .withCode(source.getCode())
                .withDescription(source.getDescription())
                .withContent(source.getContent())
                .withOwnerClass(source.getOwnerClass())
                .withOwnerAttr(source.getOwnerAttr())
                .withId(source.getId())
                .withType(source.getType())
                .withTimeZone(source.getTimeZone())
                .withCategory(source.getCategory())
                .withPriority(source.getPriority())
                .withParticipants(source.getParticipants())
                .withNotifications(source.getNotifications())
                .withConfig(source.getConfig())
                .withEventTime(source.getEventTime())
                .withLastEvent(source.getLastEvent())
                .withScope(source.getScope())
                .withDelay(source.getDelay());
    }

    public static class CalendarTriggerImplBuilder implements Builder<CalendarTriggerImpl, CalendarTriggerImplBuilder>, CalendarBuilderWithTimezone<CalendarTriggerImplBuilder> {

        private String code;
        private String description;
        private String content;
        private String ownerClass;
        private String ownerAttr;
        private Long id;
        private CalendarEventType type;
        private String timeZone;
        private LookupValue category;
        private LookupValue priority;
        private List<String> participants;
        private List<EmailTemplateInlineData> notifications;
        private CalendarSequenceConfig config;
        private Boolean isActive;
        private LocalTime eventTime;
        private Interval delay;
        private LocalDate lastEvent;
        private TriggerScope triggerScope;

        public CalendarTriggerImplBuilder withScope(TriggerScope triggerScope) {
            this.triggerScope = triggerScope;
            return this;
        }

        public CalendarTriggerImplBuilder withEventTime(LocalTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public CalendarTriggerImplBuilder withLastEvent(LocalDate lastEvent) {
            this.lastEvent = lastEvent;
            return this;
        }

        public CalendarTriggerImplBuilder withDelay(Interval delay) {
            this.delay = delay;
            return this;
        }

        public CalendarTriggerImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public CalendarTriggerImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public CalendarTriggerImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CalendarTriggerImplBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public CalendarTriggerImplBuilder withOwnerClass(String ownerClass) {
            this.ownerClass = ownerClass;
            return this;
        }

        public CalendarTriggerImplBuilder withOwnerAttr(String ownerAttr) {
            this.ownerAttr = ownerAttr;
            return this;
        }

        public CalendarTriggerImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CalendarTriggerImplBuilder withType(CalendarEventType type) {
            this.type = type;
            return this;
        }

        @Override
        public CalendarTriggerImplBuilder withTimeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        @Override
        public String getTimeZone() {
            return timeZone;
        }

        public CalendarTriggerImplBuilder withCategory(LookupValue category) {
            this.category = category;
            return this;
        }

        public CalendarTriggerImplBuilder withPriority(LookupValue priority) {
            this.priority = priority;
            return this;
        }

        public CalendarTriggerImplBuilder withParticipants(List<String> participants) {
            this.participants = participants;
            return this;
        }

        public CalendarTriggerImplBuilder withParticipants(String... participants) {
            return this.withParticipants(list(participants));
        }

        public CalendarTriggerImplBuilder withNotifications(List<EmailTemplateInlineData> notifications) {
            this.notifications = notifications;
            return this;
        }

        public CalendarTriggerImplBuilder withConfig(CalendarSequenceConfig config) {
            this.config = config;
            return this;
        }

        public CalendarTriggerImplBuilder withConfig(Consumer<CalendarSequenceConfigImplBuilder> config) {
            this.config = (this.config == null ? CalendarSequenceConfigImpl.builder() : CalendarSequenceConfigImpl.copyOf(this.config)).accept(config).build();
            return this;
        }

        @Override
        public CalendarTriggerImpl build() {
            return new CalendarTriggerImpl(this);
        }

    }
}
