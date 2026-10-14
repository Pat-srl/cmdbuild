/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsTriggerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class CalendarTriggerWsCommand {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CalendarService calendarService;
    private final UserCardService userCardService;

    public CalendarTriggerWsCommand(CalendarService calendarService, UserCardService userCardService) {
        this.calendarService = checkNotNull(calendarService);
        this.userCardService = checkNotNull(userCardService);
    }

    public CalendarTrigger doReadOne(Long triggerId) {
        return calendarService.getTriggerById(triggerId);
    }

    public CalendarSequence doGetSequencePreview(Long triggerId, String dateValue) {
        return calendarService.buildSequenceFromTrigger(triggerId, toDate(dateValue));
    }

    public void doCreateEvents(String triggerIdOrCode, WsQueryOptions wsQueryOptions) {
        CalendarTrigger trigger;
        if (NumberUtils.isCreatable(triggerIdOrCode)) {
            trigger = calendarService.getTriggerById(toLong(triggerIdOrCode));
        } else {
            trigger = calendarService.getTriggerByCode(triggerIdOrCode);
        }
        List<Card> cards = userCardService.getUserCards(trigger.getOwnerClass(), wsQueryOptions.getQuery()).elements();
        logger.info("create events for {} cards", cards.size());
        cards.forEach(c -> calendarService.createSequenceFromTrigger(trigger.getId(), c));
    }

    public List<CalendarTrigger> doReadMany() {
        return calendarService.getAllTriggers();
    }

    public CalendarTrigger doCreate(WsTriggerData data) {
        CalendarTrigger trigger = data.toTrigger().accept((Consumer) calendarService.fixTimeZone()).build();
        return calendarService.createTrigger(trigger);
    }

    public CalendarTrigger doUpdate(Long triggerId, WsTriggerData data) {
        CalendarTrigger trigger = calendarService.getTriggerById(triggerId);
        return calendarService.updateTrigger(data.toTrigger().withId(trigger.getId()).build());
    }

    public void doDelete(Long triggerId) {
        calendarService.deleteTrigger(triggerId);
    }
}
