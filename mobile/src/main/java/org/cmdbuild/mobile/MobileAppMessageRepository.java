/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.mobile;

import org.cmdbuild.notification.mobileapp.beans.MobileAppMessage;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;

public interface MobileAppMessageRepository {

    PagedElements<MobileAppMessage> getMessagesForUser(String username, DaoQueryOptions options);

    MobileAppMessage getMessageByRecordId(long id);

    MobileAppMessage getMessageByMessageId(String messageId);

    MobileAppMessage createMessage(MobileAppMessage message);

    MobileAppMessage updateMessage(MobileAppMessage message);

    void deleteMessage(MobileAppMessage message);

}
