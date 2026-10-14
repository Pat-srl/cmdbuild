package org.cmdbuild.auth.grant;

import static java.lang.String.format;

public class AuthorizationException extends RuntimeException {

	public AuthorizationException(String message, Object... args) {
		super(format(message, args));
	}

	public AuthorizationException(Throwable cause, String message, Object... args) {
		super(format(message, args), cause);
	}

}
