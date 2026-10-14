/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.requestcontext;

import java.util.Set;

public interface RequestContextActiveService {

    boolean isRequestContextActive(String requestId);

    boolean isRequestContextActiveForSession(String sessionId);

    Set<String> getLocalActiveRequestContextIds();

}
