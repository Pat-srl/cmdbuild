/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.common.helpers;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.classe.access.UserCardQueryForDomain;
import org.cmdbuild.classe.access.UserCardQueryOptions;
import org.cmdbuild.classe.access.UserCardQueryOptionsImpl;
import org.cmdbuild.classe.access.UserCardService;
import static org.cmdbuild.classe.access.UserCardService.FOR_DOMAIN_HAS_ANY_RELATION;
import static org.cmdbuild.classe.access.UserCardService.FOR_DOMAIN_HAS_THIS_RELATION;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_MANY;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class CardsForDomainFetcherImpl implements CardsForDomainFetcher {

    private final DaoService dao;
    private final UserCardService cardService;
    private final CardWsSerializationHelperv3 helper;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CardsForDomainFetcherImpl(CardWsSerializationHelperv3 helper, UserCardService cardService, DaoService dao) {
        this.helper = checkNotNull(helper);
        this.cardService = checkNotNull(cardService);
        this.dao = checkNotNull(dao);
    }

    @Override
    public PagedElements<Card> fetchCards(UserCardQueryForDomain forDomain, String classId, DaoQueryOptions queryOptions, String selectFunctionValue) {
        UserCardQueryOptions userQueryOptions = UserCardQueryOptionsImpl.builder().withQueryOptions(queryOptions).withForDomain(forDomain).withFunctionValue(selectFunctionValue).build();
        PagedElements<Card> userCards = cardService.getUserCards(classId, userQueryOptions);
        return userCards;
    }

    @Override
    public List<Map<String, Object>> fetchCardsForDomain(UserCardQueryForDomain forDomain, PagedElements<Card> cards, DaoQueryOptions queryOptions, String selectFunctionValue) {
        if (cards.isEmpty()) {
            return emptyList();
        }
        Domain domain = forDomain != null ? dao.getDomain(forDomain.getDomainName()).getThisDomainWithDirection(forDomain.getDirection()) : null;

        return cards.stream().map(c -> helper.serializeCard(c, queryOptions).accept((m) -> {
            if (domain != null) {
                boolean hasAnyRelation = c.getBoolean(FOR_DOMAIN_HAS_ANY_RELATION),
                        hasThisRelation = c.getBoolean(FOR_DOMAIN_HAS_THIS_RELATION),
                        available = checkCardAvalability(domain, forDomain, c, hasAnyRelation, hasThisRelation);
                m.put(format("_%s_available", domain.getName()), available, format("_%s_hasrelation", domain.getName()), hasThisRelation);
            }
            if (isNotBlank(selectFunctionValue)) {
                Attribute param = dao.getFunctionByName(selectFunctionValue).getOnlyOutputParameter();
                helper.addCardValuesAndDescriptionsAndExtras(param.getName(), param.getType(), c::get, m::put);
            }
        })).collect(toList());
    }

    private boolean checkCardAvalability(Domain domain, UserCardQueryForDomain forDomain, Card card, boolean hasAnyRelation, boolean hasThisRelation) {
        if (!forDomain.getAll() && cardService.isManySide(domain, forDomain) && cardService.isRelatedInDomain(forDomain.getOriginId(), getClassAtManySide(domain), forDomain.getDirection(), domain)) {
            return false;
        }
        return (!hasThisRelation || domain.hasDomainKeyAttrs()) && domain.isDomainForTargetClasse(card.getType()) && (!hasAnyRelation || (domain.hasCardinality(MANY_TO_ONE) || domain.hasCardinality(MANY_TO_MANY)));
    }

    private String getClassAtManySide(Domain dom) {
        if (dom.hasCardinality(ONE_TO_MANY)) {
            return dom.getTargetClassName();
        } else if (dom.hasCardinality(MANY_TO_ONE)) {
            return dom.getSourceClassName();
        }
        throw illegalArgument("Expected `%s`|`%s`, found `%s`", ONE_TO_MANY, MANY_TO_MANY, dom.getCardinality());
    }

    private String getClassAtOneSide(Domain dom) {
        if (dom.hasCardinality(ONE_TO_MANY)) {
            return dom.getSourceClassName();
        } else if (dom.hasCardinality(MANY_TO_ONE)) {
            return dom.getTargetClassName();
        }

        throw illegalArgument("Expected `%s`|`%s`, found `%s`", ONE_TO_MANY, MANY_TO_MANY, dom.getCardinality());
    }
}
