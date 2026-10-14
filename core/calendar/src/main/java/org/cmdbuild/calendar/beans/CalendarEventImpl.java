/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_TABLE;
import org.cmdbuild.calendar.beans.CalendarEventConfigImpl.CalendarEventConfigImplBuilder;
import static org.cmdbuild.calendar.beans.CalendarEventSource.CSO_SYSTEM;
import static org.cmdbuild.calendar.beans.CalendarEventSource.CSO_USER;
import static org.cmdbuild.calendar.beans.CalendarEventStatus.CST_ACTIVE;
import static org.cmdbuild.calendar.beans.CalendarEventStatus.CST_COMPLETED;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_DATE;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_RANGE;
import org.cmdbuild.calendar.utils.CalendarUtils;
import org.cmdbuild.common.beans.LookupValue;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NOTES;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.dao.utils.LookupUtils.checkLookupNotNull;
import org.cmdbuild.email.beans.EmailTemplateInlineData;
import static org.cmdbuild.participant.ParticipantUtils.checkParticipants;
import static org.cmdbuild.utils.date.CmDateUtils.checkThatZoneIdIsRegion;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(EVENT_TABLE)
public class CalendarEventImpl implements CalendarEvent {

    private final String description, content, owner;
    private final CalendarEventSource source;
    private final Long id, sequence, card;
    private final CalendarEventStatus status;
    private final CalendarEventType type;
    private final ZonedDateTime begin, end, completed;
    private final String timeZone, notes;
    private final LookupValue category, priority;
    private final List<String> participants, processedNotifications;
    private final List<EmailTemplateInlineData> notifications;
    private final CalendarEventConfig config;

    private CalendarEventImpl(CalendarEventImplBuilder builder) {
        this.id = builder.id;
        this.description = nullToEmpty(builder.description);
        this.content = nullToEmpty(builder.content);
        this.notes = nullToEmpty(builder.notes);
        this.source = firstNotNull(builder.source, CSO_SYSTEM);
        this.card = builder.card;
        this.status = firstNotNull(builder.status, CST_ACTIVE);
        this.completed = switch (status) {
            case CST_COMPLETED ->
                firstNotNull(builder.completed, now());
            default ->
                null;
        };
        this.type = checkNotNull(builder.type, "missing event type");
        ZoneId zoneId = checkThatZoneIdIsRegion(ZoneId.of(checkNotBlank(builder.timeZone)));
        switch (type) {
            case CT_DATE -> {
                LocalDate date = CalendarUtils.getDate(builder.begin, zoneId);
                this.begin = date.atStartOfDay(zoneId);
                this.end = date.plusDays(1).atStartOfDay(zoneId).minusSeconds(1);
            }
            case CT_INSTANT ->
                this.begin = this.end = checkNotNull(builder.begin);
            case CT_RANGE -> {
                this.begin = checkNotNull(builder.begin);
                this.end = checkNotNull(builder.end);
            }
            default ->
                throw illegalArgument("unsupported type = %s", type);
        }
        this.timeZone = zoneId.getId();
        this.category = checkLookupNotNull(builder.category);
        this.priority = checkLookupNotNull(builder.priority);
        category.checkLookupType(CALENDAR_CATEGORY_LOOKUP_TYPE);
        priority.checkLookupType(CALENDAR_PRIORITY_LOOKUP_TYPE);
        this.sequence = builder.sequence;
        this.participants = checkParticipants(firstNotNull(builder.participants, emptyList()));
        this.notifications = ImmutableList.copyOf(firstNotNull(builder.notifications, emptyList()));
        this.processedNotifications = ImmutableList.copyOf(builder.processedNotifications);
        checkArgument(this.begin.compareTo(this.end) <= 0);
        this.owner = switch (source) {
            case CSO_USER ->
                checkNotBlank(builder.owner, "user event must have an owner");
            case CSO_SYSTEM ->
                null;
            default ->
                throw illegalArgument("unsupported type = %s", source);
        };
        config = checkNotNull(builder.config);
    }

    @Nullable
    @CardAttr(ATTR_ID)
    @Override
    public Long getId() {
        return id;
    }

    @Nullable
    @CardAttr(EVENT_ATTR_COMPLETION)
    @Override
    public ZonedDateTime getCompleted() {
        return completed;
    }

    @CardAttr
    @Override
    public CalendarEventConfig getConfig() {
        return config;
    }

    @CardAttr(EVENT_ATTR_DESCRIPTION)
    @Override
    public String getDescription() {
        return description;
    }

    @CardAttr(ATTR_NOTES)
    @Override
    public String getNotes() {
        return notes;
    }

    @CardAttr(EVENT_ATTR_CONTENT)
    @Override
    public String getContent() {
        return content;
    }

    @Nullable
    @CardAttr
    @Override
    public String getOwner() {
        return owner;
    }

    @CardAttr
    @Override
    public CalendarEventSource getSource() {
        return source;
    }

    @Nullable
    @CardAttr(EVENT_ATTR_SEQUENCE)
    @Override
    public Long getSequence() {
        return sequence;
    }

    @Nullable
    @CardAttr(EVENT_ATTR_CARD)
    @Override
    public Long getCard() {
        return card;
    }

    @CardAttr(EVENT_ATTR_STATUS)
    @Override
    public CalendarEventStatus getStatus() {
        return status;
    }

    @CardAttr(EVENT_ATTR_TYPE)
    @Override
    public CalendarEventType getType() {
        return type;
    }

    @CardAttr(EVENT_ATTR_BEGIN)
    @Override
    public ZonedDateTime getBegin() {
        return begin;
    }

    @CardAttr(EVENT_ATTR_END)
    @Override
    public ZonedDateTime getEnd() {
        return end;
    }

    @CardAttr(value = EVENT_ATTR_DATE, readFromDb = false)
    @Override
    public LocalDate getDate() {
        return getBegin().withZoneSameInstant(getZoneId()).toLocalDate();
    }

    @CardAttr
    @Override
    public String getTimeZone() {
        return timeZone;
    }

    @CardAttr(EVENT_ATTR_CATEGORY)
    @Override
    public LookupValue getCategory() {
        return category;
    }

    @CardAttr(EVENT_ATTR_PRIORITY)
    @Override
    public LookupValue getPriority() {
        return priority;
    }

    @CardAttr
    @Override
    public List<String> getParticipants() {
        return participants;
    }

    @CardAttr
    @Override
    public List<EmailTemplateInlineData> getNotifications() {
        return notifications;
    }

    @CardAttr
    @Override
    public List<String> getProcessedNotifications() {
        return processedNotifications;
    }

    @Override
    public String toString() {
        return "CalendarEvent{" + "description=" + description + ", id=" + id + ", begin=" + begin + '}';
    }

    public static CalendarEventImplBuilder builder() {
        return new CalendarEventImplBuilder();
    }

    public static CalendarEventImplBuilder copyOf(CalendarEvent source) {
        return new CalendarEventImplBuilder()
                .withId(source.getId())
                .withDescription(source.getDescription())
                .withContent(source.getContent())
                .withOwner(source.getOwner())
                .withSource(source.getSource())
                .withSequence(source.getSequence())
                .withCard(source.getCard())
                .withStatus(source.getStatus())
                .withType(source.getType())
                .withBegin(source.getBegin())
                .withEnd(source.getEnd())
                .withTimeZone(source.getTimeZone())
                .withCategory(source.getCategory())
                .withPriority(source.getPriority())
                .withParticipants(source.getParticipants())
                .withNotifications(source.getNotifications())
                .withProcessedNotifications(source.getProcessedNotifications())
                .withConfig(source.getConfig())
                .withCompleted(source.getCompleted())
                .withNotes(source.getNotes());
    }

    public static class CalendarEventImplBuilder implements Builder<CalendarEventImpl, CalendarEventImplBuilder>, CalendarBuilderWithTimezone<CalendarEventImplBuilder> {

        private String description;
        private String content;
        private String owner, notes;
        private CalendarEventSource source;
        private Long sequence, id;
        private Long card;
        private CalendarEventStatus status;
        private CalendarEventType type;
        private ZonedDateTime begin;
        private ZonedDateTime end, completed;
        private String timeZone;
        private LookupValue category;
        private LookupValue priority;
        private Collection<String> participants;
        private final Set<String> processedNotifications = set();
        private Collection<EmailTemplateInlineData> notifications;
        private CalendarEventConfig config = CalendarEventConfigImpl.builder().build();

        public CalendarEventImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public CalendarEventImplBuilder withConfig(CalendarEventConfig config) {
            this.config = checkNotNull(config);
            return this;
        }

        public CalendarEventImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public CalendarEventImplBuilder withContent(String content) {
            this.content = content;
            return this;
        }

        public CalendarEventImplBuilder withOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public CalendarEventImplBuilder withSource(CalendarEventSource source) {
            this.source = source;
            return this;
        }

        public CalendarEventImplBuilder withSequence(Long sequence) {
            this.sequence = sequence;
            return this;
        }

        public CalendarEventImplBuilder withCard(Long card) {
            this.card = card;
            return this;
        }

        public CalendarEventImplBuilder withStatus(CalendarEventStatus status) {
            this.status = status;
            return this;
        }

        public CalendarEventImplBuilder withType(CalendarEventType type) {
            this.type = type;
            return this;
        }

        public CalendarEventImplBuilder withBegin(ZonedDateTime begin) {
            this.begin = begin;
            return this;
        }

        @Override
        public CalendarEventImplBuilder withTimeZone(String timeZone) {
            this.timeZone = timeZone;
            return this;
        }

        @Override
        public String getTimeZone() {
            return timeZone;
        }

        public CalendarEventImplBuilder withEnd(ZonedDateTime end) {
            this.end = end;
            return this;
        }

        public CalendarEventImplBuilder withCompleted(ZonedDateTime completed) {
            this.completed = completed;
            return this;
        }

        public CalendarEventImplBuilder withCategory(LookupValue category) {
            this.category = category;
            return this;
        }

        public CalendarEventImplBuilder withPriority(LookupValue proprity) {
            this.priority = proprity;
            return this;
        }

        public CalendarEventImplBuilder withParticipants(Collection<String> participants) {
            this.participants = participants;
            return this;
        }

        public CalendarEventImplBuilder withNotifications(Collection<EmailTemplateInlineData> notifications) {
            this.notifications = notifications;
            return this;
        }

        public CalendarEventImplBuilder withProcessedNotifications(Collection<String> processedNotifications) {
            this.processedNotifications.clear();
            this.processedNotifications.addAll(firstNotNull(processedNotifications, emptyList()));
            return this;
        }

        public CalendarEventImplBuilder addProcessedNotifications(Collection<String> processedNotifications) {
            this.processedNotifications.addAll(firstNotNull(processedNotifications, emptyList()));
            return this;
        }

        public CalendarEventImplBuilder withNotes(String notes) {
            this.notes = notes;
            return this;
        }

        public CalendarEventImplBuilder withDate(LocalDate date, String timeZone) {
            return this.withType(CT_DATE).withTimeZone(timeZone).withBegin(date.atStartOfDay(ZoneId.of(checkNotBlank(timeZone))));
        }

        public CalendarEventImplBuilder withInterval(ZonedDateTime from, ZonedDateTime to) {
            return this.withBegin(from).withEnd(to).withType(CT_RANGE);
        }

        public CalendarEventImplBuilder withConfig(Consumer<CalendarEventConfigImplBuilder> config) {
            this.config = CalendarEventConfigImpl.copyOf(this.config).accept(config).build();
            return this;
        }

        @Override
        public CalendarEventImpl build() {
            return new CalendarEventImpl(this);
        }

    }
}
