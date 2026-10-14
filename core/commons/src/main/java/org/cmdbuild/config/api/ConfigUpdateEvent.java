/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

/**
 * inner config update event; config classes should respond to this event by dropping
 * any cached config value they may have
 *
 * @author davide
 */
public interface ConfigUpdateEvent extends ConfigEvent {

}
