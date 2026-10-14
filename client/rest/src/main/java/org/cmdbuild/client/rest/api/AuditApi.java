/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.util.List;
import org.cmdbuild.audit.RequestData;
import org.cmdbuild.audit.RequestInfo;
import org.cmdbuild.client.rest.core.RestServiceClient;

public interface AuditApi extends RestServiceClient {

    String mark();

    List<RequestInfo> getRequestsSince(String mark);

    RequestData getRequestData(String requestId);

    List<RequestInfo> getLastRequests(int limit);

    List<RequestInfo> getLastErrors(int limit);

    default List<RequestInfo> getLastRequests() {
        return AuditApi.this.getLastRequests(10);
    }

}
