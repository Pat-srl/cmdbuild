/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import org.apache.commons.io.FileUtils;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.easyupload.EasyuploadItemInfo.EASYUPLOAD_TABLE;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.context.annotation.Primary;

@Primary
@CardMapping(EASYUPLOAD_TABLE)
public class EasyuploadItemImpl implements EasyuploadItem {

    private final String path, fileName, hash, mimeType, description;
    private final int size;
    private final byte[] content;
    private final Long id;

    private EasyuploadItemImpl(EasyuploadItemImplBuilder builder) {
        this.id = builder.id;
        this.path = checkNotBlank(builder.path);
        this.fileName = checkNotBlank(builder.fileName);
        this.hash = checkNotBlank(builder.hash);
        this.mimeType = checkNotBlank(builder.mimeType);
        this.size = checkNotNull(builder.size);
        this.content = checkNotNull(builder.content);
        this.description = nullToEmpty(builder.description);
    }

    @CardAttr(ATTR_ID)
    @Override
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(EASYUPLOAD_PATH)
    public String getPath() {
        return path;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    @CardAttr(EASYUPLOAD_FILENAME)
    public String getFileName() {
        return fileName;
    }

    @Override
    @CardAttr(EASYUPLOAD_HASH)
    public String getHash() {
        return hash;
    }

    @Override
    @CardAttr(EASYUPLOAD_MIMETYPE)
    public String getMimeType() {
        return mimeType;
    }

    @Override
    @CardAttr(EASYUPLOAD_SIZE)
    public int getSize() {
        return size;
    }

    @Override
    @CardAttr(EASYUPLOAD_CONTENT)
    public byte[] getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "EasyuploadItemImpl{" + "path=" + path + ", hash=" + hash + ", mimeType=" + mimeType + ", size=" + FileUtils.byteCountToDisplaySize(size) + '}';
    }

    public static EasyuploadItemImplBuilder builder() {
        return new EasyuploadItemImplBuilder();
    }

    public static EasyuploadItemImplBuilder copyOf(EasyuploadItem source) {
        return new EasyuploadItemImplBuilder()
                .withId(source.getId())
                .withPath(source.getPath())
                .withFileName(source.getFileName())
                .withHash(source.getHash())
                .withMimeType(source.getMimeType())
                .withSize(source.getSize())
                .withContent(source.getContent())
                .withDescription(source.getDescription());
    }

    public static EasyuploadItemImpl toImpl(EasyuploadItem source) {
        if (source instanceof EasyuploadItemImpl) {
            return (EasyuploadItemImpl) source;
        } else {
            return copyOf(source).build();
        }
    }

    public static class EasyuploadItemImplBuilder implements Builder<EasyuploadItemImpl, EasyuploadItemImplBuilder> {

        private Long id;
        private String path;
        private String fileName, description;
        private String hash;
        private String mimeType;
        private int size;
        private byte[] content;

        public EasyuploadItemImplBuilder withPath(String path) {
            this.path = path;
            return this;
        }

        public EasyuploadItemImplBuilder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public EasyuploadItemImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EasyuploadItemImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EasyuploadItemImplBuilder withHash(String hash) {
            this.hash = hash;
            return this;
        }

        public EasyuploadItemImplBuilder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public EasyuploadItemImplBuilder withSize(int size) {
            this.size = size;
            return this;
        }

        public EasyuploadItemImplBuilder withContent(byte[] content) {
            this.content = content;
            return this;
        }

        @Override
        public EasyuploadItemImpl build() {
            return new EasyuploadItemImpl(this);
        }

    }
}
