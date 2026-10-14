/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.dao.beans.CMRelation;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class RelationHistoryWsCommand {

    private final CardHistoryService cardHistoryService;

    public RelationHistoryWsCommand(CardHistoryService cardHistoryService) {
        this.cardHistoryService = checkNotNull(cardHistoryService);
    }

    public CMRelation doGetRelationHistoryRecord(String domainId, Long relationId) {
        return cardHistoryService.getRelationHistoryRecord(domainId, relationId);
    }
}
