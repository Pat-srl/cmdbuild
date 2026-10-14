/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis.geoserver;

import org.cmdbuild.utils.lang.CmException;

public class GeoserverException extends CmException {

    public GeoserverException(Throwable nativeException) {
        super(nativeException);
    }

    public GeoserverException(String message) {
        super(message);
    }

    public GeoserverException(Throwable nativeException, String message) {
        super(nativeException, message);
    }

    public GeoserverException(Throwable nativeException, String format, Object... params) {
        super(nativeException, format, params);
    }

    public GeoserverException(String format, Object... params) {
        super(format, params);
    }

}
