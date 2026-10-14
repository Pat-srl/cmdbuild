/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.List;
import org.apache.commons.io.FileUtils;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.easyupload.EasyuploadItemInfo.EASYUPLOAD_TABLE;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping(EASYUPLOAD_TABLE)
public class EasyuploadItemInfoImpl implements EasyuploadItemInfo {

    private final String path, fileName, hash, mimeType, description;
    private final int size;
    private final Long id;

    private EasyuploadItemInfoImpl(EasyuploadItemInfoImplBuilder builder) {
        this.id = builder.id;
        this.path = checkNotBlank(builder.path);
        this.fileName = checkNotBlank(builder.fileName);
        this.hash = checkNotBlank(builder.hash);
        this.mimeType = checkNotBlank(builder.mimeType);
        this.size = checkNotNull(builder.size);
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
    public String toString() {
        return "EasyuploadItemInfoImpl{" + "path=" + path + ", hash=" + hash + ", mimeType=" + mimeType + ", size=" + FileUtils.byteCountToDisplaySize(size) + '}';
    }

    public static EasyuploadItemInfoImplBuilder builder() {
        return new EasyuploadItemInfoImplBuilder();
    }

    public static EasyuploadItemInfoImplBuilder copyOf(EasyuploadItemInfo source) {
        return new EasyuploadItemInfoImplBuilder()
                .withId(source.getId())
                .withPath(source.getPath())
                .withFileName(source.getFileName())
                .withHash(source.getHash())
                .withMimeType(source.getMimeType())
                .withSize(source.getSize())
                .withDescription(source.getDescription());
    }

    public static class EasyuploadItemInfoImplBuilder implements Builder<EasyuploadItemInfoImpl, EasyuploadItemInfoImplBuilder> {

        private Long id;
        private String path;
        private String fileName;
        private String hash;
        private String mimeType, description;
        private int size;

        public EasyuploadItemInfoImplBuilder withPath(String path) {
            this.path = path;
            return this;
        }

        public EasyuploadItemInfoImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public EasyuploadItemInfoImplBuilder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public EasyuploadItemInfoImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public EasyuploadItemInfoImplBuilder withHash(String hash) {
            this.hash = hash;
            return this;
        }

        public EasyuploadItemInfoImplBuilder withMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public EasyuploadItemInfoImplBuilder withSize(int size) {
            this.size = size;
            return this;
        }

        @Override
        public EasyuploadItemInfoImpl build() {
            return new EasyuploadItemInfoImpl(this);
        }

    }
}
