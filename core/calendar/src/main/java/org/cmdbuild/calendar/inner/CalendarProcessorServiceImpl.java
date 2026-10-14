/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PreDestroy;
import static java.lang.String.format;
import java.time.Duration;
import java.time.ZonedDateTime;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.tuple.Triple;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarEvent;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_ATTR_END;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_ATTR_STATUS;
import org.cmdbuild.calendar.beans.CalendarEventImpl;
import static org.cmdbuild.calendar.beans.CalendarEventStatus.CST_ACTIVE;
import static org.cmdbuild.calendar.beans.CalendarEventStatus.CST_EXPIRED;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.events.CalendarEventCompletedEvent;
import org.cmdbuild.calendar.events.CalendarEventUpdateEvent;
import static org.cmdbuild.calendar.inner.CalendarProcessorService.CALENDAR_SERVICE_NAME;
import static org.cmdbuild.calendar.utils.CalendarUtils.sequenceToEvents;
import org.cmdbuild.config.CalendarServiceConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.LT;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailService;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.beans.EmailTemplateInlineData;
import org.cmdbuild.email.beans.EmailTemplateInlineDataImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.jobs.JobExecutorService;
import static org.cmdbuild.jobs.JobExecutorService.JOBUSER_SYSTEM;
import org.cmdbuild.jobs.JobSessionService;
import org.cmdbuild.lock.AutoCloseableItemLock;
import static org.cmdbuild.lock.LockScope.LS_REQUEST;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.minions.PostStartup;
import org.cmdbuild.minions.PreShutdown;
import org.cmdbuild.notification.NotificationService;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExecutorUtils.restartable;
import static org.cmdbuild.utils.lang.CmExecutorUtils.scheduledExecutorService;
import org.cmdbuild.utils.lang.RestartableExecutorHelper;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(CALENDAR_SERVICE_NAME)
public class CalendarProcessorServiceImpl implements CalendarProcessorService {

    private final static String CALENDAR_EVENT_PROCESSING = "org.cmdbuild.calendar.inner.CALENDAR_EVENT_PROCESSING";

    protected final Logger logger;

    private final CalendarService calendarService;
    private final DaoService dao;
    private final EmailTemplateService emailTemplateService;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final LockService lockService;
    private final CalendarServiceConfiguration calendarServiceConfiguration;
    private final JobExecutorService jobExecutorService;

    private final Object calendarEventUpdateObserver; // needed for test purposes
    private final RestartableExecutorHelper<ScheduledExecutorService> scheduledExecutorService;

    public CalendarProcessorServiceImpl(JobSessionService sessionService, EmailTemplateService emailTemplateService, EmailService emailService, NotificationService notificationService, DaoService dao, CalendarService calendarService, LockService lockService, RequestContextService contextService, CalendarServiceConfiguration calendarServiceConfiguration, JobExecutorService jobExecutorService) {
        this.logger = LoggerFactory.getLogger(getClass());
        this.calendarService = checkNotNull(calendarService);
        this.lockService = checkNotNull(lockService);
        this.dao = checkNotNull(dao);
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.notificationService = checkNotNull(notificationService);
        this.emailService = checkNotNull(emailService);
        this.calendarServiceConfiguration = checkNotNull(calendarServiceConfiguration);
        this.jobExecutorService = checkNotNull(jobExecutorService);
        checkNotNull(contextService);
        checkNotNull(sessionService);
        scheduledExecutorService = restartable(() -> scheduledExecutorService("calendar_processor",
                () -> sessionService.createJobSessionContextWithUser(JOBUSER_SYSTEM, "calendar event processing background job"), //TODO do not expire these sessions (at least increase expiration time)
                sessionService::destroyJobSessionContext));
        this.calendarEventUpdateObserver = new Object() {

            @Subscribe
            public void handleCalendarEventCompletedEvent(CalendarEventCompletedEvent event) {
                checkAndGrowSequenceForCompletedEvent(event.getEvent());
            }

            @Subscribe
            public void handleCalendarEventUpdateEvent(CalendarEventUpdateEvent event) {
                logger.debug("calendar service is handling its event {}", event);
                processUpcomingEventsAndNotifications();//TODO improve this, handle only modified events! check also cluster support !!
            }

        };
        this.calendarService.getEventBus().register(calendarEventUpdateObserver);
    }

    @PostStartup
    public void processUpcomingEventsAndNotificationsPostStartup() throws Exception {
        logger.info("start");
        scheduledExecutorService.start().submit(() -> processUpcomingEventsAndNotifications()).get();//TODO improve this, cluster run (?)
    }

    @ScheduledJob(alias = "processEventsAndNotifications", value = "0 10 * * * ?", clusterMode = RUN_ON_SINGLE_NODE, user = JOBUSER_SYSTEM)//once per hour
    public void processUpcomingEventsAndNotifications() {//TODO check   cluster support, expecially when triggered from event !!
        if (calendarServiceConfiguration.isEnabled()) {
            try (AutoCloseableItemLock lock = lockService.aquireLockOrWaitOrFail(CALENDAR_EVENT_PROCESSING, LS_REQUEST)) {
                ZonedDateTime scheduledProcessingDateTimeWindowLimit = now().plus(Duration.ofMinutes(60));
                processEvents(dao.selectAll().from(CalendarEvent.class)
                        .where(EVENT_ATTR_STATUS, EQ, CST_ACTIVE)
                        .where(EVENT_ATTR_END, LT, scheduledProcessingDateTimeWindowLimit)
                        .asList());
                processNotifications(dao.getJdbcTemplate().query(format("WITH q AS ( SELECT"
                        + " e.\"Id\" _event,"
                        + " en _notification,"
                        + " e.\"EventBegin\" + format( '%%s seconds', coalesce((en ->> 'delay') :: int, n.\"Delay\", 0) ) :: interval _notification_datetime"
                        + " FROM \"_CalendarEvent\" e, jsonb_array_elements(e.\"Notifications\") en, \"_EmailTemplate\" n"
                        + " WHERE e.\"Status\" = 'A' AND n.\"Status\" = 'A' AND n.\"Code\" = en ->> 'template' AND e.\"EventStatus\" <> 'canceled' AND en ->> 'id' <> ALL (e.\"ProcessedNotifications\"))"
                        + " SELECT * FROM q WHERE _notification_datetime < %s",
                        systemToSqlExpr(scheduledProcessingDateTimeWindowLimit)), (r, i) -> Triple.of(r.getLong("_event"), fromJson(r.getString("_notification"), EmailTemplateInlineDataImpl.class), toDateTime(r.getTimestamp("_notification_datetime")))));
            }
        }
    }

    @PreShutdown
    public void stop() {
        logger.info("stop");
        scheduledExecutorService.stop();
    }

    @PreDestroy
    public void cleanup() {
        logger.info("cleanup");
    }

    /**
     * For testing purposes only
     */
    public void unregisterCalendarEventObserver() {
        logger.debug("unregistering calendar event update observer");
        calendarService.getEventBus().unregister(this.calendarEventUpdateObserver);
    }

    /**
     * For testing purposes only
     */
    public void registerCalendarEventObserver() {
        logger.debug("registering calendar event update observer");
        calendarService.getEventBus().register(this.calendarEventUpdateObserver);
    }

    private void processEvents(List<CalendarEvent> events) {
        logger.debug("processing {} calendar events", events.size());
        events.forEach(this::processCalendarEvent);
        logger.debug("processed {} calendar events", events.size());
    }

    private void processNotifications(List<Triple<Long, EmailTemplateInlineData, ZonedDateTime>> notifications) {
        logger.debug("processing {} calendar notifications", notifications.size());
        notifications.forEach(n -> processCalendarNotification(n.getLeft(), n.getMiddle(), n.getRight()));
        logger.debug("processed {} calendar notifications", notifications.size());
    }

    private void processCalendarEventWithLock(long eventId) {
        try (AutoCloseableItemLock lock = lockService.aquireLockOrWaitOrFail(CALENDAR_EVENT_PROCESSING, LS_REQUEST)) {
            processCalendarEvent(calendarService.getEventById(eventId));
        }
    }

    private void processCalendarEvent(CalendarEvent event) {
        try {
            long expirationTime = SECONDS.between(now(), event.getEnd());
            boolean isExpired = expirationTime <= 0;
            if (isExpired) {
                handleExpiredEvent(event);
            } else {
                scheduledExecutorService.get().schedule(() -> processCalendarEventWithLock(event.getId()), expirationTime + 1, TimeUnit.SECONDS);
            }
        } catch (Exception ex) {
            logger.error(marker(), "error processing calendar event = {}", event, ex);
        }
    }

    private void handleExpiredEvent(CalendarEvent event) {
        if (event.isActive()) {
            logger.info("update event, set expired = {}", event);
            calendarService.updateEvent(CalendarEventImpl.copyOf(event).withStatus(CST_EXPIRED).build());
            logger.debug("send draft emails attached to this event");
            emailService.getAllForCard(event.getId()).stream()
                    .filter(Email::isDraft)//TODO add new status for this kind of processing
                    .forEach(e -> emailService.update(EmailImpl.copyOf(e).withStatus(ES_OUTGOING).build()));
        } else {
            logger.debug("skipping event = {} : already processed", event);
        }
    }

    private void processCalendarNotification(long eventId, EmailTemplateInlineData notification, ZonedDateTime expirationDateTime) {
        try {
            logger.debug("processing notification = {} for event = {} with expiration = {}", notification, eventId, toIsoDateTime(expirationDateTime));
            long expirationTime = SECONDS.between(now(), expirationDateTime);
            boolean isExpired = expirationTime <= 0;
            if (isExpired) {
                handleExpiredNotification(eventId, notification);
            } else {
                long delaySeconds = expirationTime + 2;//add two to account from approximations
                logger.debug("schedule notification = {} for event = {} with delay = {}", notification, eventId, toUserDuration(Duration.ofSeconds(delaySeconds)));
                scheduledExecutorService.get().schedule(() -> processCalendarNotificationWithLock(eventId, notification, expirationDateTime), delaySeconds, TimeUnit.SECONDS);
            }
        } catch (Exception ex) {
            logger.error(marker(), "error processing calendar event notification for event = {} notification = {}", eventId, notification, ex);
        }
    }

    private void processCalendarNotificationWithLock(long eventId, EmailTemplateInlineData notification, ZonedDateTime expirationDateTime) {
        try (AutoCloseableItemLock lock = lockService.aquireLockOrWaitOrFail(CALENDAR_EVENT_PROCESSING, LS_REQUEST)) {
            processCalendarNotification(eventId, notification, expirationDateTime);
        }
    }

    private void handleExpiredNotification(long eventId, EmailTemplateInlineData notification) {
        CalendarEvent event = calendarService.getEventById(eventId);
        EmailTemplate emailTemplate = emailTemplateService.getTemplate(notification);
        if (event.getProcessedNotifications().contains(notification.getId())) {
            logger.debug("skipping notification = {} for event = {} : already processed", notification, event);
        } else {
            logger.debug("handling event notification = {} for event = {}", notification, event);
            sendNotificationAsync(event, emailTemplate);
            logger.debug("submitted event notification = {} for event = {}", notification, event);
            calendarService.updateEvent(CalendarEventImpl.copyOf(event).withProcessedNotifications(set(event.getProcessedNotifications()).with(notification.getId())).build());
            logger.debug("calendar updated for event notification = {} for event = {}", notification, event);
        }
    }

    /**
     *
     * @param event
     * @param emailTemplate
     * @return <code>Future</code> containing <dl><dt><code>true</code> <dd>if
     * sent successful;
     * <dt><code>false</code> <dd>otherwise</dl>
     */
    protected Future<Boolean> sendNotificationAsync(CalendarEvent event, EmailTemplate emailTemplate) {
        logger.debug("submit async event = {} for template = {}", event, emailTemplate);
        Future<Boolean> future = jobExecutorService.executeJobAs(() -> sendNotification(event, emailTemplate), JOBUSER_SYSTEM);

        logger.debug("future obtained for async event = {} for template = {}", event, emailTemplate);
        return future;
    }

    /**
     *
     * @param event
     * @param notification
     * @return <dl><dt><code>true</code> <dd>if sent successful;
     * <dt><code>false</code> <dd>otherwise</dl>
     */
    protected Boolean sendNotification(CalendarEvent event, EmailTemplate notification) {//TODO move this to notification service (?)
        try {
            logger.info("send calendar event notification = {} for event = {}", notification, event);
            Email email = calendarService.buildNotificationEmail(event, notification);
            notificationService.sendNotification(email);
            logger.info("sent calendar event notification = {}", email);
        } catch (Exception ex) {
            logger.error(marker(), "error sending calendar event notification for event = {} notification = {}", event, notification, ex);
            return false;
        }

        logger.debug("sent calendar event [exiting sendNotification()] notification = {} for event = {}", notification, event);
        return true;
    }

    private void checkAndGrowSequenceForCompletedEvent(CalendarEvent event) {
        logger.debug("received completed event = {}, processing sequence", event);
        if (event.hasSequence()) {
            CalendarSequence sequence = calendarService.getSequence(event.getSequence());
            List<CalendarEvent> newEvents = sequenceToEvents(sequence, calendarService.getEventsForSequence(sequence));
            newEvents.forEach(e -> {
                logger.info(marker(), "add new event = {} for sequence = {}", e, sequence);
                calendarService.createEvent(e);
            });
        }
    }
}
