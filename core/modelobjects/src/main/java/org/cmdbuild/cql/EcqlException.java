/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cql;

import static java.lang.String.format;

public class EcqlException extends RuntimeException {

	public EcqlException(String message, Object... params) {
		super(format(message, params));
	}

	public EcqlException(Throwable cause, String message, Object... params) {
		super(format(message, params), cause);
	}

}
