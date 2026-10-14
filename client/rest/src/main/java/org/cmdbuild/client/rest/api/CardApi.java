/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import org.cmdbuild.client.rest.model.Card;
import org.cmdbuild.client.rest.model.RestApiCardImpl;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface CardApi {

    Card createCard(String classId, Card card);

    Card updateCard(Card card);

    CardApi deleteCard(String classId, Object cardId);

    Card getCard(String classId, Object cardId);

    List<Card> getCards(String classeId, String queryParams);

    List<Card> getCards(String classId);

    CardQuery queryCards();

    default Card createCard(String classId, Map<String, Object> cardAttributes) {
        return createCard(classId, new RestApiCardImpl(cardAttributes));
    }

    default Card createCard(String classId, Object... cardAttributes) {
        return createCard(classId, map(cardAttributes));
    }

    default Card updateCard(String classId, Object cardId, Map<String, Object> cardAttributes) {
        return updateCard(new RestApiCardImpl(classId, toLong(cardId), cardAttributes));
    }

    default Card updateCard(String classId, Object cardId, Object... cardAttributes) {
        return updateCard(classId, cardId, map(cardAttributes));
    }

    interface CardQuery {

        CardQuery limit(@Nullable Integer limit);

        CardQuery offset(@Nullable Integer start);

        CardQuery filter(@Nullable CmdbFilter filter);

        default CardQuery filter(@Nullable String filter) {
            return filter(CmFilterUtils.parseFilter(filter));
        }

        CardQuery sort(@Nullable CmdbSorter sort);

        default CardQuery sort(@Nullable String sort) {
            return sort(CmSorterUtils.parseSorter(sort));
        }

        List<Card> getCards();

    }

}
