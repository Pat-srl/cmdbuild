package org.cmdbuild.cardfilter;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.isEmpty;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.cardfilter.CardFilterConst.SESSION_REPO;
import static org.cmdbuild.utils.json.CmJsonUtils.collectionType;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component(SESSION_REPO)
public class SessionUserCardFilterRepository implements UserCardFilterRepository {

    private final static String SESSION_FILTERS_KEY = "org.cmdbuild.SESSION_FILTERS";

    private final Random random = new Random();

    private final Provider<SessionService> sessionService;//TODO improve this
    private final OperationUserSupplier user;

    public SessionUserCardFilterRepository(Provider<SessionService> sessionLogic, OperationUserSupplier user) {
        this.sessionService = checkNotNull(sessionLogic);
        this.user = checkNotNull(user);
    }

    @Override
    public StoredFilter create(StoredFilter filter) {
        Long id = newId();
        filter = StoredFilterImpl.copyOf(filter).withId(id).build();
        setCurrentSessionFilters(list(getCurrentSessionFilters()).with(filter));
        return filter;
    }

    @Override
    @Nullable
    public StoredFilter readOrNull(Long filterId) {
        checkNotNull(filterId);
        return getCurrentSessionFilters().stream().filter((s) -> equal(s.getId(), filterId)).findAny().orElse(null);
    }

    @Override
    public StoredFilter update(StoredFilter filter) {
        filter = StoredFilterImpl.copyOf(filter).build();
        long id = filter.getId();
        setCurrentSessionFilters(list(getCurrentSessionFilters())
                .without((f) -> f.getId() == id)
                .with(filter));
        return filter;

    }

    @Override
    public void delete(long filterId) {
        setCurrentSessionFilters(list(getCurrentSessionFilters()).without((f) -> Objects.equals(f.getId(), filterId)));
    }

    @Override
    public List<StoredFilter> readNonSharedFilters(String className, long userId) {
        checkNotBlank(className);
        checkArgument(userId == user.getUser().getLoginUser().getId());
        return getCurrentSessionFilters().stream().filter((f) -> equal(f.getOwnerName(), className)).collect(toList());
    }

    private long newId() {
        return random.nextLong();
    }

    private List<StoredFilter> getCurrentSessionFilters() {
        String filters = sessionService.get().getCurrentSessionDataSafe().get(SESSION_FILTERS_KEY);
        if (isBlank(filters)) {
            return emptyList();
        } else {
            return checkNotNull(fromJson(filters, collectionType(StoredFilter.class)));
        }
    }

    private void setCurrentSessionFilters(List<StoredFilter> filters) {
        if (isEmpty(filters)) {
            sessionService.get().updateCurrentSessionData((m) -> map(m).withoutKey(SESSION_FILTERS_KEY));
        } else {
            String str = toJson(filters);
            sessionService.get().updateCurrentSessionData((m) -> map(m).with(SESSION_FILTERS_KEY, str));
        }
    }
}
