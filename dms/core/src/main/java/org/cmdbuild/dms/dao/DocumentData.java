/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_Document")
public class DocumentData {

    public final static String DOCUMENTDATA_ATTR_DOCUMENTID = "DocumentId", DOCUMENTDATA_ATTR_VERSION = "Version";

    private final byte[] data;
    private final String documentId, version;

    private DocumentData(DocumentDataBuilder builder) {
        this.data = checkNotNull(builder.data);
        this.documentId = checkNotBlank(builder.documentId);
        this.version = checkNotBlank(builder.version);
    }

    @CardAttr
    public byte[] getData() {
        return data;
    }

    @CardAttr(DOCUMENTDATA_ATTR_DOCUMENTID)
    public String getDocumentId() {
        return documentId;
    }

    @CardAttr(DOCUMENTDATA_ATTR_VERSION)
    public String getVersion() {
        return version;
    }

    public static DocumentDataBuilder builder() {
        return new DocumentDataBuilder();
    }

    public static DocumentDataBuilder copyOf(DocumentData source) {
        return new DocumentDataBuilder()
                .withData(source.getData())
                .withDocumentId(source.getDocumentId())
                .withVersion(source.getVersion());
    }

    public static class DocumentDataBuilder implements Builder<DocumentData, DocumentDataBuilder> {

        private byte[] data;
        private String documentId, version;

        public DocumentDataBuilder withData(byte[] data) {
            this.data = data;
            return this;
        }

        public DocumentDataBuilder withDocumentId(String documentId) {
            this.documentId = documentId;
            return this;
        }

        public DocumentDataBuilder withVersion(String version) {
            this.version = version;
            return this;
        }

        @Override
        public DocumentData build() {
            return new DocumentData(this);
        }

    }
}
