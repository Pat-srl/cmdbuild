package org.cmdbuild.dao.postgres.q3.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.postgres.q3.SuperclassQueryBuilderService.SuperclassSubclassQuery;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.data.filter.beans.CmdbFilterImpl.noopFilter;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class SuperclassSubclassQueryImpl implements SuperclassSubclassQuery {

    private final Classe subclass;
    private final CmdbFilter filter;
    private final List<MatchFilter> matchFilters;
    private final List<WhereArg> where;

    public SuperclassSubclassQueryImpl(Classe subclass) {
        this(subclass, null, emptyList());
    }

    public SuperclassSubclassQueryImpl(Classe subclass, @Nullable CmdbFilter filter, List<MatchFilter> matchFilters) {
        this(subclass, filter, matchFilters, emptyList());
    }

    public SuperclassSubclassQueryImpl(Classe subclass, @Nullable CmdbFilter filter, List<MatchFilter> matchFilters, List<WhereArg> where) {
        this.subclass = checkNotNull(subclass);
        this.filter = firstNotNull(filter, noopFilter());
        this.matchFilters = ImmutableList.copyOf(matchFilters);
        this.where = ImmutableList.copyOf(where);
    }

    @Override
    public Classe getSubclass() {
        return subclass;
    }

    @Override
    public CmdbFilter getFilter() {
        return filter;
    }

    @Override
    public List<MatchFilter> getMatchFilters() {
        return matchFilters;
    }

    @Override
    public List<WhereArg> getWhereArgs() {
        return where;
    }

}
