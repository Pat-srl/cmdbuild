/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report;

import static java.lang.String.format;

public class ReportException extends RuntimeException {

	public ReportException(Throwable cause) {
		super(cause);
	}

	public ReportException(String message, Object... args) {
		super(format(message, args));
	}

	public ReportException(Throwable cause, String message, Object... args) {
		super(format(message, args), cause);
	}

}
