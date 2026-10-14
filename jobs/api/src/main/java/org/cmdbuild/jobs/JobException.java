/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static java.lang.String.format;

public class JobException extends RuntimeException {

	public JobException() {
	}

	public JobException(String message) {
		super(message);
	}

	public JobException(Throwable cause, String message, Object... args) {
		super(format(message, args), cause);
	}

	public JobException(Throwable cause) {
		super(cause);
	}

}
