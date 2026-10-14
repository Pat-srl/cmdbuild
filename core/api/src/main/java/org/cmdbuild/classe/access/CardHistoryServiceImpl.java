/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import java.util.Collections;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.classe.access.CardHistoryService.HistoryElement.HE_CARDS;
import static org.cmdbuild.classe.access.CardHistoryService.HistoryElement.HE_REFERENCES;
import static org.cmdbuild.classe.access.CardHistoryService.HistoryElement.HE_RELATIONS;
import static org.cmdbuild.classe.access.CardHistoryService.HistoryElement.HE_SYSTEM;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CURRENTID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_U;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.DaoService.ATTRS_ALL;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.LT;
import static org.cmdbuild.dao.core.q3.WhereOperator.LTEQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.MATCHES_REGEXP;
import static org.cmdbuild.dao.core.q3.WhereOperator.NOT_MATCHES_REGEXP;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.primitiveEquals;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CardHistoryServiceImpl implements CardHistoryService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final UserDomainService domainService;

    public CardHistoryServiceImpl(DaoService dao, UserDomainService domainService) {
        this.dao = checkNotNull(dao);
        this.domainService = checkNotNull(domainService);
    }

    @Override
    public PagedElements<Card> getHistory(String classId, long cardId, DaoQueryOptions queryOptions) { //TODO apply user permission
        List<Card> cards = dao.select().from(classId).includeHistory()
                .where(ATTR_CURRENTID, EQ, cardId)
                .withOptions(queryOptions)
                .getCards();
        if (queryOptions.isPaged()) {
            long total = dao.selectCount().from(classId).includeHistory()
                    .where(ATTR_CURRENTID, EQ, cardId)
                    .where(queryOptions.getFilter())
                    .getCount();
            return paged(cards, total);
        } else {
            return paged(cards);
        }
    }

    @Override
    public PagedElements<DatabaseRecord> getHistoryElements(String classId, long cardId, DaoQueryOptions queryOptions, List<HistoryElement> types) {
        List<DatabaseRecord> historyElements = list();
        Set<String> referenceDomainNames = dao.getClasse(classId).getActiveServiceAttributes().stream().filter(a -> a.isOfType(REFERENCE)).map(a -> a.getMetadata().getDomain()).collect(toImmutableSet());
        types.forEach(t -> {
            switch (t) {
                case HE_CARDS ->
                    dao.select().from(classId).includeHistory()
                            .where(ATTR_CURRENTID, EQ, cardId)
                            .where(ATTR_USER, NOT_MATCHES_REGEXP, "^system")
                            .withOptions(queryOptions.withoutPaging())
                            .getCards().forEach(historyElements::add);
                case HE_SYSTEM ->
                    dao.select().from(classId).includeHistory()
                            .where(ATTR_CURRENTID, EQ, cardId)
                            .where(ATTR_USER, MATCHES_REGEXP, "^system")
                            .withOptions(queryOptions.withoutPaging())
                            .getCards().forEach(historyElements::add);
                case HE_REFERENCES ->
                    domainService.getUserRelationsForCard(classId, cardId, queryOptions.withoutPaging(), true).stream()
                            .filter(r -> referenceDomainNames.contains(r.getType().getName()))
                            .map(r -> RelationImpl.copyOf(r).withAttribute("_isReference", true).build()).forEach(historyElements::add);
                case HE_RELATIONS ->
                    domainService.getUserRelationsForCard(classId, cardId, queryOptions.withoutPaging(), true).stream()
                            .filter(r -> !referenceDomainNames.contains(r.getType().getName())).forEach(historyElements::add);
            }
        });
        sortHistoryRecords(historyElements); //TODO handle custom sort ??
        return paged(historyElements, queryOptions.getOffset(), queryOptions.getLimit());
    }

    @Override
    public List<Card> getHistoryElementsOnlyChanges(String classId, Long cardId, DaoQueryOptions queryOptions, List<HistoryElement> types) {
        String attributeName = queryOptions.getAttrs().stream().collect(onlyElement("attr"));

        // Reverse ordered, partial (only standard attributes) history elements
        List<DatabaseRecord> allHistoryElements = getHistoryElements(classId, cardId, queryOptions, types).elements();

        // Sort by creation date
        Collections.reverse(allHistoryElements);

        // Load Cards with full attributes
        List<Card> allHistoryFullElements = allHistoryElements.stream().map(e -> getHistoryRecord(classId, e.getId(), attributeName)).collect(toList());

        List<Card> onlyChangesElements = getChanges(allHistoryFullElements, attributeName);

        sortHistoryRecords(onlyChangesElements);

        return onlyChangesElements;
    }

    @Override
    public Card getHistoryRecord(String classId, long recordId) {
        return getHistoryRecord(classId, recordId, null);
    }

    private Card getHistoryRecord(String classId, long recordId, String attributeId) { //TODO apply user permission
        Card card = dao.select(isBlank(attributeId) ? ATTRS_ALL : attributeId).from(classId).includeHistory()
                .where(ATTR_ID, EQ, recordId)
                .getCard();

        Card previousCard = dao.select(isBlank(attributeId) ? ATTRS_ALL : attributeId).from(classId).includeHistory()
                .where(ATTR_CURRENTID, EQ, card.getCurrentId())
                .where(ATTR_BEGINDATE, LTEQ, card.getBeginDate())
                .where(ATTR_STATUS, EQ, ATTR_STATUS_U)
                .accept(q -> {
                    if (!card.hasStatusActive()) {
                        q.where(ATTR_ID, LT, card.getId());//note: this relies on sequential ids
                    }
                })
                .orderBy(ATTR_BEGINDATE, DESC, ATTR_ID, DESC)
                .limit(1l)
                .getCardOrNull();

        if (previousCard != null) {
            return addDiffToHistoricCard(card, previousCard);
        } else {
            return card;
        }
    }

    @Override
    public CMRelation getRelationHistoryRecord(String domainId, long recordId) {
        CMRelation rel = dao.selectAll().fromDomain(domainId).includeHistory()
                .where(ATTR_ID, EQ, recordId)
                .getRelation();

        CMRelation previousRelation = dao.selectAll().fromDomain(domainId).includeHistory()
                .where(ATTR_CURRENTID, EQ, rel.getCurrentId())
                .where(ATTR_BEGINDATE, LTEQ, rel.getBeginDate())
                .where(ATTR_STATUS, EQ, ATTR_STATUS_U)
                .accept(q -> {
                    if (!rel.hasStatusActive()) {
                        q.where(ATTR_ID, LT, rel.getId());//note: this relies on sequential ids
                    }
                })
                .orderBy(ATTR_BEGINDATE, DESC, ATTR_ID, DESC)
                .limit(1l)
                .getRelationOrNull();
        if (previousRelation != null) {
            return addDiffToHistoricRelation(rel, previousRelation);
        } else {
            return rel;
        }
    }

    private CMRelation addDiffToHistoricRelation(CMRelation currentRelation, CMRelation previousRelation) {
        logger.debug("addDiffToHistoricRelation = {} from previous = {}", currentRelation, previousRelation);
        return RelationImpl.copyOf(currentRelation)
                .accept((c) -> {
                    currentRelation.getAttrsChangedFrom(previousRelation).forEach(a -> {
                        Object previousValue = previousRelation.get(a), thisValue = currentRelation.get(a);
                        c.withAttribute("_" + a + "_changed", true);
                        c.withAttribute("_" + a + "_previous", previousValue);
                    });
                }).build();
    }

    private Card addDiffToHistoricCard(Card thisCard, Card previousCard) {
        logger.debug("addDiffToHistoricCard = {} from previous = {}", thisCard, previousCard);
        return CardImpl.copyOf(thisCard)
                .accept((c) -> {
                    thisCard.getType().getServiceAttributes().stream().map(Attribute::getName).forEach((a) -> {
                        logger.trace("checking differences in attr = {}", a);
                        Object previousValue = previousCard.get(a), thisValue = thisCard.get(a);
                        if (!primitiveEquals(previousValue, thisValue)) {
                            logger.trace("attr = {} changed from {} to {}", a, previousValue, thisValue);
                            c.withAttribute("_" + a + "_changed", true);
                            c.withAttribute("_" + a + "_previous", previousValue);
                        }
                    });
                }).build();
    }

    private List<Card> getChanges(List<Card> allSortedElements, String attributeId) {
        if (allSortedElements.isEmpty()) {
            return emptyList();
        }
        Card previousCard = allSortedElements.stream().findFirst().orElseThrow();
        List<Card> historyElements = list(previousCard);
        for (Card currentCard : allSortedElements) {
            if (currentCard.isAttributeChanged(attributeId, previousCard)) {
                historyElements.add(currentCard);
            }
            previousCard = currentCard;
        }

        return historyElements;
    }

}
