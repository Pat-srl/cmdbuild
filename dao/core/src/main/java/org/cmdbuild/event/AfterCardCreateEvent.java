/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

import static org.cmdbuild.event.DaoEventType.DE_CARD_CREATE_AFTER;

public interface AfterCardCreateEvent extends AfterCardCreateUpdateEvent {

    @Override
    default DaoEventType getType() {
        return DE_CARD_CREATE_AFTER;
    }
}
