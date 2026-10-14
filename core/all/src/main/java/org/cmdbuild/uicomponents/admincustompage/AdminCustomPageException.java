/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents.admincustompage;

import static java.lang.String.format;

public class AdminCustomPageException extends RuntimeException {

    public AdminCustomPageException(Throwable cause, String message, Object... args) {
        super(format(message, args), cause);
    }

}
