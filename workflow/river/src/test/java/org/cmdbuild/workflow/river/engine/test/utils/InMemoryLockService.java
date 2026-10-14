/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.test.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.workflow.river.engine.lock.AquiredLock;
import org.cmdbuild.workflow.river.engine.lock.AquiredLockImpl;
import static org.cmdbuild.workflow.river.engine.lock.AquiredLockImpl.aquiredLock;
import static org.cmdbuild.workflow.river.engine.lock.NotAquiredLockResponseImpl.notAquired;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.cmdbuild.workflow.river.engine.lock.RiverLockService;
import org.cmdbuild.workflow.river.engine.lock.RiverLockResponse;

public class InMemoryLockService implements RiverLockService {

	private final Map<String, AquiredLock> locksByWalkId = map();

	@Override
	public synchronized RiverLockResponse aquireLock(String walkId) {
		AquiredLock currentLock = locksByWalkId.get(walkId);
		if (currentLock != null) {
			return notAquired();
		} else {
			AquiredLockImpl newLock = aquiredLock(walkId, randomId(), this);
			locksByWalkId.put(walkId, newLock);
			return newLock;
		}
	}

	@Override
	public synchronized void releaseLock(AquiredLock lock) {
		AquiredLock currentLock = locksByWalkId.get(lock.getWalkId());
		checkNotNull(currentLock);
		checkArgument(equal(currentLock.getLockId(), lock.getLockId()));
		locksByWalkId.remove(lock.getWalkId());
	}

}
