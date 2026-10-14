/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import static org.cmdbuild.calendar.beans.CalendarTrigger.TriggerScope.TS_ALWAYS;
import org.cmdbuild.eventbus.EventBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.workflow.WorkflowService.CONTEXT_SCOPE_WORKFLOW;
import org.cmdbuild.event.AfterCardUpdateEvent;
import org.cmdbuild.event.AfterCardCreateEvent;

@Component
public class CalendarTriggerBatchHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CalendarService calendarService;
    private final OperationUserSupplier user;
    private final RequestContextService contextService;

    public CalendarTriggerBatchHandler(OperationUserSupplier user, CalendarService calendarService, EventBusService eventBusService, RequestContextService contextService) {
        this.calendarService = checkNotNull(calendarService);
        this.user = checkNotNull(user);
        this.contextService = checkNotNull(contextService);
        eventBusService.getCardEventBus().register(new Object() {

            @Subscribe
            public void handleAfterCardCreateEvent(AfterCardCreateEvent event) {
                handleCardModified(null, event.getCurrentCard());
            }

            @Subscribe
            public void handleAfterCardUpdateEvent(AfterCardUpdateEvent event) {
                handleCardModified(event.getPreviousCard(), event.getCurrentCard());
            }
        });
    }

    private void handleCardModified(@Nullable Card previousCard, Card currentCard) {
        logger.trace("processing calendar triggers for card {} -> {}", previousCard, currentCard);
        calendarService.getTriggersByOwnerClassIncludeInherited(currentCard.getTypeName()).stream().filter(CalendarTrigger::isActive).forEach(t -> {
            Object currentValue = currentCard.get(t.getOwnerAttr()),
                    previousValue = previousCard == null ? null : previousCard.get(t.getOwnerAttr());
            boolean changed = !equal(currentValue, previousValue),
                    shouldUpdate = (user.getUser().isBatch() || contextService.getRequestContext().hasContextScope(CONTEXT_SCOPE_WORKFLOW)) && t.hasScope(TS_ALWAYS);
            if (changed && shouldUpdate) {
                logger.debug("activate calendar trigger = {} for card = {}", t, currentCard);
                calendarService.createSequenceFromTrigger(t.getId(), currentCard);
            }
        });
    }

}
