package org.cmdbuild.cardfilter;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;

public interface UserCardFilterRepository {

    @Nullable
    StoredFilter readOrNull(Long filterId);

    default StoredFilter getOne(long filterId) {
        return checkNotNull(readOrNull(filterId), "stored filter not found for id = %s", filterId);
    }

    List<StoredFilter> readNonSharedFilters(String className, long userId);

//	List<CardFilter> readNonSharedFilters(String className);
    StoredFilter create(StoredFilter filter);

    StoredFilter update(StoredFilter filter);

    void delete(long filterId);
}
