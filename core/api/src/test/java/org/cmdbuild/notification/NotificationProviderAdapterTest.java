/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.notification;

import static java.lang.String.format;
import java.util.List;
import java.util.Objects;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.config.SubscriptionConfiguration;
import org.cmdbuild.email.Email;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_CHAT;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_EMAIL;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.notification.NotificationCommonData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationDataImpl;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationHelper;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.fromListToString;
import org.hamcrest.Description;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class NotificationProviderAdapterTest {

    private static final String CUSTOMER_CODE = "<CUSTOMER_CODE>";

    private final static String A_USER = "aUser";
    private final static Long A_USER_ID = 42L;

    private final static String A_GROUP_NAME = "aGroup";

    private final static String A_GROUP_USER_NAME = "aGroup_user1";
    private final static Long A_GROUP_USER_ID = 101L;
    private final static String A_GROUP_ROLE = NotificationProviderAdapter.buildGroupRoleTarget(A_GROUP_NAME);

    private final static String ANOTHER_GROUP_USER_NAME = "aGroup_user2";
    private final static Long ANOTHER_GROUP_USER_ID = 102L;

    private final static String A_SUBJECT = "<Subject>";
    private final static String A_CONTENT = "<Content>";

    private final NotificationProvider emailNotificationProvider = mock(NotificationProvider.class); // For EmailServiceImpl
    private final NotificationProvider chatNotificationProvider = mock(NotificationProvider.class); // For ChatNotificationProvider
    private final NotificationProvider mobileAppNotificationProvider = mock(NotificationProvider.class); // For MobileAppNotificationProvider
    private final List<NotificationProvider> allProviders = list(mobileAppNotificationProvider, emailNotificationProvider, chatNotificationProvider);

    private final UserRoleService userRoleService = mock(UserRoleService.class);
    private final MobileConfiguration mobileConfiguration = mock(MobileConfiguration.class);
    private final SubscriptionConfiguration subscriptionConfiguration = mock(SubscriptionConfiguration.class);

    private NotificationProviderAdapter instance;

    @Before
    public void setUp() {
        when(emailNotificationProvider.getNotificationProviderName()).thenReturn(Email.NOTIFICATION_PROVIDER_EMAIL);
        when(chatNotificationProvider.getNotificationProviderName()).thenReturn(Email.NOTIFICATION_PROVIDER_CHAT);
        when(mobileAppNotificationProvider.getNotificationProviderName()).thenReturn(MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP);

        UserData userOne = buildUserData(A_GROUP_USER_NAME, A_GROUP_USER_ID);
        when(userRoleService.getUserDataByUsernameOrNull(A_GROUP_USER_NAME)).thenReturn(userOne);
        UserData userTwo = buildUserData(ANOTHER_GROUP_USER_NAME, ANOTHER_GROUP_USER_ID);
        when(userRoleService.getUserDataByUsernameOrNull(ANOTHER_GROUP_USER_NAME)).thenReturn(userTwo);
        when(userRoleService.getUsersWithRole(eq(A_GROUP_NAME))).thenReturn(list(userOne, userTwo));
        UserData user = buildUserData(A_USER, A_USER_ID);
        when(userRoleService.getUserDataByUsernameOrNull(A_USER)).thenReturn(user);

        when(subscriptionConfiguration.getSubscriptionCustomerCode()).thenReturn(CUSTOMER_CODE);

        instance = new NotificationProviderAdapter(allProviders, userRoleService);

        // To correctly use verifyNoMoreInteractions() in test assertions
        verify(emailNotificationProvider, times(1)).getNotificationProviderName();
        verify(chatNotificationProvider, times(1)).getNotificationProviderName();
        verify(mobileAppNotificationProvider, times(1)).getNotificationProviderName();
    }

    /**
     * Test of sendNotification method, single target on email, of class
     * NotificationProviderAdapter.
     */
    @Test
    public void testSendNotification_Email() {
        System.out.println("sendNotification_Email");

        // arrange:
        Email notificationToSend = buildToSendEmailNotification(A_USER);
        NotificationCommonData expNotificationToSend = notificationToSend;

        // act:
        instance.sendNotification(notificationToSend);

        // assert:
        verify(emailNotificationProvider, times(1)).sendNotification(matchNotificationData(expNotificationToSend));
        verifyNoMoreInteractions(emailNotificationProvider, chatNotificationProvider, mobileAppNotificationProvider);
    }

    /**
     * Test of sendNotification method, single target on chat, of class
     * NotificationProviderAdapter.
     */
    @Test
    public void testSendNotification_Chat_SingleTarget() {
        System.out.println("sendNotification_Chat_SingleTarget");

        // arrange:
        Email notificationToSend = buildToSendChatNotification(A_USER);
        NotificationCommonData expNotificationToSend = notificationToSend;

        // act:
        instance.sendNotification(notificationToSend);

        // assert:
        verify(chatNotificationProvider, times(1)).sendNotification(matchNotificationData(expNotificationToSend));
        verifyNoMoreInteractions(emailNotificationProvider, chatNotificationProvider, mobileAppNotificationProvider);
    }

    /**
     * Test of sendNotification method, group role on chat, of class
     * NotificationProviderAdapter.
     */
    @Test
    public void testSendNotification_Chat_GroupRole() {
        System.out.println("sendNotification_groupRole");

        // arrange:
        Email notificationToSend = buildToSendChatNotification(A_GROUP_ROLE);
        NotificationCommonData expNotificationToSend = buildExpChatNotification(A_GROUP_USER_NAME + "," + ANOTHER_GROUP_USER_NAME);

        // act:
        instance.sendNotification(notificationToSend);

        // assert:
        verify(chatNotificationProvider, times(1)).sendNotification(matchNotificationData(expNotificationToSend));
        verifyNoMoreInteractions(emailNotificationProvider, chatNotificationProvider, mobileAppNotificationProvider);
    }

    /**
     * Test of sendNotification method, no target on mobileApp, of class
     * NotificationProviderAdapter.
     */
    @Test
    public void testSendNotification_MobileApp_NoTarget() {
        System.out.println("sendNotification_MobileApp_NoTarget");

        // arrange:
        Email notificationToSend = buildToSendMobileAppNotification("");
        NotificationCommonData expNotificationToSend = buildExpMobileAppNotification("");
        when(mobileAppNotificationProvider.sendNotification(matchNotificationData(expNotificationToSend))).thenReturn(expNotificationToSend);

        // act:
        instance.sendNotification(notificationToSend);

        // assert:
        verify(mobileAppNotificationProvider, times(1)).sendNotification(matchNotificationData(expNotificationToSend));
        verifyNoMoreInteractions(emailNotificationProvider, chatNotificationProvider, mobileAppNotificationProvider);
    }

    /**
     * Test of sendNotification method, single target on mobileApp, of class
     * NotificationProviderAdapter.
     */
    @Test
    public void testSendNotification_MobileApp_SingleTarget() {
        System.out.println("sendNotification_MobileApp_SingleTarget");

        // arrange:
        Email notificationToSend = buildToSendMobileAppNotification(A_USER);
        NotificationCommonData expNotificationToSend = buildExpMobileAppNotification(A_USER);
        when(mobileAppNotificationProvider.sendNotification(matchNotificationData(expNotificationToSend))).thenReturn(expNotificationToSend);

        // act:
        instance.sendNotification(notificationToSend);

        // assert:
        verify(mobileAppNotificationProvider, times(1)).sendNotification(matchNotificationData(expNotificationToSend));
        verifyNoMoreInteractions(emailNotificationProvider, chatNotificationProvider, mobileAppNotificationProvider);
    }

    /**
     * Test of sendNotification method, group role on mobileApp, of class
     * NotificationProviderAdapter.
     */
    @Test
    public void testSendNotification_MobileApp_GroupRole() {
        System.out.println("sendNotification_MobileApp_GroupRole");

        // arrange:
        Email notificationToSend = buildToSendMobileAppNotification(A_GROUP_ROLE);
        NotificationCommonData expNotificationToSend = buildExpMobileAppNotification(A_GROUP_USER_NAME + "," + ANOTHER_GROUP_USER_NAME);
        when(mobileAppNotificationProvider.sendNotification(matchNotificationData(expNotificationToSend))).thenReturn(expNotificationToSend);

        // act:
        instance.sendNotification(notificationToSend);

        // assert:
        verify(mobileAppNotificationProvider, times(1)).sendNotification(matchNotificationData(expNotificationToSend));
        verifyNoMoreInteractions(emailNotificationProvider, chatNotificationProvider, mobileAppNotificationProvider);
    }

    /**
     * Test of sendNotification method, single target on chat+mobileApp, of
     * class NotificationProviderAdapter.
     */
    @Test
    public void testSendNotification_ChatPlusMobileApp_SingleTarget() {
        System.out.println("sendNotification_ChatPlusMobileApp_SingleTarget");

        // arrange:
        Email notificationToSend = buildToSendNotification(A_USER, fromListToString(list(
                NOTIFICATION_PROVIDER_EMAIL, // Intruso, da scartare
                NOTIFICATION_PROVIDER_MOBILE_APP,
                NOTIFICATION_PROVIDER_CHAT)));
        NotificationCommonData expChatNotificationToSend = buildToSendChatNotification(A_USER);
        NotificationCommonData expMobileAppNotificationToSend = buildExpMobileAppNotification(A_USER);
        when(mobileAppNotificationProvider.sendNotification(matchNotificationData(expMobileAppNotificationToSend))).thenReturn(expMobileAppNotificationToSend);
        NotificationDataMatcher expResultMatcher = new NotificationDataMatcher(expMobileAppNotificationToSend);

        // act:
        Email result = instance.sendNotification(notificationToSend);

        // assert:
        assertTrue(expResultMatcher.matches(result));
        verify(chatNotificationProvider, times(1)).sendNotification(matchNotificationData(expChatNotificationToSend));
        verify(mobileAppNotificationProvider, times(1)).sendNotification(matchNotificationData(expMobileAppNotificationToSend));
        verifyNoMoreInteractions(emailNotificationProvider, chatNotificationProvider, mobileAppNotificationProvider);
    }

    private static Email buildToSendEmailNotification(final String to) {
        return buildToSendNotification(to, NOTIFICATION_PROVIDER_EMAIL);
    }

    private static Email buildToSendChatNotification(final String to) {
        return buildToSendNotification(to, NOTIFICATION_PROVIDER_CHAT);
    }

    private static Email buildToSendMobileAppNotification(final String to) {
        return buildToSendNotification(to, NOTIFICATION_PROVIDER_MOBILE_APP);
    }

    private static Email buildToSendNotification(final String to, final String provider) {
        return EmailImpl.builder()
                .withNotificationProvider(provider)
                .withSubject(A_SUBJECT)
                .withContent(A_CONTENT)
                .withTo(to)
                .build();
    }

    private static Email buildExpEmailNotification(final String to) {
        return buildExpNotification(to, NOTIFICATION_PROVIDER_EMAIL);
    }

    private static NotificationCommonData buildExpChatNotification(final String to) {
        return buildExpNotification(to, NOTIFICATION_PROVIDER_CHAT);
    }

    private static MobileAppNotificationData buildExpMobileAppNotification(final String to) {
        return MobileAppNotificationDataImpl.builder()
                .withSubject(A_SUBJECT)
                .withContent(A_CONTENT)
                .withTopics(to)
                .build();
    }

    private static Email buildExpNotification(final String to, final String provider) {
        return EmailImpl.builder()
                .withNotificationProvider(provider)
                .withSubject(A_SUBJECT)
                .withContent(A_CONTENT)
                .withTo(to)
                .build();
    }

    private UserData buildUserData(String userName, Long userId) {
        UserData user = mock(UserData.class);
        when(user.getUsername()).thenReturn(userName);
        when(user.isActive()).thenReturn(true);
        when(user.isNotService()).thenReturn(true);
        when(user.getId()).thenReturn(userId);

        return user;
    }

    private static NotificationCommonData matchNotificationData(NotificationCommonData notificationData) {
        System.out.println("" + new NotificationDataMatcher(notificationData) + "\n" + notificationData);
        return argThat(new NotificationDataMatcher(notificationData));
    }

} // end NotificationProviderAdapterTest class

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

