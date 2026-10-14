/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.notification;

import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.email.Email;
import org.cmdbuild.email.template.EmailTemplate;

public interface NotificationService {

    @Nullable
    Email sendNotification(Email notificationData);

    @Nullable
    Email sendNotificationFromTemplate(EmailTemplate template, Map<String, Object> data);

}
