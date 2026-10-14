/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.event.DaoEventType.DE_CARD_UPDATE_BEFORE;

public interface BeforeCardUpdateEvent extends CardEvent {

    Card getNextCard();

    @Override
    default DaoEventType getType() {
        return DE_CARD_UPDATE_BEFORE;
    }
}
