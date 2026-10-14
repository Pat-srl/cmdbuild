/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.xpdl;

import static java.lang.String.format;

/**
 *
 * @author davide
 */
public class XpdlParserException extends RuntimeException {

    public XpdlParserException() {
    }

    public XpdlParserException(String message) {
        super(message);
    }

    public XpdlParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public XpdlParserException(Throwable cause) {
        super(cause);
    }

    public XpdlParserException(Throwable cause, String message, Object... args) {
        super(format(message, args), cause);
    }

}
