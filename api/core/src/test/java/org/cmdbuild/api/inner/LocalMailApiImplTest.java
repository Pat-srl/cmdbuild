/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.api.inner;

import static java.lang.String.format;
import java.util.List;
import java.util.Objects;
import org.cmdbuild.api.fluent.NewMail;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.chat.ChatMessageData;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.config.SubscriptionConfiguration;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.EmailService;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.EmailStatus;
import static org.cmdbuild.email.EmailStatus.ES_OUTGOING;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.notification.NotificationCommonData;
import org.cmdbuild.notification.NotificationProvider;
import org.cmdbuild.notification.NotificationServiceImpl;
import org.cmdbuild.notification.NotificationStatus;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationDataImpl;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationHelper;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.hamcrest.Description;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class LocalMailApiImplTest {

    private static final String CUSTOMER_CODE = "<CUSTOMER_CODE>";

    private final NotificationProvider emailNotificationProvider = mock(NotificationProvider.class); // For EmailServiceImpl
    private final NotificationProvider chatNotificationProvider = mock(NotificationProvider.class); // For ChatNotificationProvider
    private final NotificationProvider mobileAppNotificationProvider = mock(NotificationProvider.class); // For MobileAppNotificationProvider
    private final List<NotificationProvider> allProviders = list(mobileAppNotificationProvider, emailNotificationProvider, chatNotificationProvider);

    private final UserRoleService userRoleService = mock(UserRoleService.class);
    private final MobileConfiguration mobileConfiguration = mock(MobileConfiguration.class);
    private final SubscriptionConfiguration subscriptionConfiguration = mock(SubscriptionConfiguration.class);

    private NotificationServiceImpl instance;
    private LocalMailApiImpl notificationCmApi;

    private static final long FAKE_ID = 1L;

    @Before
    public void setUp() {
        when(emailNotificationProvider.getNotificationProviderName()).thenReturn(Email.NOTIFICATION_PROVIDER_EMAIL);
        when(chatNotificationProvider.getNotificationProviderName()).thenReturn(Email.NOTIFICATION_PROVIDER_CHAT);
        when(mobileAppNotificationProvider.getNotificationProviderName()).thenReturn(MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP);

        instance = new NotificationServiceImpl(mock(EmailTemplateProcessorService.class), allProviders, userRoleService);
        notificationCmApi = new LocalMailApiImpl(instance, mock(EmailService.class), mock(EmailAccountService.class), mock(EmailSignatureService.class));
    }

    /**
     * Test of sendNotification method, default, of class
     * NotificationServiceImpl.
     */
    @Test
    public void testSendNotification_default() {
        System.out.println("sendNotification_default");

        // arrange:
        NewMail wrappedNotificationDataToSend = notificationCmApi.newMail();
        Email innerNotificationToSend = notificationToSendBuilder().build();

        Email expNotificationResult = EmailImpl.copyOf(innerNotificationToSend).withId(FAKE_ID).withStatus(EmailStatus.ES_SENT).build();
        when(emailNotificationProvider.sendNotification(matchNotificationData(innerNotificationToSend))).thenReturn(expNotificationResult);

        // act:
        Long result = wrappedNotificationDataToSend.send();

        // assert:
        assertEquals(Long.valueOf(FAKE_ID), result);
        verify(emailNotificationProvider, times(1)).sendNotification(matchNotificationData(innerNotificationToSend));
    }

    /**
     * Test of sendNotification method, email, of class NotificationServiceImpl.
     */
    @Test
    public void testSendNotification_email() {
        System.out.println("sendNotification_email");

        // arrange:
        NewMail wrappedNotificationToSend = notificationCmApi.newMail().withProvider(Email.NOTIFICATION_PROVIDER_EMAIL);
        Email innerNotificationDataToSend = notificationToSendBuilder().withNotificationProvider(Email.NOTIFICATION_PROVIDER_EMAIL).build();

        Email expNotificationResult = EmailImpl.copyOf(innerNotificationDataToSend).withId(FAKE_ID).withStatus(EmailStatus.ES_SENT).build();
        when(emailNotificationProvider.sendNotification(matchNotificationData(innerNotificationDataToSend))).thenReturn(expNotificationResult);

        // act:
        Long result = wrappedNotificationToSend.send();

        // assert:
        assertEquals(Long.valueOf(FAKE_ID), result);
        verify(emailNotificationProvider, times(1)).sendNotification(matchNotificationData(innerNotificationDataToSend));
    }

    /**
     * Test of sendNotification method, chat, of class NotificationServiceImpl.
     */
    @Test
    public void testSendNotification_chat() {
        System.out.println("sendNotification_chat");

        // arrange:
        NewMail wrappedNotificationToSend = notificationCmApi.newMail().withProvider(Email.NOTIFICATION_PROVIDER_CHAT);
        Email innerNotificationDataToSend = notificationToSendBuilder().withNotificationProvider(Email.NOTIFICATION_PROVIDER_CHAT).build();

        // ChatNotificationProvider.sendNotification always returns null
        when(chatNotificationProvider.sendNotification(matchNotificationData(innerNotificationDataToSend))).thenReturn(null);

        // act:
        Long result = wrappedNotificationToSend.send();

        // assert:
        assertNull(result); // ChatNotificationProvider.sendNotification() always returns null
        verify(chatNotificationProvider, times(1)).sendNotification(matchNotificationData(innerNotificationDataToSend));
    }

    /**
     * Test of sendNotification method, mobileApp, of class
     * NotificationServiceImpl.
     */
    @Test
    public void testSendNotification_mobileApp() {
        System.out.println("sendNotification_mobileApp");

        // arrange:
        when(subscriptionConfiguration.getSubscriptionCustomerCode()).thenReturn(CUSTOMER_CODE);

        NewMail wrappedNotificationDataToSend = notificationCmApi.newMail().withProvider(MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP);
        Email innerNotificationToSend = notificationToSendBuilder().withNotificationProvider(MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP).build(); // skip store mobile app

        final MobileAppNotificationData typedNotificationToSend = buildToSendNotification(innerNotificationToSend);
        final MobileAppNotificationData innerTypedNotificationToSend = MobileAppNotificationDataImpl.copyOf(typedNotificationToSend).build();
        MobileAppNotificationData expNotificationResult = MobileAppNotificationDataImpl.copyOf(innerTypedNotificationToSend).withStatus(NotificationStatus.NS_SENT).build();

        when(mobileAppNotificationProvider.sendNotification(matchNotificationData(innerTypedNotificationToSend))).thenReturn(expNotificationResult);

        // act:
        Long result = wrappedNotificationDataToSend.send();

        // assert:
        assertNull(result); // MobileAppNotificationProvider.sendNotification() doesn't valorize NotificationCommonData.getId() [no stored in db]
        verify(mobileAppNotificationProvider, times(1)).sendNotification(matchNotificationData(innerTypedNotificationToSend));
    }

    /**
     * See behavior in {@link LocalMailApiImpl} for <code>email</code> in
     * created {@link SendableNewMailImpl} with <code>newMail()</code>
     *
     * @return
     */
    private static EmailImpl.EmailImplBuilder notificationToSendBuilder() {
        return EmailImpl.builder().withStatus(ES_OUTGOING);
    }

    private static NotificationCommonData matchNotificationData(NotificationCommonData notificationData) {
        return argThat(new NotificationDataMatcher(notificationData));
    }

    private static MobileAppNotificationData buildToSendNotification(Email innerNotificationToSend) {
        return MobileAppNotificationDataImpl.builder()
                .withNotificationProvider(MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP)
                .withSubject(innerNotificationToSend.getSubject())
                .withContent(innerNotificationToSend.getContent())
                .withTopics(innerNotificationToSend.getTo())
                .build();
    }

} // end LocalMailApiImplTest class

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
                ChatMessageData.class.getName(),
                left.getTo(), left.getSubject(), left.getContent());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(toString());
    }
}
