/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents.custompage;

import static java.lang.String.format;

public class CustomPageException extends RuntimeException {

	public CustomPageException(Throwable cause, String message, Object... args) {
		super(format(message, args), cause);
	}

}
