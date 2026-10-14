/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.calendar.inner;

import com.google.common.eventbus.EventBus;
import static java.lang.String.format;
import static java.time.ZonedDateTime.now;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.commons.lang3.tuple.Triple;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarEvent;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_CATEGORY_LOOKUP_TYPE;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_PRIORITY_LOOKUP_TYPE;
import org.cmdbuild.calendar.beans.CalendarEventConfigImpl;
import org.cmdbuild.calendar.beans.CalendarEventImpl;
import org.cmdbuild.calendar.beans.CalendarEventType;
import org.cmdbuild.config.CalendarServiceConfiguration;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.config.SubscriptionConfiguration;
import static org.cmdbuild.dao.beans.LookupValueImpl.fromCode;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.QueryBuilder;
import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.email.Email;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_CHAT;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_EMAIL;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.beans.EmailTemplateImpl;
import org.cmdbuild.email.beans.EmailTemplateInlineData;
import org.cmdbuild.email.beans.EmailTemplateInlineDataImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.jobs.JobSessionService;
import org.cmdbuild.jobs.inner.JobExecutorServiceImpl;
import org.cmdbuild.lock.AutoCloseableItemLock;
import static org.cmdbuild.lock.LockScope.LS_REQUEST;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.notification.NotificationCommonData;
import org.cmdbuild.notification.NotificationProvider;
import org.cmdbuild.notification.NotificationProviderAdapter;
import org.cmdbuild.notification.NotificationService;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author afelice
 */
public class CalendarProcessorServiceImplTest {

    private static final String CUSTOMER_CODE = "<CUSTOMER_CODE>";
    private static final String A_USER = "<USER>";
    private final static Long A_USER_ID = 42L;

    private final NotificationProvider emailNotificationProvider = mock(NotificationProvider.class); // For EmailServiceImpl
    private final NotificationProvider chatNotificationProvider = mock(NotificationProvider.class); // For ChatNotificationProvider
    private final NotificationProvider mobileAppNotificationProvider = mock(NotificationProvider.class); // For MobileAppNotificationProvider
    private final List<NotificationProvider> allProviders = list(mobileAppNotificationProvider, emailNotificationProvider, chatNotificationProvider);

    private final UserRoleService userRoleService = mock(UserRoleService.class);
    private final MobileConfiguration mobileConfiguration = mock(MobileConfiguration.class);
    private final SubscriptionConfiguration subscriptionConfiguration = mock(SubscriptionConfiguration.class);

    private NotificationService notificationService;

    private final JobSessionService sessionService = mock(JobSessionService.class);
    private final EmailTemplateService emailTemplateService = mock(EmailTemplateService.class);
    private final DaoService dao = mock(DaoService.class);
    private final CalendarService calendarService = mock(CalendarService.class);
    private final LockService lockService = mock(LockService.class);
    private final RequestContextService requestContextService = mock(RequestContextService.class);
    private final CalendarServiceConfiguration calendarServiceConfiguration = mock(CalendarServiceConfiguration.class);

    private final EventBus eventBus = mock(EventBus.class);

    private CalendarProcessorServiceImpl instance;

    EmailService emailService = mock(EmailService.class);

    @Before
    public void setUp() {
        when(emailNotificationProvider.getNotificationProviderName()).thenReturn(NOTIFICATION_PROVIDER_EMAIL);
        when(emailNotificationProvider.sendNotification(any())).then(returnsFirstArg());
        when(chatNotificationProvider.getNotificationProviderName()).thenReturn(NOTIFICATION_PROVIDER_CHAT); // in ChatNotificationProvider, sendNotification() returns null
        when(mobileAppNotificationProvider.getNotificationProviderName()).thenReturn(NOTIFICATION_PROVIDER_MOBILE_APP);
        when(mobileAppNotificationProvider.sendNotification(any())).then(returnsFirstArg());

        notificationService = new NotificationService() { // As done in NotificationServiceImpl
            NotificationProviderAdapter notificationProvider = new NotificationProviderAdapter(allProviders, userRoleService);

            @Override
            public Email sendNotification(Email notificationData) {
                return notificationProvider.sendNotification(notificationData);
            }

            @Override
            public Email sendNotificationFromTemplate(EmailTemplate template, Map<String, Object> data) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        };

        when(calendarService.getEventBus()).thenReturn(eventBus);
        when(calendarServiceConfiguration.isEnabled()).thenReturn(true);
        when(lockService.aquireLockOrWaitOrFail(anyString(), eq(LS_REQUEST))).thenReturn(mock(AutoCloseableItemLock.class));

        // For processEvents(): skip all
        QueryBuilder mockQueryBuilder = mock(QueryBuilder.class);
        when(dao.selectAll()).thenReturn(mockQueryBuilder);
        when(mockQueryBuilder.from(CalendarEvent.class)).thenReturn(mockQueryBuilder);
        when(mockQueryBuilder.where(anyString(), any(WhereOperator.class), any())).thenReturn(mockQueryBuilder);
        when(mockQueryBuilder.asList()).thenReturn(emptyList());

        // For processNotifications(): handle a single EmailTemplateInlineData
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(dao.getJdbcTemplate()).thenReturn(jdbcTemplate);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(toTripleList(buildFakeTemplateInlineData()));

        // For handleExpiredNotification():
        when(calendarService.getEventById(1L)).thenReturn(buildFakeJustExpiredCalendarEvent(1L));

        // For sendNotification():
        when(emailTemplateService.getTemplate(any())).thenReturn(buildFakeEmailTemplate(1L));

        instance = mockBuildCalendarProcessorServiceImpl_SyncExecutor();
    }

    /**
     * Test of processUpcomingEventsAndNotifications method, email, of class
     * CalendarProcessorServiceImpl.
     */
    @Test
    public void testProcessUpcomingEventsAndNotifications_email() {
        System.out.println("processUpcomingEventsAndNotifications_email");

        // arrange:
        Email email = notificationToSend(NOTIFICATION_PROVIDER_EMAIL);
        when(calendarService.buildNotificationEmail(any(), any())).thenReturn(email);

        // act:
        instance.processUpcomingEventsAndNotifications();

        // assert:
        verify(emailNotificationProvider, times(1)).sendNotification(matchNotificationData(email));
        // verify(emailService, times(1)).create(any(Email.class)); // OLD
    }

    /**
     * Test of processUpcomingEventsAndNotifications method, chat, of class
     * CalendarProcessorServiceImpl.
     */
    @Test
    public void testProcessUpcomingEventsAndNotifications_chat() {
        System.out.println("processUpcomingEventsAndNotifications_chat");

        // arrange:
        Email email = notificationToSend(NOTIFICATION_PROVIDER_CHAT);
        when(calendarService.buildNotificationEmail(any(), any())).thenReturn(email);

        // act:
        instance.processUpcomingEventsAndNotifications();

        // assert:
        verify(chatNotificationProvider, times(1)).sendNotification(matchNotificationData(email));
    }

    /**
     * Test of processUpcomingEventsAndNotifications method, chat, of class
     * CalendarProcessorServiceImpl.
     */
    @Test
    public void testProcessUpcomingEventsAndNotifications_mobileApp() {
        System.out.println("processUpcomingEventsAndNotifications_mobileApp");

        // arrange:
        UserData user = buildUserData(A_USER, A_USER_ID);
        when(userRoleService.getUserDataByUsernameOrNull(eq(A_USER))).thenReturn(user);
        when(subscriptionConfiguration.getSubscriptionCustomerCode()).thenReturn(CUSTOMER_CODE);
        Email email = notificationToSend(NOTIFICATION_PROVIDER_MOBILE_APP);
        when(calendarService.buildNotificationEmail(any(), any())).thenReturn(email);
        Email innerToSendEmail = EmailImpl.copyOf(email).withTo(A_USER).build();

        // act:
        instance.processUpcomingEventsAndNotifications();

        // assert:
        verify(mobileAppNotificationProvider, times(1)).sendNotification(matchNotificationData(innerToSendEmail));
    }

    /**
     * Test of processUpcomingEventsAndNotifications method, email, real asynch
     * notification, waiting for completion, of class
     * CalendarProcessorServiceImpl.
     *
     * <b>Used to test stuff on
     * {@link CalendarProcessorServiceImpl#handleExpiredNotification()},
     * <b>to wait termination</b> of real code
     * {@link CalendarProcessorServiceImpl#sendNotificationAsync()} * to assert
     * invocations on {@link NotificationProvider} and {@link CalendarService}.
     */
    @Test
    public void testProcessUpcomingEventsAndNotifications_asynchNotification() {
        System.out.println("processUpcomingEventsAndNotifications_asynchNnotification");

        // arrange:
        Email email = notificationToSend(NOTIFICATION_PROVIDER_EMAIL);
        when(calendarService.buildNotificationEmail(any(), any())).thenReturn(email);

        // act:
        instance.processUpcomingEventsAndNotifications();

        // assert:
        verify(emailNotificationProvider, times(1)).sendNotification(matchNotificationData(email));
        verify(calendarService, times(1)).updateEvent(any(CalendarEvent.class));
    }

    private CalendarProcessorServiceImpl mockBuildCalendarProcessorServiceImpl_SyncExecutor() {

        return new CalendarProcessorServiceImpl(
                sessionService,
                emailTemplateService,
                emailService,
                notificationService,
                dao,
                calendarService,
                lockService,
                requestContextService,
                calendarServiceConfiguration,
                new JobExecutorServiceImpl(sessionService)
        ) {
            @Override
            protected Future<Boolean> sendNotificationAsync(CalendarEvent event, EmailTemplate emailTemplate) {
                logger.debug("test - async notification fixture for creating notification for event = {} for template = {} - {}", event, emailTemplate);
                Future<Boolean> future = super.sendNotificationAsync(event, emailTemplate);

                // Wait max 5s for task completion.
                try {
                    logger.debug("test - waiting max 5 seconds for creating notification for event = {} for template = {} - {}", event, emailTemplate);
                    // Throws ExcecutionException, TimeoutException, InterruptedException:
                    future.get(5, TimeUnit.SECONDS);
                    logger.debug("test - done send notification for event = {} for template = {} - {}", event, emailTemplate);
                } catch (ExecutionException | TimeoutException | InterruptedException exc) {
                    logger.error("error while async creating notification for event = {} for template = {} - {}", event, emailTemplate, exc);
                }

                return future;
            }
        };
    }

    private static CalendarEventImpl buildFakeJustExpiredCalendarEvent(long id) {
        return CalendarEventImpl.builder()
                .withId(id)
                .withTimeZone("Europe/Rome")
                .withType(CalendarEventType.CT_INSTANT)
                .withBegin(now().minusSeconds(1))
                .withCategory(fromCode(CALENDAR_CATEGORY_LOOKUP_TYPE, "default"))
                .withPriority(fromCode(CALENDAR_PRIORITY_LOOKUP_TYPE, "default"))
                .withConfig(CalendarEventConfigImpl.builder().build())
                .build();
    }

    private static EmailTemplateImpl buildFakeEmailTemplate(long id) {
        return EmailTemplateImpl.builder()
                .withId(id)
                .withCode(format("<ATemplate_%s>", 1))
                .withTextPlainContentType()
                .build();
    }

    private static EmailTemplateInlineDataImpl buildFakeTemplateInlineData() {
        return EmailTemplateInlineDataImpl.builder()
                .withTemplate("{}")
                .build();
    }

    private List toTripleList(EmailTemplateInlineData templateData) {
        return list(Triple.of(1L, templateData, now()));
    }

    private static EmailImpl notificationToSend(String notificationType) {
        return EmailImpl.builder()
                .withNotificationProvider(notificationType)
                .withSubject("<Subject>")
                .withContent("<Content>")
                .withTo(A_USER)
                .build();
    }

    private static NotificationCommonData matchNotificationData(NotificationCommonData notificationData) {
        return argThat(new NotificationDataMatcher(notificationData));
    }

    private UserData buildUserData(String userName, Long userId) {
        UserData user = mock(UserData.class);
        when(user.getUsername()).thenReturn(userName);
        when(user.isActive()).thenReturn(true);
        when(user.isNotService()).thenReturn(true);
        when(user.getId()).thenReturn(userId);

        return user;
    }

} // end CalendarProcessorServiceImplTest class

class NotificationDataMatcher extends ArgumentMatcher<NotificationCommonData> {

    private final NotificationCommonData left;

    NotificationDataMatcher(NotificationCommonData left) {
        this.left = left;
    }

    @Override
    public boolean matches(Object obj) {
        NotificationCommonData right = (NotificationCommonData) obj;
        return Objects.equals(left.getTo(), right.getTo())
                && Objects.equals(left.getSubject(), right.getSubject())
                && Objects.equals(left.getContent(), right.getContent())
                && Objects.equals(left.getNotificationProvider(), right.getNotificationProvider());
    }

    @Override
    public String toString() {
        return format("%s{to =< %s >, subject =< %s >, content =< %s >}",
                NotificationCommonData.class.getName(),
                left.getTo(), left.getSubject(), left.getContent());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(toString());
    }

} // end NotificationDataMatcher class
