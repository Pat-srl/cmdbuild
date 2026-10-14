/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

public interface ConfigEvent {

    boolean impactNamespace(String namespace);

    boolean isGlobalConfigEvent();

}
