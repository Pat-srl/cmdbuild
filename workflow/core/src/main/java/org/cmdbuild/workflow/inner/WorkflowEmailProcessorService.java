/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.workflow.FlowEvent;
import static org.cmdbuild.workflow.FlowEvent.FlowEventType.FE_BEFORE_ADVANCE;
import org.cmdbuild.workflow.model.Flow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.eventbus.EventBusService;

@Component
public class WorkflowEmailProcessorService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EmailService emailService;

    public WorkflowEmailProcessorService(EventBusService eventService, EmailService emailService) {
        this.emailService = checkNotNull(emailService);
        eventService.getWorkflowEventBus().register(new Object() {

            @Subscribe
            public void handleFlowEvent(FlowEvent event) {
                if (event.isOfType(FE_BEFORE_ADVANCE)) {
                    processEmailForFlowAdvance(event.getFlow());
                }
            }

        });
    }

    private void processEmailForFlowAdvance(Flow flow) {
        logger.debug("processEmailForFlowAdvance for flow = {}", flow);
        List<Email> list = emailService.getAllForCard(flow.getCardId()).stream().filter(Email::isDraft).collect(toList());
        if (!list.isEmpty()) {
            logger.info("processing {} emails for flow = {}", list.size(), flow);
            list.forEach((email) -> {
                logger.info("set status to outging for email = {}", email);
                emailService.update(EmailImpl.copyOf(email).withStatus(ES_OUTGOING).build());
            });
        }
    }

}
