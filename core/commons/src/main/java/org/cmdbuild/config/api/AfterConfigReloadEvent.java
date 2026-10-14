/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

/**
 * after config reload event; all classes should respond to this event by 
 * re-checking service status and stuff that may have changed after config reload
 * by services
 */
public interface AfterConfigReloadEvent extends ConfigEvent {

}
