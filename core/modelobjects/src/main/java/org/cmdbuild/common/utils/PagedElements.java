package org.cmdbuild.common.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import com.google.common.collect.Streams;
import static java.util.Collections.emptyList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;

public class PagedElements<T> implements Iterable<T> {

    private final List<T> elements;
    private final long totalSize;
    private final PositionOf positionOf;

    public PagedElements(Iterable<T> elements, long totalSize, @Nullable PositionOf positionOf) {
        this.elements = CmCollectionUtils.toList(elements);
        this.totalSize = totalSize;
        this.positionOf = positionOf;
    }

    public PagedElements(Iterable<T> elements, long totalSize) {
        this(elements, totalSize, null);
    }

    public PagedElements(Iterable<T> elements) {
        this(elements, Iterables.size(elements), null);
    }

    public PagedElements<T> withTotal(long newTotal) {
        return new PagedElements<>(elements, newTotal, positionOf);
    }

    public PagedElements<T> withPositionOf(PositionOf positionOf) {
        return new PagedElements<>(elements, totalSize, positionOf);
    }

    public <E> PagedElements<E> map(Function<T, E> mapper) {
        return new PagedElements<>(elements.stream().map(mapper::apply).collect(toList()), totalSize, positionOf);
    }

    @Override
    public Iterator<T> iterator() {
        return elements.iterator();
    }

    public List<T> elements() {
        return elements;
    }

    public long totalSize() {
        return totalSize;
    }

    public boolean isEmpty() {
        return Iterables.isEmpty(elements);
    }

    public boolean hasPositionOf() {
        return positionOf != null;
    }

    public PositionOf getPositionOf() {
        return checkNotNull(positionOf, "position of info is not available for this paged elements");
    }

    @Override
    public String toString() {
        return "PagedElements{" + "elements=" + (elements.size() < 10 ? elements : (elements.size() + " elements")) + ", totalSize=" + totalSize + ", positionOf=" + positionOf + '}';
    }

    public Stream<T> stream() {
        return elements.stream();
    }

    public int size() {
        return elements.size();
    }

    public static <T> PagedElements<T> empty() {
        final Iterable<T> emptyList = emptyList();
        return new PagedElements<>(emptyList, 0);
    }

    public static <T> PagedElements<T> paged(Iterable<T> elements, long total) {
        return new PagedElements<>(elements, total);
    }

    public static <T> PagedElements<T> paged(Stream<T> elements) {
        return new PagedElements<>(elements.collect(toList()));
    }

    public static <T> PagedElements<T> paged(Iterable<T> elements) {
        return new PagedElements<>(elements);
    }

    public static <T> PagedElements<T> paged(Iterable<T> elements, PagingInfo options) {
        return paged(elements, options.getOffset(), options.getLimit());
    }

    public static <T, E> PagedElements<E> paged(Iterable<T> elements, Function<T, E> fun, PagingInfo options) {
        return paged(elements, fun, options.getOffset(), options.getLimit());
    }

    public static <T> PagedElements<T> paged(Iterable<T> elements, @Nullable Integer offset, @Nullable Integer limit) {
        return paged(elements, identity(), offset, limit);
    }

    public static <T> PagedElements<T> paged(Iterable<T> elements, @Nullable Long offset, @Nullable Long limit) {
        return paged(elements, identity(), offset, limit);
    }

    public static <T, E> PagedElements<E> paged(Iterable<T> elements, Function<T, E> fun, @Nullable Integer offset, @Nullable Integer limit) {
        return paged(elements, fun, convert(offset, Long.class), convert(limit, Long.class));
    }

    public static <T, E> PagedElements<E> paged(Iterable<T> elements, Function<T, E> fun, @Nullable Long offset, @Nullable Long limit) {
        int total = Iterables.size(elements);
        return new PagedElements<>(Streams.stream(elements).skip(firstNonNull(offset, 0l)).limit(firstNonNull(ltEqZeroToNull(limit), Long.MAX_VALUE)).map(fun).collect(toList()), total);
    }

    public static boolean hasOffset(@Nullable Number offset) {
        return offset != null && offset.longValue() > 0;
    }

    public static boolean hasLimit(@Nullable Number limit) {
        return limit != null && limit.longValue() > 0;
    }

    public static boolean isPaged(@Nullable Number offset, @Nullable Number limit) {
        return hasOffset(offset) || hasLimit(limit);
    }

}
