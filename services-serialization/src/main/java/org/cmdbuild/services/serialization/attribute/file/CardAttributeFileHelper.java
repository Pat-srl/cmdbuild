/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.services.serialization.attribute.file;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.user.UserData;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.auth.utils.AuthUtils;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.WhereOperator;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.dao.DocumentInfoRepository;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import org.cmdbuild.services.permissions.PermissionsHandlerProxy;
import org.cmdbuild.services.serialization.CardAttributeSerializationHelper;
import org.cmdbuild.services.serialization.widget.WidgetHelper;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.core.q3.WhereOperator.IN;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_DOCUMENTID;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * Subdivides:
 * <ol>
 * <li>handling of attribute FILE through DMS service;
 * <li>serialization of related values:
 * <dl><dt>category <dd>category of file in DMS;
 * <dt>name <dd>filename of related file;
 * <dt>description <dd>description of file;
 * <dt>version <dd>version of file;
 * <dt>author <dd>author of file;
 * </dl>
 * </ol>
 *
 * @author afelice
 */
@Component
public class CardAttributeFileHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final DmsService dmsService;
    private final DocumentInfoRepository repository;
    private final UserClassService userClassService;
    private final ObjectTranslationService translationService;
    private final UserRepository userRepository;

    private final PermissionsHandlerProxy permissionsHandler;

    public CardAttributeFileHelper(DaoService dao, DmsService dmsService, DocumentInfoRepository repository, UserClassService userClassService, ObjectTranslationService translationService, UserRepository userRepository, PermissionsHandlerProxy permissionsHandlerProxy) {
        this.dao = checkNotNull(dao);
        this.dmsService = checkNotNull(dmsService);
        this.repository = checkNotNull(repository);
        this.userClassService = checkNotNull(userClassService);
        this.translationService = checkNotNull(translationService);
        this.userRepository = userRepository;

        this.permissionsHandler = permissionsHandlerProxy;
    }

    /**
     * Used from CardWsSerializationHelperv3.
     *
     * @param attributeName
     * @param classId
     * @param cardDocId
     * @return
     */
    public CardAttributeFileSerializationData fetchDocument(String attributeName, String classId, long cardDocId) {
        boolean isDmsServiceOk = dmsService.getService().isServiceOk();
        DocumentInfoAndDetail document = loadDocument(isDmsServiceOk, cardDocId);

        return initSerializationData(attributeName, classId, document);
    }

    /**
     * Used from EmailTemplateProcessorServiceImpl.
     *
     * @param attributeName
     * @param classId
     * @param docCard
     * @return
     */
    public CardAttributeFileSerializationData fetchDocument(String attributeName, String classId,
                                                            Card docCard) {
        DocumentInfoAndDetail documentData = null;

        boolean isDmsServiceOk = dmsService.getService().isServiceOk();

        Object attributeFileValue = docCard.getAllValuesAsMap().get(attributeName);

        if (attributeFileValue instanceof String documentId) {
            logger.debug("card attribute value documentId = <{}>", documentId);
            documentData = loadDocument(attributeName, isDmsServiceOk, documentId);
            logger.debug("loaded document data: <{}>", documentData);
        } else if (attributeFileValue instanceof IdAndDescription cardDocIdAndDescr) {
            documentData = loadDocument(isDmsServiceOk, cardDocIdAndDescr.getId());
        } else if (attributeFileValue instanceof Long cardDocId) {
            documentData = loadDocument(isDmsServiceOk, cardDocId);
        }

        return initSerializationData(attributeName, classId, documentData);
    }

    /**
     * To get a serialization without <code>_&lt;attributeName&gt;_</code>
     * prefix.
     *
     * @param classId
     * @param document
     * @return
     */
    public CardAttributeFileSerializationData initSerializationData(String classId, DocumentInfoAndDetail document) {
        return initSerializationData(CardAttributeFileSerializationData.ANONYMOUS_SERIALIZATION_MODE, classId, document);
    }

    /**
     *
     * @param attributeName
     * @param classId
     * @param document      <b>Warning</b>: contained metadata may be null if
     *                      <code>wsQueryOptions.isDetailed()</code> not set when invoking
     *                      serialization through a WS
     * @return
     */
    public CardAttributeFileSerializationData initSerializationData(String attributeName, String classId, DocumentInfoAndDetail document) {
        checkNotNull(document, format("Missing document handling attribute FILE %s", attributeName));

        CardAttributeFileSerializationData result = new CardAttributeFileSerializationData(attributeName, classId);

        // @todo evaluate #7686
        result.isDmsServiceOk = dmsService.getService().isServiceOk();
        result.document = document;
        result.documentAuthorDescription = userDescription(document.getAuthor());
        if (document.getMetadata() != null) {
            result.cardData = document.getMetadata().getAllValuesAsMap();
        }

        return result;
    }

    /**
     *
     * @param data <b>Attention</b>: modified by current method
     * @return
     */
    public CardAttributeFileSerializationData fetchCategory(CardAttributeFileSerializationData data) {
        checkNotNull(data.document);

        String classId = data.classId;

        // Category
        boolean canDmsCategoryWritePermission = false;
        org.cmdbuild.lookup.LookupValue category = null;
        Classe userClass = null;
        if (data.document.hasCategory()) {
            category = dmsService.getCategoryLookupForAttachment(dao.getClasse(data.classId), data.document);
            logger.debug("found document category: <{}>", category);

            boolean specialEventMode = isSpecialEvent(classId);
            if (!specialEventMode) {
                userClass = userClassService.getUserClass(data.classId);
            }

            String defaultDmsCategory = dmsService.getDefaultDmsCategory();
            canDmsCategoryWritePermission = permissionsHandler.cardWsSerializationHelperv3_hasDmsCategoryWritePermission(data.classId, userClass, category, defaultDmsCategory);

            if (category.isActive()) {
                data.categoryDescriptionTranslation = translationService.translateLookupDescription(category.getType().getName(), category.getCode(), category.getDescription());
                data.categoryCanUpdate = specialEventMode || permissionsHandler.cardWsSerializationHelperv3_checkDmsPermission(userClass, category.getCode(), CP_UPDATE, defaultDmsCategory);
                data.categoryCanDelete = specialEventMode || permissionsHandler.cardWsSerializationHelperv3_checkDmsPermission(userClass, category.getCode(), CP_DELETE, defaultDmsCategory);
            }
        }

        // Related Card
        if (data.document.hasMetadata()) {
            Card card = data.document.getMetadata();
            data.cardWithPermissions = permissionsHandler.cardWsSerializationHelperv3_fetchCardPermissions(card, canDmsCategoryWritePermission);
        }

        data.canDmsCategoryWritePermission = canDmsCategoryWritePermission;
        data.category = category;
        data.userClass = userClass;

        return data;
    }

    /**
     * @param widgets_helper helper to fetch and serialize card widgets
     * @param data           <b>Attention</b>: modified by current method
     * @return
     */
    public CardAttributeFileSerializationData fetchWidgets(CardAttributeFileSerializationData data, WidgetHelper widgets_helper) {
        checkNotNull(data.document);

        data.widgets = widgets_helper.fetchWidgets(data.document.getMetadata());

        return data;
    }

    public FluentMap<String, Object> serialize(CardAttributeFileSerializationData data, CardAttributeSerializationHelper<CardAttributeFileSerializationData> serializer) {
        return serializer.serialize(data);
    }

    private DocumentInfoAndDetail loadDocument(boolean isDmsServiceOk, long cardDocId) {
        boolean documentExists = dao.selectCount().from(DMS_MODEL_PARENT_CLASS).where(ATTR_ID, WhereOperator.EQ, cardDocId).getCount() > 0;
        boolean documentDeleted = dao.selectCount().from(DMS_MODEL_PARENT_CLASS).includeHistory().where(ATTR_ID, WhereOperator.EQ, cardDocId).getCount() > 0;
        if (!documentExists && documentDeleted) {
            return loadDeletedDocument(cardDocId);
        } else {
            return loadActiveDocument(isDmsServiceOk, cardDocId);
        }
    }

    private DocumentInfoAndDetail loadActiveDocument(boolean isDmsServiceOk, long cardDocId) {
        DocumentInfoAndDetail document;
        if (isDmsServiceOk) {
            logger.debug("loading card document <{}> from DMS", cardDocId);
            document = dmsService.getCardAttachmentByMetadataId(cardDocId);
        } else {
            logger.debug("loading card document <{}> from persistence", cardDocId);
            Card documentCard = dao.selectAll().from(DMS_MODEL_PARENT_CLASS).where(ATTR_ID, WhereOperator.EQ, cardDocId).getCard();
            String documentId = checkNotBlank(documentCard.getString(DOCUMENT_ATTR_DOCUMENTID));
            document = repository.getById(documentId);
            document = fillWithCardMetadata(documentCard, documentId, document);
        }

        return document;
    }

    private DocumentInfoAndDetail loadDeletedDocument(long cardDocId) {
        DocumentInfoAndDetail document;
        logger.debug("loading card document <{}> from persistence", cardDocId);
        Card documentCard = dao.selectAll().from(DMS_MODEL_PARENT_CLASS).includeHistory().where(ATTR_ID, WhereOperator.EQ, cardDocId).getCard();
        String documentId = checkNotBlank(documentCard.getString(DOCUMENT_ATTR_DOCUMENTID));
        document = repository.getByIdIncludeDeleted(documentId);
        document = fillWithHistoryCardMetadata(documentCard, documentId, document);

        return document;
    }

    /**
     *
     * @param isDmsServiceOk
     * @param documentId     an alphanumerical id like
     *                       <code>7jjh657632mm50mn34thm7cm38nl6l7h6ln1m26j697476</code>
     * @return
     */
    private DocumentInfoAndDetail loadDocument(String attributeName, boolean isDmsServiceOk, String documentId) {
        checkNotNull(documentId, format("Missing documentId handling attribute FILE %s", attributeName));

        DocumentInfoAndDetail document;
        if (isDmsServiceOk) {
            document = dmsService.getCardAttachmentById(documentId);
        } else {
            Card documentCard = dao.selectAll().from(DMS_MODEL_PARENT_CLASS).where(DOCUMENT_ATTR_DOCUMENTID, WhereOperator.EQ, documentId).getCard();
            document = repository.getById(documentId);
            document = fillWithCardMetadata(documentCard, documentId, document);
        }

        return document;
    }

    private DocumentInfoAndDetail fillWithCardMetadata(Card documentCard, String documentId, DocumentInfoAndDetail document) {
        Card cardMetadata = dao.selectAll().from(documentCard.getTypeName())
                .whereExpr("\"DocumentId\" = ?", checkNotBlank(documentId))
                .getCardOrNull();
        return DocumentInfoAndDetailImpl.copyOf(document).withMetadata(cardMetadata).build();
    }

    private DocumentInfoAndDetail fillWithHistoryCardMetadata(Card documentCard, String documentId, DocumentInfoAndDetail document) {
        Card cardMetadata = dao.selectAll().from(documentCard.getTypeName())
                .includeHistory()
                .whereExpr("\"DocumentId\" = ?", checkNotBlank(documentId))
                .where(ATTR_STATUS, IN, list("A", "N"))
                .getCardOrNull();
        return DocumentInfoAndDetailImpl.copyOf(document).withMetadata(cardMetadata).build();
    }

    private boolean isSpecialEvent(String classId) {
        return classId.equals("Email") || classId.equals("_CalendarEvent");
    }

    @Nullable
    private String userDescription(String user) {
        return Optional.ofNullable(trimToNull(user))
                .map(AuthUtils::getUsernameFromHistoryUser)
                .map(userRepository::getUserDataByUsernameOrNull)
                .map(UserData::getDescription)
                .orElse(user);
    }

}
