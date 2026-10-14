/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.collect.Ordering;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.lock.LockService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

/**
 * @author ldare
 */
@Component
public class LockWsCommand {

    private final LockService lockService;

    public LockWsCommand(LockService lockService) {
        this.lockService = checkNotNull(lockService);
    }

    public List<ItemLock> doGetLocks() {
        return lockService.getAllLocks().stream().sorted(Ordering.natural().reverse().onResultOf(ItemLock::getLastActiveDate)).collect(toList());
    }

    public ItemLock doGetLock(String lockId) {
        return lockService.getLock(lockId);
    }

    public void doDeleteLock(String lockId) {
        lockService.deleteLock(lockId);
    }

    public void doDeleteAllLocks() {
        lockService.releaseAllLocks();
    }
}
