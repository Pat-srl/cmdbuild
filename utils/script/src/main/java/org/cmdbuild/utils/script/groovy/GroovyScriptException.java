/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.groovy;

import org.cmdbuild.utils.lang.CmException;

public class GroovyScriptException extends CmException {

    public GroovyScriptException(Throwable nativeException) {
        super(nativeException);
    }

    public GroovyScriptException(String message) {
        super(message);
    }

    public GroovyScriptException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public GroovyScriptException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public GroovyScriptException(String format, Object... params) {
        super(format, params);
    }

}
