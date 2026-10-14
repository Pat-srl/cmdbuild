package org.cmdbuild.cardfilter;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.util.Collection;
import java.util.Optional;

import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Classe;

public interface CardFilterService {

    StoredFilter create(StoredFilter filter);

    @Nullable
    StoredFilter readOrNull(long filterId);

    StoredFilter getSharedFilterById(long filterId);

    StoredFilter update(StoredFilter filter);

    void delete(long filterId);

    List<StoredFilter> readAllForCurrentUser(String className);

    List<StoredFilter> readAllSharedFilters();

    List<StoredFilter> readSharedForCurrentUser(String className);

    List<CardFilterAsDefaultForClass> getDefaultFiltersForRole(long roleId);

    List<CardFilterAsDefaultForClass> getDefaultFiltersForFilter(long filterId);

    void setDefaultFiltersForRole(long roleId, Collection<CardFilterAsDefaultForClass> newFilters);

    List<CardFilterAsDefaultForClass> getAllDefaultFiltersForCurrentUser();

    List<CardFilterAsDefaultForClass> setDefaultFiltersForFilterWithMatchingClass(long filterId, List<CardFilterAsDefaultForClass> filtersUpdate);

    default Optional<StoredFilter> read(StoredFilter filter) {
        StoredFilter res = readOrNull(filter.getId());
        return Optional.ofNullable(res);
    }

    default StoredFilter getById(long filterId) {
        return checkNotNull(readOrNull(filterId), "filter not found for id = %s", filterId);
    }

    default void delete(StoredFilter filter) {
        delete(filter.getId());
    }

    @Nullable
    default StoredFilter getDefaultFilterForCurrentUserAndClassOrNull(Classe classe) {
        return getAllDefaultFiltersForCurrentUser().stream().filter(f -> equal(f.getDefaultForClass(), classe.getName())).collect(toOptional()).map(CardFilterAsDefaultForClass::getFilter).orElse(null);
    }

}
