/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email.beans;

import org.cmdbuild.email.EmailAttachment;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.getFilenameFromContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class EmailAttachmentImpl implements EmailAttachment {

    private final byte[] data;
    private final String contentType, fileName, contentId, contentDisposition;
    
    private EmailAttachmentImpl(EmailAttachmentImplBuilder builder) {
        this.data = checkNotNull(builder.data);
        this.contentType = isBlank(builder.mimeType) ? CmIoUtils.getContentType(data) : builder.mimeType;
        this.fileName = isBlank(builder.fileName) ? getFilenameFromContentType(contentType) : builder.fileName;
        this.contentId = builder.contentId;
        this.contentDisposition = firstNotNull(builder.contentDisposition, "");
    }

    @Override
    public byte[] getData() {
        return data;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    @Nullable
    public String getContentId() {
        return contentId;
    }

    @Override
    public String getContentDisposition() {
        return contentDisposition;
    }

    @Override
    public String toString() {
        return "EmailAttachment{" + "data=" + byteCountToDisplaySize(data.length) + ", mimeType=" + contentType + ", fileName=" + fileName + '}';
    }

    public static EmailAttachmentImplBuilder builder() {
        return new EmailAttachmentImplBuilder();
    }

    public static EmailAttachmentImplBuilder copyOf(EmailAttachment source) {
        return new EmailAttachmentImplBuilder()
                .withData(source.getData())
                .withContentType(source.getContentType())
                .withFileName(source.getFileName())
                .withContentId(source.getContentId())
                .withContentDisposition(source.getContentDisposition());
    }

    public static EmailAttachmentImplBuilder copyOf(DataSource data) {
        return builder()
                .withData(toByteArray(data))
                .withFileName(data.getName())
                .withContentType(data.getContentType());
    }

    public static EmailAttachmentImpl build(DataSource data) {
        return copyOf(data).build();
    }

    public static EmailAttachmentImpl build(byte[] data, String contentType) {
        return builder().withData(data).withContentType(contentType).build();
    }

    public static class EmailAttachmentImplBuilder implements Builder<EmailAttachmentImpl, EmailAttachmentImplBuilder> {

        private byte[] data;
        private String mimeType;
        private String fileName, contentId, contentDisposition;

        public EmailAttachmentImplBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public EmailAttachmentImplBuilder withContentType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public EmailAttachmentImplBuilder withFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public EmailAttachmentImplBuilder withContentId(String contentId) {
            this.contentId = contentId;
            return this;
        }

        public EmailAttachmentImplBuilder withContentDisposition(String contentDisposition) {
            this.contentDisposition = contentDisposition;
            return this;
        }

        @Override
        public EmailAttachmentImpl build() {
            return new EmailAttachmentImpl(this);
        }

    }
}
