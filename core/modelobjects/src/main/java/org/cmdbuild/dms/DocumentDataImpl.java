/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms;

import static com.google.common.base.Preconditions.checkArgument;
import java.io.InputStream;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.apache.commons.io.FilenameUtils;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class DocumentDataImpl implements DocumentData {

    private final String author, filename, category;
    private final boolean majorVersion;
    private final byte[] data;
    private final Map<String, Object> metadata;

    private DocumentDataImpl(DocumentDataImplBuilder builder) {
        this.author = builder.author;
        this.category = builder.category;
        this.majorVersion = builder.majorVersion;
        this.metadata = builder.metadata == null ? emptyMap() : map(builder.metadata).immutable();
        this.data = builder.data;
        if (data != null) {
            checkArgument(data.length > 0, "document size is 0 bytes");
            filename = checkNotBlank(FilenameUtils.getName(builder.filename), "missing document file name");
        } else {
            filename = null;
        }
    }

    @Override
    @Nullable
    public String getAuthor() {
        return author;
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    @Nullable
    public String getCategory() {
        return category;
    }

    @Override
    public boolean isMajorVersion() {
        return majorVersion;
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "DocumentData{" + "filename=" + filename + ", category=" + category + ", data=" + (data == null ? null : byteCountToDisplaySize(data.length)) + '}';
    }

    public static DocumentDataImplBuilder builder() {
        return new DocumentDataImplBuilder();
    }

    public static DocumentDataImplBuilder copyOf(DataHandler data) {
        return builder().withData(data).withFilename(data.getName());
    }

    public static DocumentDataImplBuilder copyOf(DocumentData source) {
        return new DocumentDataImplBuilder()
                .withAuthor(source.getAuthor())
                .withFilename(source.getFilename())
                .withCategory(source.getCategory())
                .withMajorVersion(source.isMajorVersion())
                .withData(source.getData())
                .withMetadata(source.getMetadata());
    }

    public static DocumentDataImplBuilder copyOf(DocumentInfoAndDetail source) {
        return new DocumentDataImplBuilder()
                .withAuthor(source.getAuthor())
                .withFilename(source.getFileName())
                .withCategory(source.getCategory())
                .withDescription(source.getDescription());
    }

    public static class DocumentDataImplBuilder implements Builder<DocumentDataImpl, DocumentDataImplBuilder> {

        private String author, filename, category;
        private boolean majorVersion = false;
        private byte[] data;
        private final Map<String, Object> metadata = map();

        public DocumentDataImplBuilder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public DocumentDataImplBuilder withFilename(String filename) {
            this.filename = filename;
            return this;
        }

        public DocumentDataImplBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public DocumentDataImplBuilder withDescription(String description) {
            this.metadata.put(ATTR_DESCRIPTION, description);
            return this;
        }

        public DocumentDataImplBuilder withMajorVersion(boolean majorVersion) {
            this.majorVersion = majorVersion;
            return this;
        }

        public DocumentDataImplBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public DocumentDataImplBuilder withData(InputStream data) {
            return this.withData(toByteArray(data));
        }

        public DocumentDataImplBuilder withData(@Nullable DataHandler dataHandler) {
            if (dataHandler == null) {
                return this.withData((byte[]) null);
            } else {
                return this.withData(toByteArray(dataHandler)).withFilename(dataHandler.getName());
            }
        }

        public DocumentDataImplBuilder withMetadata(Map<String, Object> metadata) {
            this.metadata.clear();
            this.metadata.putAll(metadata);
            return this;
        }

        @Override
        public DocumentDataImpl build() {
            return new DocumentDataImpl(this);
        }

    }
}
