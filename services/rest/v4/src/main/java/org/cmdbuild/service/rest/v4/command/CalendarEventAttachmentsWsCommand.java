package org.cmdbuild.service.rest.v4.command;

import com.google.common.base.Splitter;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.PathParam;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsAttachmentData;
import org.cmdbuild.temp.TempInfo;
import org.cmdbuild.temp.TempService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.*;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;

@Component
public class CalendarEventAttachmentsWsCommand {

    private final DmsService dmsService;
    private final TempService tempService;

    public CalendarEventAttachmentsWsCommand(DmsService dmsService, TempService tempService) {
        this.dmsService = checkNotNull(dmsService);
        this.tempService = checkNotNull(tempService);
    }

    public List<DocumentInfoAndDetail> doCreate(String classId, long cardId, @Nullable WsAttachmentData attachment, List<String> tempIds) throws IOException {
        List<Pair<DataHandler, TempInfo>> files = nullToEmpty(tempIds).stream().flatMap(t -> Splitter.on(",").omitEmptyStrings().splitToList(t).stream()).map(t -> Pair.of(tempService.getTempData(t), tempService.getTempInfo(t))).collect(toImmutableList());
        checkArgument(!files.isEmpty(), "missing file to upload");
        return files.stream().map(rethrowFunction(d -> doCreate(classId, cardId, attachment, d.getLeft(), !d.getRight().isSourceSecure()))).collect(toImmutableList());
    }

    public DocumentInfoAndDetail doCreate(String classId, long cardId, @Nullable WsAttachmentData attachment, DataHandler dataHandler, boolean checkFile) throws IOException {
        checkNotNull(dataHandler);
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        DocumentData documentData = DocumentDataImpl.builder()
                .withData(dataHandler.getInputStream())
                .accept((b) -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory())
                                .withFilename(firstNotBlankOrEmpty(attachment.getFileName(), dataHandler.getName()))
                                .withMetadata(attachment.getCardValues())
                                .withDescription(attachment.getDescription());
                    } else {
                        b.withDescription("").withFilename(dataHandler.getName());
                    }
                })
                .withMajorVersion(true)
                .build();
        if (checkFile) {
            dmsService.checkRegularFileAttachment(documentData, classId);
            dmsService.checkRegularFileSize(documentData, classId);
        }
        return dmsService.create(classId, cardId, documentData);
    }

    public DocumentInfoAndDetail doCopyFrom(String classId, long cardId, @Nullable WsAttachmentData attachment, @Nullable String sourceClassId, @Nullable Long sourceCardId, @Nullable String sourceAttachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        checkCanRead(sourceClassId, sourceCardId);
        DocumentInfoAndDetail source = dmsService.getCardAttachmentById(sourceClassId, sourceCardId, sourceAttachmentId);
        byte[] data = toByteArray(dmsService.getDocumentData(source.getDocumentId()));
        return dmsService.create(classId, cardId, DocumentDataImpl.builder() //TODO duplicate code
                .withData(data)
                .withFilename(source.getFileName())
                .accept((b) -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory()).withMetadata(attachment.getCardValues()).withDescription(attachment.getDescription());
                    } else {
                        b.withDescription("");
                    }
                })
                .withMajorVersion(true)
                .build());
    }

    public List<DocumentInfoAndDetail> doReadMany(WsQueryOptions wsQueryOptions, String classId, long cardId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        return dmsService.getCardAttachments(classId, cardId, wsQueryOptions.getQuery(), wsQueryOptions.isDetailed());
    }

    public DocumentInfoAndDetail doReadOne(String classId, long cardId, String attachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId, attachmentId);
        return dmsService.getCardAttachmentById(classId, cardId, attachmentId);
    }

    public DataHandler doDownload(String classId, long cardId, String attachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        return dmsService.getDocumentData(attachmentId);//TODO permission check
    }

    public Optional<DataHandler> doPreview(String classId, long cardId, String attachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        return dmsService.preview(attachmentId);//TODO permission check
    }

    public DocumentInfoAndDetail doUpdate(String classId, long cardId, String attachmentId, @Nullable WsAttachmentData attachment, @Nullable DataHandler dataHandler) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        DocumentData documentData = DocumentDataImpl.builder()
                .accept((b) -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory()).withMetadata(attachment.getCardValues()).withDescription(attachment.getDescription()).withMajorVersion(attachment.getMajorVersion());
                    }
                    if (dataHandler != null) {
                        b.withData(dataHandler);
                    }
                }).build();
        if (dataHandler != null) {
            dmsService.checkRegularFileAttachment(documentData, classId);
            dmsService.checkRegularFileSize(documentData, classId);
        }
        return dmsService.updateDocumentWithAttachmentId(classId, cardId, attachmentId, documentData);

    }

    public void doDelete(String classId, long cardId, String attachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        dmsService.deleteByAttachmentId(classId, cardId, attachmentId);//TODO permission check
    }

    public List<DocumentInfoAndDetail> doGetAttachmentHistory(String classId, long cardId, String attachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId, attachmentId);
        DocumentInfoAndDetail cardAttachmentById = dmsService.getCardAttachmentById(classId, cardId, attachmentId);
        return dmsService.getCardAttachmentVersions(classId, cardId, cardAttachmentById.getFileName());
    }

    public DataHandler doDownloadPreviousVersion(String classId, long cardId, String attachmentId, @PathParam("version") String versionId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId, attachmentId);
        return dmsService.download(attachmentId, versionId);//TODO permission check
    }

    public static String toDataUrl(DataHandler dataHandler) {
        try {
            StringBuilder dataUrl = new StringBuilder("data:");
            String mediaType = dataHandler.getContentType();
            dataUrl.append(mediaType);
            dataUrl.append(";base64,");
            String base64payload = Base64.encodeBase64String(toByteArray(dataHandler.getInputStream()));
            dataUrl.append(base64payload);
            return dataUrl.toString();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void checkCanRead(String classId, long cardId) {//TODO
        //TODO handle special case for 'Email' classes
//		Classe targetClass = dataAccessLogic.findClass(classId);
//		if (targetClass == null) {
//			errorHandler.classNotFound(classId);
//		}
//		try {
//			dataAccessLogic.fetchCMCard(classId, cardId);
//		} catch (NoSuchElementException e) {
//			errorHandler.cardNotFound(cardId);
//		}
    }

    private void checkCanRead(String classId, long cardId, String attachmentId) {//TODO
        //TODO handle special case for 'Email' classes
//		Classe targetClass = dataAccessLogic.findClass(classId);
//		if (targetClass == null) {
//			errorHandler.classNotFound(classId);
//		}
//		try {
//			dataAccessLogic.fetchCMCard(classId, cardId);
//		} catch (NoSuchElementException e) {
//			errorHandler.cardNotFound(cardId);
//		}
//		if (isBlank(attachmentId)) {
//			errorHandler.missingAttachmentId();
//		}
    }

}
