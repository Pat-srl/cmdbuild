/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.privileges;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Maps.transformValues;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.utils.lang.Builder;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentSet;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.queue;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmMapUtils.toMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrivilegeProcessorImpl<P> implements PrivilegeProcessor<P> {

    private final Map<P, Set<P>> privilegeImplicationMap, reversePrivilegeImplicationMap;
    private final Set<P> nullPrivilegeValues;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private PrivilegeProcessorImpl(PrivilegeProcessorImplBuilder<P> builder) {
        try {
            Map<P, Set<P>> map = checkNotNull(builder.privilegeImplicationMap).entrySet().stream().collect(toMap(Entry::getKey, (e) -> set(e.getValue())));
            Queue<P> queue = queue(map.keySet());
            while (!queue.isEmpty()) {
                P element = queue.poll();
                Set<P> targets = map.get(element);
                Set<P> expandedTargets = targets.stream().map(map::get).map(CmCollectionUtils::nullToEmpty).flatMap(Collection::stream).collect(toSet());
                boolean changed = targets.addAll(expandedTargets);
                if (changed) {
                    queue.clear();
                    queue.addAll(map.keySet());
                }
            }
            map.forEach((k, v) -> v.remove(k));
            this.privilegeImplicationMap = ImmutableMap.copyOf(transformValues(map, ImmutableSet::copyOf));
            this.reversePrivilegeImplicationMap = ImmutableMap.copyOf(transformValues(privilegeImplicationMap.entrySet().stream()
                    .flatMap((e) -> e.getValue().stream().map((v) -> Pair.of(v, e.getKey()))).collect(toMultimap(Pair::getKey, Pair::getValue)).asMap(), ImmutableSet::copyOf));
            this.nullPrivilegeValues = ImmutableSet.copyOf(nullToEmpty(builder.nullPrivilegeValues));

        } catch (Exception ex) {
            logger.error("error creating privilege processor with params = {}", builder, ex);
            throw runtime(ex);
        }
    }

    @Override
    public Set<P> expandPrivileges(Iterable<P> privileges) {
        return expandPrivileges(privileges, privilegeImplicationMap);
    }

    @Override
    public Set<P> expandPrivilegesBackwards(Iterable<P> privileges) {
        return expandPrivileges(privileges, reversePrivilegeImplicationMap);
    }

    private Set<P> expandPrivileges(Iterable<P> privileges, Map<P, Set<P>> implicationMap) {
        checkNotNull(privileges);
        FluentSet<P> set = set();
        for (P p : privileges) {
            set.add(p);
            set.addAll(nullToEmpty(implicationMap.get(p)));
        }
        return set.without(nullPrivilegeValues);
    }

    @Override
    public Set<P> mergePrivileges(Iterable<P> first, Iterable<P> second) {
        return expandPrivileges(set(first).with(second));
    }

    public Map<P, Set<P>> getPrivilegeImplicationMap() {
        return privilegeImplicationMap;
    }

    public Set<P> getNullPrivilegeValues() {
        return nullPrivilegeValues;
    }

    public static <P> PrivilegeProcessorImplBuilder<P> builder() {
        return new PrivilegeProcessorImplBuilder<>();
    }

    public static <P> PrivilegeProcessorImplBuilder<P> copyOf(PrivilegeProcessorImpl<P> source) {
        return new PrivilegeProcessorImplBuilder<P>()
                .withPrivilegeImplicationMap(source.getPrivilegeImplicationMap())
                .withNullPrivilegeValues(source.getNullPrivilegeValues());
    }

    public static class PrivilegeProcessorImplBuilder<P> implements Builder<PrivilegeProcessorImpl<P>, PrivilegeProcessorImplBuilder<P>> {

        private Map<P, Set<P>> privilegeImplicationMap;
        private Set<P> nullPrivilegeValues;

        public PrivilegeProcessorImplBuilder<P> withPrivilegeImplicationMap(Map<P, Set<P>> privilegeImplicationMap) {
            this.privilegeImplicationMap = privilegeImplicationMap;
            return this;
        }

        public PrivilegeProcessorImplBuilder<P> withNullPrivilegeValues(P... nullPrivilegeValues) {
            return this.withNullPrivilegeValues(set(nullPrivilegeValues));
        }

        public PrivilegeProcessorImplBuilder<P> withNullPrivilegeValues(Set<P> nullPrivilegeValues) {
            this.nullPrivilegeValues = nullPrivilegeValues;
            return this;
        }

        @Override
        public PrivilegeProcessorImpl<P> build() {
            return new PrivilegeProcessorImpl<>(this);
        }

        @Override
        public String toString() {
            return "PrivilegeProcessorImplBuilder{" + "privilegeImplicationMap=" + privilegeImplicationMap + ", nullPrivilegeValues=" + nullPrivilegeValues + '}';
        }

    }
}
