/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import java.util.Optional;

public interface AttributeInfo {

    String getName();

//  org.cmdbuild.api.fluent.ws.WsFluentApiExecutor;
    Object getWsType();

    Optional<String> getTargetClassName();

}
