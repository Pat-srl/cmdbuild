/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.networknt.schema.utils.StringUtils;
import java.util.List;
import java.util.Map;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.DaoQueryOptionsImplBuilder;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.utils.lang.CmExceptionUtils;
import org.springframework.stereotype.Component;

/**
 * <b>Note</b>: can't be all based on ids, because if diffing different systems
 * ids won't match. The <i>name</i> (that is, the <code>code</code> in CMDBuild
 * model) is used as <i>distinguishing name</i>. A temporarily generated one has
 * to used for newly inserted data.
 *
 *
 * @author afelice
 */
@Component
public class CardSyncImpl implements CardSync {

    private final UserCardService cardService;

    public CardSyncImpl(UserCardService cardService) {
        this.cardService = checkNotNull(cardService);
    }

    @Override
    public Card read(Classe classe, Long cardId) {
        return cardService.getUserCard(classe.getName(), cardId);
    }

    @Override
    public Card insert(Classe classe, Map<String, Object> values) {
        return cardService.createCard(classe.getName(), values);
    }

    @Override
    public Card update(Classe classe, Card card) {
        return cardService.updateCard(classe.getName(), card.getId(), card.getAllValuesAsMap());
    }

    @Override
    public void remove(Classe classe, Card card) {
        cardService.deleteCard(classe.getName(), card.getId());
    }

    @Override
    public Card read(Classe classe, Long cardId, String classeFilter, List<String> classeSelectedAttrs) {
        // Create filter for selection of the card with that id and selected attributes
        DaoQueryOptions curClasseQueryOptions = buildQueryOptions(cardId, classeFilter, classeSelectedAttrs,
                classe.getAliasToAttributeMap());

        PagedElements<Card> foundCardPaged = cardService.getUserCards(classe.getName(), curClasseQueryOptions);
        if (foundCardPaged.isEmpty()) {
            return null;
        }

        if (foundCardPaged.size() > 1) {
            throw CmExceptionUtils.runtime("loading classe =< %s > card =< %d > produced [%d] elements; expected only one", classe.getName(), cardId, foundCardPaged.size());
        }

        return foundCardPaged.elements().get(0);
    }

    public static final String FILTER_CARDS_BY_ID = "{\"attribute\":{\"simple\":{\"attribute\":\"_id\",\"operator\":\"equal\",\"value\":\"%d\"}}}";

    private DaoQueryOptions buildQueryOptions(long cardId, String cqlCardsFilter,
            List<String> curClassSelectedAttrs,
            Map<String, String> aliasToAttributeMap) {


        DaoQueryOptionsImplBuilder filterBuilder = DaoQueryOptionsImpl.builder()
                .withFilter(FILTER_CARDS_BY_ID.formatted(cardId)); // only Card with given cardId

        if (StringUtils.isNotBlank(cqlCardsFilter)) {
            CmdbFilter cardsFilter = CmdbFilterImpl.builder()
                    .withCqlFilter(cqlCardsFilter)
                    .build();

            filterBuilder.and(cardsFilter); // apply given CQL filter
        }

        return filterBuilder
                .withAttrs(curClassSelectedAttrs.stream().map(a -> aliasToAttributeMap.getOrDefault(a, a)).collect(toImmutableList())) // as in UserCardServiceImpl.getUserCards(String, UserCardQueryOptions, Pair)
                .build();
    }

}
