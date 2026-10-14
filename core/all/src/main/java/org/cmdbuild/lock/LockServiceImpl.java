package org.cmdbuild.lock;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.auth.session.inner.CurrentSessionHolder;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.lock.LockRepository.ItemLockAquireResponse;
import static org.cmdbuild.lock.LockType.ILT_OFFLINE;
import static org.cmdbuild.lock.LockTypeUtils.parseLockTypeFromItemId;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.date.CmDateUtils.toUserDuration;
import static org.cmdbuild.utils.hash.CmHashUtils.hashIfLongerThan;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LockServiceImpl implements LockService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LockRepository lockRepository;
    private final CurrentSessionHolder sessionHolder;
    private final RequestContextService requestContextService;
    private final CoreConfiguration coreConfiguration;

    public LockServiceImpl(CoreConfiguration coreConfiguration, LockRepository lockStore, CurrentSessionHolder sessionHolder, RequestContextService requestContextService) {
        this.lockRepository = checkNotNull(lockStore);
        this.sessionHolder = checkNotNull(sessionHolder);
        this.requestContextService = checkNotNull(requestContextService);
        this.coreConfiguration = checkNotNull(coreConfiguration);
    }

    @Override
    public LockResponse aquireLockOrWait(String itemId, LockScope lockScope) {
        return aquireLockOrWait(itemId, lockScope, coreConfiguration.getLockAcquireTimeout().toMillis());
    }

    @Override
    public LockResponse aquireLock(String itemId, LockScope lockScope) {
        logger.debug("aquire lock for item id = {}", itemId);
        itemId = checkItemIdAndShrinkIfNecessary(itemId);
        String sessionId = sessionHolder.getCurrentSessionIdNotNull(), requestId = requestContextService.getRequestContextId();

        ItemLockAquireResponse response = lockRepository.aquireLock(ItemLockImpl.builder()
                .withItemId(itemId)
                .withScope(lockScope)
                .withSessionId(sessionId)
                .withRequestId(requestId)
                .withTimeToLiveSeconds(getTTLSecondsFromLockType(itemId))
                .build());
        if (response.isAquired()) {
            return new SuccessfulLockResponse(new AutocloseableItemLockImpl(response.getLock()));
        } else {
            return new UnsuccessfullLockResponse(itemId);
        }
    }

    @Override
    public LockResponse aquireLockTimeToLiveSeconds(String itemId, LockScope lockScope, int timeToLiveSeconds) {
        logger.debug("aquire lock for item id = {}", itemId);
        itemId = checkItemIdAndShrinkIfNecessary(itemId);
        String sessionId = sessionHolder.getCurrentSessionIdNotNull(), requestId = requestContextService.getRequestContextId();

        ItemLockAquireResponse response = lockRepository.aquireLock(ItemLockImpl.builder()
                .withItemId(itemId)
                .withScope(lockScope)
                .withSessionId(sessionId)
                .withRequestId(requestId)
                .withTimeToLiveSeconds(timeToLiveSeconds)
                .build());
        if (response.isAquired()) {
            return new SuccessfulLockResponse(new AutocloseableItemLockImpl(response.getLock()));
        } else {
            return new UnsuccessfullLockResponse(itemId);
        }
    }

    @Override
    public LockResponse aquireLockOrWait(String itemId, LockScope lockScope, long waitForMillis) {
        logger.trace("acquire lock for itemid =< {} > scope = {}, wait at most {}", itemId, lockScope, toUserDuration(waitForMillis));
        Stopwatch stopwatch = Stopwatch.createStarted();
        LockResponse response = aquireLock(itemId, lockScope);
        while (!response.isAquired() && stopwatch.elapsed(TimeUnit.MILLISECONDS) < waitForMillis) {
            sleepSafe(1000);
            response = aquireLock(itemId, lockScope);
        }
        return response;
    }

    @Override
    @Nullable
    public ItemLock getLockOrNull(String itemId) {
        logger.trace("get lock for item id = {}", itemId);
        itemId = checkItemIdAndShrinkIfNecessary(itemId);
        return lockRepository.getLockByItemIdOrNull(itemId);
    }

    @Override
    public void releaseLock(ItemLock lock) {
        logger.debug("release lock = {}", lock);
        ItemLock currentLock = getNotLockedByOther(lock.getItemId());
        if (currentLock != null) {
            lockRepository.removeLock(currentLock);
        }
    }

    @Override
    public void deleteLock(String lockId) {
        lockRepository.removeLock(getLock(lockId));
    }

    @Nullable
    private ItemLock getNotLockedByOther(String itemId) {
        return checkNotLockedByOthers(getLockOrNull(itemId));
    }

    @Nullable
    private ItemLock checkNotLockedByOthers(@Nullable ItemLock lock) {
        if (lock != null) {
            checkArgument(!isLockedByOther(sessionHolder.getCurrentSessionIdNotNull(), lock), "item = %s is locked by another session", lock.getItemId());
        }
        return lock;
    }

    private boolean isLockedByOther(String sessionId, ItemLock lock) {
        return !equal(lock.getSessionId(), checkNotBlank(sessionId));
    }

    @Override
    public void releaseAllLocks() {
        logger.info("release all locks");
        lockRepository.removeAllLocks();
    }

    @Override
    public List<ItemLock> getAllLocks() {
        logger.debug("get all locks");
        return lockRepository.getAllLocks();
    }

    @Override
    public void requireNotLockedByOthers(String itemId) {
        logger.trace("requireNotLockedByOthers for item id = {}", itemId);
        itemId = checkItemIdAndShrinkIfNecessary(itemId);
        getNotLockedByOther(itemId);
    }

    @Override
    public void requireLockedByCurrent(String itemId) {
        logger.trace("requireLockedByCurrent for item id = {}", itemId);
        itemId = checkItemIdAndShrinkIfNecessary(itemId);
        aquireLockOrFail(itemId);
    }

    private int getTTLSecondsFromLockType(String itemId) {
        return switch (parseLockTypeFromItemId(itemId)) {
            case ILT_OFFLINE ->
                2592000;
            default ->
                coreConfiguration.getLockCardTimeOut();
        };
    }

    private String checkItemIdAndShrinkIfNecessary(String itemId) {
        return hashIfLongerThan(checkNotBlank(itemId), 50);
    }

    private static class UnsuccessfullLockResponse implements LockResponse {

        private final String itemId;

        public UnsuccessfullLockResponse(String itemId) {
            this.itemId = checkNotBlank(itemId);
        }

        @Override
        public boolean isAquired() {
            return false;
        }

        @Override
        public AutoCloseableItemLock getLock() {
            throw new IllegalStateException(format("unable to acquire lock for item id =< %s >", itemId));
        }

    }

    private static class SuccessfulLockResponse implements LockResponse {

        private final AutoCloseableItemLock itemLock;

        public SuccessfulLockResponse(AutoCloseableItemLock itemLock) {
            this.itemLock = checkNotNull(itemLock);
        }

        @Override
        public boolean isAquired() {
            return true;
        }

        @Override
        public AutoCloseableItemLock getLock() {
            return itemLock;
        }

    }

    private class AutocloseableItemLockImpl implements AutoCloseableItemLock {

        private final ItemLock lock;

        public AutocloseableItemLockImpl(ItemLock lock) {
            this.lock = checkNotNull(lock);
        }

        @Override
        public void close() {
            releaseLock(lock);
        }

        @Override
        public String getItemId() {
            return lock.getItemId();
        }

        @Override
        public String getSessionId() {
            return lock.getSessionId();
        }

        @Override
        public String getRequestId() {
            return lock.getRequestId();
        }

        @Override
        public ZonedDateTime getBeginDate() {
            return lock.getBeginDate();
        }

        @Override
        public ZonedDateTime getLastActiveDate() {
            return lock.getLastActiveDate();
        }

        @Override
        public int getTimeToLiveSeconds() {
            return lock.getTimeToLiveSeconds();
        }

        @Override
        public LockScope getScope() {
            return lock.getScope();
        }

        @Override
        public String toString() {
            return "AutocloseableItemLockImpl{" + "lock=" + lock + '}';
        }

    }

}
