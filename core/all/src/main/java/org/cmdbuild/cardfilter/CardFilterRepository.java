package org.cmdbuild.cardfilter;

import java.util.Collection;
import java.util.List;

public interface CardFilterRepository extends SharedCardFilterRepository, UserCardFilterRepository {

    List<CardFilterAsDefaultForClass> getFiltersForRole(long roleId);

    List<CardFilterAsDefaultForClass> getDefaultFiltersForFilter(long filterId);

    void setFiltersForRole(long roleId, Collection<CardFilterAsDefaultForClass> filters);

    void setDefaultFiltersForFilterWithMatchingClass(long filterId, Collection<CardFilterAsDefaultForClass> newFilters);

}
