/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.helpers;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;

/**
 *
 * @author afelice
 */
public class CardLoader {
    private final UserCardService cardService;
    
    public CardLoader(UserCardService cardService) {
        this.cardService = checkNotNull(cardService);
    }
    
    public Card load(String classId, long cardId) {
        return cardService.getUserCard(classId, cardId);
    }
    
    public Card loadWithInfo(String classId, long cardId) {
        return cardService.getUserCardInfo(classId, cardId);
    }    
}
