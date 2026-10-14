/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

/**
 * config reload event; all classes should respond to this event by 
 * reloading their configuration from config services/classes
 */
public interface ConfigReloadEvent extends ConfigEvent {

}
