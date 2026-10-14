package org.cmdbuild.dms;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.activation.DataHandler;

import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;

public interface DmsService {

    final String DOCUMENT_ATTR_DOCUMENTID = "DocumentId",
            DOCUMENT_ATTR_CARD = "Card",
            DOCUMENT_ATTR_DESCRIPTION = "Description",
            DOCUMENT_ATTR_FILENAME = "FileName",
            DOCUMENT_ATTR_MIMETYPE = "MimeType",
            DOCUMENT_ATTR_SIZE = "Size",
            DOCUMENT_ATTR_HASH = "Hash",
            DOCUMENT_ATTR_CREATED = "CreationDate",
            DOCUMENT_ATTR_CATEGORY = "Category",
            DOCUMENT_ATTR_VERSION = "Version",
            DMS_MODEL_ALFRESCO_CATEGORY = "AlfrescoCategory";

    Classe getDmsModel(String className, @Nullable String category);

    List<DocumentInfoAndDetail> getCardAttachments(String className, long cardId, DaoQueryOptions queryOptions, boolean includeMetadata);

    DocumentInfoAndDetail getCardAttachmentFromDms(String className, long cardId, String fileName);

    int getCardAttachmentCountSafe(CardIdAndClassName card);

    @Nullable
    DocumentInfoAndDetail getCardAttachmentOrNull(String className, long cardId, String fileName);

    boolean attachmentWithFileNameAlreadyExistsForCard(long cardId, String filename);

    DocumentInfoAndDetail getCardAttachmentById(String className, long cardId, String documentId);

    DocumentInfoAndDetail getCardAttachmentById(String documentId);

    DocumentInfoAndDetail getCardAttachmentByMetadataId(long metadataCardId);

    List<DocumentInfoAndDetail> getCardAttachmentVersions(String className, long cardId, String filename);

    DocumentInfoAndDetail create(String className, long cardId, DocumentData document);

    @Nullable
    DocumentInfoAndDetail createAndSkipExisting(String className, long cardId, DocumentData document);

    DocumentInfoAndDetail createAndReplaceExisting(String className, long cardId, DocumentData document);

    DocumentInfoAndDetail updateDocumentWithAttachmentId(String className, long cardId, String attachmentId, DocumentData documentData);

    DocumentInfoAndDetail updateDocumentWithMetadataId(String className, long cardId, long metadataCardId, DocumentData documentData);

    DocumentInfoAndDetail updateDocumentMetadata(String className, long cardId, String documentId, Map<String, Object> metadata);

    void deleteByMetadataId(String className, long cardId, Long metadataCardId);

    DataHandler download(String documentId, @Nullable String version);

    Optional<DataHandler> preview(String documentId);

    void delete(String className, long cardId, String filename);

    LookupType getCategoryLookupType(String classId);

    LookupType getCategoryLookupType(Classe classe);

    LookupValue getCategoryLookup(String classId, String category);

    LookupValue getCategoryLookup(Classe classe, String category);

    DataHandler exportAllDocuments();

    boolean isEnabled();

    void checkRegularFileAttachment(DocumentData document, String classId);

    void checkRegularFileSize(DocumentData document, String classId);

    void checkIncomingEmailAttachment(DocumentData document);

    DmsProviderService getService();

    String getDefaultDmsCategory();

    default DataHandler download(String documentId) {
        return download(documentId, null);
    }

    default LookupValue getCategoryLookupForAttachment(Classe classe, DocumentInfoAndDetail document) {
        return getCategoryLookup(classe, document.getCategory());
    }

    default Classe getDmsModel(String className, long category) {
        return getDmsModel(className, Long.toString(category));
    }

    default List<DocumentInfoAndDetail> getCardAttachments(String className, long cardId, DaoQueryOptions queryOptions) {
        return getCardAttachments(className, cardId, queryOptions, false);
    }

    default List<DocumentInfoAndDetail> getCardAttachments(String className, long cardId) {
        return getCardAttachments(className, cardId, DaoQueryOptionsImpl.emptyOptions());
    }

    default List<DocumentInfoAndDetail> getCardAttachments(CardIdAndClassName card) {
        return getCardAttachments(card.getClassName(), card.getId());
    }

    default List<DocumentInfoAndDetail> getCardAttachments(CardIdAndClassName card, boolean includeMetadata) {
        return getCardAttachments(card.getClassName(), card.getId(), DaoQueryOptionsImpl.emptyOptions(), includeMetadata);
    }

    default DocumentInfoAndDetail copy(CardIdAndClassName sourceCard, String fileName, CardIdAndClassName targetCard) {
        DocumentInfoAndDetail source = getCardAttachmentWithFilename(sourceCard.getClassName(), sourceCard.getId(), fileName);
        return create(targetCard.getClassName(), targetCard.getId(), DocumentDataImpl.copyOf(source).withData(getDocumentBytes(source.getDocumentId())).build());
    }

    default DocumentInfoAndDetail copyAndMerge(CardIdAndClassName sourceCard, String fileName, CardIdAndClassName targetCard) {
        DocumentInfoAndDetail source = getCardAttachmentWithFilename(sourceCard.getClassName(), sourceCard.getId(), fileName);
        return createAndSkipExisting(targetCard.getClassName(), targetCard.getId(), DocumentDataImpl.copyOf(source).withData(getDocumentBytes(source.getDocumentId())).build());
    }

    default DocumentInfoAndDetail move(CardIdAndClassName sourceCard, String fileName, CardIdAndClassName targetCard) {
        DocumentInfoAndDetail res = copy(sourceCard, fileName, targetCard);
        delete(sourceCard.getClassName(), sourceCard.getId(), fileName);
        return res;
    }

    default DocumentInfoAndDetail getCardAttachmentWithFilename(String className, long cardId, String fileName) {
        return checkNotNull(getCardAttachmentOrNull(className, cardId, fileName), "card attachment not found for class = %s card = %s fileName = %s", className, cardId, fileName);
    }

    default boolean hasCardAttachmentWithFileName(String className, long cardId, String fileName) {
        return getCardAttachmentOrNull(className, cardId, fileName) != null;
    }

    default DocumentInfoAndDetail create(CardIdAndClassName card, DocumentData document) {
        return create(card.getClassName(), card.getId(), document);
    }

    default DataHandler getDocumentData(String className, long cardId, String filename) {
        return getDocumentData(className, cardId, filename, null);
    }

    default DataHandler getDocumentData(String className, long cardId, String filename, @Nullable String version) {
        DocumentInfoAndDetail document = getCardAttachmentWithFilename(className, cardId, filename);
        return download(document.getDocumentId(), version);
    }

    default DataHandler getDocumentData(String documentId) {
        return download(documentId, null);
    }

    default byte[] getDocumentBytes(String documentId) {
        return toByteArray(getDocumentData(documentId));
    }

    default DocumentInfoAndDetail updateDocumentWithFilename(String className, long cardId, String filename, DocumentData documentData) {
        DocumentInfoAndDetail document = getCardAttachmentWithFilename(className, cardId, filename);
        return updateDocumentWithAttachmentId(className, cardId, document.getDocumentId(), documentData);
    }

    default byte[] getDocumentBytes(DocumentInfoAndDetail documentInfo) {
        return getDocumentBytes(documentInfo.getDocumentId());
    }

    default void deleteByAttachmentId(String classId, long cardId, String attachmentId) {
        DocumentInfoAndDetail document = getCardAttachmentById(classId, cardId, attachmentId);
        delete(classId, cardId, document.getFileName());
    }

}
