/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;

public interface DocumentDataRepository {

    @Nullable
    byte[] getDocumentDataOrNull(String documentId, String version);

    boolean hasDocumentData(String documentId, String version);

    void createDocumentData(String documentId, String version, byte[] data);

    default byte[] getDocumentData(String documentId, String version) {
        return checkNotNull(getDocumentDataOrNull(documentId, version), "document data not found for documentId =< %s > version =< %s >", documentId, version);
    }

    default byte[] getDocumentData(DocumentInfoAndDetail document) {
        return getDocumentData(document.getDocumentId(), document.getVersion());
    }

    default void createDocumentData(DocumentInfoAndDetail document, byte[] data) {
        createDocumentData(document.getDocumentId(), document.getVersion(), data);
    }

    default boolean hasDocumentData(DocumentInfoAndDetail document) {
        return hasDocumentData(document.getDocumentId(), document.getVersion());
    }
}
