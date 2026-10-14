/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler;

import static java.lang.String.format;

public class SchedulerException extends RuntimeException {

	public SchedulerException(Throwable cause) {
		super(cause);
	}

	public SchedulerException(String message, Object... params) {
		super(format(message, params));
	}

	public SchedulerException(Throwable cause, String message, Object... params) {
		super(format(message, params), cause);
	}

}
