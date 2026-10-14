/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.mobile.notification.firebase;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.common.annotations.VisibleForTesting;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.config.MobileConfiguration;
import org.cmdbuild.config.SubscriptionConfiguration;
import org.cmdbuild.notification.NotificationStatus;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationDataImpl;
import org.cmdbuild.plugin.notification.mobileapp.MobileAppNotificationSender;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class FirebaseSender implements MobileAppNotificationSender {

    public static final String MOBILE_APP_NOTIFICATION_SENDER_FIREBASE = "Firebase";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRoleService userRoleService;
    private final MobileConfiguration mobileConfiguration;
    private final SubscriptionConfiguration subscriptionConfiguration;

    public FirebaseSender(
            UserRoleService userRoleService,
            MobileConfiguration mobileConfiguration,
            SubscriptionConfiguration subscriptionConfiguration
    ) {
        this.userRoleService = checkNotNull(userRoleService);
        this.mobileConfiguration = mobileConfiguration;
        this.subscriptionConfiguration = subscriptionConfiguration;
    }

    @Override
    public String getSenderName() {
        return MOBILE_APP_NOTIFICATION_SENDER_FIREBASE;
    }

    @Override
    public MobileAppNotificationData sendNotification(MobileAppNotificationData notificationData) {
        checkNotBlank(notificationData.getAuthInfo());
        checkNotBlank(notificationData.getSubject());
        checkNotBlank(notificationData.getContent());

        // Identify serviceAccount used
        String serviceAccountJson = notificationData.getAuthInfo();
        String serviceAccountId = identifyServiceAccount(serviceAccountJson);
        logger.debug("trying to send Firebase msg with serviceAccount =< {} >, topic =< {} >, mobile app notification =< {} >", serviceAccountId, notificationData.getTo(), notificationData);

        // Init Firebase Admin SDK for fgiven Google Firebase project
        FirebaseApp firebaseApp = initFirebaseApp(serviceAccountJson, serviceAccountId);

        // NS_SENT only if all notifications are sent correctly
        NotificationStatus globalStatus = buildAndSend(firebaseApp, notificationData).getStatus();

        return MobileAppNotificationDataImpl.copyOf(notificationData).withSentDate(now()).withStatus(globalStatus).build();
    }

    private MobileAppNotificationData buildAndSend(FirebaseApp firebaseApp, MobileAppNotificationData notificationData) {
        // Setting default sound for iOS notification
        ApnsConfig apnsConfig = ApnsConfig.builder().setAps(Aps.builder().setSound("default").build()).build();
        
        // Define Message payload
        // Html is stripped off only here, just before sending to Firebase, because
        // is not correctly interpreted in mobile phones and text is expected.
        // In repository instead is stored the html string, so in CMDBuild UI
        // is more user friendly when visualized.
        final Notification msgToSend = Notification.builder()
                .setTitle(htmlToText(notificationData.getSubject()))
                .setBody(htmlToText(notificationData.getContent()))
                .build();
        final String topic = fetchTopics(notificationData.getTo());        

        Message.Builder messageBuilder = Message.builder()
                .setTopic(topic)
                .setApnsConfig(apnsConfig)
                .setNotification(msgToSend);

        if (MapUtils.isNotEmpty(notificationData.getMeta())) {
            notificationData.getMeta().forEach((k, v) -> logger.debug("adding this meta to firebase data -> key: {} - value: {}", k, v));
            messageBuilder.putAllData(notificationData.getMeta());
        }

        // Send a message to the devices subscribed to the provided token/topic.
        return sendFirebaseNotification(messageBuilder, topic, notificationData, firebaseApp);
    }

    /**
     * (Eventually) Delete the initialized Firebase resources
     *
     * @param mobileAppNotificationData
     */
    @Override
    public boolean release(MobileAppNotificationData mobileAppNotificationData) {
        checkNotBlank(mobileAppNotificationData.getAuthInfo());

        String serviceAccountJson = mobileAppNotificationData.getAuthInfo();
        final String serviceAccountId = identifyServiceAccount(serviceAccountJson);

        final Optional<FirebaseApp> foundInstance = FirebaseApp.getApps().stream()
                .filter(a -> a.getName().equals(serviceAccountId))
                .findAny();

        foundInstance.ifPresent(firebaseApp -> release(firebaseApp));

        return foundInstance.isPresent();
    }

    /**
     * (Eventually) Delete all initialized Firebase resources
     */
    @Override
    public void releaseAll() {
        FirebaseApp.getApps().stream().forEach(firebaseApp -> release(firebaseApp));
    }

    private void release(FirebaseApp firebaseApp) {
        firebaseApp.delete();
        logger.info("released FirebaseApp for serviceAccount =< {} >", firebaseApp.getName());
    }

    private String fetchTopics(String notificationTo) {
        String customerCode = checkNotBlank(subscriptionConfiguration.getSubscriptionCustomerCode(), "no subscription customer code defined");
        return notificationTo != null ? prependCustomerCode(customerCode, toUserId(notificationTo)) : customerCode;
    }

    private String prependCustomerCode(String customerCode, String item) {
        return format("%s-%s", customerCode, item);
    }

    private String toUserId(String userName) {
        Long userId = checkNotNull(userRoleService.getUserDataByUsernameOrNull(userName), "user not found for userName =< %s > to send mobile app notification", userName).getId();
        return toStringNotBlank(userId);
    }

    private String identifyServiceAccount(String serviceAccountJson) {
        ServiceAccountClient client = CmJsonUtils.fromJson(serviceAccountJson, ServiceAccountClient.class);

        checkNotNull(client.getProjectId(), "Invalid serviceAccount json, project_id is missing");
        checkNotNull(client.getClientId(), "Invalid serviceAccount json, client_id is missing");

        return format("%s_%s", client.getProjectId(), client.getClientId());
    }

    /**
     * Send a message to the devices subscribed to the provided topic
     *
     * @param messageBuilder
     * @param notificationData
     */
    private MobileAppNotificationData sendFirebaseNotification(Message.Builder messageBuilder, String topic, MobileAppNotificationData notificationData, FirebaseApp firebaseApp) {

        String serviceAccountId = firebaseApp.getName();
        try {
            // Response is a message ID string.
            String sentMessageID = getFirebaseInstance(firebaseApp).send(messageBuilder.build());
            logger.debug("sent Firebase msg with serviceAccount =< {} >, topic =< {} >, mobile app notification =< {} >, generated messageID =< {} >", serviceAccountId, topic, notificationData, sentMessageID);

            return MobileAppNotificationDataImpl.builder()
                    .withSubject(notificationData.getSubject())
                    .withContent(notificationData.getContent())
                    .withStatus(NotificationStatus.NS_SENT)
                    .build();

        } catch (FirebaseMessagingException ex) {
            logger.error("error sending Firebase msg with serviceAccount =< {} >, topic =< {} >, mobile app notification =< {} >", serviceAccountId, topic, notificationData, ex);
        }

        return MobileAppNotificationDataImpl.builder()
                .withStatus(NotificationStatus.NS_ERROR)
                .build();
    }

    /**
     * Handle for testing: to mock Firebase Messaging instance and notification
     * sending.
     *
     * @param firebaseApp
     * @return
     */
    @VisibleForTesting
    protected FirebaseMessaging getFirebaseInstance(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    private FirebaseApp initFirebaseApp(String serviceAccountJson, final String serviceAccountId) {

        // Avoid error "FirebaseApp name [DEFAULT] already exists": build it only if not
        // already existent, see https://stackoverflow.com/a/44407983
        return FirebaseApp.getApps().stream()
                .filter(a -> a.getName().equals(serviceAccountId))
                .findAny()
                .orElseGet(() -> buildFirebaseApp(serviceAccountJson, serviceAccountId));
    }

    private FirebaseApp buildFirebaseApp(String serviceAccountJson, String serviceAccountId) {
        FirebaseApp firebaseApp = null;
        try (InputStream serviceAccountJsonStream = IOUtils.toInputStream(serviceAccountJson, StandardCharsets.UTF_8)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccountJsonStream)) // Raises IOException
                    .build();

            firebaseApp = FirebaseApp.initializeApp(options, serviceAccountId);
            logger.info("initialized FirebaseApp for serviceAccount =< {} >", serviceAccountId);
        } catch (IOException ex) {
            logger.error("error initializing FirebaseApp for serviceAccount =< {} >", serviceAccountId, ex);
        }

        return firebaseApp;
    }

    private String htmlToText(String htmlStr) {
        return CmStringUtils.htmlToString(htmlStr);        
    }

} // end FirebaseSender class

class ServiceAccountClient {

    private final String clientId;
    private final String projectId;

    public ServiceAccountClient(
            @JsonProperty(value = "client_id") String clientId,
            @JsonProperty(value = "project_id") String projectId
    ) {
        this.clientId = clientId;
        this.projectId = projectId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getProjectId() {
        return projectId;
    }

} // end ServiceAccountClient class
