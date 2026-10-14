package org.cmdbuild.classe.access;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.Map;
import org.cmdbuild.classe.access.UserCardFileService;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FILE;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_DOCUMENTID;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.temp.TempInfo;
import org.cmdbuild.temp.TempService;
import static org.cmdbuild.temp.TempServiceUtils.isTempId;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserCardFileServiceImpl implements UserCardFileService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DmsService dmsService;
    private final TempService tempService;
    private final DaoService dao;

    public UserCardFileServiceImpl(DmsService dmsService, TempService tempService, DaoService dao) {
        this.dmsService = checkNotNull(dmsService);
        this.tempService = checkNotNull(tempService);
        this.dao = checkNotNull(dao);
    }

    @Nullable
    @Override
    public Object prepareFileAttribute(Attribute attribute, Map<String, Object> values, String ownerName, @Nullable Card oldCard, long cardId) {
        Object value = values.get(attribute.getName());
        if (isBlank(value)) {
            return null;
        } else if (!dmsService.getService().isServiceOk()) {
            logger.warn(marker(), "CM: dms service not ok, skip processing for file attr = {}", attribute.getName());
            return null;
        } else {
            logger.debug("processing file attribute = {} with new value =< {} >", attribute, value);
            String documentId = toStringNotBlank(value);
            Map<String, Object> metadata = map(values).filterMapKeys(format("_%s_", attribute.getName()));
            if (toBooleanOrDefault(metadata.get("isDmsServiceOk"), true)) {
                if (isTempId(documentId)) {
                    TempInfo tempInfo = tempService.getTempInfo(documentId);
                    DocumentData documentData = DocumentDataImpl.builder()
                            .withCategory(toStringNotBlank(dmsService.getCategoryLookup(ownerName, attribute.getMetadata().getDmsCategory()).getId()))
                            .withMetadata(metadata)
                            .withData(tempService.getTempData(documentId))
                            .withFilename(tempInfo.getFileName()).build();
                    dmsService.checkRegularFileAttachment(documentData, attribute.getOwnerName());
                    dmsService.checkRegularFileSize(documentData, attribute.getOwnerName());
                    return dmsService.createAndReplaceExisting(ownerName, cardId, documentData).getMetadataCardId();
                } else {
                    checkNotNullAndGtZero(cardId, "error processing file attr value =< {} > : missing current card id,value", value);
                    DocumentInfoAndDetail previousDocument = dmsService.getCardAttachmentById(documentId);
                    if (oldCard == null) {
                        previousDocument = dmsService.create(ownerName, cardId, DocumentDataImpl.copyOf(previousDocument).withData(dmsService.getDocumentData(documentId)).build());
                    }
                    Map<String, Object> mergeMetadata = map(previousDocument.getMetadataMap()).with(metadata);
                    DocumentDataImpl docData = DocumentDataImpl.builder()
                            .withAuthor(previousDocument.getAuthor())
                            .withCategory(previousDocument.getCategory())
                            .withFilename(previousDocument.getFileName())
                            .withData((DataHandler) null)
                            .withMetadata(mergeMetadata)
                            .withDescription(previousDocument.getDescription())
                            .accept(d -> {
                                if (values.get(format("_%s_Description", attribute.getName())) != null) {
                                    d.withDescription(values.get(format("_%s_Description", attribute.getName())).toString());
                                }
                                if (oldCard == null) {
                                    d.withMetadata(map(mergeMetadata).with("card", cardId));
                                }
                            })
                            .build();
                    DocumentInfoAndDetail document = dmsService.updateDocumentWithAttachmentId(attribute.getOwnerName(), cardId, previousDocument.getDocumentId(), docData);
                    return document.getMetadataCardId();
                }
            } else {
                return dao.selectAll().from(DMS_MODEL_PARENT_CLASS).where(DOCUMENT_ATTR_DOCUMENTID, EQ, checkNotBlank(documentId)).getCardIdOrNull();
            }
        }
    }

    @Override
    public void clearDeletedAttachments(Card oldCard, Card newCard) {
        list(oldCard.getType().getActiveServiceAttributes()).filter(a -> a.isOfType(FILE)).forEach(a -> {
            if (!equal(oldCard.getLong(a.getName()), newCard.getLong(a.getName())) && isNotNullAndGtZero(oldCard.getLong(a.getName()))) {
                dmsService.deleteByMetadataId(oldCard.getTypeName(), oldCard.getId(), oldCard.getLong(a.getName()));
            }
        });
    }
}
