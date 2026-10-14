/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.navtree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.dao.beans.AbstractMetadataImpl;
import static org.cmdbuild.navtree.NavTreeMetadata.INITIAL_EXPANSION_DEPTH;
import org.cmdbuild.navtree.NavTreeMetadataImpl.NavTreeMetadataImplBuilder;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * A simplified version of {@link AbstractMetadataImpl}, because not needed:
 * <ul>
 * <li><code>active</code>;
 * <li><code>description</code>;
 * <li>further custom <code>metadata</code>.
 * </ul>
 *
 * similar to what done in {@link LookupValueDataImpl} with
 * {@link LookupConfigImpl}.
 *
 * @author afelice
 */
public class NavTreeMetadataImpl implements NavTreeMetadata {

    private static final Set<String> NAV_TREE_METADATA_KEYS = ImmutableSet.of(INITIAL_EXPANSION_DEPTH);

    @JsonIgnore()
    private final Integer initialExpansionDepth;
    private final Map<String, String> metadata;

    public NavTreeMetadataImpl(Map<String, String> map) {
        metadata = map(Maps.filterKeys(map, NAV_TREE_METADATA_KEYS::contains));
        initialExpansionDepth = toIntegerOrNull(metadata.get(INITIAL_EXPANSION_DEPTH));
    }

    @Nullable
    @Override
    public Integer getInitialExpansionDepth() {
        return initialExpansionDepth;
    }

    @Override
    public Map<String, String> asMap() {
        return metadata;
    }

    public static NavTreeMetadataImplBuilder builder() {
        return new NavTreeMetadataImplBuilder();
    }

    public static NavTreeMetadataImplBuilder copyOf(NavTreeMetadataImpl source) {
        return builder().withMetadata(source.metadata);
    }

    /**
     * To be serialized in persistence as json
     *
     * @return
     */
    @JsonIgnore()
    public Map<String, String> getAll() {
        return metadata;
    }

    public static class NavTreeMetadataImplBuilder implements Builder<NavTreeMetadataImpl, NavTreeMetadataImplBuilder> {

        private final Map<String, String> map = map();

        public NavTreeMetadataImplBuilder withMetadata(Map<String, String> metadata) {
            map.putAll(metadata);
            return this;
        }

        public NavTreeMetadataImplBuilder withInitialExpansionDepth(@Nullable Integer initialExpansionDepth) {
            map.put(INITIAL_EXPANSION_DEPTH, toStringOrNull(initialExpansionDepth));
            return this;
        }

        @Override
        public NavTreeMetadataImpl build() {
            return new NavTreeMetadataImpl(map);
        }
    }
}
