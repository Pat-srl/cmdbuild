/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.RelationImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.service.rest.v4.model.WsRelationData;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class CardRelationWsCommand {

    private final UserDomainService userDomainService;
    private final DaoService daoService;

    public CardRelationWsCommand(UserDomainService userDomainService, DaoService daoService) {
        this.userDomainService = checkNotNull(userDomainService);
        this.daoService = checkNotNull(daoService);
    }

    public PagedElements<CMRelation> doRead(String className, Long cardId, Long limit, Long offset, String filterStr, String sort) {
        return userDomainService.getUserRelationsForCard(className, cardId, DaoQueryOptionsImpl.builder()
                .withFilter(filterStr).
                withPaging(offset, limit)
                .withSorter(sort).build());
    }

    public CMRelation doCreate(Long cardId, WsRelationData relationData) {
        relationData = relationData.getDataDirect();
        CMRelation relation = RelationImpl.builder()
                .withType(daoService.getDomain(relationData.getDomainType()))
                .withSourceCard(daoService.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(daoService.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();

        relation = daoService.create(relation);
        return relation.getRelationWithSource(cardId);
    }

    public CMRelation doUpdate(Long cardId, Long relationId, WsRelationData relationData) {
        relationData = relationData.getDataDirect();
        CMRelation relation = daoService.getRelation(relationData.getDomainType(), relationId);

        relation = RelationImpl.copyOf(relation)
                .withSourceCard(daoService.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(daoService.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();

        relation = daoService.update(relation);
        return relation.getRelationWithSource(cardId);
    }

    public void doDelete(Long relationId) {
        CMRelation relation = daoService.getRelation(relationId);
        daoService.delete(relation);
    }
}
