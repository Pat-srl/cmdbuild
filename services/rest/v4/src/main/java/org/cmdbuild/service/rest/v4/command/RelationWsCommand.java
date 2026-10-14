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
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.model.WsDomainAndDirectionInfo;
import org.cmdbuild.service.rest.v4.model.WsRelationCopyParams;
import org.cmdbuild.service.rest.v4.model.WsRelationData;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.entrytype.ClassPermission.*;
import static org.cmdbuild.dao.entrytype.PermissionScope.PS_SERVICE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * @author ldare
 */
@Component
public class RelationWsCommand {

    private final UserDomainService userDomainService;
    private final DaoService daoService;

    public RelationWsCommand(UserDomainService userDomainService, DaoService daoService, CardWsSerializationHelperv3 cardWsSerializationHelperv3) {
        this.userDomainService = checkNotNull(userDomainService);
        this.daoService = checkNotNull(daoService);
    }

    public PagedElements<CMRelation> doReadAll(String domainId, WsQueryOptions queryOptions) {
        return userDomainService.getUserRelations(domainId, queryOptions.getQuery());
    }

    public CMRelation doRead(String domainId, Long relationId) {
        return userDomainService.getUserRelation(domainId, relationId);
    }

    public CMRelation doCreate(String domainId, WsRelationData relationData) {
        Domain domain = userDomainService.getUserDomain(domainId);
        domain.checkPermission(PS_SERVICE, CP_CREATE);
        relationData = relationData.getDataDirect();
        CMRelation relation = RelationImpl.builder()
                .withType(domain)
                .withSourceCard(daoService.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(daoService.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();
        return daoService.create(relation);
    }

    public CMRelation doUpdate(String domainId, Long relationId, WsRelationData relationData) {
        CMRelation relation = userDomainService.getUserRelation(domainId, relationId);
        relation.getType().checkPermission(PS_SERVICE, CP_UPDATE);
        relationData = relationData.getDataDirect();
        relation = RelationImpl.copyOf(relation)
                .withSourceCard(daoService.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(daoService.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();
        return daoService.update(relation);
    }

    public void doDelete(String domainId, Long relationId) {
        CMRelation relation = userDomainService.getUserRelation(domainId, relationId);
        relation.getType().checkPermission(PS_SERVICE, CP_DELETE);
        daoService.delete(relation);
    }

    public void doMoveManyRelations(String domainId, WsRelationCopyParams params) {
        checkArgument(equal(domainId, "_ANY"), "domain id path param must be set to '_ANY'");
        copyOrMoveManyRelations(params, false);
    }

    public void doCopyManyRelations(String domainId, WsRelationCopyParams params) {
        checkArgument(equal(domainId, "_ANY"), "domain id path param must be set to '_ANY'");
        copyOrMoveManyRelations(params, true);
    }

    private void copyOrMoveManyRelations(WsRelationCopyParams params, boolean copy) {
        List<WsDomainAndDirectionInfo> domainInfo = params.getDomains();
        long sourceCardId = params.getSourceCardId(), destinationCardId = params.getDestinationCardId();
        if (isNullOrEmpty(domainInfo)) {
            List<WsDomainAndDirectionInfo> list = domainInfo = list();
            userDomainService.getUserRelationsForCard(daoService.getCard(sourceCardId).getClassName(), sourceCardId, DaoQueryOptionsImpl.emptyOptions()).stream()
                    .map(r -> new WsDomainAndDirectionInfo(r.getType().getName(), r.getDirection()))
                    .filter(i -> list.stream().noneMatch(ii -> equal(ii.getDirection(), i.getDirection()) && equal(ii.getDomainId(), i.getDomainId())))
                    .forEach(list::add);
        }
        domainInfo.forEach(d -> {
            if (copy) {
                userDomainService.copyManyRelations(sourceCardId, destinationCardId, d.getDomainId(), d.getDirection());
            } else {
                userDomainService.moveManyRelations(sourceCardId, destinationCardId, d.getDomainId(), d.getDirection());
            }
        });
    }
}
