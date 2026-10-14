/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.lock;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.function.Consumer;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class AquiredLockImpl implements AquiredLock, RiverLockResponse {

    private final String walkId, lockId;
    private final Consumer<AquiredLock> releaseLockCallback;

    private AquiredLockImpl(String walkId, String lockId, Consumer<AquiredLock> releaseLockCallback) {
        this.walkId = checkNotBlank(walkId);
        this.lockId = checkNotBlank(lockId);
        this.releaseLockCallback = checkNotNull(releaseLockCallback);
    }

    @Override
    public String getWalkId() {
        return walkId;
    }

    @Override
    public String getLockId() {
        return lockId;
    }

    @Override
    public boolean isAquired() {
        return true;
    }

    @Override
    public AquiredLock aquired() {
        return this;
    }

    @Override
    public void release() {
        releaseLockCallback.accept(this);
    }

    @Override
    public String toString() {
        return "AquiredLockImpl{" + "walkId=" + walkId + ", lockId=" + lockId + '}';
    }

    public static AquiredLockImpl aquiredLock(String walkId, String lockId, RiverLockService lockService) {
        return new AquiredLockImpl(walkId, lockId, (lock) -> lockService.releaseLock(lock));
    }

}
