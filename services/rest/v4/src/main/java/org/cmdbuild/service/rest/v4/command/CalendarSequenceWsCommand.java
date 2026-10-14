/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.service.rest.v4.model.WsSequenceData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class CalendarSequenceWsCommand {

    private final CalendarService calendarService;

    public CalendarSequenceWsCommand(CalendarService calendarService) {
        this.calendarService = checkNotNull(calendarService);
    }

    public CalendarSequence doReadOne(Long sequenceId, Boolean includeEvents) {
        return includeEvents ? calendarService.getSequenceIncludeEvents(sequenceId) : calendarService.getSequence(sequenceId);
    }

    public List<CalendarSequence> doReadManyByCard(Long cardId, Boolean includeEvents) {
        return includeEvents ? calendarService.getSequencesByCardIncludeEvents(cardId) : calendarService.getSequencesByCard(cardId); //TODO card access control, sequence access control
    }

    public CalendarSequence doCreate(WsSequenceData data) {
        CalendarSequence sequence = data.toSequence(calendarService).accept((Consumer) calendarService.fixTimeZone()).build();
        return calendarService.createSequence(sequence);
    }

    public CalendarSequence doUpdate(Long sequenceId, WsSequenceData data) {
        CalendarSequence sequence = calendarService.getSequence(sequenceId);
        return calendarService.updateSequence(data.toSequence(calendarService).withId(sequence.getId()).build());
    }

    public void doDelete(Long sequenceId) {
        calendarService.deleteSequence(sequenceId);
    }

    public List<CalendarEvent> doGetEventsPreview(WsSequenceData data) {
        return calendarService.buildEventsFromSequence(data.toSequence(calendarService).build());
    }
}
