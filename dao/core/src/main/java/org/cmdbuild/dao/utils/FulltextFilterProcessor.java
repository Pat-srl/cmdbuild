package org.cmdbuild.dao.utils;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.dao.utils.FulltextMatcherImpl.fulltextMatcher;
import org.cmdbuild.data.filter.FulltextFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.object.CmBeanUtils.getBeanProperties;

public class FulltextFilterProcessor<T> {

    private final FulltextFilter filter;
    private final BiFunction<String, T, Object> keyToValueFunction;
    private final Function<T, Collection<String>> keyFunction;

    private FulltextFilterProcessor(FulltextFilter filter, BiFunction<String, T, Object> keyToValueFunction, Function<T, Collection<String>> keyFunction) {
        this.filter = checkNotNull(filter);
        this.keyToValueFunction = checkNotNull(keyToValueFunction);
        this.keyFunction = checkNotNull(keyFunction);
    }

    public static <T> FulltextFilterProcessor<T> build(FulltextFilter filter) {
        return new FulltextFilterProcessor(filter, (BiFunction) DefaultBeanKeyToValueFunction.INSTANCE, (b) -> getBeanProperties(b));
    }

    public FulltextFilterProcessor<T> withKeyToValueFunction(BiFunction<String, T, Object> keyToValueFunction) {
        return new FulltextFilterProcessor(filter, keyToValueFunction, keyFunction);
    }

    public FulltextFilterProcessor<T> withKeyFunction(Function<T, Collection<String>> keyFunction) {
        return new FulltextFilterProcessor(filter, keyToValueFunction, keyFunction);
    }

    public List<T> filter(Iterable<T> list) {
        return stream(list).filter(this::match).collect(toList());
    }

    public boolean match(T item) {
        return list(keyFunction.apply(item)).map(k -> keyToValueFunction.apply(k, item)).stream().filter(Objects::nonNull).anyMatch(fulltextMatcher(filter.getQuery()));
    }
}
