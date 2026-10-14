/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import java.time.Duration;
import jakarta.annotation.Nullable;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent("org.cmdbuild.chat")
public class ChatConfigurationImpl implements ChatConfiguration {

    @ConfigValue(key = "archivedMessageTimeToLive", description = "retention time for archived messages", defaultValue = "P30D")
    private Duration archivedMessageTimeToLive;

    @ConfigValue(key = "unreadMessageTimeToLive", description = "retention time for new (unread) messages", defaultValue = "")
    private Duration unreadMessageTimeToLive;

    @Override
    @Nullable
    public Duration getArchivedMessageTimeToLive() {
        return archivedMessageTimeToLive;
    }

    @Override
    @Nullable
    public Duration getUnreadMessageTimeToLive() {
        return unreadMessageTimeToLive;
    }
}
