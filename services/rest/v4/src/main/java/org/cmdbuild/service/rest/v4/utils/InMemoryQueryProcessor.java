/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.lang.Math.toIntExact;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.dao.utils.FulltextFilterProcessor;
import org.cmdbuild.dao.utils.SorterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.data.filter.FilterType.ATTRIBUTE;
import static org.cmdbuild.data.filter.FilterType.FULLTEXT;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltZeroToNull;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;

public class InMemoryQueryProcessor<T> {

    private final DaoQueryOptions query;
    private final Function<T, Map<String, Object>> standardSerializer, detailedSerializer;
    private final boolean detailed;
    private final CmdbFilter filter;
    private final CmdbSorter sorter;

    private InMemoryQueryProcessor(InMemoryQueryProcessorBuilder<T> builder) {
        this.query = checkNotNull(builder.query);
        this.detailed = firstNotNull(builder.detailed, false);
        this.standardSerializer = checkNotNull(builder.standardSerializer);
        this.detailedSerializer = firstNotNull(builder.detailedSerializer, standardSerializer);
        this.filter = query.getFilter();
        this.sorter = query.getSorter();
    }

    /**
     * @param data <dl><dt>list<dd> of <code>objects</code>
     * @return paged data, filtered and sorted
     */
    public Object toResponse(List<T> data) {
        boolean processDetailedList = detailed || !filter.isNoop() || !sorter.isNoop();
        List<Pair<Map<String, Object>, Map<String, Object>>> standardDetailedList = data.stream().map(r -> {
            Map<String, Object> standardRecord = standardSerializer.apply(r), detailedRecord;
            if (standardSerializer == detailedSerializer || !processDetailedList) {
                detailedRecord = standardRecord;
            } else {
                detailedRecord = detailedSerializer.apply(r);
            }

            return Pair.of(standardRecord, detailedRecord);
        }).collect(toImmutableList());

        standardDetailedList = applyFilter(standardDetailedList);

        standardDetailedList = applySorter(standardDetailedList);

        int offset = toIntExact(query.getOffset());

        Map meta;
        if (query.hasPositionOf()) {
            Map positionMeta;
            List<Pair<Map<String, Object>, Map<String, Object>>> list = standardDetailedList;
            Integer positionInTable = ltZeroToNull(IntStream.range(0, standardDetailedList.size())
                    .filter((i) -> equal(toStringOrEmpty(list.get(i).getRight().get("_id")), Long.toString(query.getPositionOf())))
                    .findFirst().orElse(-1));

            if (positionInTable != null) {
                int positionInPage = positionInTable % toIntExact(query.getLimit()),
                        pageOffset = positionInTable - positionInPage;
                if (query.getGoToPage()) {
                    offset = pageOffset;
                }
                positionMeta = map("found", true,
                        "positionInPage", positionInPage,
                        "positionInTable", positionInTable,
                        "pageOffset", pageOffset);
            } else {
                positionMeta = map("found", false);
            }
            meta = map("positions", map(query.getPositionOf(), positionMeta), START, offset, LIMIT, query.getLimit());
        } else {
            meta = emptyMap();
        }

        PagedElements<Map<String, Object>> paged = paged(standardDetailedList.stream().map(detailed ? Pair::getRight : Pair::getLeft).collect(toImmutableList()), (long) offset, query.getLimit());

        return response(paged.elements(), paged.totalSize(), meta);
    }

    private List<Pair<Map<String, Object>, Map<String, Object>>> applyFilter(List<Pair<Map<String, Object>, Map<String, Object>>> standardDetailedList) {
        if (filter.isNoop()) {
            return standardDetailedList;
        } else {
            filter.checkHasOnlySupportedFilterTypes(ATTRIBUTE, FULLTEXT);
            if (filter.hasAttributeFilter()) {
                standardDetailedList = AttributeFilterProcessor.<Pair<Map<String, Object>, Map<String, Object>>>builder()
                        .withKeyToValueFunction((k, r) -> r.getRight().get(k))
                        .withFilter(filter.getAttributeFilter())
                        .filter(standardDetailedList);
            }
            if (filter.hasFulltextFilter()) {
                standardDetailedList = FulltextFilterProcessor.<Pair<Map<String, Object>, Map<String, Object>>>build(filter.getFulltextFilter())
                        .withKeyFunction(r -> r.getRight().keySet())
                        .withKeyToValueFunction((k, r) -> r.getRight().get(k))
                        .filter(standardDetailedList);
            }
            return standardDetailedList;
        }
    }

    private List<Pair<Map<String, Object>, Map<String, Object>>> applySorter(List<Pair<Map<String, Object>, Map<String, Object>>> standardDetailedList) {
        if (sorter.isNoop()) {
            return standardDetailedList;
        } else {
            return SorterProcessor.sorted(standardDetailedList, sorter, (k, r) -> (Comparable) r.getRight().get(k));
        }
    }

    public DaoQueryOptions getQuery() {
        return query;
    }

    public Function<T, Map<String, Object>> getStandardSerializer() {
        return standardSerializer;
    }

    public Function<T, Map<String, Object>> getDetailedSerializer() {
        return detailedSerializer;
    }

    public boolean getDetailed() {
        return detailed;
    }

    public static <T> InMemoryQueryProcessorBuilder<T> builder() {
        return new InMemoryQueryProcessorBuilder<>();
    }

    public static <T> InMemoryQueryProcessorBuilder<T> copyOf(InMemoryQueryProcessor<T> source) {
        return new InMemoryQueryProcessorBuilder<T>()
                .withQuery(source.getQuery())
                .withStandardSerializer(source.getStandardSerializer())
                .withDetailedSerializer(source.getDetailedSerializer())
                .withDetailed(source.getDetailed());
    }

    public static <T> Object toResponse(List<T> data, DaoQueryOptions query, boolean detailed, Function<T, Map<String, Object>> standardSerializer, Function<T, Map<String, Object>> detailedSerializer) {
        return InMemoryQueryProcessor.<T>builder().withQuery(query).withStandardSerializer(standardSerializer).withDetailedSerializer(detailedSerializer).withDetailed(detailed).build().toResponse(data);
    }

    public static class InMemoryQueryProcessorBuilder<T> implements Builder<InMemoryQueryProcessor<T>, InMemoryQueryProcessorBuilder<T>> {

        private DaoQueryOptions query;
        private Function<T, Map<String, Object>> standardSerializer;
        private Function<T, Map<String, Object>> detailedSerializer;
        private Boolean detailed;

        public InMemoryQueryProcessorBuilder<T> withQuery(DaoQueryOptions query) {
            this.query = query;
            return this;
        }

        public InMemoryQueryProcessorBuilder<T> withStandardSerializer(Function<T, Map<String, Object>> standardSerializer) {
            this.standardSerializer = standardSerializer;
            return this;
        }

        public InMemoryQueryProcessorBuilder<T> withDetailedSerializer(Function<T, Map<String, Object>> detailedSerializer) {
            this.detailedSerializer = detailedSerializer;
            return this;
        }

        public InMemoryQueryProcessorBuilder<T> withDetailed(Boolean detailed) {
            this.detailed = detailed;
            return this;
        }

        @Override
        public InMemoryQueryProcessor<T> build() {
            return new InMemoryQueryProcessor<>(this);
        }

    }
}
