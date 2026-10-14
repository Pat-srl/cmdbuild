/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.log.LogService;

public interface LogMessage {

    LogService.LogLevel getLevel();

    String getMessage();

    String getLine();

    ZonedDateTime getTimestamp();

    @Nullable
    String getStacktrace();

    default boolean hasStacktrace() {
        return StringUtils.isNotBlank(getStacktrace());
    }

}
