/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl;

import org.cmdbuild.utils.lang.CmException;

public class EtlException extends CmException {

    public EtlException(Throwable nativeException) {
        super(nativeException);
    }

    public EtlException(String message) {
        super(message);
    }

    public EtlException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public EtlException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public EtlException(String format, Object... params) {
        super(format, params);
    }

}
