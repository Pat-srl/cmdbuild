/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.audit;

public interface RequestTrackingService {

    void requestBegin(RequestData data);

    void requestComplete(RequestData data);

    void dropAllData();

}
