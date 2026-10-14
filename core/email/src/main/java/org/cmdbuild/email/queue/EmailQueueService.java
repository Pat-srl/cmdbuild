/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.queue;

public interface EmailQueueService {

    void triggerEmailQueue();

    void sendSingleEmail(long emailId);

}
