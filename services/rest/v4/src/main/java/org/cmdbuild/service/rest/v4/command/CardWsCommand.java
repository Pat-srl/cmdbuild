/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsCardData;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class CardWsCommand {

    private final UserCardService userCardService;

    public CardWsCommand(UserCardService userCardService) {
        this.userCardService = checkNotNull(userCardService);
    }

    public Card doReadOne(String classId, Long cardId, Boolean infoOnly) {
        if (Boolean.TRUE.equals(infoOnly)) {
            return userCardService.getUserCardInfo(classId, cardId);
        } else {
            return userCardService.getUserCard(classId, cardId);
        }
    }

    public Card doCreate(String classId, WsCardData data) {
        return userCardService.createCard(classId, data.getValues());
    }

    public Card doUpdate(String classId, Long cardId, WsCardData data) {
        return userCardService.updateCard(classId, cardId, data.getValues());
    }

    public void doDelete(String classId, Long cardId) {
        userCardService.deleteCard(classId, cardId);
    }

    public void doUpdateMany(String classId, WsCardData data, WsQueryOptions wsQueryOptions) {
        userCardService.updateCards(classId, wsQueryOptions.getQuery().getFilter(), data.getValues());
    }
}
