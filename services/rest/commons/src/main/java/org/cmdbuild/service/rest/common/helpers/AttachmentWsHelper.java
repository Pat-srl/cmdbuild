package org.cmdbuild.service.rest.common.helpers;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Splitter;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.PathParam;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.utils.AttributeFilterProcessor;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.temp.TempInfo;
import org.cmdbuild.temp.TempService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.getOnlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.*;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;

@Component
public class AttachmentWsHelper {

    private final DmsService service;
    private final TempService temp;
    private final CardWsSerializationHelperv3 helper;

    public AttachmentWsHelper(DmsService service, TempService temp, CardWsSerializationHelperv3 helper) {
        this.service = checkNotNull(service);
        this.temp = checkNotNull(temp);
        this.helper = checkNotNull(helper);
    }

    public Object create(String classId, long cardId, @Nullable WsAttachmentData attachment, List<String> tempIds) throws IOException {
        List<Pair<DataHandler, TempInfo>> files = nullToEmpty(tempIds).stream().flatMap(t -> Splitter.on(",").omitEmptyStrings().splitToList(t).stream()).map(t -> Pair.of(temp.getTempData(t), temp.getTempInfo(t))).collect(toImmutableList());
        checkArgument(!files.isEmpty(), "missing file to upload");
        List<Object> res = files.stream().map(rethrowFunction(d -> create(classId, cardId, attachment, d.getLeft(), !d.getRight().isSourceSecure()))).collect(toImmutableList());
        return res.size() == 1 ? getOnlyElement(res) : success();
    }

    public Object create(String classId, long cardId, @Nullable WsAttachmentData attachment, DataHandler dataHandler) throws IOException {
        return create(classId, cardId, attachment, dataHandler, true);
    }

    private Object create(String classId, long cardId, @Nullable WsAttachmentData attachment, DataHandler dataHandler, boolean checkFile) throws IOException {
        checkNotNull(dataHandler);
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        DocumentData documentData = DocumentDataImpl.builder()
                .withData(dataHandler.getInputStream())
                .accept(b -> {
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
            service.checkRegularFileAttachment(documentData, classId);
            service.checkRegularFileSize(documentData, classId);
        }
        DocumentInfoAndDetail document = service.create(classId, cardId, documentData);
        return response(helper.serializeAttachment_FullDetail(classId, document, false));
    }

    public Object copyFrom(String classId, long cardId, @Nullable WsAttachmentData attachment, @Nullable String sourceClassId, @Nullable Long sourceCardId, @Nullable String sourceAttachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        checkCanRead(sourceClassId, sourceCardId);
        DocumentInfoAndDetail source = service.getCardAttachmentById(sourceClassId, sourceCardId, sourceAttachmentId);
        byte[] data = toByteArray(service.getDocumentData(source.getDocumentId()));
        DocumentInfoAndDetail document = service.create(classId, cardId, DocumentDataImpl.builder() //TODO duplicate code
                .withData(data)
                .withFilename(source.getFileName())
                .accept(b -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory()).withMetadata(attachment.getCardValues()).withDescription(attachment.getDescription());
                    } else {
                        b.withDescription("");
                    }
                })
                .withMajorVersion(true)
                .build());
        return response(helper.serializeAttachment_FullDetail(classId, document, false));
    }

    public Object readMany(WsQueryOptions wsQueryOptions, String classId, long cardId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        List<DocumentInfoAndDetail> list = service.getCardAttachments(classId, cardId, wsQueryOptions.getQuery(), wsQueryOptions.isDetailed());
        return response(list.stream().map(d -> helper.serializeAttachment_FullDetail(classId, d, false)).toList());
    }

    public Object readOne(String classId, long cardId, String attachmentId) {
        return readOne(classId, cardId, attachmentId, false);
    }

    public Object readOne(String classId, long cardId, String attachmentId, boolean includeWidgets) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId, attachmentId);
        DocumentInfoAndDetail document = service.getCardAttachmentById(classId, cardId, attachmentId);
        return response(helper.serializeAttachment_FullDetail(classId, document, includeWidgets));
    }

    public DataHandler download(String classId, long cardId, String attachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        return service.getDocumentData(attachmentId);//TODO permission check
    }

    public Object preview(String classId, long cardId, String attachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        Optional<DataHandler> preview = service.preview(attachmentId);//TODO permission check
        return map("success", true, "data", map().accept(map -> {
            map.put("hasPreview", preview.isPresent());
            if (preview.isPresent()) {
                map.put("dataUrl", toDataUrl(preview.get()));
            }
        }));
    }

    public List<String> getAllDocumentIdFromCard(String classId, long cardId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        return service.getCardAttachments(classId, cardId, DaoQueryOptionsImpl.emptyOptions(), false).stream().map(d -> d.getDocumentId()).toList();
    }

    private String toDataUrl(DataHandler dataHandler) {
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

    public Object update(String classId, Long cardId, String attachmentId, WsAttachmentData attachment, String tempId) {
        return update(classId, 0, attachmentId, attachment, temp.getTempData(tempId));
    }

    public Object update(String classId, long cardId, String attachmentId, @Nullable WsAttachmentData attachment, @Nullable DataHandler dataHandler) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        DocumentData documentData = DocumentDataImpl.builder()
                .accept(b -> {
                    if (attachment != null) {
                        b.withCategory(attachment.getCategory()).withMetadata(attachment.getCardValues()).withDescription(attachment.getDescription()).withMajorVersion(attachment.getMajorVersion());
                    }
                    if (dataHandler != null) {
                        b.withData(dataHandler);
                    }
                }).build();
        if (dataHandler != null) {
            service.checkRegularFileAttachment(documentData, classId);
            service.checkRegularFileSize(documentData, classId);
        }
        DocumentInfoAndDetail document = service.updateDocumentWithAttachmentId(classId, cardId, attachmentId, documentData);
        return response(helper.serializeAttachment_FullDetail(classId, document, false));
    }

    public Object delete(String classId, long cardId, String attachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        service.deleteByAttachmentId(classId, cardId, attachmentId);//TODO permission check
        return success();
    }

    public void deleteAttachments(String classId, long cardId, CmdbFilter filter) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId);
        AttributeFilterProcessor attributeFilterProcessor = AttributeFilterProcessor.<DocumentInfoAndDetail>builder().withKeyToValueFunction((key, doc) -> helper.serializeAttachment_FullDetail(classId, doc, false).get(key)).withFilter(filter.getAttributeFilter()).build();
        service.getCardAttachments(classId, cardId).stream().filter(attributeFilterProcessor::match).forEach(d -> service.deleteByAttachmentId(classId, cardId, d.getDocumentId()));
    }

    public Object getAttachmentHistory(String classId, long cardId, String attachmentId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId, attachmentId);
        DocumentInfoAndDetail cardAttachmentById = service.getCardAttachmentById(classId, cardId, attachmentId);
        List<DocumentInfoAndDetail> versions = service.getCardAttachmentVersions(classId, cardId, cardAttachmentById.getFileName());
        return response(versions.stream().map(d -> helper.serializeAttachment_FullDetail(classId, d, false)).toList());
    }

    public DataHandler downloadPreviousVersion(String classId, long cardId, String attachmentId, @PathParam("version") String versionId) {
        checkNotBlank(classId);
        checkCanRead(classId, cardId, attachmentId);
        return service.download(attachmentId, versionId);//TODO permission check
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

    public static class WsAttachmentData {

        private final String category, fileName;
        private final boolean majorVersion;
        private final Map<String, Object> cardValues;

        @JsonCreator
        public WsAttachmentData(Map<String, Object> values) {
            this.category = toStringOrNull(values.get("category"));
            this.fileName = toStringOrNull(values.get("fileName"));
            this.majorVersion = toBooleanOrDefault(values.get("majorVersion"), true);
            this.cardValues = map(values).withoutKeys("category", "fileName", "majorVersion").immutable();
        }

        @Nullable
        public String getCategory() {
            return category;
        }

        public String getDescription() {
            return toStringOrEmpty(cardValues.get(ATTR_DESCRIPTION));
        }

        @Nullable
        public String getFileName() {
            return fileName;
        }

        public boolean getMajorVersion() {
            return majorVersion;
        }

        public Map<String, Object> getCardValues() {
            return cardValues;
        }

    }

}
