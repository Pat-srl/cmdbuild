/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import static java.lang.String.format;

public class TranslationException extends RuntimeException {

	public TranslationException() {
	}

	public TranslationException(String message) {
		super(message);
	}

	public TranslationException(String message, Throwable cause) {
		super(message, cause);
	}

	public TranslationException(Throwable cause, String message, Object... params) {
		super(format(message, params), cause);
	}

}
