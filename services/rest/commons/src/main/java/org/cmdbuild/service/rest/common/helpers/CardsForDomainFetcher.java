/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.service.rest.common.helpers;

import java.util.List;
import java.util.Map;
import org.cmdbuild.classe.access.UserCardQueryForDomain;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;

/**
 *
 * @author afelice
 */
public interface CardsForDomainFetcher {

    /**
     * Was in <code>services-rest-v3::CardWS.readMany()</code>
     *
     * @param forDomain
     * @param classId <dl><dt>target class id <dd>for
     * {@link RelationDirection#RD_DIRECT} association; <dt>source class id
     * <dd>for {@link RelationDirection#RD_INVERSE} association.</dl>
     * @param queryOptions
     * @param selectFunctionValue
     * @return (paged) list of cards for <code>classId</code>
     */
    PagedElements<Card> fetchCards(UserCardQueryForDomain forDomain, String classId, DaoQueryOptions queryOptions, String selectFunctionValue);

    /**
     * Was in <code>services-rest-v3::CardWS.readMany()</code>
     *
     * @param forDomain represent the relation the client wants to establish, as
     * domain, direction and source class.
     * @param cards
     * @param queryOptions
     * @param selectFunctionValue
     * @return list of target cards, each card represented as a map of
     * attributes to return in the <code>CardWs</code>. Specifically for the
     * given domain and each card:
     * <dt>
     * <dl>_<code>domainName</code>_available <dd>if the card is selectable for
     * this relation;
     * <dl>_<code>domainName</code>_hasrelation <dd>if the card is in other
     * relations.
     * </dt>
     */
    List<Map<String, Object>> fetchCardsForDomain(UserCardQueryForDomain forDomain, PagedElements<Card> cards, DaoQueryOptions queryOptions, String selectFunctionValue);
}
