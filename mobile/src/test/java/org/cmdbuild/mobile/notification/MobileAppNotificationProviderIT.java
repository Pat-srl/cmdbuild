/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.mobile.notification;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.auth.user.LoginUser;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.auth.user.UserDataImpl;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.config.SubscriptionConfiguration;
import org.cmdbuild.mobile.MobileAppMessageImpl;
import org.cmdbuild.mobile.MobileAppMessageImpl.MobileAppMessageImplBuilder;
import org.cmdbuild.mobile.MobileAppMessageRepository;
import org.cmdbuild.mobile.MobileAppServiceImpl;
import static org.cmdbuild.mobile.notification.TokenEncrypter.clearJavaEncryptionKey;
import static org.cmdbuild.mobile.notification.TokenEncrypter.setJavaEncryptionKey;
import static org.cmdbuild.mobile.notification.TokenLoaderHelper.loadCredentialsStr;
import org.cmdbuild.mobile.notification.firebase.FirebaseSender;
import org.cmdbuild.notification.NotificationStatus;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageData.MOBILE_APP_MESSAGE_META_VOLATILE;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageDataImpl;
import org.cmdbuild.notification.mobileapp.beans.MobileAppMessageSourceType;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_NEW;
import static org.cmdbuild.notification.mobileapp.beans.MobileAppMessageStatus.MAMS_OUTGOING;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationDataImpl;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppNotificationSender;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class MobileAppNotificationProviderIT {

    private static final String TOPIC_CMDBUILD_TEST = "topic_cmdbuild_test_2"; // Change this if receiving "message": "Topic quota exceeded.", "status": "RESOURCE_EXHAUSTED"

    public static final String GOD_USER_SYSTEM = "system"; // or cmgod
    private static final String GOD_USER_SYSTEM_SOURCE_DESCRIPTION = "the system";

    private static final String A_TEST_USER = "aTestUser";
    private static final String A_TEST_USER_SOURCE_DESCRIPTION = format("the user for %s", TOPIC_CMDBUILD_TEST);

    static String GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS;
    private static final String GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH = "/org/cmdbuild/test/mobile/notification/service/test/tokens/cmdbuild-mobile-test-firebase-adminsdk-jsz6a-806345d57c.encr";

    static {
        setJavaEncryptionKey();
        GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS = loadCredentialsStr(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH, MobileAppNotificationProviderIT.class.getName());
    }

    private static final String MISSING_PROJECT_ID_CREDENTIALS = """
                                                                 {
                                                                   "type": "service_account",
                                                                   "client_id": "108038488858066818469"
                                                                 }
                                                                 """;

    private static final String MISSING_CLIENT_ID_CREDENTIALS = """
                                                                {
                                                                  "type": "service_account",
                                                                  "project_id": "tecn77"
                                                                }
                                                                """;

    MobileConfiguration mobileConfiguration = mock(MobileConfiguration.class);
    SubscriptionConfiguration subscriptionConfiguration = mock(SubscriptionConfiguration.class);
    MobileAppMessageRepository mobileAppMessageRepository = mock(MobileAppMessageRepository.class);
    OperationUserSupplier operationUser = mock(OperationUserSupplier.class);
    UserRoleService userRoleService = mock(UserRoleService.class);

    MobileAppService mobileAppService;
    MobileAppNotificationProvider instance;
    List<MobileAppNotificationSender> firebaseSender = list(new FirebaseSender(userRoleService, mobileConfiguration, subscriptionConfiguration));

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @BeforeClass
    public static void setUpClass() {
        try (InputStream serviceAccount = IOUtils.toInputStream(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS, StandardCharsets.UTF_8)) {
            // Nothing to do
        } catch (IOException ex) {
            System.err.println(format("error in test %s - error opening stream from file =< %s >", MobileAppNotificationProviderIT.class.getName(), GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS_FILE_PATH));
        }
    }

    @AfterClass
    public static void tearDown() {
        clearJavaEncryptionKey();
    }

    /**
     * Test of sendNotification method, a system message, of class
     * MobileAppNotificationProvider.
     */
    @Ignore("Tofix with changes")
    @Test
    public void testSendNotification_SystemMessage() {
        System.out.println("sendNotification_SystemMessage");

        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(A_TEST_USER)
                .build();
        UserDataImpl userData = UserDataImpl.builder()
                .withId(1234L)
                .withUsername(A_TEST_USER).build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS);
        when(subscriptionConfiguration.getSubscriptionCustomerCode()).thenReturn(TOPIC_CMDBUILD_TEST);
        when(userRoleService.getUserDataByUsernameOrNull(any())).thenReturn(userData);
        mockOperationUser(GOD_USER_SYSTEM, GOD_USER_SYSTEM_SOURCE_DESCRIPTION); // god user
        MobileAppMessage createdMessage = MobileAppMessageImpl.builder()
                .withSubject(notificationData.getSubject())
                .withContent(notificationData.getContent())
                .withTarget(notificationData.getTo())
                .withSourceType(MobileAppMessageSourceType.MAMST_SYSTEM)
                .withSourceName(GOD_USER_SYSTEM)
                .withSourceDescription(GOD_USER_SYSTEM_SOURCE_DESCRIPTION)
                .withStatus(MAMS_OUTGOING)
                .build();
        MobileAppMessage sentMessage = MobileAppMessageImpl
                .copyOf(createdMessage)
                .withStatus(MAMS_NEW).
                build();
        mobileAppService = new MobileAppServiceImpl(firebaseSender, mobileConfiguration, mobileAppMessageRepository, operationUser);
        when(mobileAppMessageRepository.createMessage(any())).thenReturn(createdMessage);
        when(mobileAppMessageRepository.updateMessage(any())).thenReturn(sentMessage);
        instance = new MobileAppNotificationProvider(mobileAppService);

        // act:
        MobileAppNotificationData result = instance.sendNotification(notificationData);

        // assert:
        assertEquals(NotificationStatus.NS_SENT, result.getStatus());
        verify(mobileAppMessageRepository, times(1)).createMessage(any());
        verify(mobileAppMessageRepository, times(1)).updateMessage(any());
        checkEquals(sentMessage, result);
    }

    /**
     * Test of sendNotification method, a user message, stored, of class
     * MobileAppNotificationProvider.
     */
    @Test
    public void testSendNotification_UserMessage() {
        System.out.println("sendNotification_UserMessage");

        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(A_TEST_USER)
                .build();
        UserDataImpl userData = UserDataImpl.builder()
                .withId(1234L)
                .withUsername(A_TEST_USER).build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS);
        when(subscriptionConfiguration.getSubscriptionCustomerCode()).thenReturn(TOPIC_CMDBUILD_TEST);
        when(userRoleService.getUserDataByUsernameOrNull(any())).thenReturn(userData);
        mockOperationUser(A_TEST_USER, A_TEST_USER_SOURCE_DESCRIPTION); // not a god user
        MobileAppMessage createdMessage = new MobileAppMessageImplBuilder()
                .withSubject(notificationData.getSubject())
                .withContent(notificationData.getContent())
                .withTarget(notificationData.getTo())
                .withSourceType(MobileAppMessageSourceType.MAMST_USER)
                .withSourceName(A_TEST_USER)
                .withSourceDescription(A_TEST_USER_SOURCE_DESCRIPTION)
                .withStatus(MAMS_OUTGOING)
                .build();
        MobileAppMessage sentMessage = MobileAppMessageImpl
                .copyOf(createdMessage)
                .withStatus(MAMS_NEW).
                build();
        mobileAppService = new MobileAppServiceImpl(firebaseSender, mobileConfiguration, mobileAppMessageRepository, operationUser);
        when(mobileAppMessageRepository.createMessage(any())).thenReturn(createdMessage);
        when(mobileAppMessageRepository.updateMessage(any())).thenReturn(sentMessage);
        instance = new MobileAppNotificationProvider(mobileAppService);

        // act:
        MobileAppNotificationData result = instance.sendNotification(notificationData);

        // assert:
        assertEquals(NotificationStatus.NS_SENT, result.getStatus());
        verify(mobileAppMessageRepository, times(1)).createMessage(matchMobileAppMessage(createdMessage));
        verify(mobileAppMessageRepository, times(1)).updateMessage(matchMobileAppMessage(sentMessage));
        checkEquals(sentMessage, result);
    }

    /**
     * Test of sendNotification method, a user volatile message, of class
     * MobileAppNotificationProvider.
     */
    @Ignore("Tofix with changes")
    @Test
    public void testSendNotification_VolatileMessage() {
        System.out.println("sendNotification_VolatileMessage");

        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(A_TEST_USER)
                .withMeta(map(MOBILE_APP_MESSAGE_META_VOLATILE, "true"))
                .build();
        UserDataImpl userData = UserDataImpl.builder()
                .withId(1234L)
                .withUsername(A_TEST_USER).build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS);
        when(subscriptionConfiguration.getSubscriptionCustomerCode()).thenReturn(TOPIC_CMDBUILD_TEST);
        when(userRoleService.getUserDataByUsernameOrNull(any())).thenReturn(userData);
        mockOperationUser(A_TEST_USER, A_TEST_USER_SOURCE_DESCRIPTION); // not a god user
        MobileAppMessage createdMessage = new MobileAppMessageImplBuilder()
                .withSubject(notificationData.getSubject())
                .withContent(notificationData.getContent())
                .withTarget(notificationData.getTo())
                .withSourceType(MobileAppMessageSourceType.MAMST_USER)
                .withSourceName(A_TEST_USER)
                .withSourceDescription(A_TEST_USER_SOURCE_DESCRIPTION)
                .withStatus(MAMS_OUTGOING)
                .withMeta(notificationData.getMeta())
                .build();
        MobileAppMessage sentMessage = MobileAppMessageImpl
                .copyOf(createdMessage)
                .withStatus(MAMS_NEW).
                build();
        mobileAppService = new MobileAppServiceImpl(firebaseSender, mobileConfiguration, mobileAppMessageRepository, operationUser);
        instance = new MobileAppNotificationProvider(mobileAppService);

        // act:
        MobileAppNotificationData result = instance.sendNotification(notificationData);

        // assert:
        assertEquals(NotificationStatus.NS_SENT, result.getStatus());
        checkEquals(sentMessage, result);
    }

    /**
     * Test of sendNotification method, a user message, html content is
     * transformed to text, stored, of class MobileAppNotificationProvider.
     * @throws com.google.firebase.messaging.FirebaseMessagingException
     */
    @Test
    public void testSendNotification_UserMessage_HtmlContent() throws FirebaseMessagingException {
        System.out.println("sendNotification_UserMessage_HtmlContent");

        // arrange:
        final String aHtmlTitle = "Subject_test&nbsp;<b>PreventiveMaint</b>";
        final String expTitle = "Subject_test PreventiveMaint";
        final String aHtmlBody = "Content_test <b>PM.2024.0377&nbsp;<b>è stata avviata ed assegnata a &nbsp;<b>Squadra Au-Ag</b>";       
        final String expBody = "Content_test PM.2024.0377 è stata avviata ed assegnata a  Squadra Au-Ag";       
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject(aHtmlTitle)
                .withContent(aHtmlBody)
                .withTopics(A_TEST_USER)
                .build();
        FirebaseMessaging mockFirebaseMessaging = mock(FirebaseMessaging.class);
        when(mockFirebaseMessaging.send(any())).thenReturn("id2");
        FirebaseSender mockFirebaseSender = new FirebaseSender(userRoleService, mobileConfiguration, subscriptionConfiguration) {
            @Override
            protected FirebaseMessaging getFirebaseInstance(FirebaseApp firebaseApp) {
                return mockFirebaseMessaging;
            }            
        };
        UserDataImpl userData = UserDataImpl.builder()
                .withId(1234L)
                .withUsername(A_TEST_USER).build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS);
        when(subscriptionConfiguration.getSubscriptionCustomerCode()).thenReturn(TOPIC_CMDBUILD_TEST);
        when(userRoleService.getUserDataByUsernameOrNull(any())).thenReturn(userData);
        mockOperationUser(A_TEST_USER, A_TEST_USER_SOURCE_DESCRIPTION); // not a god user
        MobileAppMessage createdMessage = new MobileAppMessageImplBuilder()
                .withSubject(notificationData.getSubject())
                .withContent(notificationData.getContent())
                .withTarget(notificationData.getTo())
                .withSourceType(MobileAppMessageSourceType.MAMST_USER)
                .withSourceName(A_TEST_USER)
                .withSourceDescription(A_TEST_USER_SOURCE_DESCRIPTION)
                .withStatus(MAMS_OUTGOING)
                .build();
        MobileAppMessage sentMessage = MobileAppMessageImpl
                .copyOf(createdMessage)
                .withStatus(MAMS_NEW).
                build();
        mobileAppService = new MobileAppServiceImpl(asList(mockFirebaseSender), mobileConfiguration, mobileAppMessageRepository, operationUser);
        when(mobileAppMessageRepository.createMessage(any())).thenReturn(createdMessage);
        when(mobileAppMessageRepository.updateMessage(any())).thenReturn(sentMessage);
        instance = new MobileAppNotificationProvider(mobileAppService);

        // act:
        MobileAppNotificationData result = instance.sendNotification(notificationData);

        // assert:
        // Throws FirebaseMessagingException
        verify(mockFirebaseMessaging, times(1)).send(matchNotification(expTitle, expBody));
        assertEquals(NotificationStatus.NS_SENT, result.getStatus());
        verify(mobileAppMessageRepository, times(1)).createMessage(matchMobileAppMessage(createdMessage));
        verify(mobileAppMessageRepository, times(1)).updateMessage(matchMobileAppMessage(sentMessage));
        checkEquals(sentMessage, result);
    }

    /**
     * Test of sendNotification method, of class MobileAppNotificationProvider.
     * mobile disabled.
     */
    @Test
    public void testSendNotification_MobileDisabled() {
        System.out.println("sendNotification_MobileDisabled");

        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(A_TEST_USER)
                .build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(false);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS);
        mockOperationUser(A_TEST_USER, A_TEST_USER_SOURCE_DESCRIPTION); // not a god user
        MobileAppNotificationSender sender = mock(MobileAppNotificationSender.class);
        when(sender.getSenderName()).thenReturn(FirebaseSender.MOBILE_APP_NOTIFICATION_SENDER_FIREBASE);
        mobileAppService = new MobileAppServiceImpl(asList(sender), mobileConfiguration, mobileAppMessageRepository, operationUser);
        instance = new MobileAppNotificationProvider(mobileAppService);

        // act:
        MobileAppNotificationData result = instance.sendNotification(notificationData);

        // assert:
        verify(sender, times(1)).getSenderName();
        verifyNoMoreInteractions(sender);
        assertEquals(NotificationStatus.NS_ERROR, result.getStatus());
    }

    /**
     * Test of sendNotification method, missing project_id in auth json, of
     * class MobileAppNotificationProvider.
     */
    @Test
    public void testSendNotification_MissingProjectId() {
        System.out.println("sendNotification_MissingProjectId");

        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(A_TEST_USER)
                .withMeta(map(MOBILE_APP_MESSAGE_META_VOLATILE, "true")) // skip repository steps, no need to mock them
                .build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(MISSING_PROJECT_ID_CREDENTIALS);
        mockOperationUser(A_TEST_USER, A_TEST_USER_SOURCE_DESCRIPTION); // not a god user
        mobileAppService = new MobileAppServiceImpl(firebaseSender, mobileConfiguration, mobileAppMessageRepository, operationUser);
        instance = new MobileAppNotificationProvider(mobileAppService);

        // act:
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("Invalid serviceAccount json, project_id is missing");
        MobileAppNotificationData result = instance.sendNotification(notificationData);

        // assert:
        assertEquals(NotificationStatus.NS_ARCHIVED, result.getStatus());
    }

    /**
     * Test of sendNotification method, missing project_id in auth json, of
     * class MobileAppNotificationProvider.
     */
    @Test
    public void testSendNotification_MissingClientId() {
        System.out.println("sendNotification_MissingClientId");

        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(A_TEST_USER)
                .withMeta(map(MOBILE_APP_MESSAGE_META_VOLATILE, "true")) // skip repository steps, no need to mock them
                .build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(MISSING_CLIENT_ID_CREDENTIALS);
        mockOperationUser(A_TEST_USER, A_TEST_USER_SOURCE_DESCRIPTION); // not a god user
        mobileAppService = new MobileAppServiceImpl(firebaseSender, mobileConfiguration, mobileAppMessageRepository, operationUser);
        instance = new MobileAppNotificationProvider(mobileAppService);
        exceptionRule.expect(NullPointerException.class);
        exceptionRule.expectMessage("Invalid serviceAccount json, client_id is missing");
        MobileAppNotificationData result = instance.sendNotification(notificationData);

        // assert:
        assertEquals(NotificationStatus.NS_ARCHIVED, result.getStatus());
    }

    /**
     * Test of release method, of class MobileAppNotificationProvider.
     */
    @Test
    public void testRelease() {
        System.out.println("release");

        // arrange:
        MobileAppNotificationData notificationData = MobileAppNotificationDataImpl.builder()
                .withAuthInfo(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS)
                .withSubject("Subject_test")
                .withContent("Content_test")
                .withTopics(A_TEST_USER)
                .withMeta(map(MOBILE_APP_MESSAGE_META_VOLATILE, "true")) // skip repository steps, no need to mock them
                .build();
        UserDataImpl userData = UserDataImpl.builder()
                .withId(1234L)
                .withUsername(A_TEST_USER).build();
        when(mobileConfiguration.isMobileEnabled()).thenReturn(true);
        when(mobileConfiguration.getMobileNotificationAuthInfo()).thenReturn(GOOGLE_FIREBASE_SERVICE_ACCOUNT_CREDENTIALS);
        when(subscriptionConfiguration.getSubscriptionCustomerCode()).thenReturn(TOPIC_CMDBUILD_TEST);
        when(userRoleService.getUserDataByUsernameOrNull(any())).thenReturn(userData);
        mockOperationUser(GOD_USER_SYSTEM, GOD_USER_SYSTEM_SOURCE_DESCRIPTION); // god user
        mobileAppService = new MobileAppServiceImpl(firebaseSender, mobileConfiguration, mobileAppMessageRepository, operationUser);
        instance = new MobileAppNotificationProvider(mobileAppService);
        instance.sendNotification(notificationData);

        // act:
        boolean result = instance.releaseSender(notificationData);

        // assert:
        assertTrue(result);

    }

    private static MobileAppMessage matchMobileAppMessage(MobileAppMessage message) {
        return argThat(new MobileAppMessageMatcher(message));
    }

    private void mockOperationUser(String userName, String userDescription) {
        OperationUser user = mock(OperationUser.class);
        LoginUser loginUser = mock(LoginUser.class);
        when(loginUser.getDescription()).thenReturn(userDescription);
        when(user.getLoginUser()).thenReturn(loginUser);
        when(operationUser.getUsername()).thenReturn(userName);
        when(operationUser.getUser()).thenReturn(user);
    }

    private void checkEquals(MobileAppMessage expStoredMessage, MobileAppNotificationData actual) {
        MobileAppNotificationData exp = MobileAppMessageDataImpl.copyNotificationDataOf(expStoredMessage).build();
        checkEquals(exp, actual);
    }

    private void checkEquals(MobileAppNotificationData exp, MobileAppNotificationData actual) {
        assertEquals(exp.getTo(), actual.getTo());
        assertEquals(exp.getSubject(), actual.getSubject());
        assertEquals(exp.getContent(), actual.getContent());
        assertEquals(exp.getContentType(), actual.getContentType());
        assertEquals(exp.getStatus(), actual.getStatus());
        assertEquals(exp.getNotificationProvider(), actual.getNotificationProvider());
        assertEquals(exp.getMeta(), actual.getMeta());
        assertEquals(exp.getErrorCount(), actual.getErrorCount());
        assertEquals(exp.getAuthInfo(), actual.getAuthInfo());
    }

    private com.google.firebase.messaging.Message matchNotification(String expTitle, String expBody) {
        return argThat(new FirebaseMessageMatcher(expTitle, expBody));
    }

} // end MobileAppNotificationProviderIT class

class MobileAppMessageMatcher extends ArgumentMatcher<MobileAppMessage> {

    private final MobileAppMessage left;

    MobileAppMessageMatcher(MobileAppMessage left) {
        this.left = left;
    }

    @Override
    public boolean matches(Object obj) {
        MobileAppMessage right = (MobileAppMessage) obj;
        return Objects.equals(left.getSourceType(), right.getSourceType())
                && Objects.equals(left.getSourceName(), right.getSourceName())
                && Objects.equals(left.getSourceDescription(), right.getSourceDescription())
                && Objects.equals(left.getSubject(), right.getSubject())
                && Objects.equals(left.getContent(), right.getContent())
                && Objects.equals(left.getTarget(), right.getTarget())
                && Objects.equals(left.getStatus(), right.getStatus());
    }
} // end NotificationDataMatcher class

class FirebaseMessageMatcher extends ArgumentMatcher<com.google.firebase.messaging.Message> {
    
    final String expTitle;
    final String expBody;    
    
    FirebaseMessageMatcher(String expTitle, String expBody) {
        this.expTitle = expTitle;
        this.expBody = expBody;
    }

    @Override
    public boolean matches(Object obj) {
        System.out.println("test match with object %s".formatted(obj.getClass()));
        com.google.firebase.messaging.Message actualMessage = (com.google.firebase.messaging.Message)obj;        
        com.google.firebase.messaging.Notification actualNotification = getFieldValue(actualMessage, "notification");
        String actualTile = getFieldValue(actualNotification, "title");
        String actualBody = getFieldValue(actualNotification, "body");
        
        assertEquals(expTitle, actualTile);
        assertEquals(expBody, actualBody);
        return Objects.equals(expTitle, actualTile) &&
                Objects.equals(expBody, actualBody);
    }
        
    static private <T> T getFieldValue(Object obj, String fieldName) {

        // Ottenere la classe dell'istanza
        Class<?> theObjClass = obj.getClass();

        // Ottenere il valore del campo privato
        Object value = null;
        try {
            // Ottenere il campo privato per nome
            java.lang.reflect.Field privateField = theObjClass.getDeclaredField(fieldName);

            // Rendere accessibile il campo privato
            privateField.setAccessible(true);
        
            value = privateField.get(obj);
        } catch (IllegalAccessException | NoSuchFieldException exc) {
            fail("test - error accessing field =< %s.%s >, %s".formatted(theObjClass.getName(), fieldName, exc));
        }
        return (T) value;
    }

}