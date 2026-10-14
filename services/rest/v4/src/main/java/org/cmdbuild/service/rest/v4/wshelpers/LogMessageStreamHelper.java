/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.wshelpers;

public interface LogMessageStreamHelper {

    void startReceivingLogMessages();

    void stopReceivingLogMessages();
}
