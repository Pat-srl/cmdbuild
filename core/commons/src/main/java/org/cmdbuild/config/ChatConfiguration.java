/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import java.time.Duration;
import jakarta.annotation.Nullable;

public interface ChatConfiguration {

    @Nullable
    Duration getArchivedMessageTimeToLive();

    @Nullable
    Duration getUnreadMessageTimeToLive();

}
