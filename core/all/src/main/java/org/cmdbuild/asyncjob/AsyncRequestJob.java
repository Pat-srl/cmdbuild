/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.asyncjob;

import java.util.Map;

public interface AsyncRequestJob {

    long getId();

    byte[] getResponseContent();

    Map<String, String> getResponseHeaders();

    int getStatusCode();

    boolean isCompleted();

}
