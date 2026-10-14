/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc.utils;

import org.cmdbuild.utils.lang.CmException;

public class IfcException extends CmException {

    public IfcException(Throwable nativeException) {
        super(nativeException);
    }

    public IfcException(String message) {
        super(message);
    }

    public IfcException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public IfcException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public IfcException(String format, Object... params) {
        super(format, params);
    }

}
