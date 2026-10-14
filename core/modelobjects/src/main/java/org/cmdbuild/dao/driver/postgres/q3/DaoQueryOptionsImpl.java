/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.q3;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import static java.util.Collections.emptySet;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FORMULA;
import org.cmdbuild.dao.utils.CmFilterUtils;
import static org.cmdbuild.dao.utils.CmFilterUtils.noopFilter;
import org.cmdbuild.dao.utils.CmSorterUtils;
import static org.cmdbuild.dao.utils.CmSorterUtils.noopSorter;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElementDirection;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public class DaoQueryOptionsImpl implements DaoQueryOptions {

    private final Set<String> attrs;
    private final Long offset, limit, positionOfCard;
    private final CmdbSorter sorter;
    private final CmdbFilter filter;
    private final Boolean goToPage, onlyGridAttrs;

    private DaoQueryOptionsImpl(DaoQueryOptionsImplBuilder builder) {
        this.attrs = ImmutableSet.copyOf(firstNonNull(builder.attrs, emptySet()));
        this.onlyGridAttrs = firstNonNull(builder.onlyGridAttrs, false);
        this.offset = firstNonNull(builder.offset, 0l);
        this.limit = builder.limit;
        this.sorter = firstNonNull(builder.sorter, noopSorter());
        this.filter = firstNonNull(builder.filter, noopFilter());
        this.positionOfCard = builder.positionOfCard;
        if (positionOfCard != null) {
            this.goToPage = checkNotNull(builder.goToPage);
            checkArgument(isNotNullAndGtZero(limit), "must set valid 'limit' along with 'positionOf'");
        } else {
            goToPage = null;
        }
    }

    @Override
    public Set<String> getAttrs() {
        return attrs;
    }

    @Override
    public boolean getOnlyGridAttrs() {
        return onlyGridAttrs;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    @Nullable
    public Long getLimit() {
        return limit;
    }

    @Override
    public CmdbSorter getSorter() {
        return sorter;
    }

    @Override
    public CmdbFilter getFilter() {
        return filter;
    }

    @Override
    public boolean hasPositionOf() {
        return positionOfCard != null;
    }

    @Override
    public long getPositionOf() {
        return positionOfCard;
    }

    @Override
    public boolean getGoToPage() {
        return goToPage;
    }

    @Override
    public String toString() {
        return "QueryOptions{" + "offset=" + offset + ", limit=" + limit + ", sorter=" + sorter + ", filter=" + filter + '}';
    }

    @Override
    public DaoQueryOptions mapAttrNames(Map<String, String> map) {
        return copyOf(this)
                .withSorter(sorter.mapAttributeNames(map))
                .withFilter(filter.mapNames(map))
                .withAttrs(attrs.stream().map(a -> map.getOrDefault(a, a)).collect(toImmutableList()))
                .build();
    }

    @Override
    public DaoQueryOptions withOffset(@Nullable Long offset) {
        return copyOf(this).withOffset(offset).build();
    }

    @Override
    public DaoQueryOptions withoutPaging() {
        return copyOf(this).withOffset(null).withLimit(null).build();
    }

    @Override
    public DaoQueryOptions expandFulltextFilter(Classe classe) {
        if (filter.hasFulltextFilter()) {
            if (filter.hasAttachmentFilter()) {
                return copyOf(this).withFilter(CmdbFilterImpl.copyOf(filter.expandFulltextFilter(list(classe.getActiveUiAttributes()).map(Attribute::getName))).withAttachmentFilter(filter.getAttachmentFilter()).build()).build();
            } else {
                return copyOf(this).withFilter(filter.expandFulltextFilter(list(classe.getActiveUiAttributes().stream().filter(a -> !a.isOfType(FORMULA)).toList()).map(Attribute::getName))).build();
            }
        } else {
            return this;
        }
    }

    public static DaoQueryOptions emptyOptions() {
        return builder().build();
    }

    public static DaoQueryOptionsImplBuilder builder() {
        return new DaoQueryOptionsImplBuilder();
    }

    public static DaoQueryOptionsImpl build(CmdbFilter filter) {
        return builder().withFilter(filter).build();
    }

    public static DaoQueryOptionsImplBuilder copyOf(DaoQueryOptions source) {
        return new DaoQueryOptionsImplBuilder()
                .withOffset(source.getOffset())
                .withLimit(source.getLimit())
                .withSorter(source.getSorter())
                .withFilter(source.getFilter())
                .withAttrs(source.getAttrs())
                .withOnlyGridAttrs(source.getOnlyGridAttrs())
                .accept(b -> {
                    if (source.hasPositionOf()) {
                        b.withPositionOf(source.getPositionOf(), source.getGoToPage());
                    }
                });
    }

    public static class DaoQueryOptionsImplBuilder implements Builder<DaoQueryOptionsImpl, DaoQueryOptionsImplBuilder> {

        private Collection<String> attrs;
        private Long offset, positionOfCard, limit;
        private CmdbSorter sorter;
        private CmdbFilter filter;
        private Boolean goToPage, onlyGridAttrs;

        public DaoQueryOptionsImplBuilder withPaging(@Nullable Long offset, @Nullable Long limit) {
            return this.withOffset(offset).withLimit(limit);
        }

        public DaoQueryOptionsImplBuilder withPaging(@Nullable Integer offset, @Nullable Integer limit) {
            return this.withOffset(toLongOrNull(offset)).withLimit(toLongOrNull(limit));
        }

        public DaoQueryOptionsImplBuilder withOffset(@Nullable Long offset) {
            this.offset = offset;
            return this;
        }

        public DaoQueryOptionsImplBuilder withLimit(@Nullable Long limit) {
            this.limit = limit;
            return this;
        }

        public DaoQueryOptionsImplBuilder withSorter(CmdbSorter sorter) {
            this.sorter = sorter;
            return this;
        }

        public DaoQueryOptionsImplBuilder withAttrs(String... attrs) {
            return withAttrs(list(attrs));
        }

        public DaoQueryOptionsImplBuilder withAttrs(@Nullable Collection<String> attrs) {
            this.attrs = attrs;
            return this;
        }

        public DaoQueryOptionsImplBuilder withOnlyGridAttrs(@Nullable Boolean onlyGridAttrs) {
            this.onlyGridAttrs = onlyGridAttrs;
            return this;
        }

        public DaoQueryOptionsImplBuilder withSorter(@Nullable String sorter) {
            return this.withSorter(CmSorterUtils.parseSorter(sorter));
        }

        public DaoQueryOptionsImplBuilder withFilter(CmdbFilter filter) {
            this.filter = filter;
            return this;
        }

        public DaoQueryOptionsImplBuilder and(CmdbFilter otherFilter) {
            return this.withFilter(filter == null ? otherFilter : filter.and(otherFilter));
        }

        public DaoQueryOptionsImplBuilder withFilter(@Nullable String filter) {
            return this.withFilter(CmFilterUtils.parseFilter(filter));
        }

        public DaoQueryOptionsImplBuilder withPositionOf(@Nullable Long positionOfCard, @Nullable Boolean goToPage) {
            this.positionOfCard = positionOfCard;
            this.goToPage = goToPage;
            return this;
        }

        @Override
        public DaoQueryOptionsImpl build() {
            return new DaoQueryOptionsImpl(this);
        }

        public DaoQueryOptionsImplBuilder orderBy(String attr, SorterElementDirection dir) {
            sorter = firstNotNull(sorter, noopSorter()).thenSortBy(attr, dir);
            return this;
        }

    }
}
