package org.cmdbuild.auth.grant;

import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.eventbus.EventBusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Comparator.comparing;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.grant.GrantConstants.GRANT_ATTR_ROLE_ID;
import static org.cmdbuild.auth.grant.GrantMode.GM_NONE;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;

@Component
@Primary
public class GrantDataRepositoryImpl implements GrantDataRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBusService grantEventBusService;

    private final DaoService dao;
    private final CmCache<List<GrantData>> grantDataByRoleId;

    public GrantDataRepositoryImpl(DaoService dao, CacheService cacheService, EventBusService grantEventBusService1) {
        this.dao = checkNotNull(dao);
        this.grantEventBusService = checkNotNull(grantEventBusService1);
        this.grantDataByRoleId = cacheService.newCache("grant_data_by_role_id");
    }

    private void invalidateCache() {
        grantDataByRoleId.invalidateAll();
        grantEventBusService.getGrantEventBus().post(GrantDataUpdatedEvent.INSTANCE);
    }

    @Override
    public List<GrantData> getGrantsForRole(long roleId) {
        return grantDataByRoleId.get(Long.toString(roleId), () -> doGetGrantsForRole(roleId));
    }

    private List<GrantData> doGetGrantsForRole(long roleId) {
        return sortGrantsByKey(dao.selectAll().from(GrantData.class)
                .where(GRANT_ATTR_ROLE_ID, EQ, roleId)
                .asList());
    }

    @Override
    public List<GrantData> getGrantsForTypeAndRole(PrivilegedObjectType type, long groupId) {
        checkNotNull(type);
        return getGrantsForRole(groupId).stream().filter(equal(GrantData::getType, type)).collect(toList());
    }

    @Override
    public List<GrantData> setGrantsForRole(long roleId, Collection<GrantData> grants) {
        return doUpdateGrantsForRole(roleId, grants, true);
    }

    @Override
    public List<GrantData> updateGrantsForRole(long roleId, Collection<GrantData> grants) {
        return doUpdateGrantsForRole(roleId, grants, false);
    }

    private List<GrantData> doUpdateGrantsForRole(long roleId, Collection<GrantData> grants, boolean deleteMissing) {
        grants = list(grants).map(g -> GrantDataImpl.copyOf(g).withRoleId(roleId).build());

        Map<String, GrantData> currentGrants = map(getGrantsForRole(roleId), GrantDataRepositoryImpl::keyForGrant);
        Map<String, GrantData> newGrants = map(grants, GrantDataRepositoryImpl::keyForGrant);

        Collection<GrantData> toDelete = list();
        if (deleteMissing) {
            map(currentGrants).filterKeys(not(newGrants.keySet()::contains)).values().forEach(toDelete::add);
        }
        Collection<GrantData> toCreate = list(map(newGrants).filterKeys(not(currentGrants.keySet()::contains)).values());

        List<GrantData> toUpdate = currentGrants.keySet().stream().filter(newGrants.keySet()::contains).map(key -> {
            GrantData currentGrant = checkNotNull(currentGrants.get(key));
            GrantData newGrant = checkNotNull(newGrants.get(key));

            return GrantDataImpl.copyOf(newGrant).withId(currentGrant.getId()).build();
        }).collect(toList());

        toUpdate.stream().filter(g -> g.isMode(GM_NONE)).forEach(toDelete::add);
        toUpdate.removeIf(g -> g.isMode(GM_NONE));
        toCreate.removeIf(g -> g.isMode(GM_NONE));

        toDelete.forEach(dao::delete);
        List<GrantData> res = list(toCreate.stream().map(dao::create))
                .with(toUpdate.stream().map(dao::update));

        invalidateCache();

        return sortGrantsByKey(res);
    }

    private static String keyForGrant(GrantData grantData) {
        return key(grantData.getType(), grantData.getObjectIdOrClassNameOrCode());
    }

    private static List<GrantData> sortGrantsByKey(List<GrantData> grants) {
        return list(grants).sorted(comparing(GrantDataRepositoryImpl::keyForGrant)).immutableCopy();
    }
}
