package org.cmdbuild.cardfilter;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import org.cmdbuild.auth.user.OperationUser;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.auth.role.RolePrivilege.RP_ADMIN_SEARCHFILTERS_MODIFY;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.cardfilter.CardFilterConst.DB_REPO;
import static org.cmdbuild.cardfilter.CardFilterConst.SESSION_REPO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class CardFilterServiceImpl implements CardFilterService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CardFilterRepository dbRepo;
    private final UserCardFilterRepository sessionRepo;
    private final OperationUserSupplier userStore;

    public CardFilterServiceImpl(@Qualifier(DB_REPO) CardFilterRepository store, @Qualifier(SESSION_REPO) UserCardFilterRepository sessionRepo, OperationUserSupplier userStore) { //TODO use also temp filter repo
        this.dbRepo = checkNotNull(store);
        this.sessionRepo = checkNotNull(sessionRepo);
        this.userStore = checkNotNull(userStore);
    }

    @Override
    public StoredFilter create(StoredFilter filter) {
        checkCanWrite(filter);
        return dbRepo.create(filter);
    }

    @Override
    public StoredFilter getSharedFilterById(long filterId) {
        StoredFilter filter = dbRepo.getOne(filterId);
        checkArgument(filter.isShared(), "invalid filter for id = %s: filter is not shared", filterId);
        return filter;
    }

    @Override
    public StoredFilter readOrNull(long filterId) {
        return firstNonNull(dbRepo.readOrNull(filterId), sessionRepo.readOrNull(filterId), null);
    }

    @Override
    public StoredFilter update(StoredFilter filter) {
        checkCanWrite(filter);
        return dbRepo.update(filter);
    }

    @Override
    public void delete(long filterId) {
        checkCanWrite(dbRepo.getOne(filterId));
        dbRepo.delete(filterId);
    }

    @Override
    public List<StoredFilter> readAllForCurrentUser(String className) {
        return list(readSharedForCurrentUser(className)).accept((l) -> {
            if (userStore.getUser().hasId()) {
                l.addAll(dbRepo.readNonSharedFilters(className, userStore.getUser().getId()));
            }
        }).stream().sorted(Ordering.natural().onResultOf(StoredFilter::getName)).collect(toList());
    }

    @Override
    public List<StoredFilter> readSharedForCurrentUser(String className) {
        OperationUser operationUser = userStore.getUser();
        return dbRepo.readSharedFilters(className).stream().filter(operationUser::hasReadAccess).collect(toList());
    }

    @Override
    public List<StoredFilter> readAllSharedFilters() {
        return dbRepo.getAllSharedFilters();
    }

    @Override
    public List<CardFilterAsDefaultForClass> getDefaultFiltersForRole(long roleId) {
        return dbRepo.getFiltersForRole(roleId);
    }

    @Override
    public List<CardFilterAsDefaultForClass> getDefaultFiltersForFilter(long filterId) {
        return dbRepo.getDefaultFiltersForFilter(filterId);
    }

    @Override
    public void setDefaultFiltersForRole(long roleId, Collection<CardFilterAsDefaultForClass> newFilters) {
        dbRepo.setFiltersForRole(roleId, newFilters);
    }

    @Override
    public List<CardFilterAsDefaultForClass> setDefaultFiltersForFilterWithMatchingClass(long filterId, List<CardFilterAsDefaultForClass> newFilters) {
        dbRepo.setDefaultFiltersForFilterWithMatchingClass(filterId, newFilters);
        return dbRepo.getDefaultFiltersForFilter(filterId);
    }

    @Override
    public List<CardFilterAsDefaultForClass> getAllDefaultFiltersForCurrentUser() {
        OperationUser user = userStore.getUser();
        if (user.hasDefaultGroupId()) {
            return dbRepo.getFiltersForRole(user.getDefaultGroupId());
        } else {
            return emptyList();
        }
    }

    private void checkCanWrite(StoredFilter filter) {
        userStore.checkPrivileges((u, p) -> filter.isShared() ? p.hasPrivileges(RP_ADMIN_SEARCHFILTERS_MODIFY) : (filter.isNew() || equal(filter.getUserId(), u.getId())), "access denied for filter = %s", filter);
    }
}
