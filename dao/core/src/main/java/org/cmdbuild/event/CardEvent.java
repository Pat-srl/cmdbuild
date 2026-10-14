/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.event;

import org.cmdbuild.dao.beans.Card;

public interface CardEvent {

    Card getCurrentCard();

    DaoEventType getType();
}
