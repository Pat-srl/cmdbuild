/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.lock.LockType.ILT_CARD;
import static org.cmdbuild.lock.LockTypeUtils.itemIdWithLockType;

/**
 * @author ldare
 */
@Component
public class CardLockWsCommand {

    private final LockService lockService;

    public CardLockWsCommand(LockService lockService) {
        this.lockService = checkNotNull(lockService);
    }

    public ItemLock doGetLock(Long cardId) {
        return lockService.getLockOrNull(itemIdWithLockType(ILT_CARD, cardId));
    }

    public LockResponse doCreateLock(Long cardId) {
        return lockService.aquireLock(itemIdWithLockType(ILT_CARD, cardId));
    }

    public void doReleaseLock(Long cardId) {
        lockService.releaseLock(itemIdWithLockType(ILT_CARD, cardId));
    }
}
