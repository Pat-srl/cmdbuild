/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

/**
 * triggered when system shutdown has been initiated, but before any shutdown operation is actually applied; services may listen to this event to alert users or log shutdown event
 */
public enum SystemShutdownInitiatedEvent {
    INSTANCE

}
