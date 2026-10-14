/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.notification;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailStatus;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.notification.NotificationCommonData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationData;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationDataImpl;
import org.cmdbuild.notification.mobileapp.beans.MobileAppNotificationHelper;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.fromListToString;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adapter tra {@link NotificationServiceImpl} e i client
 * {@link LocalMailApiImpl} e {@link CalendarProcessorServiceImpl}, che usano
 * come input e output {@link Email} per il send con i vari
 * {@link NotificationProvider}, che usano invece tipi più specifici derivati da
 * {@link NotificationCommondData}.
 *
 * @author afelice
 */
public class NotificationProviderAdapter {

    public static final String ROLE_GROUP_TARGET_PREFIX = "role:";
    private static final Pattern ROLE_GROUP_PATTERN = Pattern.compile(ROLE_GROUP_TARGET_PREFIX + "(.+)");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, NotificationProvider> providers;
    private final UserRoleService userRoleService;

    public NotificationProviderAdapter(List<NotificationProvider> providers, UserRoleService userRoleService) {
        this.userRoleService = checkNotNull(userRoleService);
        this.providers = uniqueIndex(providers, NotificationProvider::getNotificationProviderName);
    }

    /**
     * A sort of Pattern strategy.
     *
     * @param emailNotificationData
     * @return
     */
    public Email sendNotification(Email emailNotificationData) {
        String foundNotificationProviderName = emailNotificationData.getNotificationProvider();

        // (Eventually) handle "chat,mobileApp" providers
        List<String> notificationProviderNames = handleMultipleProviders(foundNotificationProviderName);
        List<Email> results = notificationProviderNames.stream().map(np -> innerSendNotification(EmailImpl.copyOf(emailNotificationData).withNotificationProvider(np).build())).collect(toList());
        return results.get(results.size() - 1); // (if multiple) return mobileApp result
    }

    public List<String> getHandledProviderNames() {
        return list(this.providers.keySet()).sorted();
    }

    /**
     * Crea una regola per l'espansione di un gruppo fornito
     *
     * @param group
     * @return
     */
    public static String buildGroupRoleTarget(String group) {
        return ROLE_GROUP_TARGET_PREFIX + "%s".formatted(checkNotBlank(group));
    }

    /**
     * Ogni utente del customer cui inviare una notifica mobileApp è
     * identificato da <code>&lt;customer.code&gt;-&lt;user_id&gt;</code>.
     * <br>(default)Se nessun utente specificato, invia al token del customer
     * (identificato da <code>customer.code</code>).
     *
     * @param emailNotificationData
     * @return
     */
    private MobileAppNotificationData toMobileAppNotificationData(Email emailNotificationData) {
        List<String> givenTargets = fetchNotificationTargets(emailNotificationData.getTo());

        return MobileAppNotificationDataImpl.builder()
                .withTopics(fromListToString(givenTargets))
                .withSubject(emailNotificationData.getSubject())
                .withContent(emailNotificationData.getContent())
                .withContentType(emailNotificationData.getContentType())
                .withNotificationProvider(emailNotificationData.getNotificationProvider())
                .withMeta(emailNotificationData.getMeta())
                .build();
    }

    private NotificationCommonData toChatNotificationData(Email emailNotificationData) {
        List<String> targets = fetchNotificationTargets(emailNotificationData.getTo());

        return EmailImpl.builder()
                .withTo(fromListToString(targets))
                .withSubject(emailNotificationData.getSubject())
                .withContent(emailNotificationData.getContent())
                .withContentType(emailNotificationData.getContentType())
                .withNotificationProvider(emailNotificationData.getNotificationProvider())
                .withMeta(emailNotificationData.getMeta())
                .build();
    }

    private Email toEmailData(MobileAppNotificationData mobileAppNotificationData) {
        return EmailImpl.builder()
                .withId(mobileAppNotificationData.getId())
                .withTo(mobileAppNotificationData.getTo())
                .withSubject(mobileAppNotificationData.getSubject())
                .withContent(mobileAppNotificationData.getContent())
                .withContentType(mobileAppNotificationData.getContentType())
                .withNotificationProvider(mobileAppNotificationData.getNotificationProvider())
                .withMeta(mobileAppNotificationData.getMeta())
                .build();
    }

    private Email innerSendNotification(Email emailNotificationData) {
        String notificationProviderName = emailNotificationData.getNotificationProvider();
        logger.debug("send notification = {} with provider =< {} >", emailNotificationData, notificationProviderName);
        return switch (notificationProviderName) {
            case Email.NOTIFICATION_PROVIDER_CHAT ->
                sendChatNotification(emailNotificationData);
            case MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP ->
                sendMobileAppNotification(emailNotificationData);
            default ->
                sendEmailNotification(emailNotificationData);
        };
    }

    private Email sendEmailNotification(Email emailNotificationData) {
        NotificationProvider emailNotificationProvider = getNotificationProvider(Email.NOTIFICATION_PROVIDER_EMAIL);

        return (Email) emailNotificationProvider.sendNotification(EmailImpl.copyOf(emailNotificationData).withStatus(EmailStatus.ES_OUTGOING).build());
    }

    private Email sendChatNotification(Email emailNotificationData) {
        NotificationProvider chatNotificationProvider = getNotificationProvider(Email.NOTIFICATION_PROVIDER_CHAT);

        // Returns null
        chatNotificationProvider.sendNotification(toChatNotificationData(emailNotificationData));
        return null;
    }

    private Email sendMobileAppNotification(Email emailNotificationData) {
        NotificationProvider mobileAppNotificationProvider = getNotificationProvider(MobileAppNotificationHelper.NOTIFICATION_PROVIDER_MOBILE_APP);

        MobileAppNotificationData result = (MobileAppNotificationData) mobileAppNotificationProvider.sendNotification(toMobileAppNotificationData(emailNotificationData));
        return toEmailData(result);
    }

    /**
     * (Eventualmente) Applica l'esplosione delle regole di gruppo negli utenti
     * appartenenti a tale/i gruppo/i.
     *
     * @param notificationTargetStr
     * @return
     */
    private List<String> fetchNotificationTargets(String notificationTargetStr) {
        return list(toListOfStrings(notificationTargetStr)).flatMap(target -> {
            Matcher matcher = ROLE_GROUP_PATTERN.matcher(target);
            if (matcher.matches()) {
                String group = matcher.group(1);
                return list(userRoleService.getUsersWithRole(group)).filter(u -> u.isActive() && u.isNotService()).map(UserData::getUsername);
            } else {
                return singletonList(target);
            }
        }).sorted().distinct();
    }

    private NotificationProvider getNotificationProvider(String providerName) {
        checkNotNull(providers.get(providerName), "notification provider not found for name =< %s >", providerName);

        return providers.get(providerName);
    }

    private List<String> handleMultipleProviders(String notificationProviderName) {
        FluentList<String> toSendAtProviders = list(toListOfStrings(notificationProviderName));
        if (toSendAtProviders.size() > 1) {
            toSendAtProviders.without(Email.NOTIFICATION_PROVIDER_EMAIL); // Email non compatibile gli altri tipi di notifiche
        }

        return list(toSendAtProviders).sorted().distinct();
    }

}
