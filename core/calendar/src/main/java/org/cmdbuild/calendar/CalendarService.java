/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar;

import com.google.common.eventbus.EventBus;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.calendar.beans.CalendarBuilderWithTimezone;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import org.cmdbuild.calendar.data.CalendarEventRepository;
import org.cmdbuild.calendar.data.CalendarTriggerRepository;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.template.EmailTemplate;

public interface CalendarService extends CalendarEventRepository, CalendarTriggerRepository {

    EventBus getEventBus();

    CalendarSequence getSequence(long id);

    CalendarSequence getSequenceIncludeEvents(long id);

    CalendarSequence createSequence(CalendarSequence sequence, Collection<CalendarEvent> events);

    CalendarSequence updateSequence(CalendarSequence sequence, Collection<CalendarEvent> events);

    CalendarSequence buildSequenceFromTrigger(long triggerId, LocalDate date);

    void deleteSequence(long sequenceId);

    CalendarEvent createUserEvent(CalendarEvent event);

    List<CalendarEvent> buildEventsFromSequence(CalendarSequence sequence);

    List<CalendarSequence> getSequencesByCard(long cardId);

    List<CalendarSequence> getSequencesByCardIncludeEvents(long cardId);

    List<CalendarTrigger> getTriggersByOwnerClassIncludeInherited(String ownerClass);

    List<CalendarTrigger> getTriggersByOwnerClassOwnerAttrIncludeInherited(String ownerClass, String ownerAttr);

    ZoneId getUserTimeZone();

    Email buildNotificationEmail(CalendarEvent event, EmailTemplate notification);

    void createSequenceFromTrigger(long triggerId, Card card);

    default Consumer<? extends CalendarBuilderWithTimezone> fixTimeZone() {
        return (b) -> {
            if (isBlank(b.getTimeZone())) {
                b.withTimeZone(getUserTimeZone().toString());
            }
        };
    }

    default CalendarSequence createSequence(CalendarSequence sequence) {
        return createSequence(sequence, sequence.getEvents());
    }

    default CalendarSequence updateSequence(CalendarSequence sequence) {
        return updateSequence(sequence, sequence.getEvents());
    }

}
