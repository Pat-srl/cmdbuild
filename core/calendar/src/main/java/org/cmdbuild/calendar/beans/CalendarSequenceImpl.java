/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import static org.cmdbuild.calendar.beans.CalendarEventSource.CSO_SYSTEM;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_DATE;
import org.cmdbuild.calendar.beans.CalendarSequenceConfigImpl.CalendarSequenceConfigImplBuilder;
import static org.cmdbuild.calendar.beans.EventFrequency.EF_ONCE;
import static org.cmdbuild.calendar.beans.SequenceEndType.SET_DATE;
import static org.cmdbuild.calendar.beans.SequenceEndType.SET_NEVER;
import static org.cmdbuild.calendar.beans.SequenceEndType.SET_NUMBER;
import static org.cmdbuild.calendar.beans.SequenceEndType.SET_OTHER;
import org.cmdbuild.common.beans.LookupValue;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.dao.utils.LookupUtils.checkLookupNotNull;
import org.cmdbuild.email.beans.EmailTemplateInlineData;
import static org.cmdbuild.participant.ParticipantUtils.checkParticipants;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_CalendarSequence")
public class CalendarSequenceImpl implements CalendarSequence {

    private final String description, content, owner;
    private final CalendarEventSource source;
    private final Long id, card, trigger;
    private final CalendarEventType type;
    private final String timeZone;
    private final LookupValue category, priority; //TODO store as varchar/lookup code
    private final List<String> participants;
    private final List<EmailTemplateInlineData> notifications;
    private final LocalDate firstEvent, lastEvent;
    private final CalendarSequenceConfig config;
    private final LocalTime eventTime;
    private final List<CalendarEvent> events;

    private CalendarSequenceImpl(CalendarSequenceImplBuilder builder) {
        this.id = builder.id;
        this.description = nullToEmpty(builder.description);
        this.content = nullToEmpty(builder.content);
        this.owner = builder.owner;
        this.source = firstNotNull(builder.source, CSO_SYSTEM);
        this.card = builder.card;
        this.trigger = builder.trigger;
        this.type = firstNotNull(builder.type, CT_DATE);
        ZoneId zoneId = ZoneId.of(checkNotBlank(builder.timeZone));
        this.timeZone = zoneId.getId();
        this.category = checkLookupNotNull(builder.category);
        this.priority = checkLookupNotNull(builder.priority);
        this.participants = checkParticipants(firstNotNull(builder.participants, emptyList()));
        this.notifications = ImmutableList.copyOf(firstNotNull(builder.notifications, emptyList()));
        this.events = ImmutableList.copyOf(firstNotNull(builder.events, emptyList()));
        CalendarSequenceConfig thisConfig = firstNotNull(builder.config, CalendarSequenceConfigImpl.builder().build());
        SequenceEndType endType = thisConfig.getEndType();
        this.firstEvent = checkNotNull(builder.firstEvent);
        switch (thisConfig.getFrequency()) {
            case EF_ONCE -> {
                this.lastEvent = this.firstEvent;
                endType = SET_OTHER;
            }
            default -> {
                if (equal(endType, SET_OTHER)) {
                    if (builder.lastEvent != null) {
                        endType = SET_DATE;
                    } else if (thisConfig.hasEventCount()) {
                        endType = SET_NUMBER;
                    } else {
                        endType = SET_NEVER;
                    }
                }
                switch (endType) {
                    case SET_DATE -> {
                        this.lastEvent = checkNotNull(builder.lastEvent, "missing last event for end type = date");
                        checkArgument(firstEvent.compareTo(lastEvent) <= 0, "invalid event dates");
                    }
                    case SET_NEVER -> {
                        this.lastEvent = null;
                        checkArgument(thisConfig.hasMaxActiveEvents(), "missing max active events param for end type = never");
                    }
                    case SET_NUMBER -> {
                        this.lastEvent = null;
                        checkArgument(thisConfig.hasEventCount(), "missing event count param for end type = number");
                    }
                    default ->
                        throw new IllegalArgumentException("unsupported sequence end type = " + endType);
                }
            }

        }
        this.config = CalendarSequenceConfigImpl.copyOf(thisConfig).withEndType(endType).build();
        this.eventTime = firstNotNull(builder.eventTime, LocalTime.of(0, 0));
        switch (type) {
            case CT_DATE, CT_INSTANT -> {
            }
            default ->
                throw new IllegalArgumentException("unsupported event type = " + type);
        }
    }

    @Nullable
    @Override
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    public List<CalendarEvent> getEvents() {
        return events;
    }

    @Override
    @CardAttr("Time")
    public LocalTime getEventTime() {
        return eventTime;
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
    @Nullable
    @CardAttr
    public String getOwner() {
        return owner;
    }

    @Override
    @CardAttr
    public CalendarEventSource getSource() {
        return source;
    }

    @Override
    @Nullable
    @CardAttr(CALENDAR_ATTR_CARD)
    public Long getCard() {
        return card;
    }

    @Override
    @CardAttr("EventType")
    public CalendarEventType getType() {
        return type;
    }

    @Override
    @CardAttr
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
    public LocalDate getFirstEvent() {
        return firstEvent;
    }

    @Override
    @Nullable
    @CardAttr
    public LocalDate getLastEvent() {
        return lastEvent;
    }

    @Override
    @CardAttr
    public CalendarSequenceConfig getConfig() {
        return config;
    }

    @Override
    @Nullable
    @CardAttr
    public Long getTrigger() {
        return trigger;
    }

    @Override
    public String toString() {
        return "CalendarSequence{" + "description=" + description + ", id=" + id + '}';
    }

    public static CalendarSequenceImplBuilder builder() {
        return new CalendarSequenceImplBuilder();
    }

    public static CalendarSequenceImplBuilder copyOf(CalendarSequence source) {
        return new CalendarSequenceImplBuilder()
                .withDescription(source.getDescription())
                .withContent(source.getContent())
                .withOwner(source.getOwner())
                .withSource(source.getSource())
                .withId(source.getId())
                .withCard(source.getCard())
                .withType(source.getType())
                .withTimeZone(source.getTimeZone())
                .withCategory(source.getCategory())
                .withPriority(source.getPriority())
                .withParticipants(source.getParticipants())
                .withNotifications(source.getNotifications())
                .withFirstEvent(source.getFirstEvent())
                .withLastEvent(source.getLastEvent())
                .withConfig(source.getConfig())
                .withEventTime(source.getEventTime())
                .withTrigger(source.getTrigger())
                .withEvents(source.getEvents());
    }

    public static class CalendarSequenceImplBuilder implements Builder<CalendarSequenceImpl, CalendarSequenceImplBuilder>, CalendarBuilderWithTimezone<CalendarSequenceImplBuilder> {

        private String description;
        private String content;
        private String owner;
        private CalendarEventSource source;
        private Long id;
        private Long card;
        private Long trigger;
        private CalendarEventType type;
        private String timeZone;
        private LookupValue category;
        private LookupValue priority;
        private List<String> participants;
        private List<EmailTemplateInlineData> notifications;
        private Collection<CalendarEvent> events;
        private LocalDate firstEvent;
        private LocalDate lastEvent;
        private CalendarSequenceConfig config;
        private LocalTime eventTime;

        public CalendarSequenceImplBuilder withEventTime(LocalTime eventTime) {
            this.eventTime = eventTime;
            return this;
        }

        public CalendarSequenceImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CalendarSequenceImplBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public CalendarSequenceImplBuilder withOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public CalendarSequenceImplBuilder withSource(CalendarEventSource source) {
            this.source = source;
            return this;
        }

        public CalendarSequenceImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CalendarSequenceImplBuilder withCard(Long card) {
            this.card = card;
            return this;
        }

        public CalendarSequenceImplBuilder withTrigger(Long trigger) {
            this.trigger = trigger;
            return this;
        }

        public CalendarSequenceImplBuilder withType(CalendarEventType type) {
            this.type = type;
            return this;
        }

        @Override
        public CalendarSequenceImplBuilder withTimeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        @Override
        public String getTimeZone() {
            return timeZone;
        }

        public CalendarSequenceImplBuilder withCategory(LookupValue category) {
            this.category = category;
            return this;
        }

        public CalendarSequenceImplBuilder withPriority(LookupValue priority) {
            this.priority = priority;
            return this;
        }

        public CalendarSequenceImplBuilder withParticipants(List<String> participants) {
            this.participants = participants;
            return this;
        }

        public CalendarSequenceImplBuilder withNotifications(List<EmailTemplateInlineData> notifications) {
            this.notifications = notifications;
            return this;
        }

        public CalendarSequenceImplBuilder withFirstEvent(LocalDate firstEvent) {
            this.firstEvent = firstEvent;
            return this;
        }

        public CalendarSequenceImplBuilder withFirstEvent(LocalDate firstEvent, String timeZone) {
            return this.withFirstEvent(firstEvent).withTimeZone(timeZone);
        }

        public CalendarSequenceImplBuilder withLastEvent(LocalDate lastEvent) {
            this.lastEvent = lastEvent;
            return this;
        }

        public CalendarSequenceImplBuilder withConfig(CalendarSequenceConfig config) {
            this.config = config;
            return this;
        }

        public CalendarSequenceImplBuilder withConfig(Consumer<CalendarSequenceConfigImplBuilder> config) {
            this.config = (this.config == null ? CalendarSequenceConfigImpl.builder() : CalendarSequenceConfigImpl.copyOf(this.config)).accept(config).build();
            return this;
        }

        public CalendarSequenceImplBuilder withEvents(Collection<CalendarEvent> events) {
            this.events = events;
            return this;
        }

        @Override
        public CalendarSequenceImpl build() {
            return new CalendarSequenceImpl(this);
        }

    }
}
