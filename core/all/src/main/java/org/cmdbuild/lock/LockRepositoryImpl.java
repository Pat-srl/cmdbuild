/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lock;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import jakarta.annotation.Nullable;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.lock.ItemLock.ITEMLOCK_ATTR_SCOPE;
import static org.cmdbuild.lock.LockScope.LS_REQUEST;
import static org.cmdbuild.lock.LockScopeUtils.serializeLockScope;
import org.cmdbuild.requestcontext.RequestContextActiveService;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toJavaDate;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import org.cmdbuild.utils.lang.CmException;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_SINGLE_NODE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class LockRepositoryImpl implements LockRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final JdbcTemplate jdbcTemplate;
    private final CoreConfiguration configuration;
    private final RequestContextActiveService requestService;

    private final CmCache<Optional<ItemLock>> itemLocksByItemId;

    public LockRepositoryImpl(DaoService dao, CoreConfiguration configuration, CacheService cacheService, RequestContextActiveService requestService) {
        logger.info("init");
        this.dao = checkNotNull(dao);
        this.jdbcTemplate = dao.getJdbcTemplate();
        this.configuration = checkNotNull(configuration);
        itemLocksByItemId = cacheService.newCache("item_lock_cache");
        this.requestService = checkNotNull(requestService);
    }

    @ScheduledJob(value = "0 */1 * * * ?", persistRun = false)//once per minute. This must be less than TimeToLive 
    public void scrubLockTable() { //TODO improve this (??)
        dao.selectAll().from(ItemLock.class).whereExpr("\"LastActiveDate\" + format('%s seconds', \"TimeToLive\" / 2)::interval < ?", now()).where(ITEMLOCK_ATTR_SCOPE, EQ, serializeEnum(LS_REQUEST)).asList(ItemLock.class).forEach(itemLock -> {
            if (requestService.isRequestContextActive(itemLock.getRequestId())) {//TODO improve this (??)
                aquireLockOnDb(itemLock);
            }
        });
    }

    @ScheduledJob(value = "0 10 * * * ?", persistRun = false, clusterMode = RUN_ON_SINGLE_NODE)//once per hour
    public void cleanupLockTable() {
        int res = jdbcTemplate.update(format("DELETE FROM \"_Lock\" WHERE \"LastActiveDate\" + format('%%s seconds', \"TimeToLive\")::interval < %s", systemToSqlExpr(now())));
        logger.debug("deleteted {} expired locks", res);
    }

    @Override
    public ItemLockAquireResponse aquireLock(ItemLock itemLock) {
        ItemLock currentLock = getLockByItemIdOrNull(itemLock.getItemId());
        if (currentLock == null || currentLock.isExpired() || !currentLock.isCompatibleWith(itemLock)) {
            logger.debug("current lock for item id =< {} > is missing or expired or incompatible, try to aquire from db", itemLock.getItemId());
            return aquireLockOnDb(itemLock);
        } else if (currentLock.getAgeSeconds() > configuration.getLockCardPersistDelay()) {
            logger.debug("current lock = {} is too old, will renew", currentLock);
            return aquireLockOnDb(itemLock);
        } else {
            logger.debug("found lock = {}", currentLock);
            return new ItemLockAquireResponseImpl(true, currentLock);
        }
    }

    private ItemLockAquireResponse aquireLockOnDb(ItemLock itemLock) {
        try {
            logger.debug("acquiring lock = {}", itemLock);
            ItemLock actualLock = null;
            boolean aquired = false;
            while (actualLock == null) {
                Map<String, Object> result = jdbcTemplate.queryForMap("SELECT * FROM _cm3_lock_aquire_try(?,?,?,?,?,?)", itemLock.getItemId(), itemLock.getSessionId(), itemLock.getRequestId(), serializeLockScope(itemLock.getScope()), itemLock.getTimeToLiveSeconds(), toJavaDate(now()));
                aquired = toBoolean(result.get("is_aquired"));
                long lockId = toLong(result.get("lock_id"));
                actualLock = dao.getByIdOrNull(ItemLock.class, lockId);
            }
            itemLocksByItemId.put(actualLock.getItemId(), Optional.of(actualLock));
            return new ItemLockAquireResponseImpl(aquired, actualLock);
        } catch (Exception ex) {
            throw new CmException(ex, "error acquiring lock = %s", itemLock);
        }
    }

    @Override
    public List<ItemLock> getAllLocks() {
        logger.debug("get all locks");
        return dao.selectAll().from(ItemLock.class).asList();
    }

    @Override
    public void removeAllLocks() {
        logger.debug("remove all locks");
        jdbcTemplate.update("DELETE FROM \"_Lock\"");
        itemLocksByItemId.invalidateAll();
    }

    @Override
    public void removeLock(ItemLock lock) {
        logger.debug("remove lock = {}", lock);
        jdbcTemplate.update("DELETE FROM \"_Lock\" WHERE \"ItemId\" = ?", lock.getItemId());
        itemLocksByItemId.invalidate(lock.getItemId());
    }

    @Nullable
    @Override
    public ItemLock getLockByItemIdOrNull(String itemId) {
        return itemLocksByItemId.get(itemId, () -> Optional.ofNullable(doGetLockByItemIdOrNull(itemId))).orElse(null);
    }

//    @Nullable
//    private ItemLock doGetLockByItemIdOrNull(String itemId) {
//        ItemLock itemLock = doGetLockByItemId(itemId);
//        return itemLock == null ? null : new ItemLockHolder(itemLock, itemLock.getLastActiveDate());
//    }
    @Nullable
    private ItemLock doGetLockByItemIdOrNull(String itemId) {
        return dao.selectAll().from(ItemLock.class).where("ItemId", EQ, itemId).getOneOrNull();
    }

//    private class ItemLockHolder {
//
//        private final ItemLock itemLock;
//        private final ZonedDateTime lastPersistedDate;
//
//        public ItemLockHolder(ItemLock itemLock, ZonedDateTime lastPersistedDate) {
//            this.itemLock = checkNotNull(itemLock);
//            this.lastPersistedDate = checkNotNull(lastPersistedDate);
//        }
//
//        public ItemLock getItemLock() {
//            return itemLock;
//        }
//
//        public ZonedDateTime getLastPersistedDate() {
//            return lastPersistedDate;
//        }
//
//        public boolean isExpired() {
//            return itemLock.getLastActiveDate().plusSeconds(itemLock.getTimeToLiveSeconds()).isBefore(now());
//        }
//
//    }
    private static class ItemLockAquireResponseImpl implements ItemLockAquireResponse {

        private final boolean isAquired;
        private final ItemLock itemLock;

        public ItemLockAquireResponseImpl(boolean isAquired, ItemLock itemLock) {
            this.isAquired = isAquired;
            this.itemLock = checkNotNull(itemLock);
        }

        @Override
        public boolean isAquired() {
            return isAquired;
        }

        @Override
        public ItemLock getLock() {
            return itemLock;
        }
    }

}
