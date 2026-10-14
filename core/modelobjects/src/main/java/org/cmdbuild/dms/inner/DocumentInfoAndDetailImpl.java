/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class DocumentInfoAndDetailImpl implements DocumentInfoAndDetail {

    private final String author, description, documentId, fileName, mimeType, version, category, hash;
    private final int size;
    private final ZonedDateTime created, modified;
    private final boolean hasContent;
    private final Card metadata;

    private DocumentInfoAndDetailImpl(DocumentInfoAndDetailImplBuilder builder) {
        this.author = builder.author;
        this.description = builder.description;
        this.documentId = checkNotBlank(builder.documentId, "document id is null");
        this.fileName = checkNotBlank(builder.fileName, "document filename is null");
        this.mimeType = checkNotBlank(builder.mimeType, "document mime type is null");
        this.version = checkNotBlank(builder.version, "document version is null");
        this.category = builder.category;
        this.size = checkNotNull(builder.size, "document size is null");
        this.hash = checkNotBlank(builder.hash, "document hash is null");
        this.created = checkNotNull(builder.created, "missing create date");
        this.modified = checkNotNull(builder.modified, "missing modified date");
        this.hasContent = builder.hasContent;
        this.metadata = builder.metadata;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDocumentId() {
        return documentId;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public int getFileSize() {
        return size;
    }

    @Override
    @Nullable
    public String getCategory() {
        return category;
    }

    @Override
    public ZonedDateTime getCreated() {
        return created;
    }

    @Override
    public ZonedDateTime getModified() {
        return modified;
    }

    @Override
    public boolean hasContent() {
        return hasContent;
    }

    @Override
    public String getHash() {
        return hash;
    }

    @Override
    @Nullable
    public Card getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "DocumentInfoAndDetail{" + "documentId=" + documentId + ", fileName=" + fileName + ", version=" + version + '}';
    }

    public static DocumentInfoAndDetailImplBuilder builder() {
        return new DocumentInfoAndDetailImplBuilder();
    }

    public static DocumentInfoAndDetailImplBuilder copyOf(DocumentInfoAndDetail source) {
        return builder()
                .withAuthor(source.getAuthor())
                .withCategory(source.getCategory())
                .withCreated(source.getCreated())
                .withDescription(source.getDescription())
                .withFileName(source.getFileName())
                .withHash(source.getHash())
                .withDocumentId(source.getDocumentId())
                .withMimeType(source.getMimeType())
                .withModified(source.getModified())
                .withFileSize(source.getFileSize())
                .withMetadata(source.getMetadata())
                .withVersion(source.getVersion());
    }

    public static class DocumentInfoAndDetailImplBuilder implements Builder<DocumentInfoAndDetailImpl, DocumentInfoAndDetailImplBuilder> {

        private String author, description, documentId, fileName, mimeType, version, category, hash;
        private Integer size;
        private ZonedDateTime created, modified;
        private boolean hasContent = false;
        private Card metadata;

        public DocumentInfoAndDetailImplBuilder withMetadata(Map<String, ?> metadata) {
            this.metadata = CardImpl.buildCard(ClasseImpl.builder().withName("DUMMY_METADATA_CLASS").build(), (Map) metadata);
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withMetadata(Card metadata) {
            this.metadata = metadata;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withDocumentId(String name) {
            this.documentId = name;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withFileName(String filename) {
            this.fileName = filename;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withMimeType(String mimetype) {
            this.mimeType = mimetype;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withHash(String hash) {
            this.hash = hash;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withFileSize(Integer size) {
            this.size = size;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withCreated(ZonedDateTime created) {
            this.created = created;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder withModified(ZonedDateTime modified) {
            this.modified = modified;
            return this;
        }

        public DocumentInfoAndDetailImplBuilder hasContent(boolean hasContent) {
            this.hasContent = hasContent;
            return this;
        }

        @Override
        public DocumentInfoAndDetailImpl build() {
            return new DocumentInfoAndDetailImpl(this);
        }

    }

}
