package org.cmdbuild.exception;

import static java.lang.String.format;

public class DmsException extends RuntimeException {

	public DmsException(String message, Object... params) {
		super(format(message, params));
	}

	public DmsException(Throwable cause, String message, Object... params) {
		super(format(message, params), cause);
	}

	public DmsException(Throwable cause) {
		super(cause);
	}

}
