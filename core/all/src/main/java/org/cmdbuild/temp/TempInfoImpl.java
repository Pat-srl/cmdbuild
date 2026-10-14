/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.temp;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.annotation.Nullable;
import org.cmdbuild.temp.TempInfoImpl.TempFileInfoImplBuilder;
import static org.cmdbuild.temp.TempInfoSource.TS_OTHER;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

@JsonDeserialize(builder = TempFileInfoImplBuilder.class)
public class TempInfoImpl implements TempInfo {

    private final String contentType, fileName;
    private final Long size, timeToLiveInSeconds;
    private final TempInfoSource source;

    private TempInfoImpl(TempFileInfoImplBuilder builder) {
        this.contentType = firstNotBlank(builder.contentType, "application/octet-stream");
        this.fileName = builder.fileName;
        this.size = builder.size;
        this.source = firstNotNull(builder.source, TS_OTHER);
        this.timeToLiveInSeconds = firstNotNull(builder.timeToLiveInSeconds, 43200L);
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    @Nullable
    public String getFileName() {
        return fileName;
    }

    @Nullable
    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public TempInfoSource getSource() {
        return source;
    }

    @Override
    public Long getTimeToLive() {
        return timeToLiveInSeconds;
    }

    public static TempFileInfoImplBuilder builder() {
        return new TempFileInfoImplBuilder();
    }

    public static TempFileInfoImplBuilder copyOf(TempInfo source) {
        return new TempFileInfoImplBuilder()
                .withContentType(source.getContentType())
                .withFileName(source.getFileName())
                .withSize(source.getSize())
                .withSource(source.getSource())
                .withTimeToLive(source.getTimeToLive());
    }

    public static class TempFileInfoImplBuilder implements Builder<TempInfoImpl, TempFileInfoImplBuilder> {

        private String contentType;
        private String fileName;
        private Long size, timeToLiveInSeconds;
        private TempInfoSource source;

        public TempFileInfoImplBuilder withContentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public TempFileInfoImplBuilder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public TempFileInfoImplBuilder withSize(Long size) {
            this.size = size;
            return this;
        }

        public TempFileInfoImplBuilder withSource(TempInfoSource source) {
            this.source = source;
            return this;
        }

        public TempFileInfoImplBuilder withTimeToLive(Long timeToLiveInSeconds) {
            this.timeToLiveInSeconds = timeToLiveInSeconds;
            return this;
        }

        @Override
        public TempInfoImpl build() {
            return new TempInfoImpl(this);
        }

    }
}
