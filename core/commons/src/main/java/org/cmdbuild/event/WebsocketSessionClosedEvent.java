/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

public interface WebsocketSessionClosedEvent extends Event {

    String getSessionId();

    String getClientId();

}
