/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Spliterators;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.function.Function.identity;
import java.util.function.Predicate;
import static java.util.function.Predicate.not;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toUnmodifiableList;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.stream.Streams;
import static org.cmdbuild.utils.lang.CmCollectionUtils.RenameMode.RM_KEEP_ORIGINAL;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

public class CmCollectionUtils {

    public enum RenameMode {
        RM_KEEP_ORIGINAL, RM_RENAME_ALL
    }

    public static <T> List<T> renameDuplicates(List<T> list, Function<T, String> nameFunction, BiFunction<T, String, T> renamerFunction) {
        return renameDuplicates(RM_KEEP_ORIGINAL, list, nameFunction, renamerFunction);
    }

    public static <T> List<T> renameDuplicates(Collection<String> otherNames, List<T> list, Function<T, String> nameFunction, BiFunction<T, String, T> renamerFunction) {
        return renameDuplicates(RM_KEEP_ORIGINAL, otherNames, list, nameFunction, renamerFunction);
    }

    public static <T> List<T> renameDuplicates(RenameMode renameMode, List<T> list, Function<T, String> nameFunction, BiFunction<T, String, T> renamerFunction) {
        return renameDuplicates(renameMode, emptySet(), list, nameFunction, renamerFunction);
    }

    public static <T> List<T> renameDuplicates(RenameMode renameMode, Collection<String> otherNames, List<T> list, Function<T, String> nameFunction, BiFunction<T, String, T> renamerFunction) {
        Set<String> duplicates = list(list).map(nameFunction).with(otherNames).duplicates();
        if (!duplicates.isEmpty()) {
            List<T> thisList = list(list);
            duplicates.forEach(n -> {
                AtomicInteger index = new AtomicInteger(1);
                boolean skipFirst = !otherNames.contains(n) && Objects.equals(renameMode, RM_KEEP_ORIGINAL);
                for (int i = 0; i < thisList.size(); i++) {
                    if (Objects.equals(n, nameFunction.apply(thisList.get(i)))) {
                        if (skipFirst) {
                            skipFirst = false;
                        } else {
                            String newName = isBlank(FilenameUtils.getExtension(n)) ? format("%s_%s", n, index.getAndIncrement()) : format("%s_%s.%s", FilenameUtils.getBaseName(n), index.getAndIncrement(), FilenameUtils.getExtension(n));
                            thisList.set(i, renamerFunction.apply(thisList.get(i), newName));
                        }
                    }
                }
            });
            return renameDuplicates(renameMode, otherNames, thisList, nameFunction, renamerFunction);
        } else {
            return list;
        }
    }

    public static boolean isEmpty(Iterable<?> iterable) {
        if (iterable instanceof Collection) {
            return ((Collection<?>) iterable).isEmpty();
        }
        return !iterable.iterator().hasNext();
    }

    public static <T> java.util.function.Predicate<T> distinctOn(Function<T, Object> keyExtractor) {
        Map<Object, Boolean> map = new ConcurrentHashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    public static <T> Stream<T> stream(Object obj) {
        checkNotNull(obj);
        if (obj instanceof Iterable iterable) {
            return stream(iterable);
        } else if (obj instanceof Iterator iterator) {
            return stream(iterator);
        } else if (obj.getClass().isArray()) {
            return convert(obj, List.class).stream();//TODO improve performance
        } else {
            throw unsupported("unsupported conversion of value = %s (%s) to stream", obj, getClassOfNullable(obj));
        }
    }

    public static <T extends Object> Stream<T> stream(Iterable<T> iterable) {
        return (iterable instanceof Collection collection) ? collection.stream() : StreamSupport.stream(iterable.spliterator(), false);
    }

    public static <T extends Object> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
    }

    public static <A, B, X> List<X> zip(Iterable<A> first, Iterable<B> second, BiFunction<A, B, X> function) {
        Iterator<A> one = first.iterator();
        Iterator<B> two = second.iterator();
        return (List) list().accept(l -> {
            while (one.hasNext()) {
                l.add(function.apply(one.next(), two.next()));
            }
            Validate.isTrue(!two.hasNext(), "mismatching iterable size");
        });
    }

    public static <T, A, R> Collector<T, A, R> onlyElementOrNull() {
        return (Collector) Collector.of(CollectorHelper::new, CollectorHelper::add, CollectorHelper::combine, (c) -> c.getOptional().orElse(null), Collector.Characteristics.UNORDERED);
    }

    public static <T, A, R> Collector<T, A, R> onlyElement() {
        return (Collector) Collector.of(CollectorHelper::new, CollectorHelper::add, CollectorHelper::combine, CollectorHelper::getElement, Collector.Characteristics.UNORDERED);
    }

    public static <T, A, R> Collector<T, A, R> onlyElement(String message) {
        return onlyElement(message, new Object[]{});
    }

    public static <T, A, R> Collector<T, A, R> onlyElement(String message, Object... args) {
        return (Collector) Collector.of(CollectorHelper::new, CollectorHelper::add, CollectorHelper::combine, (c) -> checkNotNull(c.getOptional().orElse(null), message, args), Collector.Characteristics.UNORDERED);
    }

    public static <T> T getOnlyElement(Collection<T> collection) {
        return collection.stream().collect(onlyElement());
    }

    public static <T> T getOnlyElement(Collection<T> collection, String message) {
        return collection.stream().collect(onlyElement(message));
    }

    public static <T> T getOnlyElement(Collection<T> collection, String message, Object... args) {
        return collection.stream().collect(onlyElement(message, args));
    }

    public static <T> T getOnlyElementOrNull(Collection<T> collection) {
        return collection.stream().collect(onlyElementOrNull());
    }

    public static <T> Collector<T, ?, Optional<T>> toOptional() {
        return (Collector) Collector.of(CollectorHelper::new, CollectorHelper::add, CollectorHelper::combine, CollectorHelper::getOptional, Collector.Characteristics.UNORDERED);
    }

    public static <T> Optional<T> getOptionalElement(Collection<T> collection) {
        return collection.stream().collect(toOptional());
    }

    public static <T> Collector<T, ?, Stream<T>> toReverse() {
        return Collectors.collectingAndThen(Collectors.toList(), list -> {
            Collections.reverse(list);
            return list.stream();
        });
    }

    public static boolean isNullOrEmpty(@Nullable Collection collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotNullOrEmpty(@Nullable Collection collection) {
        return !isNullOrEmpty(collection);
    }

    public static boolean hasContent(@Nullable Collection collection) {
        return !isNullOrEmpty(collection);
    }

    public static boolean isNullOrEmpty(@Nullable Map map) {
        return map == null || map.isEmpty();
    }

    public static <X, T extends Collection<X>> T nullToEmpty(@Nullable T collection) {
        if (collection == null) {
            return (T) emptyList();//TODO: will break if T is, for example, Set
        } else {
            return collection;
        }
    }

    @Nullable
    public static <X, T extends Collection<X>> T emptyToNull(@Nullable T collection) {
        if (isNullOrEmpty(collection)) {
            return null;
        } else {
            return collection;
        }
    }

    public static <X, T extends Set<X>> T nullToEmpty(T set) {
        if (set == null) {
            return (T) emptySet();
        } else {
            return set;
        }
    }

    /**
     * cast to list; avoid making a copy if possible
     *
     * @param <T>
     * @param iterable
     * @return
     */
    public static <T> List<T> toList(Iterable<T> iterable) {
        return iterable instanceof List list ? list : list(iterable);
    }

    @Nullable
    public static <T> List<T> toListOrNull(@Nullable Iterable<T> iterable) {
        return iterable == null ? null : toList(iterable);
    }

    public static <T> List<T> toListOrEmpty(@Nullable Iterable<T> iterable) {
        return nullToEmpty(toListOrNull(iterable));
    }

    @Nullable
    public static <T> List<T> toListOrNull(@Nullable T[] arr) {
        return arr == null ? null : list(arr);
    }

    public static <T> List<T> toListOrEmpty(@Nullable T[] arr) {
        return nullToEmpty(toListOrNull(arr));
    }

    /**
     * cast to collection; avoid making a copy if possible
     *
     * @param <T>
     * @param iterable
     * @return
     */
    public static <T> Collection<T> toCollection(Iterable<T> iterable) {
        if (iterable instanceof Collection collection) {
            return collection;
        }
        Collection<T> collection = new ArrayList();
        Iterator<T> iterator = iterable.iterator();
        while (iterator.hasNext()) {
            collection.add(iterator.next());
        }
        return collection;
    }

    /**
     * always return a copy
     *
     * @param <T>
     * @param iterable
     * @return
     */
    public static <T> FluentList<T> list(Iterable<T> iterable) {
        return new FluentListImpl<T>().with(iterable);
    }

    public static <T> FluentList<T> listOf(Class<T> classe) {
        return new FluentListImpl<>();
    }

    public static <T> FluentList<T> list(Iterator<T> iterator) {
        return new FluentListImpl<T>().with(iterator);
    }

    public static <T> FluentList<T> list(Stream<T> stream) {
        return new FluentListImpl<T>().accept((l) -> stream.forEach(l::add));
    }

    public static <T> FluentList<T> list(T... items) {
        return new FluentListImpl<T>().with(items);
    }

    public static <T> FluentList<T> list(Enumeration<T> enumeration) {
        return list(Collections.list(enumeration));
    }

    public static <T, O> FluentList<O> list(Iterable<T> iterable, Function<T, O> function) {
        return list(iterable).map(function);
    }

    public static <T> Queue<T> queue() {
        return new ConcurrentLinkedQueue<>();
    }

    public static <T> Queue<T> queue(Iterable<T> iterable) {
        Queue<T> queue = queue();
        queue.addAll(toCollection(iterable));
        return queue;
    }

    public static <T> Queue<T> queue(T... items) {
        return queue(asList(items));
    }

    public static <T> FluentList<T> list() {
        return new FluentListImpl<>();
    }

    public static <T> FluentSet<T> set(Iterable<T> iterable) {
        return new FluentSetImpl<T>().with(iterable);
    }

    public static <T, O> FluentSet<O> set(Iterable<T> iterable, Function<T, O> function) {
        return set(list(iterable, function));
    }

    public static <T> FluentSet<T> duplicates(Iterable<T> iterable) {
        FluentSet<T> uniques = set(), duplicates = set();
        iterable.forEach(e -> {
            if (!uniques.add(e)) {
                duplicates.add(e);
            }
        });
        return duplicates;
    }

    public static <T, O> FluentSet<O> duplicates(Iterable<T> iterable, Function<T, O> function) {
        List<O> list = list(iterable, function);
        return duplicates(list);
    }

    public static <T> FluentSet<T> setFromNullable(@Nullable Iterable<T> iterable) {
        return new FluentSetImpl<T>().withNullable(iterable);
    }

    public static <T> FluentSet<T> set(T... items) {
        return new FluentSetImpl<T>().with(items);
    }

    public static <T> FluentSet<T> set() {
        return new FluentSetImpl<>();
    }

    public static <T> FluentSet<T> setOf(Class<T> classe) {
        return new FluentSetImpl<>();
    }

    public interface FluentList<T> extends List<T> {

        FluentList<T> with(T entry);

        FluentList<T> with(Iterable<T> entries);

        FluentList<T> with(Iterator<T> entries);

        FluentList<T> with(T... items);

        FluentList<T> without(Predicate<T> predicate);

        FluentSet<T> toSet();

        List<T> immutable();

        default FluentList<T> filter(Predicate<T> predicate) {
            return this.withOnly(predicate);
        }

        default <O> FluentList<O> filter(Class<O> type) {
            return this.filter(type::isInstance).map(type::cast);
        }

        default FluentList<T> withOnly(Predicate<T> predicate) {
            return this.without(not(predicate));
        }

        default FluentList<T> with(Stream<T> stream) {
            stream.forEach(this::add);
            return this;
        }

        default <A> FluentList<T> with(Iterable<A> source, Function<A, T> function) {
            Streams.of(source).map(function).forEach(this::add);
            return this;
        }

        default FluentSet<T> duplicates() {
            return CmCollectionUtils.duplicates(this);
        }

        default FluentList<T> add(Iterable<T> entries) {
            return this.with(entries);
        }

        default FluentList<T> sorted() {
            sort(null);
            return this;
        }

        default <X extends Comparable> FluentList<T> sorted(Function<T, X> on) {
            sort(Comparator.comparing(on, Comparator.naturalOrder()));
            return this;
        }

        default <X extends Comparable> FluentList<T> sorted(Comparator<T> comparator) {
            sort(comparator);
            return this;
        }

        default FluentList<T> without(T... items) {
            removeAll(asList(items));
            return this;
        }

        default FluentList<T> without(Collection<T> items) {
            removeAll(items);
            return this;
        }

        default FluentList<T> add(T... items) {
            return this.with(items);
        }

        default FluentList<T> accept(Consumer<FluentList<T>> consumer) {
            consumer.accept(this);
            return this;
        }

        default T addAndReturn(T item) {
            add(item);
            return item;
        }

        default <O> FluentList<O> map(Function<T, O> mapper) {
            return list(this.stream().map(mapper).collect(Collectors.toList()));
        }

        default <O> FluentList<O> flatMap(Function<T, Iterable<O>> mapper) {
            return list(this.stream().map(mapper).flatMap(Streams::of).collect(Collectors.toList()));
        }

        default <O> FluentList<O> mapWithIndex(BiFunction<Integer, T, O> mapper) {
            return (FluentList) list().accept(l -> {
                for (int i = 0; i < this.size(); i++) {
                    l.add(mapper.apply(i, this.get(i)));
                }
            });
        }

        default <R, A> R collect(Collector<? super T, A, R> collector) {
            return stream().collect(collector);
        }

        default List<T> immutableCopy() {
            return List.copyOf(this);
        }

        default FluentList<T> withoutLast() {
            remove(size() - 1);
            return this;
        }

        default FluentList<T> distinct() {
            return distinct(identity());
        }

        default <E> FluentList<T> distinct(Function<T, E> on) {
            Set<E> keys = new HashSet<>();
            List<T> list = list();
            forEach((item) -> {
                E key = on.apply(item);
                if (keys.add(key)) {
                    list.add(item);
                }
            });
            if (list.size() != size()) {
                clear();
                addAll(list);
            }
            return this;
        }

        default FluentList<T> reverse() {
            Collections.reverse(this);
            return this;
        }

        default FluentList<T> until(Predicate<T> predicate) {
            for (int i = 0; i < size(); i++) {
                if (predicate.test(get(i))) {
                    for (int j = size() - 1; j >= i; j--) {
                        remove(j);
                    }
                }
            }
            return this;
        }

        default T removeOne(Predicate<T> predicate) {
            T t = stream().filter(predicate).collect(onlyElement());
            remove(t);
            return t;
        }

        default List<T> removeMany(Predicate<T> predicate) {
            List<T> list = stream().filter(predicate).collect(toUnmodifiableList());
            removeAll(list);
            return list;
        }
    }

    public interface FluentSet<T> extends Set<T> {

        FluentSet<T> with(T entry);

        FluentSet<T> with(Iterable<T> entries);

        FluentSet<T> with(T... items);

        FluentSet<T> without(T entry);

        FluentSet<T> without(Iterable<T> entries);

        FluentSet<T> without(T... items);

        FluentSet<T> sorted(Comparator<T> comparator);

        Set<T> immutable();

        default FluentSet<T> filter(Predicate<T> predicate) {
            return this.withOnly(predicate);
        }

        default FluentSet<T> filter(T... entries) {
            return this.withOnly(entries);
        }

        default FluentSet<T> filter(Collection<T> entries) {
            return this.withOnly(entries);
        }

        default FluentSet<T> withOnly(Predicate<T> predicate) {
            return this.without(not(predicate));
        }

        default FluentSet<T> withOnly(T... entries) {
            return this.withOnly((Collection<T>) set(entries));
        }

        default FluentSet<T> withOnly(Collection<T> entries) {
            this.retainAll(entries);
            return this;
        }

        default FluentSet<T> withNullable(@Nullable Iterable<T> entries) {
            if (entries != null) {
                with(entries);
            }
            return this;
        }

        default FluentSet<T> without(Predicate<T> filter) {
            this.removeIf(filter);
            return this;
        }

        default FluentSet<T> sorted() {
            return this.sorted((Comparator<T>) Comparator.naturalOrder());
        }

        default FluentSet<T> accept(Consumer<FluentSet<T>> consumer) {
            consumer.accept(this);
            return this;
        }
    }

    private static class FluentListImpl<T> extends ArrayList<T> implements FluentList<T> {

        public FluentListImpl() {
        }

        @Override
        public FluentList<T> with(Iterable<T> entries) {
            this.addAll(toCollection(entries));
            return this;
        }

        @Override
        public FluentList<T> with(Iterator<T> entries) {
            while (entries.hasNext()) {
                this.add(entries.next());
            }
            return this;
        }

        @Override
        public FluentList<T> with(T entry) {
            add(entry);
            return this;
        }

        @Override
        public FluentList<T> with(T... items) {
            return this.with(asList(items));
        }

        @Override
        public FluentList<T> without(Predicate<T> predicate) {
            removeIf(predicate);
            return this;
        }

        @Override
        public FluentSet<T> toSet() {
            return CmCollectionUtils.set(this);
        }

        @Override
        public List<T> immutable() {
            return Collections.unmodifiableList(this);
        }

    }

    private static class FluentSetImpl<T> extends LinkedHashSet<T> implements FluentSet<T> {

        public FluentSetImpl() {
        }

        public FluentSetImpl(Collection<? extends T> c) {
            super(c);
        }

        @Override
        public FluentSet<T> with(T entry) {
            add(entry);
            return this;
        }

        @Override
        public FluentSet<T> with(Iterable<T> entries) {
            this.addAll(toCollection(entries));
            return this;
        }

        @Override
        public FluentSet<T> with(T... items) {
            return this.with(asList(items));
        }

        @Override
        public FluentSet<T> without(T entry) {
            remove(entry);
            return this;
        }

        @Override
        public FluentSet<T> without(Iterable<T> entries) {
            entries.forEach(this::remove);
            return this;
        }

        @Override
        public FluentSet<T> without(T... items) {
            return this.without(asList(items));
        }

        @Override
        public FluentSet<T> sorted(Comparator<T> comparator) {
            List<T> content = list(this);
            content.sort(comparator);
            clear();
            addAll(content);
            return this;
        }

        @Override
        public Set<T> immutable() {
            return Collections.unmodifiableSet(this);
        }

    }

    private static final class CollectorHelper {

        static final int MAX_EXTRAS = 4;

        @Nullable
        Object element;
        @Nullable
        List<Object> extras;

        CollectorHelper() {
            element = null;
            extras = null;
        }

        IllegalArgumentException multiples(boolean overflow) {
            StringBuilder sb
                    = new StringBuilder().append("expected one element but was: <").append(element);
            for (Object o : extras) {
                sb.append(", ").append(o);
            }
            if (overflow) {
                sb.append(", ...");
            }
            sb.append('>');
            throw new IllegalArgumentException(sb.toString());
        }

        void add(Object o) {
            checkNotNull(o);
            if (element == null) {
                this.element = o;
            } else if (extras == null) {
                extras = new ArrayList<>(MAX_EXTRAS);
                extras.add(o);
            } else if (extras.size() < MAX_EXTRAS) {
                extras.add(o);
            } else {
                throw multiples(true);
            }
        }

        CollectorHelper combine(CollectorHelper other) {
            if (element == null) {
                return other;
            } else if (other.element == null) {
                return this;
            } else {
                if (extras == null) {
                    extras = new ArrayList<>();
                }
                extras.add(other.element);
                if (other.extras != null) {
                    this.extras.addAll(other.extras);
                }
                if (extras.size() > MAX_EXTRAS) {
                    extras.subList(MAX_EXTRAS, extras.size()).clear();
                    throw multiples(true);
                }
                return this;
            }
        }

        Optional<Object> getOptional() {
            if (extras == null) {
                return Optional.ofNullable(element);
            } else {
                throw multiples(false);
            }
        }

        Object getElement() {
            if (element == null) {
                throw new NoSuchElementException();
            } else if (extras == null) {
                return element;
            } else {
                throw multiples(false);
            }
        }
    }

}
