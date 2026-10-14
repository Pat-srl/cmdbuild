/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.calendar.beans.CalendarEventImpl;
import static org.cmdbuild.calendar.beans.CalendarEventSource.CSO_SYSTEM;
import static org.cmdbuild.calendar.beans.CalendarEventStatus.CST_ACTIVE;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.beans.CalendarSequenceImpl;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import static org.cmdbuild.calendar.beans.EventFrequency.EF_ONCE;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CalendarUtils {

    public static CalendarSequence triggerToSequence(CalendarTrigger trigger, @Nullable Long cardId, LocalDate date) {
        checkNotNull(date);
        ZoneId zoneId = trigger.hasTimeZone() ? trigger.getZoneId() : CmDateUtils.systemZoneId();
        if (trigger.hasDelay()) {
            date = date.plus(trigger.getDelay().getPeriod());
        }
        return CalendarSequenceImpl.builder()
                .withSource(CSO_SYSTEM)
                .withCard(cardId)
                .withCategory(trigger.getCategory())
                .withPriority(trigger.getPriority())
                .withConfig(trigger.getConfig())
                .withContent(trigger.getContent())
                .withDescription(trigger.getDescription())
                .withNotifications(trigger.getNotifications())
                .withTimeZone(zoneId.getId())
                .withEventTime(trigger.getEventTime())
                .withFirstEvent(date)
                .withLastEvent(trigger.getLastEvent())
                .withParticipants(trigger.getParticipants())
                .withType(trigger.getType())
                .withTrigger(trigger.getId())
                .build();
    }

    public static LocalDate getDate(ZonedDateTime timestamp, ZoneId timezone) {
        return timestamp.withZoneSameInstant(timezone).toLocalDate();
    }

    public static List<CalendarEvent> sequenceToEvents(CalendarSequence sequence, List<CalendarEvent> currentEvents) {
        return new SequenceToEventsHelper(sequence, currentEvents).sequenceToEvents();
    }

    public static List<CalendarEvent> sequenceToEvents(CalendarSequence sequence) {
        return new SequenceToEventsHelper(sequence).sequenceToEvents();
    }

    private static class SequenceToEventsHelper {

        private final List<CalendarEvent> currentEvents, currentActiveEvents;
        private final CalendarSequence sequence;

        public SequenceToEventsHelper(CalendarSequence sequence) {
            this.sequence = checkNotNull(sequence);
            this.currentEvents = emptyList();
            this.currentActiveEvents = emptyList();
        }

        public SequenceToEventsHelper(CalendarSequence sequence, List<CalendarEvent> currentEvents) {
            this.sequence = checkNotNull(sequence);
            this.currentEvents = ImmutableList.copyOf(currentEvents);
            currentActiveEvents = list(currentEvents).withOnly(CalendarEvent::isActive).immutable();
        }

        private boolean shouldEndLoop(LocalDate eventDate, int generatedEventsCount) {
            if (sequence.hasFrequency(EF_ONCE) || (sequence.hasMaxActiveEvents() && (currentActiveEvents.size() + generatedEventsCount) >= sequence.getMaxActiveEvents())) {
                return true;
            } else {
                return switch (sequence.getEndType()) {
                    case SET_DATE ->
                        !sequence.getLastEvent().isAfter(eventDate);
                    case SET_NUMBER ->
                        (currentEvents.size() + generatedEventsCount) >= sequence.getEventCount();
                    case SET_NEVER ->
                        false;
                    default ->
                        throw new IllegalArgumentException("unsupported sequence end type = " + sequence.getEndType());
                };
            }
        }

        public List<CalendarEvent> sequenceToEvents() {
            ZoneId zoneId = sequence.getZoneId();
            LocalTime eventTime = sequence.getEventTime();
            List<ZonedDateTime> eventTimes = list();
            LocalDate eventDate;
            int skipEventCount;
            if (currentEvents.isEmpty()) {
                eventDate = sequence.getFirstEvent();
                skipEventCount = 0;
            } else {
                eventDate = currentEvents.get(currentEvents.size() - 1).getDate();
                skipEventCount = 1;
            }
            while (true) {
                ZonedDateTime eventTimestamp = eventDate.atTime(eventTime).atZone(zoneId);
                if (skipEventCount == 0) {
                    eventTimes.add(eventTimestamp);
                } else {
                    skipEventCount--;
                }
                if (shouldEndLoop(eventDate, eventTimes.size())) {
                    break;
                } else {
                    eventDate = switch (sequence.getFrequency()) {
                        case EF_DAILY ->
                            eventDate.plusDays(sequence.getFrequencyMultiplier());
                        case EF_WEEKLY ->
                            eventDate.plusWeeks(sequence.getFrequencyMultiplier());
                        case EF_MONTHLY ->
                            eventDate.plusMonths(sequence.getFrequencyMultiplier());
                        case EF_YEARLY ->
                            eventDate.plusYears(sequence.getFrequencyMultiplier());
                        default ->
                            throw new IllegalArgumentException("unsupported sequence frequency = " + sequence.getFrequency());
                    };
                }
            }
            return eventTimes.stream().map(t -> CalendarEventImpl.builder()
                    .withSequence(sequence.getId())
                    .withSource(sequence.getSource())
                    .withCard(sequence.getCard())
                    .withCategory(sequence.getCategory())
                    .withContent(sequence.getContent())
                    .withDescription(sequence.getDescription())
                    .withConfig(c -> c
                    .withEventEditMode(sequence.getEventEditMode())
                    .withOnCardDeleteAction(sequence.getOnCardDeleteAction()))
                    .withNotifications(sequence.getNotifications())
                    .withOwner(sequence.getOwner())
                    .withParticipants(sequence.getParticipants())
                    .withPriority(sequence.getPriority())
                    .withStatus(CST_ACTIVE)
                    .withType(sequence.getType())
                    .withTimeZone(zoneId.getId())
                    .withBegin(t)
                    .build()).collect(toList());
        }

    }
}
