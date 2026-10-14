/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import static com.google.common.base.Preconditions.checkNotNull;

public class RequestTrackingFilterException extends RuntimeException {

    public RequestTrackingFilterException(Throwable cause) {
        super(checkNotNull(cause));
    }

}
