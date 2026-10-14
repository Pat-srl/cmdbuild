/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.mta;

import java.util.function.Function;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.email.Email;

public interface EmailReceiveConfig {

    final static String INCOMING_FOLDER_DEFAULT = "INBOX";

    String getIncomingFolder();

    @Nullable
    String getReceivedFolder();

    @Nullable
    String getRejectedFolder();

    Function<Email, EmailProcessedAction> getCallback();

    @Nullable
    String getAccount();

    EmailReceivedAction getReceivedEmailAction();

    default boolean hasRejectedFolder() {
        return isNotBlank(getRejectedFolder());
    }

}
