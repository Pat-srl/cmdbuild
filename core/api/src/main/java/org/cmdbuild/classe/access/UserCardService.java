/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.BasicWhereMethods;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import org.cmdbuild.dao.driver.postgres.q3.stats.DaoStatsQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.stats.StatsQueryResponse;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.utils.lang.CmConsumerUtils.noop;

public interface UserCardService {

    static final String FOR_DOMAIN_HAS_THIS_RELATION = "_fordomain_hasthisrelation", FOR_DOMAIN_HAS_ANY_RELATION = "_fordomain_hasanyrelation";

    Card getUserCard(String classId, long cardId);

    Card getUserCardInfo(String classId, long cardId);

    boolean userCanReadCard(String classId, long cardId);

    Card createCard(String classId, Map<String, Object> values);

    Card updateCard(String classId, long cardId, Map<String, Object> values);

    void deleteCard(String classId, long cardId);

    UserCardAccess getUserCardAccess(String classId);

    PagedElements<Card> getUserCards(String classId, UserCardQueryOptions options, Pair<String, Function<Classe, Consumer<BasicWhereMethods>>> where);

    /**
     * Reduction of {@link #getUserCards(java.lang.String, org.cmdbuild.classe.access.UserCardQueryOptions, org.apache.commons.lang3.tuple.Pair)
     * } to get info on domain other edge relations.
     *
     * @param originCardId card to search availability of other edge cards to
     * create a new relation.
     * @param classId the target class
     * @param origDirection the search original direction (from source to
     * target)
     * @param domain
     * @return
     */
    public boolean isRelatedInDomain(Long originCardId, String classId, RelationDirection origDirection, Domain domain);

    StatsQueryResponse getStats(String classId, DaoQueryOptions options, DaoStatsQueryOptions query);

    void deleteCards(String classId, CmdbFilter filter);

    void updateCards(String classId, CmdbFilter filter, Map<String, Object> values);

    SuperclassUserQueryHelper getSuperclassQueryHelper(Classe superClass);

    PagedElements<Card> getUserCards(String classId, UserCardQueryOptions options);

    default PagedElements<Card> getUserCards(String classId, @Nullable DaoQueryOptions queryOptions) {
        return getUserCards(classId, UserCardQueryOptionsImpl.builder().withQueryOptions(queryOptions).build());
    }

    default PagedElements<Card> getUserCards(String classId) {
        return getUserCards(classId, emptyOptions());
    }

    default Pair<String, Function<Classe, Consumer<BasicWhereMethods>>> emptyWhere() {
        return Pair.of("", (c) -> noop());
    }

    default boolean isManySide(Domain dom, UserCardQueryForDomain forDomain) {
        return (dom.hasCardinality(ONE_TO_MANY) && forDomain.getDirection().equals(RelationDirection.RD_DIRECT))
                || (dom.hasCardinality(MANY_TO_ONE) && forDomain.getDirection().equals(RelationDirection.RD_INVERSE));
    }

    default boolean isOneSide(Domain dom, UserCardQueryForDomain forDomain) {
        return (dom.hasCardinality(ONE_TO_MANY) && forDomain.getDirection().equals(RelationDirection.RD_INVERSE))
                || (dom.hasCardinality(MANY_TO_ONE) && forDomain.getDirection().equals(RelationDirection.RD_DIRECT));
    }

    static String toLog(PagedElements<Card> cards) {
        return toLog(cards.elements());
    }

    static String toLog(List<Card> cards) {
        return String.join(",", cards.stream().map(UserCardService::toLog).collect(toList()));
    }

    static String toLog(Card card) {
        return format("{\"_id\"=%s, \"name\"=\"%s\", \"inThisRelation\"=\"%s\", \"inAnyRelation\"=\"%s\"}", card.getString(ATTR_ID), card.getString(ATTR_CODE), card.get(FOR_DOMAIN_HAS_THIS_RELATION), card.get(FOR_DOMAIN_HAS_ANY_RELATION));
    }
}
