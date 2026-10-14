/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.EventBus;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.CalendarTriggersService;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.calendar.beans.CalendarEventImpl;
import static org.cmdbuild.calendar.beans.CalendarEventSource.CSO_SYSTEM;
import static org.cmdbuild.calendar.beans.CalendarEventSource.CSO_USER;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.beans.CalendarSequenceImpl;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import org.cmdbuild.calendar.beans.CalendarTriggerImpl;
import org.cmdbuild.calendar.data.CalendarEventRepository;
import org.cmdbuild.calendar.data.CalendarSequenceRepository;
import org.cmdbuild.calendar.events.CalendarEventCompletedEvent;
import org.cmdbuild.calendar.events.CalendarEventUpdateEvent;
import static org.cmdbuild.calendar.utils.CalendarUtils.sequenceToEvents;
import static org.cmdbuild.calendar.utils.CalendarUtils.triggerToSequence;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.participant.ParticipantService;
import org.cmdbuild.template.ExpressionInputData;
import org.cmdbuild.template.SimpleExpressionInputData;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.userconfig.UserConfigService;
import static org.cmdbuild.userconfig.UserConfigService.USER_TIME_ZONE;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.checkThatZoneIdIsRegion;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CalendarServiceImpl implements CalendarService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus = new EventBus(logExceptions(logger));

    private final CalendarSequenceRepository sequenceRepository;
    private final CalendarEventRepository eventRepository;
    private final UserConfigService userConfigService;
    private final DaoService dao;
    private final ParticipantService participantService;
    private final EmailTemplateProcessorService emailTemplateProcessorService;
    private final ObjectTranslationService translationService;
    private final CalendarTriggersService calendarTriggersService;

    public CalendarServiceImpl(CalendarSequenceRepository sequenceRepository, CalendarEventRepository eventRepository, UserConfigService userConfigService, DaoService dao, ParticipantService participantService, EmailTemplateProcessorService emailTemplateProcessorService, ObjectTranslationService translationService, CalendarTriggersService calendarTriggersService) {
        this.sequenceRepository = checkNotNull(sequenceRepository);
        this.eventRepository = checkNotNull(eventRepository);
        this.userConfigService = checkNotNull(userConfigService);
        this.dao = checkNotNull(dao);
        this.participantService = checkNotNull(participantService);
        this.emailTemplateProcessorService = checkNotNull(emailTemplateProcessorService);
        this.translationService = checkNotNull(translationService);
        this.calendarTriggersService = checkNotNull(calendarTriggersService);
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PagedElements<CalendarEvent> getUserEvents(DaoQueryOptions query) {
        return eventRepository.getUserEvents(query);
    }

    @Override
    public CalendarEvent createUserEvent(CalendarEvent event) {
        checkArgument(equal(event.getSource(), CSO_USER), "invalid event source for user event");
        //TODO check participants, notifications, etc
        return createEvent(event);
    }

    @Override
    public CalendarSequence buildSequenceFromTrigger(long triggerId, LocalDate date) {
        CalendarTrigger trigger = getTriggerById(triggerId);
        trigger = CalendarTriggerImpl.copyOf(trigger)
                .withDescription(translationService.translateCalendarTriggerDescription(trigger.getCode(), trigger.getDescription()))
                .withContent(translationService.translateCalendarTriggerContent(trigger.getCode(), trigger.getContent()))
                .build();
        return triggerToSequence(trigger, null, date);
    }

    @Override
    public List<CalendarEvent> buildEventsFromSequence(CalendarSequence sequence) {
        return sequenceToEvents(sequence);
    }

    @Override
    public CalendarSequence createSequence(CalendarSequence seq, Collection<CalendarEvent> events) {
        if (seq.hasTrigger()) {
            CalendarTrigger trigger = getTriggerById(seq.getTrigger());
            seq = CalendarSequenceImpl.copyOf(seq)
                    .withSource(CSO_SYSTEM)
                    .withParticipants(trigger.getParticipants())
                    //TODO copy other stuff
                    .build();
        }
        CalendarSequence sequence = sequenceRepository.createSequence(seq);
        events = events.stream().map(e -> CalendarEventImpl.copyOf(e)
                .withSequence(sequence.getId())
                .withSource(sequence.getSource())
                .withCard(sequence.getCard())
                .withParticipants(sequence.getParticipants())
                //TODO copy other stuff
                .build()).map(this::handleTemplateExpressions).map(eventRepository::createEvent).collect(toList());
        eventBus.post(CalendarEventUpdateEvent.INSTANCE);//TODO improve this
        return CalendarSequenceImpl.copyOf(sequence).withEvents(events).build();
    }

    @Override
    public CalendarSequence updateSequence(CalendarSequence seq, Collection<CalendarEvent> events) {
        eventRepository.getEventsForSequence(seq.getId()).forEach(e -> eventRepository.deleteEvent(e.getId())); //TODO improve this
        CalendarSequence sequence = sequenceRepository.updateSequence(seq);
        events = events.stream().map(e -> CalendarEventImpl.copyOf(e).withSequence(sequence.getId()).withSource(sequence.getSource()).withCard(sequence.getCard()).build()).map(this::handleTemplateExpressions).map(eventRepository::createEvent).collect(toList());
        eventBus.post(CalendarEventUpdateEvent.INSTANCE);//TODO improve this
        return CalendarSequenceImpl.copyOf(sequence).withEvents(events).build();
    }

    @Override
    public List<CalendarSequence> getSequencesByCard(long cardId) {
        return sequenceRepository.getSequencesByCard(cardId);
    }

    @Override
    public List<CalendarSequence> getSequencesByCardIncludeEvents(long cardId) {
        return getSequencesByCard(cardId).stream().map(CalendarSequence::getId).map(this::getSequenceIncludeEvents).collect(toList());//TODO improve this
    }

    @Override
    public void deleteSequence(long sequenceId) {
        eventRepository.getEventsForSequence(sequenceId).forEach(e -> eventRepository.deleteEvent(e.getId()));
        sequenceRepository.deleteSequence(sequenceId);
        eventBus.post(CalendarEventUpdateEvent.INSTANCE);//TODO improve this
    }

    @Override
    public List<CalendarTrigger> getAllTriggers() {
        return calendarTriggersService.getAllTriggers();
    }

    @Override
    public List<CalendarTrigger> getTriggersByOwnerClassIncludeInherited(String ownerClass) {
        return calendarTriggersService.getTriggersByOwnerClassIncludeInherited(ownerClass);
    }

    @Override
    public List<CalendarTrigger> getTriggersByOwnerClassOwnerAttrIncludeInherited(String ownerClass, String ownerAttr) {
        return calendarTriggersService.getTriggersByOwnerClassOwnerAttrIncludeInherited(ownerClass, ownerAttr);
    }

    @Override
    public List<CalendarTrigger> getTriggersByOwnerClass(String ownerClass) {
        return calendarTriggersService.getTriggersByOwnerClass(ownerClass);
    }

    @Override
    public List<CalendarTrigger> getTriggersByOwnerClassOwnerAttr(String ownerClass, String ownerAttr) {
        return calendarTriggersService.getTriggersByOwnerClassOwnerAttr(ownerClass, ownerAttr);
    }

    @Override
    public CalendarTrigger createTrigger(CalendarTrigger trigger) {
        return calendarTriggersService.createTrigger(trigger);
    }

    @Override
    public CalendarTrigger updateTrigger(CalendarTrigger trigger) {
        return calendarTriggersService.updateTrigger(trigger);
    }

    @Override
    public CalendarTrigger getTriggerById(long id) {
        return calendarTriggersService.getTriggerById(id);
    }

    @Override
    public CalendarTrigger getTriggerByCode(String triggerCode) {
        return calendarTriggersService.getTriggerByCode(triggerCode);
    }

    @Override
    public void deleteTrigger(long id) {
        calendarTriggersService.deleteTrigger(id);
    }

    @Override
    public CalendarSequence getSequence(long id) {
        return sequenceRepository.getSequence(id);
    }

    @Override
    public CalendarSequence getSequenceIncludeEvents(long id) {
        return CalendarSequenceImpl.copyOf(getSequence(id)).withEvents(getEventsForSequence(id)).build();
    }

    @Override
    public CalendarEvent createEvent(CalendarEvent event) {
        event = eventRepository.createEvent(handleTemplateExpressions(event));
        logger.debug("create calendar event = {}", event);
        eventBus.post(CalendarEventUpdateEvent.INSTANCE);//TODO improve this
        return event;
    }

    @Override
    public CalendarEvent updateEvent(CalendarEvent event) {
        CalendarEvent current = eventRepository.getEventById(event.getId());
        event = eventRepository.updateEvent(CalendarEventImpl.copyOf(event).addProcessedNotifications(current.getProcessedNotifications()).build());
        if (current.isActive() && !event.isActive()) {
            eventBus.post(new CalendarEventCompletedEventImpl(event));
        }
        eventBus.post(CalendarEventUpdateEvent.INSTANCE);//TODO improve this
        return event;
    }

    @Override
    public void deleteEvent(long id) {
        eventRepository.deleteEvent(id);
        eventBus.post(CalendarEventUpdateEvent.INSTANCE);//TODO improve this
    }

    @Override
    public CalendarEvent getEventById(long id) {
        return eventRepository.getEventById(id);
    }

    @Override
    public List<CalendarEvent> getAllEvents() {
        return eventRepository.getAllEvents();
    }

    @Override
    public List<CalendarEvent> getEventsForSequence(long sequenceId) {
        return eventRepository.getEventsForSequence(sequenceId);
    }

    @Override
    public ZoneId getUserTimeZone() {
        String userTimezone = userConfigService.getForCurrentUsernameOrNull(USER_TIME_ZONE);
        if (isNotBlank(userTimezone)) {
            try {
                return checkThatZoneIdIsRegion(ZoneId.of(userTimezone));
            } catch (Exception ex) {
                logger.warn("invalid user time zone =< %s >", userTimezone, ex);
            }
        }
        return CmDateUtils.systemZoneId();
    }

    @Override
    public CalendarEvent getUserEvent(long id) {
        return eventRepository.getUserEvent(id);
    }

    @Override
    public Email buildNotificationEmail(CalendarEvent event, EmailTemplate notification) {
        Card card = event.hasCard() ? dao.getCard(event.getCard()) : null;
        List<String> emailAddresses = participantService.getParticipantsEmailAddresses(event.getParticipants());
        Email email = emailTemplateProcessorService.processEmail(
                null, // builds synthesized draft email
                ExpressionInputData.builder()
                        .withTemplate(notification)
                        .withClientCard(card)
                        .withOtherData(map("event", event, "card", card, "emailAddresses", emailAddresses))
                        .build()
        );
        email = EmailImpl.copyOf(email).withReference(event.getId()).build();
        if (!email.hasAnyDestinationAddress()) {
            email = EmailImpl.copyOf(email).withToAddresses(emailAddresses).build();
        }
        return email;
    }

    @Override
    public void createSequenceFromTrigger(long triggerId, Card card) {
        CalendarTrigger trigger = getTriggerById(triggerId);
        logger.debug("create sequence/events for trigger = {} card = {}", trigger, card);
        if (toDate(card.get(trigger.getOwnerAttr())) != null) {
            CalendarSequence sequence = buildSequenceFromTrigger(trigger.getId(), toDate(card.get(trigger.getOwnerAttr())));
            sequence = CalendarSequenceImpl.copyOf(sequence).withCard(card.getId()).build(); //TODO check this
            sequence = CalendarSequenceImpl.copyOf(sequence).withEvents(buildEventsFromSequence(sequence)).build();
            CalendarSequence currentSequence = getSequencesByCardIncludeEvents(card.getId()).stream().filter(s -> equal(s.getTrigger(), trigger.getId())).collect(toOptional()).orElse(null);
            if (currentSequence != null) {
                logger.debug("delete old sequence = {}", currentSequence);
                deleteSequence(currentSequence.getId());
            }
            createSequence(sequence);
        }
    }

    private CalendarEvent handleTemplateExpressions(CalendarEvent event) {
        if (equal(event.getSource(), CSO_SYSTEM)) {
            Card card = isNotNullAndGtZero(event.getCard()) ? dao.getCard(event.getCard()) : null;//TODO access control (??)
            return CalendarEventImpl.copyOf(event)
                    .withDescription(
                            emailTemplateProcessorService.processExpression(
                                    SimpleExpressionInputData.extendedBuilder()
                                            .withExpression(event.getDescription())
                                            .withClientCard(card)
                                            .build()))
                    .withContent(
                            emailTemplateProcessorService.processExpression(
                                    SimpleExpressionInputData.extendedBuilder()
                                            .withExpression(event.getContent())
                                            .withClientCard(card)
                                            .build()))
                    .build();
        } else {
            return event;
        }
    }

    private static class CalendarEventCompletedEventImpl implements CalendarEventCompletedEvent {

        private final CalendarEvent event;

        public CalendarEventCompletedEventImpl(CalendarEvent event) {
            this.event = checkNotNull(event);
        }

        @Override
        public CalendarEvent getEvent() {
            return event;
        }
    }

}
