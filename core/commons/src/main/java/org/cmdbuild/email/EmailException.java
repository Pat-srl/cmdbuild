/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import static java.lang.String.format;

public class EmailException extends RuntimeException {

	public EmailException() {
	}

	public EmailException(String message, Object... args) {
		super(format(message, args));
	}

	public EmailException(Throwable cause, String message, Object... args) {
		super(format(message, args), cause);
	}

	public EmailException(Throwable cause) {
		super(cause);
	}

}
