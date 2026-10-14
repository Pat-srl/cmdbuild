/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.script.python;

import org.cmdbuild.utils.lang.CmException;

public class PythonScriptException extends CmException {

    public PythonScriptException(Throwable nativeException) {
        super(nativeException);
    }

    public PythonScriptException(String message) {
        super(message);
    }

    public PythonScriptException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public PythonScriptException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public PythonScriptException(String format, Object... params) {
        super(format, params);
    }

}
