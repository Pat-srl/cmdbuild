/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lock;

import java.util.List;
import jakarta.annotation.Nullable;

public interface LockRepository {

    ItemLockAquireResponse aquireLock(ItemLock itemLock);

    @Nullable
    ItemLock getLockByItemIdOrNull(String itemId);

    List<ItemLock> getAllLocks();

    void removeAllLocks();

    void removeLock(ItemLock currentLock);

    interface ItemLockAquireResponse {

        boolean isAquired();

        ItemLock getLock();
    }

}
