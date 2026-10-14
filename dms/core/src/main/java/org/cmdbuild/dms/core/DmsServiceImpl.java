package org.cmdbuild.dms.core;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicates;
import static com.google.common.base.Predicates.compose;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.MoreCollectors.toOptional;
import java.io.IOException;
import static java.lang.String.format;
import java.util.Collections;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.apache.commons.lang3.math.NumberUtils.isNumber;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_DEFAULT;
import static org.cmdbuild.auth.grant.GrantAttributePrivilege.GAP_READ;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.classe.access.UserCardHelperService;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.common.Constants.BASE_CLASS_NAME;
import static org.cmdbuild.common.Constants.DMS_MODEL_DEFAULT_CLASS;
import static org.cmdbuild.common.Constants.DMS_MODEL_PARENT_CLASS;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.CardImpl;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_READ;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_WF_BASIC;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.postgres.utils.SqlQueryUtils;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import org.cmdbuild.dao.virtual.VirtualAttributeService;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.dms.DmsService;
import static org.cmdbuild.dms.DmsService.DOCUMENT_ATTR_CARD;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import static org.cmdbuild.dms.core.DmsServiceImpl.OnExistingAction.OEA_FAIL;
import static org.cmdbuild.dms.core.DmsServiceImpl.OnExistingAction.OEA_REPLACE;
import static org.cmdbuild.dms.core.DmsServiceImpl.OnExistingAction.OEA_SKIP;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import org.cmdbuild.dms.thumbnailer.ImageThumbnailer;
import org.cmdbuild.exception.DmsException;
import org.cmdbuild.lock.AutoCloseableItemLock;
import static org.cmdbuild.lock.LockScope.LS_REQUEST;
import org.cmdbuild.lock.LockService;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerExt;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import org.cmdbuild.script.ScriptService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import org.cmdbuild.utils.lang.CmPreconditions;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DmsServiceImpl implements DmsService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DmsConfiguration config;
    private final Map<String, DmsProviderService> services;
    private final LookupService lookupService;
    private final DaoService dao;
    private final OperationUserSupplier userSupplier;
    private final ScriptService scriptService;
    private final UserCardHelperService userCardService;
    private final UserClassService userClassService;
    private final ClasseRepository classeRepository;
    private final VirtualAttributeService virualAttributeService;
    private final LockService lockService;

    private final MinionHandlerExt minionHandler;

    public DmsServiceImpl(UserCardHelperService userCardService, List<DmsProviderService> services, DmsConfiguration configuration, LookupService lookupService, DaoService dao, OperationUserSupplier userSupplier, ScriptService scriptService, UserClassService userClassService, ClasseRepository classeRepository, VirtualAttributeService virualAttributeService, LockService lockService) {
        this.config = checkNotNull(configuration);
        this.services = Maps.uniqueIndex(checkNotNull(services), DmsProviderService::getDmsProviderServiceName);
        this.lookupService = checkNotNull(lookupService);
        this.dao = checkNotNull(dao);
        this.userSupplier = checkNotNull(userSupplier);
        this.scriptService = checkNotNull(scriptService);
        this.userCardService = checkNotNull(userCardService);
        this.userClassService = checkNotNull(userClassService);
        this.classeRepository = checkNotNull(classeRepository);
        this.virualAttributeService = checkNotNull(virualAttributeService);
        this.lockService = checkNotNull(lockService);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("DMS Service")
                .withConfigEnabler("org.cmdbuild.dms.enabled")
                .withEnabledChecker(configuration::isEnabled)
                .reloadOnConfigs(DmsConfiguration.class)
                //                .withStatusChecker(() -> getService().getMinionHandler().checkRuntimeStatus())//TODO check this; fails with pg provider
                .build();
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void start() {
        minionHandler.setStatus(MRS_READY);//TODO improve this, set status from minion service
    }

    @Override
    public void stop() {
        minionHandler.setStatus(MRS_NOTRUNNING);//TODO improve this, set status from minion service
    }

    @Override
    public boolean isEnabled() {
        return MinionComponent.super.isEnabled();
    }

    @Override
    public String getDefaultDmsCategory() {
        return config.getDefaultDmsCategory();
    }

    @Override
    public DmsProviderService getService() {
        return checkNotNull(services.get(config.getService()), "dms service not found for name = %s", config.getService());
    }

    @Override
    public LookupValue getCategoryLookup(String classId, String category) {
        return getCategoryLookup(dao.getClasse(classId), category);
    }

    @Override
    public LookupValue getCategoryLookup(Classe classe, String category) {
        return lookupService.getLookupByTypeAndCodeOrDescriptionOrId(getCategoryLookupType(classe).getName(), category);
    }

    @Override
    public LookupType getCategoryLookupType(String classId) {
        return getCategoryLookupType(dao.getClasse(classId));
    }

    @Override
    public LookupType getCategoryLookupType(Classe classe) {
        return lookupService.getLookupType(firstNotBlank(classe.getDmsCategoryOrNull(), config.getDefaultDmsCategory()));
    }

    @Override
    public DocumentInfoAndDetail getCardAttachmentById(String className, long cardId, String documentId) {
        checkDmsEnabled();
        className = dao.getTypeName(className, cardId);
        DmsCategoryHelper helper = helper(className);
        DocumentInfoAndDetail document = getService().getDocument(documentId);
        document = helper.cmisCategoryToCmdbuildCategory(document);
        document = helper.getDocumentMetadata(document, cardId);
        return document;
    }

    @Override
    public DocumentInfoAndDetail getCardAttachmentById(String documentId) {//TODO improve this! poor performance!!
        checkDmsEnabled();
        long cardId = dao.selectAll().from(DMS_MODEL_PARENT_CLASS).where(DOCUMENT_ATTR_DOCUMENTID, EQ, checkNotBlank(documentId)).getCard().get(DOCUMENT_ATTR_CARD, Long.class);
        return getCardAttachmentById(dao.getType(cardId).getName(), cardId, documentId);
    }

    @Override
    public DocumentInfoAndDetail getCardAttachmentByMetadataId(long metadataCardId) {//TODO improve this! poor performance!!
        checkDmsEnabled();
        Card metadataCard = dao.selectAll().from(DMS_MODEL_PARENT_CLASS).where(ATTR_ID, EQ, metadataCardId).getCard();
        long cardId = metadataCard.get(DOCUMENT_ATTR_CARD, Long.class);
        String documentId = checkNotBlank(metadataCard.getString(DOCUMENT_ATTR_DOCUMENTID));
        return getCardAttachmentById(dao.getType(cardId).getName(), cardId, documentId);
    }

    @Override
    public int getCardAttachmentCountSafe(CardIdAndClassName card) {
        try {
            return isEnabled() ? getCardAttachments(card.getClassName(), card.getId(), emptyOptions(), false).size() : 0;//TODO improve this, avoid dms service call
        } catch (Exception ex) {
            logger.warn(marker(), "error reading attachments for card = {}", card, ex);
            return 0;
        }
    }

    @Override
    public List<DocumentInfoAndDetail> getCardAttachments(String className, long cardId, DaoQueryOptions queryOptions, boolean includeMetadata) {
        return doGetCardAttachments(className, cardId, queryOptions, includeMetadata, true);
    }

    @Override
    public DocumentInfoAndDetail getCardAttachmentFromDms(String className, long cardId, String fileName) {
        List<DocumentInfoAndDetail> documents = checkDocuments(getService().getDocuments(className, cardId));
        return documents.stream().filter(d -> d.getFileName().equals(fileName)).collect(toOptional()).orElse(null);
    }

    private List<DocumentInfoAndDetail> doGetCardAttachments(String className, long cardId, DaoQueryOptions queryOptions, boolean includeMetadata, boolean syncDocuments) {
        checkDmsEnabled();
        className = dao.getTypeName(className, cardId);
        logger.debug("search all documents for className = {} and cardId = {}", className, cardId);
        DmsCategoryHelper helper = helper(className);
        List<DocumentInfoAndDetail> documents = checkDocuments(getService().getDocuments(className, cardId));
        List<DocumentInfoAndDetail> documentsWithMatchingContent = list();
        documents = list(documents).map(helper::cmisCategoryToCmdbuildCategory);
        if (includeMetadata) {
            documents = list(documents).map(d -> helper.getDocumentMetadataSafe(d, cardId));
        }
        if (syncDocuments) {
            removeMissingAttachments(documents, className, cardId);
        }
        if (!queryOptions.getFilter().isNoop()) {
            if (queryOptions.getFilter().hasFulltextFilter()) {
                List<String> attachmentsMatchingFilter = getService().queryDocumentsForCard(queryOptions.getFilter().getFulltextFilter().getQuery(), className, cardId);
                documentsWithMatchingContent = list(documents).stream().filter(d -> attachmentsMatchingFilter.contains(d.getDocumentId())).collect(toList());
            }
            List<String> matchingAttributeFilterIds = list();
            Pair<String, Boolean> commonDmsModel = getCommonDmsModel(className);
            if (commonDmsModel.getRight()) {
                dao.queryFromSuperclass(commonDmsModel.getLeft()).enableSmartSubclassFilterProcessing().enableAllSubclassesProcessing()
                        .withOptions(DaoQueryOptionsImpl.copyOf(queryOptions).withSorter(CmdbSorter.class.cast(null)).build()).getCards().forEach(c -> matchingAttributeFilterIds.add(c.getString(DOCUMENT_ATTR_DOCUMENTID)));
            } else {
                dao.select(DOCUMENT_ATTR_DOCUMENTID).from(commonDmsModel.getLeft())
                        .withOptions(DaoQueryOptionsImpl.copyOf(queryOptions).withSorter(CmdbSorter.class.cast(null)).build()).getCards().forEach(c -> matchingAttributeFilterIds.add(c.getString(DOCUMENT_ATTR_DOCUMENTID)));
            }
            documents = list(documents).withOnly(Predicates.compose(matchingAttributeFilterIds::contains, DocumentInfoAndDetail::getDocumentId));
            documentsWithMatchingContent.removeAll(documents);
            documents.addAll(documentsWithMatchingContent);
        }
        if (className.equals("Email") || className.equals("_CalendarEvent")) {//TODO handle permission "inheritance" when handling events
            return documents;
        } else {
            return documents.stream().filter(compose(buildCanReadAttachmentWithCategoryFilter(className)::test, DocumentInfoAndDetail::getCategory)).collect(toList());
        }
    }

    private static List<DocumentInfoAndDetail> checkDocuments(List<DocumentInfoAndDetail> list) {
        try {
            uniqueIndex(list, DocumentInfoAndDetail::getDocumentId);
            uniqueIndex(list, DocumentInfoAndDetail::getFileName);
            return list;
        } catch (Exception ex) {
            throw new DmsException(ex, "invalid document list returned from dms provider");
        }
    }

    //Duplicated function
    private Pair<String, Boolean> getCommonDmsModel(String className) {
        Classe userClass = userClassService.getUserClass(className);

        if (userClass.hasDmsCategory()) {
            List<String> dmsModels = list();
            lookupService.getAllLookup(userClass.getDmsCategory()).forEach(v -> dmsModels.add(v.getDmsModelClass()));
            if (!dmsModels.isEmpty() && Collections.frequency(dmsModels, dmsModels.get(0)) == dmsModels.size()) {
                return Pair.of(dmsModels.get(0), false);
            }
            Set<String> commonAncestor = set();
            dmsModels.forEach(m -> {
                commonAncestor.addAll(classeRepository.getClasse(m).getAncestors().stream().filter(a -> !a.equals(BASE_CLASS_NAME)).collect(toSet()));
            });
            if (!commonAncestor.isEmpty()) {
                return Pair.of(commonAncestor.iterator().next(), true);
            }
        }
        return Pair.of(DMS_MODEL_PARENT_CLASS, true);
    }

    private Predicate<String> buildCanReadAttachmentWithCategoryFilter(String className) {
        Classe userClass = userClassService.getUserClass(className);
        return (category) -> {
            if (category == null || category.isBlank()) {
                return true;
            }
            Set<GrantAttributePrivilege> categoryPermission = userClass.getDmsPermissions().getOrDefault(format("%s_%s", userClass.hasDmsCategory() ? userClass.getDmsCategory() : config.getDefaultDmsCategory(), lookupService.getLookup(toLong(category)).getCode()), set(GAP_DEFAULT));
            if (categoryPermission.contains(GAP_READ)) {
                return true;
            } else if (categoryPermission.contains(GAP_DEFAULT)) {
                return userClass.isProcess() ? userClass.hasUiPermission(CP_WF_BASIC) : userClass.hasUiPermission(CP_READ);//TODO improve process permission (?)
            } else {
                return false;
            }
        };
    }

    @Override
    @Nullable
    public DocumentInfoAndDetail getCardAttachmentOrNull(String className, long cardId, String fileName) {
        checkDmsEnabled();
        className = dao.getTypeName(className, cardId);
        return doGetCardAttachments(className, cardId, DaoQueryOptionsImpl.emptyOptions(), true, false).stream().filter((input) -> input.getFileName().equals(fileName)).collect(toOptional()).orElse(null);//TODO replace with more efficent query
    }

    @Override
    public boolean attachmentWithFileNameAlreadyExistsForCard(long cardId, String filename) {
        return dao.selectAll().from(DMS_MODEL_PARENT_CLASS).where("FileName", EQ, filename).where("Card", EQ, cardId).getCardOrNull() != null;
    }

    @Override
    public List<DocumentInfoAndDetail> getCardAttachmentVersions(String className, long cardId, String filename) {
        checkDmsEnabled();
        className = dao.getTypeName(className, cardId);
        DocumentInfoAndDetail document = getCardAttachmentWithFilename(className, cardId, filename);
        List<DocumentInfoAndDetail> documents = getService().getDocumentVersions(document.getDocumentId());
        documents = list(documents).map(helper(className)::cmisCategoryToCmdbuildCategory);
        return documents;
    }

    @Override
    public DocumentInfoAndDetail create(String className, long cardId, DocumentData documentData) {
        return checkNotNull(doCreate(className, cardId, documentData, OEA_FAIL));
    }

    @Override
    @Nullable
    public DocumentInfoAndDetail createAndSkipExisting(String className, long cardId, DocumentData documentData) {
        return doCreate(className, cardId, documentData, OEA_SKIP);
    }

    @Override
    public DocumentInfoAndDetail createAndReplaceExisting(String className, long cardId, DocumentData documentData) {
        return checkNotNull(doCreate(className, cardId, documentData, OEA_REPLACE));
    }

    @Override
    public DocumentInfoAndDetail updateDocumentWithAttachmentId(String className, long cardId, String documentId, DocumentData documentData) {
        checkDmsEnabled();
        className = dao.getTypeName(className, cardId);
        try (AutoCloseableItemLock lock = acquireLock(className, cardId)) {
            documentData = helperDocumentData(documentData);
            DmsCategoryHelper helper = helper(className);
            DocumentInfoAndDetail old = getCardAttachmentById(className, cardId, documentId);
            checkArgument(!documentData.hasData() || equal(getExtension(old.getFileName()).toLowerCase(), getExtension(documentData.getFilename()).toLowerCase()), "CM: invalid file extension (old file =< %s >, new file =< %s >", old.getFileName(), documentData.getFilename());
            checkArgument(equal(helper.categoryToCode(documentData.getCategory()), helper.categoryToCode(old.getCategory())), "cannot change category, previous =< %s >, new =< %s >", old.getCategory(), documentData.getCategory());
            documentData = helper.cmdbuildCategoryToCmisCategory(documentData);
            documentData = DocumentDataImpl.copyOf(documentData).withAuthor(userSupplier.getUsername()).build();
            DocumentInfoAndDetail newDocument = getService().update(documentId, helper.mapMetadataForDms(documentData));//TODO validate card id, class id
            newDocument = helper.cmisCategoryToCmdbuildCategory(newDocument);
            newDocument = helper.setDocumentMetadata(documentId, newDocument, documentData, cardId);
            autoLink(className, cardId, old, newDocument);
            getService().updateFolder(dao.getCard(className, cardId));
            return newDocument;
        }
    }

    @Override
    public DocumentInfoAndDetail updateDocumentWithMetadataId(String className, long cardId, long metadataCardId, DocumentData documentData) {
        String documentId = checkNotBlank(dao.selectAll().from(DMS_MODEL_PARENT_CLASS).where(DOCUMENT_ATTR_CARD, EQ, cardId).where(ATTR_ID, EQ, metadataCardId).getCard().getString(DOCUMENT_ATTR_DOCUMENTID));
        return updateDocumentWithAttachmentId(className, cardId, documentId, documentData);
    }

    @Override
    public DocumentInfoAndDetail updateDocumentMetadata(String className, long cardId, String documentId, Map<String, Object> metadata) {
        return helper(className).setDocumentMetadata(documentId, getCardAttachmentById(className, cardId, documentId), metadata, cardId);
    }

    @Override
    public void deleteByMetadataId(String classname, long cardId, Long metadataCardId) {
        String documentId = checkNotBlank(dao.selectAll().from(DMS_MODEL_PARENT_CLASS).where(DOCUMENT_ATTR_CARD, EQ, cardId).where(ATTR_ID, EQ, metadataCardId).getCard().getString(DOCUMENT_ATTR_DOCUMENTID));
        deleteByAttachmentId(classname, cardId, documentId);
    }

    @Override
    public DataHandler download(String attachmentId, @Nullable String version) {
        checkDmsEnabled();
        return getService().download(attachmentId, version);
    }

    @Override
    public Optional<DataHandler> preview(String attachmentId) {
        checkDmsEnabled();
        return getService().preview(attachmentId);
    }

    @Override
    public void delete(String className, long cardId, String filename) {
        checkDmsEnabled();
        className = dao.getTypeName(className, cardId);
        try (AutoCloseableItemLock lock = acquireLock(className, cardId)) {
            DmsCategoryHelper helper = helper(className);
            DocumentInfoAndDetail document = getCardAttachmentWithFilename(className, cardId, filename);
            helper.clearDocumentMetadata(document, cardId);
            getService().delete(document.getDocumentId());
            autoLink(className, cardId, document, null);
        }
    }

    private void removeMissingAttachments(List<DocumentInfoAndDetail> documents, String className, long cardId) {
        logger.debug("sync missing attachments for card = {}[{}], current documents = {} {}", className, cardId, documents.size(), documents);
        if (dao.getJdbcTemplate().queryForObject(
                !documents.isEmpty()
                ? format("SELECT EXISTS ( SELECT * FROM \"DmsModel\" WHERE \"Status\" = 'A' AND \"Card\" = %s AND \"DocumentId\" NOT IN (%s)) ", cardId, list(documents).map(DocumentInfoAndDetail::getDocumentId).map(SqlQueryUtils::systemToSqlExpr).collect(joining(",")))
                : format("SELECT EXISTS ( SELECT * FROM \"DmsModel\" WHERE \"Status\" = 'A' AND \"Card\" = %s) ", cardId), Boolean.class)) {
            try (AutoCloseableItemLock lock = acquireLock(className, cardId)) {
                int modified = !documents.isEmpty()
                        ? dao.getJdbcTemplate().update(format("UPDATE \"DmsModel\" SET \"Status\" = 'N' WHERE \"Status\" = 'A' AND \"Card\" = %s AND \"DocumentId\" NOT IN (%s)", cardId, list(documents).map(DocumentInfoAndDetail::getDocumentId).map(SqlQueryUtils::systemToSqlExpr).collect(joining(","))))
                        : dao.getJdbcTemplate().update(format("UPDATE \"DmsModel\" SET \"Status\" = 'N' WHERE \"Status\" = 'A' AND \"Card\" = %s", cardId));
                logger.debug("removed {} missing attachments for card = {} (actual count = {})", modified, cardId, documents.size());
            }
        }
    }

    enum OnExistingAction {
        OEA_REPLACE, OEA_SKIP, OEA_FAIL
    }

    @Nullable
    private DocumentInfoAndDetail doCreate(String className, long cardId, DocumentData documentData, OnExistingAction existingAction) {
        checkDmsEnabled();
        className = dao.getTypeName(className, cardId);
        try (AutoCloseableItemLock lock = acquireLock(className, cardId)) {
            documentData = helperDocumentData(documentData);
            DmsCategoryHelper helper = helper(className);
            documentData = helper.cmdbuildCategoryToCmisCategory(documentData);
            documentData = DocumentDataImpl.copyOf(documentData).withAuthor(userSupplier.getUsername()).build();
            boolean attachmentWithFilenameAlreadyExists = config.checkAttachmentExistanceOnlyOnDB()
                    ? attachmentWithFileNameAlreadyExistsForCard(cardId, documentData.getFilename())
                    : getCardAttachmentOrNull(className, cardId, documentData.getFilename()) != null;
            if (attachmentWithFilenameAlreadyExists) {
                return switch (existingAction) {
                    case OEA_SKIP -> {
                        logger.debug("document with filename {} already exists, skipping creation", documentData.getFilename());
                        yield null;
                    }
                    case OEA_REPLACE -> {
                        logger.debug("document with filename {} already exists, replace", documentData.getFilename());
                        yield updateDocumentWithFilename(className, cardId, documentData.getFilename(), documentData);
                    }
                    case OEA_FAIL ->
                        throw new DmsException("CM: file name conflict: a document with file name =< %s > does already exist for class = %s card = %s ", documentData.getFilename(), className, cardId);
                };
            } else {
                logger.debug("create document = {}", documentData);
                documentData = helper.cmdbuildCategoryToCmisCategory(documentData);
                documentData = DocumentDataImpl.copyOf(documentData).withAuthor(userSupplier.getUsername()).build();
                DocumentInfoAndDetail document = getService().create(className, cardId, helper.mapMetadataForDms(documentData));
                document = helper.cmisCategoryToCmdbuildCategory(document);
                document = helper.setDocumentMetadata(document.getDocumentId(), document, documentData, cardId);
                autoLink(className, cardId, null, document);
                getService().updateFolder(dao.getCardOrDraft(className, cardId));
                return document;
            }
        }
    }

    private DocumentData helperDocumentData(DocumentData documentData) {
        if (config.isImageResizeEnabled() && documentData.hasData()) {
            return resizeImage(documentData);
        }
        return documentData;
    }

    private DocumentData resizeImage(DocumentData documentData) {
        ImageThumbnailer imageThumbnailer = new ImageThumbnailer(config.getImageResizePixel(), config.getImageResizePixel());
        if (imageThumbnailer.getAcceptedMIMETypes().contains(getContentType(documentData.getData()))) {
            try {
                DataHandler generateThumbnail = imageThumbnailer.generateThumbnail(documentData.getDataSource().getInputStream()).get();
                return DocumentDataImpl.copyOf(documentData).withData(generateThumbnail.getInputStream().readAllBytes()).build();
            } catch (IOException ex) {
                logger.error("cannot resize image =< {} >", documentData.getFilename());
            }
        }
        return documentData;
    }

    private AutoCloseableItemLock acquireLock(String className, long cardId) {
        return lockService.aquireLockOrWaitOrFail(key(dao.getTypeName(className, cardId), cardId), LS_REQUEST);
    }

    @Nullable
    private String categoryToId(@Nullable String categoryLookup, @Nullable Object value) {
        if (isBlank(toStringOrNull(value))) {
            return null;
        } else {
            try {
                return lookupService.getLookupByTypeAndCodeOrDescriptionOrId(firstNotBlank(categoryLookup, config.getDefaultDmsCategory()), toStringNotBlank(value)).getId().toString();
            } catch (Exception ex) {
                logger.warn(marker(), "received invalid category code from cmis =< {} >", value, ex);
                return null;
            }
        }
    }

    @Override
    public DataHandler exportAllDocuments() {
        return newDataHandler(getService().exportAllDocumentsAsZipFile(), "application/zip", format("dms_export_%s.zip", CmDateUtils.dateTimeFileSuffix()));
    }

    @Override
    public void checkRegularFileAttachment(DocumentData documentData, String classId) {
        Set<String> allowedExtensions = getAllowedFileExtension(documentData, classId);
        if (!allowedExtensions.isEmpty()) {
            checkArgument(allowedExtensions.contains(FilenameUtils.getExtension(documentData.getFilename()).toLowerCase()), "CM: invalid file =< %s > : file type not allowed", documentData.getFilename());
        }
    }

    @Override
    public void checkRegularFileSize(DocumentData documentData, String classId) {
        Integer maxFileSize = getMaxFileSize(documentData, classId);
        if (maxFileSize != null && documentData.hasData()) {
            checkArgument((documentData.getData().length / 1048576) < maxFileSize, "CM: File =< %s > exceeds maximum file size of %s MB", documentData.getFilename(), maxFileSize);
        }
    }

    private Set<String> getAllowedFileExtension(DocumentData documentData, String classId) {
        if (classId != null) {
            DmsCategoryHelper helper = helper(classId);
            Classe dmsModel = helper.getDmsModel(documentData.getCategory());
            LookupValue dmsCategory = null;
            if (documentData.getCategory() != null) {
                dmsCategory = lookupService.getLookupByTypeAndId(helper.getCategoryType(), toLong(documentData.getCategory()));
            }
            if (dmsCategory != null && !dmsCategory.getDmsAllowedExtensions().isEmpty()) {
                return dmsCategory.getDmsAllowedExtensions().stream().map(String::toLowerCase).collect(toSet());
            } else if (!dmsModel.getMetadata().getDmsAllowedExtensions().isEmpty()) {
                return dmsModel.getMetadata().getDmsAllowedExtensions().stream().map(String::toLowerCase).collect(toSet());
            }
        }
        if (config.isRegularAttachmentsFileExtensionCheckEnabled()) {
            return new HashSet<>(config.getRegularAttachmentsAllowedFileExtensions()).stream().map(String::toLowerCase).collect(toSet());
        } else {
            return emptySet();
        }
    }

    private Integer getMaxFileSize(DocumentData documentData, String classId) {
        DmsCategoryHelper helper = helper(classId);
        Classe dmsModel = helper.getDmsModel(documentData.getCategory());
        LookupValue dmsCategory = null;
        if (documentData.getCategory() != null) {
            dmsCategory = lookupService.getLookupByTypeAndId(helper.getCategoryType(), toLong(documentData.getCategory()));
        }
        if (dmsCategory != null && dmsCategory.getMaxFileSize() != null) {
            return dmsCategory.getMaxFileSize();
        } else if (dmsModel.getMetadata().getMaxFileSize() != null) {
            return dmsModel.getMetadata().getMaxFileSize();
        } else if (config.isMaxFileSizeCheckEnabled()) {
            return config.getMaxFileSize();
        } else {
            return null;
        }
    }

    @Override
    public void checkIncomingEmailAttachment(DocumentData documentData) {
        if (config.isIncomingEmailFileExtensionCheckEnabled() && isNotBlank(documentData.getFilename())) {
            checkArgument(config.getIncomingEmailAttachmentsAllowedFileExtensions().contains(FilenameUtils.getExtension(documentData.getFilename()).toLowerCase()), "CM: invalid file =< %s > : file type not allowed", documentData.getFilename());
        }
    }

    @Override
    public Classe getDmsModel(String className, @Nullable String category) {
        return helper(className).getDmsModel(category);
    }

    private void checkDmsEnabled() {
        checkArgument(isEnabled(), "dms service is not enabled");
    }

    private DmsCategoryHelper helper(String className) {
        return helper(dao.getClasse(className));
    }

    private DmsCategoryHelper helper(Classe classe) {
        return new DmsCategoryHelper(classe);
    }

    private void autoLink(String classId, long cardId, @Nullable DocumentInfoAndDetail previousDocument, @Nullable DocumentInfoAndDetail nextDocument) {
        if (config.hasAutolinkHelperScript()) {
            autoLink(dao.getCard(classId, cardId), previousDocument, nextDocument);
        }
    }

    private void autoLink(Card card, @Nullable DocumentInfoAndDetail previousDocument, @Nullable DocumentInfoAndDetail nextDocument) {
        if (config.hasAutolinkHelperScript()) {
            logger.debug("execute autolink operations for document {} -> {}", previousDocument, nextDocument);
            List<AutolinkOperation> before = previousDocument == null ? emptyList() : getAutolinkOperationsForDocument(card, previousDocument), after = nextDocument == null ? emptyList() : getAutolinkOperationsForDocument(card, nextDocument);
            List<String> basepath = Splitter.on("/").omitEmptyStrings().splitToList(nullToEmpty(config.getAutolinkBasePath()));
            before.forEach(o -> {
                logger.debug("revert autolink operation = {}", o);
                getService().deleteLink(list(basepath).with(o.getTargetLink()));
            });
            after.forEach(o -> {
                logger.debug("execute autolink operation = {}", o);
                getService().createLink(nextDocument, list(basepath).with(o.getTargetLink()));
            });
        }
    }

    private List<AutolinkOperation> getAutolinkOperationsForDocument(Card card, DocumentInfoAndDetail document) {
        List<AutolinkOperation> list = list();
        logger.debug("get autolink operations for card = {} document = {}", card, document);
        scriptService.helper(getClass())
                .withScript(config.getAutolinkHelperScript())
                .withData("autolink", (AutolinkApi) (Iterable<String> path) -> {
                    list.add(new AutolinkOperation(list(path)));
                }).withData("document", map("filename", document.getFileName(), "category", document.getCategory()), "card", map(card.getAllValuesAsMap()).with("_id", card.getIdOrNull(), "_type", card.getTypeName()))
                .execute();
        return list;
    }

    public interface AutolinkApi {

        void addLink(Iterable<String> path);

        default void addLink(String path) {
            AutolinkApi.this.addLink(Splitter.on("/").omitEmptyStrings().split(path));
        }
    }

    private static class AutolinkOperation {

        private final List<String> targetLink;

        public AutolinkOperation(List<String> targetLink) {
            this.targetLink = ImmutableList.copyOf(targetLink);
            targetLink.forEach(CmPreconditions::checkNotBlank);
        }

        public List<String> getTargetLink() {
            return targetLink;
        }

        public String getKey() {
            return key(getTargetLink());
        }

        @Override
        public String toString() {
            return "AutolinkOperation{" + "targetLink=" + targetLink + '}';
        }

    }

    private class DmsCategoryHelper {

        private final Classe classe;

        public DmsCategoryHelper(Classe classe) {
            this.classe = checkNotNull(classe);
            checkArgument(classe.isLeafClass(), "invalid non-leaf class = %s", classe);
        }

        private DocumentInfoAndDetail cmisCategoryToCmdbuildCategory(DocumentInfoAndDetail document) {
            if (isBlank(document.getCategory())) {
                return document;
            } else {
                return DocumentInfoAndDetailImpl.copyOf(document).withCategory(categoryToId(classe.getDmsCategoryOrNull(), document.getCategory())).build();
            }
        }

        private DocumentData mapMetadataForDms(DocumentData data) {
            return DocumentDataImpl.copyOf(data).withMetadata((Map) map().accept(m -> {
                if (isNotBlank(data.getCategory())) {
                    getDmsModel(categoryToId(classe.getDmsCategoryOrNull(), data.getCategory())).getActiveServiceAttributes().stream().filter(a -> isNotBlank(a.getMetadata().getSyncToDmsAttr())).forEach(a -> {
                        Object value = data.getMetadata().get(a.getName());
                        value = rawToSystem(a, value);
                        switch (a.getType().getName()) {
                            case LOOKUP ->
                                value = lookupService.getLookup((org.cmdbuild.common.beans.LookupValue) value);
//                            case FOREIGNKEY:
//                            case REFERENCE:
                            //TODO
                        }
                        logger.trace("map metadata for dms, copy attr =< {} > to dms attr =< {} > ( value =< {} > )", a.getName(), a.getMetadata().getSyncToDmsAttr(), value);
                        m.put(a.getMetadata().getSyncToDmsAttr(), value);
                    });
                }
            })).withDescription(data.getDescription()).build();
        }

        private DocumentData cmdbuildCategoryToCmisCategory(DocumentData documentData) {
            if (isBlank(documentData.getCategory())) {
                return documentData;
            } else {
                return DocumentDataImpl.copyOf(documentData).withCategory(categoryToCode(documentData.getCategory())).build();
            }
        }

        @Nullable
        public String categoryToCode(@Nullable String value) {
            if (isBlank(value) || (isNumber(value) && isNullOrLtEqZero(toLongOrNull(value)))) {
                return null;
            } else {
                return lookupService.getLookupByTypeAndCodeOrDescriptionOrId(firstNotBlank(classe.getDmsCategoryOrNull(), config.getDefaultDmsCategory()), value).getCode();
            }
        }

        private String getCategoryType() {
            return firstNotBlank(classe.getDmsCategoryOrNull(), config.getDefaultDmsCategory());
        }

        private DocumentInfoAndDetail getDocumentMetadata(DocumentInfoAndDetail document, long cardId) {
            return DocumentInfoAndDetailImpl.copyOf(document).withMetadata(virualAttributeService.loadVirtualAttributes(getDocumentMetadataCardCreateIfMissing(document, cardId))).build();
        }

        private DocumentInfoAndDetail getDocumentMetadataSafe(DocumentInfoAndDetail document, long cardId) {
            try {
                return getDocumentMetadata(document, cardId);
            } catch (Exception ex) {
                logger.warn(marker(), "error retrieving document metadata for document = {} card = {}", document, cardId, ex);
                return document;
            }
        }

        private DocumentInfoAndDetail setDocumentMetadata(String documentId, DocumentInfoAndDetail document, DocumentData data, long cardId) {
            return setDocumentMetadata(documentId, document, data.getMetadata(), cardId);
        }

        private void clearDocumentMetadata(DocumentInfoAndDetail document, long cardId) {
            try (AutoCloseableItemLock lock = lockService.aquireLockOrWaitOrFail(key(classe.getName(), cardId), LS_REQUEST)) {
                Card card = getDocumentMetadataCardOrNull(cardId, document);
                if (card != null) {
                    dao.delete(card);
                }
            }
        }

        private DocumentInfoAndDetail setDocumentMetadata(String documentId, DocumentInfoAndDetail document, Map<String, Object> data, long cardId) {
            try (AutoCloseableItemLock lock = lockService.aquireLockOrWaitOrFail(key(classe.getName(), cardId), LS_REQUEST)) {
                Classe dmsModel = checkNotNull(getDmsModel(document), "cannot set document metadata: dms model not available for document = %s", document);
                logger.debug("set document metadata for document = {}, raw data =\n\n{}\n", document, mapToLoggableStringLazy(data));
                Card card = getDocumentMetadataCardOrNull(cardId, document.getCategory(), documentId);
                logger.debug("dms model type = {} metadata card = {}", dmsModel, card);
                Map<String, Object> attrs = map(data)
                        .filterKeys(k -> dmsModel.hasAttributeActive(k) && (dmsModel.getAttribute(k).hasServicePermission(AP_CREATE) || dmsModel.getAttribute(k).hasServiceWritePermission()))
                        .with(DOCUMENT_ATTR_DESCRIPTION, document.getDescription(),
                                DOCUMENT_ATTR_DOCUMENTID, document.getDocumentId(),
                                DOCUMENT_ATTR_CARD, cardId,
                                DOCUMENT_ATTR_FILENAME, document.getFileName(),
                                DOCUMENT_ATTR_VERSION, document.getVersion(),
                                DOCUMENT_ATTR_MIMETYPE, document.getMimeType(),
                                DOCUMENT_ATTR_CATEGORY, document.getCategory(),
                                DOCUMENT_ATTR_SIZE, document.getFileSize(),
                                DOCUMENT_ATTR_HASH, document.getHash(),
                                DOCUMENT_ATTR_CREATED, document.getCreated());
                logger.debug("set document metadata for document = {}, processed data =\n\n{}\n", document, mapToLoggableStringLazy(attrs));
                if (card != null) {
                    card = CardImpl.copyOf(card).withAttributes(attrs).build();
                    card = userCardService.sanitizeValues(card);
                    card = dao.update(card);
                } else {
                    card = CardImpl.buildCard(dmsModel, attrs);
                    card = userCardService.sanitizeValues(card);
                    card = dao.create(card);
                }
                return DocumentInfoAndDetailImpl.copyOf(document).withMetadata(card).build();
            }
        }

        private Card getDocumentMetadataCardCreateIfMissing(DocumentInfoAndDetail document, long cardId) {
            Card card = getDocumentMetadataCardOrNull(cardId, document);
            if (card == null) {
                logger.debug("document metadata (card) not found for document = {} card = {}, will create now", document.getDocumentId(), cardId);
                card = checkNotNull(setDocumentMetadata(document.getDocumentId(), document, emptyMap(), cardId).getMetadata());
            }
            logger.debug("get document metadata (card) for document = {} card = {}, found data =\n\n{}\n", document, cardId, mapToLoggableStringLazy(card.getAllValuesAsMap()));
            return card;
        }

        @Nullable
        private Card getDocumentMetadataCardOrNull(long cardId, DocumentInfoAndDetail document) {
            return getDocumentMetadataCardOrNull(cardId, document.getCategory(), document.getDocumentId());
        }

        @Nullable
        private Card getDocumentMetadataCardOrNull(long cardId, String category, String documentId) {
            return dao.selectAll().from(getDmsModel(category)).whereExpr("\"Card\" = ? AND \"DocumentId\" = ?", cardId, checkNotBlank(documentId)).getCardOrNull();
        }

        private Classe getDmsModel(DocumentInfoAndDetail document) {
            return getDmsModel(document.getCategory());
        }

        private Classe getDmsModel(@Nullable String category) {
            if (isBlank(category)) {
                return dao.getClasse(DMS_MODEL_DEFAULT_CLASS);
            } else {
                LookupValue lookup = lookupService.getLookupByTypeAndId(getCategoryType(), toLong(category));
                checkArgument(lookup.getType().isDmsCategorySpeciality(), "invalid dms category lookup = %s", lookup);
                Classe dmsModel = dao.getClasse(firstNotBlank(lookup.getConfig().getDmsModelClass(), DMS_MODEL_DEFAULT_CLASS));
                checkArgument(dmsModel.isDmsModel(), "invalid dms model class = %s", dmsModel);
                return dmsModel;
            }
        }

    }

}
