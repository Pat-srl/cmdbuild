/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.bim.BimObject;
import org.cmdbuild.bim.BimService;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class CardBimValueWsCommand {

    private final DaoService daoService;
    private final BimService bimService;

    public CardBimValueWsCommand(DaoService daoService, BimService bimService) {
        this.daoService = checkNotNull(daoService);
        this.bimService = checkNotNull(bimService);
    }

    public BimObject doGetAllForCard(String classId, Long cardId, Boolean checkIfExists, Boolean includeRelated) {
        Card card = daoService.getCard(classId, cardId);
        BimObject bimObject = includeRelated ? bimService.getBimObjectForCardOrViaNavTreeOrNull(card) : bimService.getBimObjectForCardOrNull(card);
        checkArgument(checkIfExists || bimObject != null, "bim object not found for car = %s", card);
        return bimObject;
    }
}
