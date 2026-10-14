package org.cmdbuild.cache;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class CmCacheStatsImpl implements CmCacheStats {

    private final String name;
    private final long size;
    private final long estimateMemSize;

    private CmCacheStatsImpl(CmCacheStatsImplBuilder builder) {
        this.name = checkNotBlank(builder.name);
        this.size = builder.size;
        this.estimateMemSize = builder.estimateMemSize;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public long getEstimateMemSize() {
        return estimateMemSize;
    }

    @Override
    public String toString() {
        return "CmCacheStats{" + "name=" + name + '}';
    }

    public static CmCacheStatsImplBuilder builder() {
        return new CmCacheStatsImplBuilder();
    }

    public static CmCacheStatsImplBuilder copyOf(CmCacheStats source) {
        return new CmCacheStatsImplBuilder()
                .withName(source.getName())
                .withSize(source.getSize())
                .withEstimateMemSize(source.getEstimateMemSize());
    }

    public static class CmCacheStatsImplBuilder implements Builder<CmCacheStatsImpl, CmCacheStatsImplBuilder> {

        private String name;
        private Long size;
        private Long estimateMemSize;

        public CmCacheStatsImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public CmCacheStatsImplBuilder withSize(Long size) {
            this.size = size;
            return this;
        }

        public CmCacheStatsImplBuilder withEstimateMemSize(Long estimateMemSize) {
            this.estimateMemSize = estimateMemSize;
            return this;
        }

        @Override
        public CmCacheStatsImpl build() {
            return new CmCacheStatsImpl(this);
        }

    }
}
