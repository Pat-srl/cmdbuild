/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import static java.lang.String.format;

public class GisException extends RuntimeException {

	public GisException(Throwable cause) {
		super(cause);
	}

	public GisException(String message) {
		super(message);
	}

	public GisException(Throwable cause, String message) {
		super(message, cause);
	}

	public GisException(Throwable cause, String format, Object... args) {
		super(format(format, args), cause);
	}

}
