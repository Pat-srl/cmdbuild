/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.log;

import com.google.common.eventbus.EventBus;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;

public interface LogService {

	/**
	 * register to this eventbus to receive {@link LogEvent} events
	 */
	EventBus getEventBus();

	interface LogEvent {

		LogLevel getLevel();

		String getMessage();

		String getLine();

		ZonedDateTime getTimestamp();

		@Nullable
		Throwable getException();

		default boolean hasException() {
			return getException() != null;
		}
	}

	enum LogLevel {
		LL_ERROR, LL_WARN, LL_INFO, LL_DEBUG, LL_TRACE
	}

}
