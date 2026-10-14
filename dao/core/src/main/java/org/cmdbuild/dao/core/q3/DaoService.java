/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.core.q3;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import static java.util.Collections.singleton;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import static org.cmdbuild.common.Constants.BASE_DOMAIN_NAME;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_A;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS_D;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.repository.AttributeRepository;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.EntryTypeType;
import org.cmdbuild.dao.entrytype.ReverseDomain;
import org.cmdbuild.dao.function.StoredFunctionService;
import org.cmdbuild.dao.graph.ClasseHierarchyService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.nullToVoid;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.jdbc.core.JdbcTemplate;

public interface DaoService extends QueryBuilderService, AttributeRepository, ClasseRepository, StoredFunctionService, DomainRepository, ClasseHierarchyService, SuperclassQueryService {

    static final String ATTRS_ALL = "*", COUNT = "count", ROW_NUMBER = "row_number";

    <T> T create(T model);

    <T> T update(T model);

    <T> long createOnly(T model);

    <T> void updateOnly(T model);

    long createOnly(DatabaseRecord card);

    List<Long> createBatch(List<Card> cards);

    void updateOnly(Card card);

    void delete(Class model, long cardId);

    void definitiveDelete(Class model, long cardId);

    Card create(Card card);

    Card update(Card card);

    CMRelation create(CMRelation relation);

    CMRelation update(CMRelation relation);

    void delete(CMRelation relation);

    JdbcTemplate getJdbcTemplate();

    void delete(DatabaseRecord record);

    void delete(Object model);

    List<CMRelation> getServiceRelationsForCard(CardIdAndClassName card, CmdbSorter sorter, boolean includeHistory);

    List<Card> deleteCards(Classe classe, CmdbFilter filter);

    List<Card> updateCards(Classe classe, CmdbFilter filter, Map<String, Object> values);

    Card getInfo(Classe classe, long cardId);

    default List<CMRelation> getServiceRelationsForCard(CardIdAndClassName card, CmdbSorter sorter) {
        return getServiceRelationsForCard(card, sorter, false);
    }

    default CMRelation getRelation(long relationId) {
        CMRelation relation = getRelation(BASE_DOMAIN_NAME, relationId);
        return getRelation(relation.getType(), relationId);
    }

    default CMRelation getRelation(String domainId, long relationId) {
        return getRelation(getDomain(domainId), relationId);
    }

    default CMRelation getRelation(Domain domain, long relationId) {
        return selectAll().from(domain).where(ATTR_ID, EQ, relationId).getRelation();
    }

    default CMRelation getRelation(String domain, long sourceId, long targetId) {
        return selectAll().fromDomain(domain).where(ATTR_IDOBJ1, EQ, sourceId).where(ATTR_IDOBJ2, EQ, targetId).getRelation();
    }

    default CMRelation getRelation(Domain domain, long sourceId, long targetId) {
        if (domain instanceof ReverseDomain) {//TODO check this, improve
            return getRelation(ReverseDomain.of(domain), targetId, sourceId);
        } else {
            return selectAll().from(domain).where(ATTR_IDOBJ1, EQ, sourceId).where(ATTR_IDOBJ2, EQ, targetId).getRelation();
        }
    }

    default CMRelation getRelation(Domain domain, CardIdAndClassName sourceCard, CardIdAndClassName targetCard) {
        return getRelation(domain, sourceCard.getId(), targetCard.getId());
    }

    default CMRelation getRelation(String domain, CardIdAndClassName sourceCard, CardIdAndClassName targetCard) {
        return getRelation(domain, sourceCard.getId(), targetCard.getId());
    }

    @Nullable
    default CMRelation getRelationOrNull(Domain domain, long sourceId, long targetId) {
        return selectAll().from(domain).where(ATTR_IDOBJ1, EQ, sourceId).where(ATTR_IDOBJ2, EQ, targetId).getRelationOrNull();
    }

    default void deleteRelation(String domain, long sourceId, long targetId) {
        delete(getRelation(domain, sourceId, targetId));
    }

    default CMRelation createRelation(Domain domain, CardIdAndClassName source, CardIdAndClassName target) {
        return create(RelationImpl.build(domain, source, target));
    }

    default CMRelation createRelation(String domain, CardIdAndClassName source, CardIdAndClassName target) {
        return createRelation(getDomain(domain), source, target);
    }

    default CMRelation createRelation(Domain domain, CardIdAndClassName source, CardIdAndClassName target, Object... data) {
        return create(RelationImpl.builder().withType(domain).withSourceCard(source).withTargetCard(target).withAttributes(map(data)).build());
    }

    default CMRelation createRelation(String domain, CardIdAndClassName source, CardIdAndClassName target, Object... data) {
        return createRelation(getDomain(domain), source, target, data);
    }

    default <T> T getById(Class<T> model, long cardId) {
        return checkNotNull(getByIdOrNull(model, cardId), "card not found for class = %s id = %s", nullToVoid(model).getName(), cardId);
    }

    @Nullable
    default <T> T getByIdOrNull(Class<T> model, long cardId) {
        return selectAll().from(model).where(ATTR_ID, EQ, cardId).getOneOrNull(model);
    }

    default <T> T getByCode(Class<T> model, String cardCode) {
        return checkNotNull(getByCodeOrNull(model, cardCode), "card not found for class = %s code =< %s >", nullToVoid(model).getName(), cardCode);
    }

    @Nullable
    default <T> T getByCodeOrNull(Class<T> model, String cardCode) {
        return selectAll().from(model).where(ATTR_CODE, EQ, checkNotBlank(cardCode)).getOneOrNull(model);
    }

    default QueryBuilder select(DaoQueryOptions daoQuery) {
        return daoQuery.hasAttrs() ? query().select(daoQuery.getAttrs()) : query().selectAll();
    }

    default QueryBuilder select(Collection<String> attrs) {
        return query().select(attrs);
    }

    default QueryBuilder select(String... attrs) {
        return query().select(attrs);
    }

    default QueryBuilder selectDistinct(String attr) {
        return query().groupBy(attr);
    }

    default QueryBuilder selectDistinctExpr(String name, String expr) {
        return query().groupByExpr(name, expr);
    }

    @Nullable
    default Card getCardOrNull(Classe classe, long cardId, String... status) {
        Card card = selectAll().accept(q -> {
            Set<String> set = set(status);
            if (!set.isEmpty() && !equal(set, singleton(ATTR_STATUS_A))) {
                q.includeHistory().where(ATTR_STATUS, IN, set);
            }
        }).from(classe).where(ATTR_ID, WhereOperator.EQ, cardId).getCardOrNull();
        if (card != null && !equal(card.getType().getName(), classe.getName())) {
            card = getCardOrNull(card.getType(), cardId);
        }
        return card;
    }

    default Card getCard(Classe thisClass, long cardId, String... status) {
        return checkNotNull(getCardOrNull(thisClass, cardId, status), "card not found for class = %s cardId = %s status = %s", thisClass, cardId, status);
    }

    default Card getCard(String thisClass, long cardId, String... status) {
        return getCard(getClasse(thisClass), cardId, status);
    }

    default Card getCard(CardIdAndClassName card) {
        return getCard(card.getClassName(), card.getId());
    }

    default Card getCardOrDraft(Classe classe, long cardId) {
        return getCard(classe, cardId, ATTR_STATUS_A, ATTR_STATUS_D);
    }

    default Card getCardOrDraft(String classe, long cardId) {
        return getCard(classe, cardId, ATTR_STATUS_A, ATTR_STATUS_D);
    }

    default Card getCard(String classId, long cardId) {
        return getCard(getClasse(classId), cardId);
    }

    default Card getCard(long cardId) {
        return getCard(getRootClass(), cardId);
    }

    default void delete(Classe classe, long cardId) {
        delete(getCard(classe, cardId));
    }

    default void delete(String classId, long cardId) {
        delete(getCard(getClasse(classId), cardId));
    }

    default void delete(EntryType entryType, long entryId) {
        switch (entryType.getEtType()) {
            case ET_CLASS ->
                delete((Classe) entryType, entryId);
            case ET_DOMAIN ->
                delete(getRelation((Domain) entryType, entryId));
            default ->
                throw unsupported("unsupported record delete with entry type = %s", entryType);
        }
    }

    default EntryType getType(EntryTypeType entryTypeType, String entryTypeId) {
        return switch (entryTypeType) {
            case ET_CLASS ->
                getClasse(entryTypeId);
            case ET_DOMAIN ->
                getDomain(entryTypeId);
            case ET_FUNCTION ->
                getFunctionByName(entryTypeId);
            default ->
                throw unsupported("unsupported entryTypeType = %s", entryTypeType);
        };
    }

    default DatabaseRecord getRecord(EntryType entryType, long entryId) {
        return switch (entryType.getEtType()) {
            case ET_CLASS ->
                getCard((Classe) entryType, entryId);
            case ET_DOMAIN ->
                getRelation((Domain) entryType, entryId);
            default ->
                throw unsupported("unsupported record get with entry type = %s", entryType);
        };
    }

    default Classe getType(IdAndDescription card) {
        checkNotNull(card);
        if (card instanceof Card card1) {
            return card1.getType();
        } else {
            return getType(card.getTypeName(), card.getId());
        }
    }

    default Classe getType(String className, long cardId) {
        return getType(getClasse(className), cardId);
    }

    default Classe getType(Classe classe, long cardId) {
        if (classe.isSuperclass()) {
            classe = getInfo(classe, cardId).getType();
        }
        return classe;
    }

    default Classe getType(long card) {
        return getCard(card).getType();
    }

    default CardIdAndClassName getCardIdAndClassName(CardIdAndClassName card) {
        Classe classe = getClasse(card.getClassName());
        return classe.isSuperclass() ? getInfo(classe, card.getId()) : card;
    }

    default String getTypeName(String className, long cardId) {
        return getType(className, cardId).getName();
    }

}
