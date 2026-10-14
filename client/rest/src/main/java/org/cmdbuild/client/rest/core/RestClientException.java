/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.core;

import static java.lang.String.format;

public class RestClientException extends RuntimeException {

	public RestClientException() {
	}

	public RestClientException(String message) {
		super(message);
	}

	public RestClientException(String message, Throwable cause) {
		super(message, cause);
	}

	public RestClientException(Throwable cause) {
		super(cause);
	}

	public RestClientException(Throwable cause, String message, Object... args) {
		super(format(message, args), cause);
	}

}
