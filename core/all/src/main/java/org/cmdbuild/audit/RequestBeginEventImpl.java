/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class RequestBeginEventImpl implements RequestBeginEvent {

    private final String requestId;

    public RequestBeginEventImpl(String requestId) {
        this.requestId = checkNotBlank(requestId);
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

}
