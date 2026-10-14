package org.cmdbuild.cardfilter;

import java.util.List;
import jakarta.annotation.Nullable;

public interface SharedCardFilterRepository {

    List<StoredFilter> readSharedFilters(@Nullable String className);

    List<StoredFilter> getAllSharedFilters();
}
