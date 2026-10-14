/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.CascadeAction;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.data.filter.CmdbFilter;

import java.util.List;

import static java.util.stream.Collectors.toList;

public interface UserDomainService {

    /**
     *
     * @return domain with (if any) informations on reference filters to be
     * applied to source/target class from:
     * <ol>
     * <li>{@link Classe] attributes;
     * <li>filters defined in domain {@link Classe};
     * </ol>
     * @throws NullPointerException if attribute as <code>useDomainFilter</code>
     * flag <b>but</b> domainFilter is null.
     */
    List<Domain> getDomains();

    Domain getDomain(String domainId);

    List<Domain> getUserDomains();

    List<Domain> getUserDomainsAsAdmin();

    /**
     *
     * @param classId
     * @return list of domains with (if any) informations on reference filters
     * to be applied to source/target class from:
     * <ol>
     * <li>{@link Classe] attributes;
     * <li>filters defined in domain {@link Classe};
     * </ol>
     * @throws NullPointerException if attribute as <code>useDomainFilter</code>
     * flag <b>but</b> domainFilter is null
     */
    List<Domain> getUserDomainsForClasse(String classId);

    /**
     *
     * @param domainId
     * @return domain with (if any) attribute <code>referenceFilters</code>
     * informations on reference filters to be applied to source/target class
     * from:
     * <ol>
     * <li>{@link Classe] attributes;
     * <li>filters defined in domain {@link Classe};
     * </ol>
     * @throws NullPointerException if attribute as <code>useDomainFilter</code>
     * flag <b>but</b> domainFilter is null.
     */
    Domain getUserDomain(String domainId);

    Domain getUserDomain(String domainId, boolean isAdminViewMode);

    PagedElements<CMRelation> getUserRelations(String domainId, DaoQueryOptions queryOptions);

    PagedElements<CMRelation> getUserRelationsForCard(String classId, long cardId, DaoQueryOptions queryOptions, boolean includeHistory);

    CMRelation getUserRelation(String domainId, long relationId);

    void moveManyRelations(long sourceCardId, long destinationCardId, String domainId, RelationDirection direction);

    void copyManyRelations(long sourceCardId, long destinationCardId, String domainId, RelationDirection direction);

    List<CardDomainRelationStats> getRelationsStats(String classId, CmdbFilter filter);

    default PagedElements<CMRelation> getUserRelationsForCard(String classId, long cardId, DaoQueryOptions queryOptions) {
        return getUserRelationsForCard(classId, cardId, queryOptions, false);
    }

    default PagedElements<CMRelation> getUserRelationsForCard(CardIdAndClassName card, DaoQueryOptions queryOptions) {
        return getUserRelationsForCard(card.getClassName(), card.getId(), queryOptions);
    }

    default List<CMRelation> getUserRelationsForCard(CardIdAndClassName card) {
        return getUserRelationsForCard(card.getClassName(), card.getId(), DaoQueryOptionsImpl.emptyOptions()).elements();
    }

    default List<Domain> getActiveUserDomains() {
        return getUserDomains().stream().filter(Domain::isActive).collect(toList());
    }

    default List<Domain> getActiveUserDomainsForClasse(String classId) {
        return getUserDomainsForClasse(classId).stream().filter(Domain::isActive).collect(toList());
    }

    interface CardDomainRelationStats {

        long getRelationCount();

        RelationDirection getDirection();

        Domain getDomain();

        default CascadeAction getCascadeAction() {
            return switch (getDirection()) {
                case RD_DIRECT ->
                    getDomain().getMetadata().getCascadeActionDirect();
                case RD_INVERSE ->
                    getDomain().getMetadata().getCascadeActionInverse();
                default ->
                    throw new UnsupportedOperationException();
            };
        }
    }

}
