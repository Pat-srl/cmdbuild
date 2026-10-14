/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.data;

import java.util.List;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;

public interface CalendarEventRepository {

    CalendarEvent createEvent(CalendarEvent event);

    CalendarEvent updateEvent(CalendarEvent event);

    void deleteEvent(long id);

    CalendarEvent getEventById(long id);

    List<CalendarEvent> getAllEvents();
    
    CalendarEvent getUserEvent(long id);

    PagedElements<CalendarEvent> getUserEvents(DaoQueryOptions query);

    List<CalendarEvent> getEventsForSequence(long sequenceId);

    default List<CalendarEvent> getEventsForSequence(CalendarSequence sequence) {
        return getEventsForSequence(sequence.getId());
    }

}
