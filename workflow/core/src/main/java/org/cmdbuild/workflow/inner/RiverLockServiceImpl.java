/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockScope;
import org.cmdbuild.lock.LockService;
import static org.cmdbuild.lock.LockType.ILT_FLOW;
import static org.cmdbuild.lock.LockTypeUtils.itemIdWithLockType;
import org.cmdbuild.workflow.river.engine.lock.AquiredLock;
import static org.cmdbuild.workflow.river.engine.lock.AquiredLockImpl.aquiredLock;
import static org.cmdbuild.workflow.river.engine.lock.NotAquiredLockResponseImpl.notAquired;
import org.cmdbuild.workflow.river.engine.lock.RiverLockResponse;
import org.cmdbuild.workflow.river.engine.lock.RiverLockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RiverLockServiceImpl implements RiverLockService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LockService lockService;

    public RiverLockServiceImpl(LockService lockService) {
        this.lockService = checkNotNull(lockService);
    }

    @Override
    public RiverLockResponse aquireLock(String flowId) {
        LockResponse response = lockService.aquireLock(itemIdWithLockType(ILT_FLOW, flowId), LockScope.LS_REQUEST);
        if (response.isAquired()) {
            return aquiredLock(flowId, response.getLock().getItemId(), this);
        } else {
            return notAquired();
        }
    }

    @Override
    public RiverLockResponse aquireLockForBatchTask(String flowId) {
        LockResponse response = lockService.aquireLockOrWait(itemIdWithLockType(ILT_FLOW, flowId), LockScope.LS_REQUEST);
        if (response.isAquired()) {
            return aquiredLock(flowId, response.getLock().getItemId(), this);
        } else {
            return notAquired();
        }
    }

    @Override
    public void releaseLock(AquiredLock lock) {
        lockService.releaseLock(lock.getLockId());
    }

}
