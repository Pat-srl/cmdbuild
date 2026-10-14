/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.api;

import org.cmdbuild.utils.ws3.inner.WsResourceBeanWithInterface;

public interface WsResourceRepository {

    Iterable<WsResourceBeanWithInterface> getResources();
}
