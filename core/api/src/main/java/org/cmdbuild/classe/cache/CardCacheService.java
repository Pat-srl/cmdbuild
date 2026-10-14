/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.cache;

import java.util.function.Supplier;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;

public interface CardCacheService {

    Card getCard(long id, Supplier<Card> loader);

    PagedElements<Card> getCards(Classe classe, DaoQueryOptions queryOptions, String userAccessPrivilegesHash, Supplier<PagedElements<Card>> loader);

    void createCard(Card card);

    void updateCard(Card card);

    void deleteCard(Card card);

}
