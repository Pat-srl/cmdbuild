/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

import static org.cmdbuild.event.DaoEventType.DE_CARD_DELETE_BEFORE;

public interface BeforeCardDeleteEvent extends CardEvent {

    @Override
    default DaoEventType getType() {
        return DE_CARD_DELETE_BEFORE;
    }
}
