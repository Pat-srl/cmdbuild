/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.userrole.UserRoleService;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.EmailStatus;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.template.ExpressionInputData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationServiceImpl implements NotificationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EmailTemplateProcessorService templateProcessorService;

    private final NotificationProviderAdapter notificationProviderAdapter;

    public NotificationServiceImpl(EmailTemplateProcessorService templateProcessorService, List<NotificationProvider> providers, UserRoleService userRoleService) {
        this.templateProcessorService = checkNotNull(templateProcessorService);
        this.notificationProviderAdapter = new NotificationProviderAdapter(providers, userRoleService); // handles all providers
        logger.info("notification service ready, loaded providers = {}", notificationProviderAdapter.getHandledProviderNames());
    }

    @Override
    @Nullable
    public Email sendNotification(Email notificationData) {
        logger.debug("send notification from email = {}", notificationData);
        return notificationProviderAdapter.sendNotification(notificationData);
    }

    @Override
    @Nullable
    public Email sendNotificationFromTemplate(EmailTemplate template, Map<String, Object> data) {
        logger.debug("send notification from template = {}", template);
        return sendNotification(
                templateProcessorService.processEmail(
                        EmailImpl.builder().withStatus(EmailStatus.ES_OUTGOING).build(),
                        ExpressionInputData
                                .builder()
                                .withTemplate(template)
                                .withOtherData(data)
                                .build()
                ));
    }

}
