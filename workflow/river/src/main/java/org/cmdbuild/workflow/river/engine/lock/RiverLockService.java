/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.lock;

import org.cmdbuild.workflow.river.engine.RiverFlow;

public interface RiverLockService {

    RiverLockResponse aquireLock(String flowId);

    void releaseLock(AquiredLock lock);

    default RiverLockResponse aquireLockForBatchTask(RiverFlow flow) {
        return aquireLock(flow.getId());
    }

    default RiverLockResponse aquireLockForBatchTask(String flowId) {
        return aquireLock(flowId);
    }

    default RiverLockResponse aquireLock(RiverFlow flow) {
        return aquireLock(flow.getId());
    }

}
