/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import org.cmdbuild.utils.lang.CmException;

public class CadException extends CmException {

    public CadException(Throwable nativeException) {
        super(nativeException);
    }

    public CadException(String message) {
        super(message);
    }

    public CadException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public CadException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public CadException(String format, Object... params) {
        super(format, params);
    }

}
