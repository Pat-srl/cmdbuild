/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.LinkedHashMultimap;
import static com.google.common.collect.Maps.filterEntries;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import jakarta.annotation.Nullable;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptyMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import static java.util.function.Function.identity;
import java.util.function.Predicate;
import static java.util.function.Predicate.not;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.MapDuplicateKeyMode.ALLOW_DUPLICATES;
import static org.cmdbuild.utils.lang.CmMapUtils.MapDuplicateKeyMode.ERROR_ON_DUPLICATES;

public class CmMapUtils {

    public static boolean isNullOrEmpty(@Nullable Map map) {
        return map == null || map.isEmpty();
    }

    @Nullable
    public static <K, V> Map<K, V> emptyToNull(@Nullable Map<K, V> map) {
        return isNullOrEmpty(map) ? null : map;
    }

    public static <K, V> Map<K, V> nullToEmpty(@Nullable Map<K, V> map) {
        return isNullOrEmpty(map) ? emptyMap() : map;
    }

    public static <T, K, U> Collector<T, ?, FluentMap<K, U>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return new FluentMapCollector(keyMapper, valueMapper);
    }

    public static <K, V> FluentMap<K, V> toMap(Object object) {
        checkArgument(object instanceof Map, "object cannot be cast to map, found a %s", object.getClass().getName());
        return (FluentMap<K, V>) object;
    }

    public static <K, V> Map<V, K> toInverseMap(Map<? extends K, ? extends V> map) {
        return map.entrySet().stream().collect(toMap(Entry::getValue, Entry::getKey));
    }

    public static <K, V> Map<K, V> lazyMap(Supplier<Map<K, V>> supplier) {
        return new LazyMap<>(supplier);
    }

    public static <T, K, U> Collector<T, ?, Map<K, U>> toImmutableMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return new Collector<T, K, Map<K, U>>() {

            private final Collector<T, K, FluentMap<K, U>> inner = new FluentMapCollector(keyMapper, valueMapper);

            @Override
            public Supplier<K> supplier() {
                return inner.supplier();
            }

            @Override
            public BiConsumer<K, T> accumulator() {
                return inner.accumulator();
            }

            @Override
            public BinaryOperator<K> combiner() {
                return inner.combiner();
            }

            @Override
            public Function<K, Map<K, U>> finisher() {
                return m -> Collections.unmodifiableMap(inner.finisher().apply(m));
            }

            @Override
            public Set<Collector.Characteristics> characteristics() {
                return inner.characteristics();
            }
        };

    }

    public static <T, K, U> Collector<T, ?, FluentMap<K, U>> toMap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper, MapDuplicateKeyMode duplicateKeyAction) {
        return new FluentMapCollector(keyMapper, valueMapper, duplicateKeyAction);
    }

    public static <T, K, U> Collector<T, ?, FluentMultimap<K, U>> toMultimap(Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
        return new FluentMultimapCollector(keyMapper, valueMapper);
    }

    public static <K, V> FluentMultimap<K, V> multimap() {
        return new FluentMultimapImpl<>();
    }

    public static <K, V> FluentMap<K, V> map(Iterable<V> iterable, Function<? super V, ? extends K> keyMapper) {
        return map(iterable, keyMapper, identity());
    }

    public static <K, V, V2> FluentMap<K, V2> map(Iterable<V> iterable, Function<? super V, ? extends K> keyMapper, Function<? super V, ? extends V2> valueMapper) {
        return Streams.stream(iterable).collect(toMap(keyMapper, valueMapper));
    }

    public static <K, V> FluentMap<K, V> mapOf(Class<K> key, Class<V> val) {
        return map();
    }

    public static <K, V> FluentMap<K, V> map() {
        return new FluentMapImpl<>();
    }

    public static <K, V> FluentMultimap<K, V> multimap(Map<? extends K, ? extends V> map) {
        return new FluentMultimapImpl<K, V>().with(map);
    }

    public static <K, V> FluentMultimap<K, V> multimap(Multimap<? extends K, ? extends V> map) {
        return new FluentMultimapImpl<K, V>().with(map);
    }

    public static <K, V> FluentMap<K, V> map(Map<? extends K, ? extends V> map) {
        return new FluentMapImpl<K, V>().with(map);
    }

    public static FluentMap<String, String> map(Properties properties) {
        return new FluentMapImpl().with((Map) properties);
    }

    public static <K, V, E> FluentMap<K, V> map(E... values) {
        return new FluentMapImpl<K, V>().with(values);
    }

    public static <K, V, E> FluentMap<K, V> map(Consumer<FluentMap<K, V>> loader) {
        return new FluentMapImpl<K, V>().accept(loader);
    }

    public static <K, V> Map<K, V> map(Function<K, V> accessor) {
        return new FunctionAsMap<>(accessor);
    }

    public interface FluentMultimap<K, V> extends Multimap<K, V> {

        FluentMultimap<K, V> with(Multimap values);

        FluentMultimap<K, V> withoutKey(K key);

        default FluentMultimap<K, V> accept(Consumer<FluentMultimap<K, V>> visitor) {
            visitor.accept(this);
            return this;
        }

        default FluentMultimap<K, V> withoutKeys(Iterable<K> keys) {
            keys.forEach(this::withoutKey);
            return this;
        }

        default FluentMultimap<K, V> withoutKeys(K... keys) {
            stream(keys).forEach(this::withoutKey);
            return this;
        }
    }

    public interface FluentMap<K, V> extends Map<K, V> {

        FluentMap<K, V> with(K key, V value);

        FluentMap<K, V> with(Map values);

        FluentMap<K, V> with(Object... values);

        FluentMap<K, V> withoutKey(K key);

        FluentMap<K, V> withoutKeys(Predicate<K> predicate);

        /**
         * note: does NOT make a defensive copy!
         *
         * @return an immutable view of this map
         */
        Map<K, V> immutable();

        default FluentMap<K, V> filterMapKeys(String prefix) {
            return this.filterKeys(k -> k.toString().startsWith(prefix)).mapKeys(k -> (K) k.toString().replaceFirst(Pattern.quote(prefix), ""));
        }

        default FluentMap<K, V> filterKeys(Predicate<K> predicate) {
            return this.withKeys(predicate);
        }

        default FluentMap<K, V> filterValues(Predicate<V> predicate) {
            return this.withValues(predicate);
        }

        default FluentMap<K, V> withoutValues(Predicate<V> predicate) {
            list(entrySet()).stream().filter(e -> predicate.test(e.getValue())).forEach(e -> remove(e.getKey()));
            return this;
        }

        default FluentMap<K, V> withValues(Predicate<V> predicate) {
            return this.withoutValues(not(predicate));
        }

        default <O> FluentMap<O, V> mapKeys(Function<K, O> mapper) {
            FluentMap<K, V> originalKeys = map(), mappedKeys = map();
            forEach((k, v) -> {
                O key = mapper.apply(k);
                if (key != null) {
                    mappedKeys.put(key, v);
                } else {
                    originalKeys.put(k, v);
                }
            });
            clear();
            putAll(originalKeys);
            putAll(mappedKeys);
            return (FluentMap) this;
        }

        default <V2> FluentMap<K, V2> mapValues(Function<V, V2> mapper) {
            forEach((k, v) -> {
                put(k, mapper.apply(v));
            });
            return (FluentMap) this;
        }

        default <V2> FluentMap<K, V2> mapValues(BiFunction<K, V, V2> mapper) {
            forEach((k, v) -> {
                put(k, mapper.apply(k, v));
            });
            return (FluentMap) this;
        }

        default FluentMap<K, V> put(Object... values) {
            return this.with(values);
        }

        default FluentMap<K, V> accept(Consumer<FluentMap<K, V>> visitor) {
            visitor.accept(this);
            return this;
        }

        /**
         *
         * @return an immutable copy of this map
         */
        default Map<K, V> immutableCopy() {
            return copy().immutable();
        }

        default FluentMap<K, V> copy() {
            return map(this);
        }

        FluentMap<K, V> skipNullValues(boolean skipNullValues);

        /**
         * skip null values added <u>after</u> this method call
         *
         * @return
         */
        default FluentMap<K, V> skipNullValues() {
            return this.skipNullValues(true);
        }

        /**
         * include null values added <u>after</u> this method call
         *
         * @return
         */
        default FluentMap<K, V> includeNullValues() {
            return this.skipNullValues(false);
        }

        /**
         * reset all insert customizations, such as skipNullValues()
         */
        default FluentMap<K, V> then() {
            return this.includeNullValues();
        }

        default FluentMap<K, V> withoutKeys(Iterable<K> keys) {
            keys.forEach(this::withoutKey);
            return this;
        }

        default FluentMap<K, V> withKeys(Iterable<K> keys) {
            return this.filterKeys(set(keys)::contains);
        }

        default FluentMap<K, V> withKeys(K... keys) {
            return this.filterKeys(set(keys)::contains);
        }

        default FluentMap<K, V> withoutKeys(K... keys) {
            stream(keys).forEach(this::withoutKey);
            return this;
        }

        default FluentMap<K, V> withKeys(Predicate<K> predicate) {
            return this.withoutKeys(not(predicate));
        }

        default Properties toProperties() {
            Properties properties = new Properties();
            properties.putAll(this);
            return properties;
        }

        default <X> FluentList<X> toList(BiFunction<K, V, X> fun) {
            return list(entrySet()).map(e -> fun.apply(e.getKey(), e.getValue()));
        }
    }

    private static class FluentMapImpl<K, V> extends LinkedHashMap<K, V> implements FluentMap<K, V> {

        private boolean skipNullValues = false;

        @Override
        public void putAll(Map<? extends K, ? extends V> values) {
            if (skipNullValues) {
                values = filterEntries(values, (entry) -> entry.getValue() != null);
            }
            super.putAll(values);
        }

        @Override
        public V put(K key, V value) {
            if (skipNullValues && value == null) {
                return null;
            } else {
                return super.put(key, value);
            }
        }

        @Override
        public FluentMap<K, V> with(K key, V value) {
            put(key, value);
            return this;
        }

        @Override
        public FluentMap<K, V> with(Map values) {
            putAll(values);
            return this;
        }

        @Override
        public FluentMap<K, V> with(Object... values) {
            checkArgument(values.length % 2 == 0, "cannot accept an odd number of values");
            Iterator<Object> iterator = asList(values).iterator();
            while (iterator.hasNext()) {
                K key = (K) iterator.next();
                V value = (V) iterator.next();
                put(key, value);
            }
            return this;
        }

        @Override
        public Map<K, V> immutable() {
            return isEmpty() ? emptyMap() : Collections.unmodifiableMap(this);
        }

        @Override
        public FluentMap<K, V> skipNullValues(boolean skipNullValues) {
            this.skipNullValues = skipNullValues;
            return this;
        }

        @Override
        public FluentMap<K, V> withoutKey(K key) {
            this.remove(key);
            return this;
        }

        @Override
        public FluentMap<K, V> withoutKeys(Predicate<K> predicate) {
            ImmutableList.copyOf(keySet()).stream().filter(predicate).forEach(this::remove);
            return this;
        }

    }

    private static class FluentMultimapImpl<K, V> implements FluentMultimap<K, V> {

        private final Multimap<K, V> inner = LinkedHashMultimap.create();

        @Override
        public FluentMultimap<K, V> with(Multimap values) {
            inner.putAll(values);
            return this;
        }

        private FluentMultimap<K, V> with(Map<? extends K, ? extends V> values) {
            values.forEach((key, value) -> inner.put(key, value));
            return this;
        }

        @Override
        public FluentMultimap<K, V> withoutKey(K key) {
            this.removeAll(key);
            return this;
        }

        @Override
        public int size() {
            return inner.size();
        }

        @Override
        public boolean isEmpty() {
            return inner.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return inner.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return inner.containsValue(value);
        }

        @Override
        public boolean containsEntry(Object key, Object value) {
            return inner.containsEntry(key, value);
        }

        @Override
        public boolean put(K key, V value) {
            return inner.put(key, value);
        }

        @Override
        public boolean remove(Object key, Object value) {
            return inner.remove(key, value);
        }

        @Override
        public boolean putAll(K key, Iterable<? extends V> values) {
            return inner.putAll(key, values);
        }

        @Override
        public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
            return inner.putAll(multimap);
        }

        @Override
        public Collection<V> replaceValues(K key, Iterable<? extends V> values) {
            return inner.replaceValues(key, values);
        }

        @Override
        public Collection<V> removeAll(Object key) {
            return inner.removeAll(key);
        }

        @Override
        public void clear() {
            inner.clear();
        }

        @Override
        public Collection<V> get(K key) {
            return inner.get(key);
        }

        @Override
        public Set<K> keySet() {
            return inner.keySet();
        }

        @Override
        public Multiset<K> keys() {
            return inner.keys();
        }

        @Override
        public Collection<V> values() {
            return inner.values();
        }

        @Override
        public Collection<Map.Entry<K, V>> entries() {
            return inner.entries();
        }

        @Override
        public Map<K, Collection<V>> asMap() {
            return inner.asMap();
        }

    }

    private static class FluentMultimapCollector<T, K, V> implements Collector<T, FluentMultimap<K, V>, FluentMultimap<K, V>> {

        private final Function<T, K> keyGetter;
        private final Function<T, V> valueGetter;

        public FluentMultimapCollector(Function<T, K> keyGetter, Function<T, V> valueGetter) {
            this.keyGetter = keyGetter;
            this.valueGetter = valueGetter;
        }

//		public static <T, K, V> MultimapCollector<T, K, V> toMultimap(Function<T, K> keyGetter, Function<T, V> valueGetter) {
//			return new MultimapCollector<>(keyGetter, valueGetter);
//		}
//
//		public static <T, K, V> MultimapCollector<T, K, T> toMultimap(Function<T, K> keyGetter) {
//			return new MultimapCollector<>(keyGetter, v -> v);
//		}
        @Override
        public Supplier<FluentMultimap<K, V>> supplier() {
            return FluentMultimapImpl::new;
        }

        @Override
        public BiConsumer<FluentMultimap<K, V>, T> accumulator() {
            return (map, element) -> map.put(keyGetter.apply(element), valueGetter.apply(element));
        }

        @Override
        public BinaryOperator<FluentMultimap<K, V>> combiner() {
            return (map1, map2) -> {
                map1.putAll(map2);
                return map1;
            };
        }

        @Override
        public Function<FluentMultimap<K, V>, FluentMultimap<K, V>> finisher() {
            return map -> map;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return ImmutableSet.of(Characteristics.IDENTITY_FINISH);
        }
    }

    public enum MapDuplicateKeyMode {
        ALLOW_DUPLICATES, ERROR_ON_DUPLICATES
    }

    private static class FluentMapCollector<T, K, V> implements Collector<T, FluentMap<K, V>, FluentMap<K, V>> {

        private final boolean allowDuplicates;
        private final Function<T, K> keyGetter;
        private final Function<T, V> valueGetter;

        public FluentMapCollector(Function<T, K> keyGetter, Function<T, V> valueGetter) {
            this(keyGetter, valueGetter, ERROR_ON_DUPLICATES);
        }

        public FluentMapCollector(Function<T, K> keyGetter, Function<T, V> valueGetter, MapDuplicateKeyMode duplicateKeyAction) {
            this.keyGetter = keyGetter;
            this.valueGetter = valueGetter;
            this.allowDuplicates = ALLOW_DUPLICATES.equals(checkNotNull(duplicateKeyAction));
        }

        @Override
        public Supplier<FluentMap<K, V>> supplier() {
            return FluentMapImpl::new;
        }

        @Override
        public BiConsumer<FluentMap<K, V>, T> accumulator() {
            return (map, element) -> {
                K key = keyGetter.apply(element);
                checkArgument(allowDuplicates || !map.containsKey(key), "duplicate key = %s", key);
                map.put(key, valueGetter.apply(element));
            };
        }

        @Override
        public BinaryOperator<FluentMap<K, V>> combiner() {
            return (map1, map2) -> {
                Sets.SetView<K> intersection = Sets.intersection(map1.keySet(), map2.keySet());
                checkArgument(allowDuplicates || intersection.isEmpty(), "duplicate keys = %s", intersection);
                map1.putAll(map2);
                return map1;
            };
        }

        @Override
        public Function<FluentMap<K, V>, FluentMap<K, V>> finisher() {
            return map -> map;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return ImmutableSet.of(Characteristics.IDENTITY_FINISH);
        }
    }

    private static class FunctionAsMap<K, V> implements Map<K, V> {

        private final Function<K, V> accessor;

        public FunctionAsMap(Function<K, V> accessor) {
            this.accessor = checkNotNull(accessor);
        }

        @Override
        public int size() {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

        @Override
        public boolean isEmpty() {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

        @Override
        public boolean containsKey(Object key) {
            return get(key) != null;
        }

        @Override
        public boolean containsValue(Object value) {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

        @Override
        public V get(Object key) {
            return accessor.apply((K) key);
        }

        @Override
        public V put(K key, V value) {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

        @Override
        public Set<K> keySet() {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

        @Override
        public Collection<V> values() {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            throw new UnsupportedOperationException("method not supported: this is not a concrete map");
        }

    };

    private static class LazyMap<K, V> implements Map<K, V> {

        private final Supplier<Map<K, V>> delegate;

        public LazyMap(Supplier<Map<K, V>> delegate) {
            checkNotNull(delegate);
            this.delegate = Suppliers.memoize(delegate::get);
        }

        @Override
        public int size() {
            return delegate.get().size();
        }

        @Override
        public boolean isEmpty() {
            return delegate.get().isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return delegate.get().containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return delegate.get().containsValue(value);
        }

        @Override
        public V get(Object key) {
            return delegate.get().get(key);
        }

        @Override
        public V put(K key, V value) {
            return delegate.get().put(key, value);
        }

        @Override
        public V remove(Object key) {
            return delegate.get().remove(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            delegate.get().putAll(m);
        }

        @Override
        public void clear() {
            delegate.get().clear();
        }

        @Override
        public Set<K> keySet() {
            return delegate.get().keySet();
        }

        @Override
        public Collection<V> values() {
            return delegate.get().values();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return delegate.get().entrySet();
        }

        @Override
        public boolean equals(Object o) {
            return delegate.get().equals(o);
        }

        @Override
        public int hashCode() {
            return delegate.get().hashCode();
        }

    }
}
