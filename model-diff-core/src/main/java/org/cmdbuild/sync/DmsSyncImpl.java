/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.DocumentDataImpl.DocumentDataImplBuilder;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.modeldiff.dms.DocumentHandle;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class DmsSyncImpl implements DmsSync {

    private final LookupService lookupService;
    private final DmsService dmsService;
    private final ObjectTranslationService translationService;

    /**
     * This attributes come from serialization of <i>card attachment</i>,
     *
     * @see
     * {@link CardAttributeFileBasicSerializer}, {@link CardAttributeFileWsSerializer}
     */
    public final static Set<String> KNOWN_SERIALIZATION_KEYS = set(
            "_id", // the document id
            "_card", // the card metadata id that represents a card attachment
            // See CardAttributeFileBasicSerializer.serializeCategory() and CardAttributeFileWsSerializer.serializeCategory()
            "category", "Category", "_category", "_category_name", "_category_description", "_category_description_translation",
            "_can_update", "_can_delete",
            // See CardAttributeFileBasicSerializer.serializeDocument()
            "name", "description", "Description", "version", "author", "_author_description", "created", "modified",
            // Needed by Mobile offline when adding a card FILE attachment
            "documentId",
            // Used while updating attachment, to enforce use of new major for version
            "majorVersion",
            // seen sent back from UI
            "Version", "__docRevision"
    );

    public DmsSyncImpl(LookupService lookupService, ObjectTranslationService translationService,
            DmsService dmsService) {
        this.lookupService = checkNotNull(lookupService);
        this.dmsService = checkNotNull(dmsService);
        this.translationService = checkNotNull(translationService);
    }

    @Override
    public boolean isEnabled() {
        return dmsService.isEnabled();
    }

    @Override
    public String readDefaultDmsCategory() {
        return dmsService.getDefaultDmsCategory();
    }

    /**
     * <i>dms category</i> name (a {@link LookupType} name)
     * @param relatedClasse
     * @return
     */
    @Override
    public String getDmsCategory(Classe relatedClasse) {
        return relatedClasse.getDmsCategory();
    }

//    @Override
//    public Classe readDmsModel(String classId) {
//        // As in DmsModeWs.getDmsModel
//        Classe classe = dao.getClasse(classId);
//        checkArgument(classe.isDmsModel(), "invalid class =< %s >: not a dms model", classId);
//        return classe;
//    }
    @Override
    public LookupType readDmsCategory(long lookupTypeId) {
        return lookupService.getLookupType(lookupTypeId);
    }

    @Override
    public List<LookupType> readDmsCategories(String filterStr) {
        // As in DmsCategoryWs.readAll()
        return lookupService.getAllTypes(filterStr).stream().filter(LookupType::isDmsCategorySpeciality).collect(toList());
    }

    /**
     *
     * @param classe the {@link Card} related {@link Classe}.
     * @param fallbackDmsCategory a fallback <i>dms category</i>, normally the
     * <i>default dms category</i>, see {@link #readDefaultDmsCategory()}
     * @return pair of <i>dms category</i> (lookup type name) and all related
     * lookup values.
     */
    @Override
    public Pair<String, List<LookupValue>> readDmsCategoryValues(Classe classe, String fallbackDmsCategory) {
        // As in ClassSerializationHelper.buildFullDetailExtendedResponse(): a DmsModel (a Classe that represents a dms category) has the lookup type name in its metadata
        String dmsCategoryType = classe.hasDmsCategory() ? classe.getDmsCategory() : fallbackDmsCategory;

        List<LookupValue> allDmsCategoryValues = readDmsCategoryValues(dmsCategoryType);
        return Pair.of(dmsCategoryType, allDmsCategoryValues);
    }

    /**
     *
     * @param dmsCategoryType
     * @return all <i>dms category</i> related lookup values.
     */
    @Override
    public List<LookupValue> readDmsCategoryValues(String dmsCategoryType) {
//        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
//        // As in LookupServiceimpl.getAllLookup()
//        return lookupRepository.getByType(lookupTypeName, filter);

        // (simpler to mock in tests that version with lookupRepository)
        return lookupService.getAllLookup(dmsCategoryType).stream().collect(toList());
    }

    @Override
    public Map<String, String> fetchDmsCategoryDescrTranslations(String lookupTypeName, List<LookupValue> valuesList) {
        return valuesList.stream()
                .collect(
                        Collectors.toMap(
                                LookupValue::getCode,
                                v -> translationService.translateLookupDescriptionSafe(lookupTypeName, v.getCode(), v.getDescription())
                        )
                );
    }

    @Override
    public Classe readDmsModel(Classe relatedClasse) {
        return dmsService.getDmsModel(relatedClasse.getName(), relatedClasse.getDmsCategory());
    }

    /**
     * Uploads the document to the <i>dms</i> with the following sequence of
     * operations:
     * <ol>
     * <li>(if <code>dataHandler</code> found) uploads the document to the <i>dms</i>;
     * <li>creates a new metadata {@link Card} with the <i>dms model</i> properties
     * expected for given {@link Classe}, containing all the document metadata (that may contain
     * custom attributes defined in the <i>dms model</i>); see
     * {@link DmsCategoryHelper#setDocumentMetadata()} invoked by
     * {@link DmsService#doCreate()};
     * <li>returns a generated {@link DocumentInfoAndDetail} with that created
     * {@link Card} metadata;
     * <li>if a <code>FILE</code> attribute:
     * <ul>
     * <li>in given <code>relatedCard</code> is
     * valued with the metadata {@link Card} <code>id</code> in
     * {@link DmsService#doCreate()};
     * <li>when serializing in json the related {@link @Card}, the <code>FILE</code> attribute is valued with the
     * <code>dms</code> <code>documentId</code> and a packed map for all
     * metadata is added.
     * </ul>
     * </ol>
     *
     * @param relatedCard
     * @param relatedClasse
     * @param documentHandle
     * @return
     */
    @Override
    public DocumentInfoAndDetail attachmentUpload(Classe relatedClasse, Card relatedCard, DocumentHandle documentHandle) {
        DocumentDataImplBuilder docBuilder = buildDocumentData(relatedClasse, documentHandle);
        // See AttachmentWsHelper.create(String classId, String cardId, WsAttachmentData attachment, DataHandler dataHandler, boolean checkFile)
        docBuilder.withMajorVersion(true);

        DocumentData documentData = docBuilder.build();

        // See AttachmentWsHelper.create(String classId, long cardId, WsAttachmentData attachment, DataHandler dataHandler, boolean checkFile)
        dmsService.checkRegularFileAttachment(documentData, relatedClasse.getName());
        dmsService.checkRegularFileSize(documentData, relatedClasse.getName());

        return dmsService.create(relatedClasse.getName(),
                relatedCard.getId(),
                documentData);
    }

    /**
     * On an existing <i>{@link Card} attachment</i>, updates the document to the
     * <i>dms</i> with the following sequence of operations:
     * <ol>
     * <li>(only if document is changed, so {@link DocumentHandle#dataHandler} is not <code>null>)
     * uploads the document to the <i>dms</i>;
     * <li>updates the <i>metadata</i> {@link Card} with the <i>dms model</i> properties
     * expected for given {@link Classe}, containing all the document metadata (that may contain
     * custom attributes defined in the <i>dms model</i>);
     * <li>returns a generated {@link DocumentInfoAndDetail} with that created
     * {@link Card} metadata;
     * </ol>
     *
     * @param relatedClasse
     * @param relatedCard
     * @param documentHandle
     * @return
     */
    @Override
    public DocumentInfoAndDetail attachmentUpdate(Classe relatedClasse, Card relatedCard, DocumentHandle documentHandle) {
        DocumentDataImplBuilder docBuilder = buildDocumentData(relatedClasse, documentHandle);

        // Contains no document data nor filename if only updating attachment properties and not the related file
        DocumentData documentData = docBuilder.build();

        if (documentData.getFilename() != null) {
            // See AttachmentWsHelper.create(String classId, long cardId, String attachmentId, WsAttachmentData attachment, DataHandler dataHandler, boolean checkFile)
            dmsService.checkRegularFileAttachment(documentData, relatedClasse.getName());
            dmsService.checkRegularFileSize(documentData, relatedClasse.getName());
        }

        // See AttachmentWsHelper.update()
        // DocumentInfoAndDetail document = service.updateDocumentWithAttachmentId(classId, cardId, attachmentId, documentData);
        // but to avoid to have to access with old documentId (if document is to be changed with this merge),
        // metadata card id is used
        return dmsService.updateDocumentWithMetadataId(relatedClasse.getName(),
                relatedCard.getId(),
                documentHandle.getCardMetadataId(),
                documentData);
    }

    // @todo AFE tbc
    @Override
    public DataSource download(Classe relatedClasse, Card relatedCard, String filename) {
        return toDataSource(dmsService.getDocumentData(relatedClasse.getName(), relatedCard.getId(), filename));
    }

    /**
     * Removes the document to the <i>dms</i> with the following sequence of
     * operations (see
     * {@link DmsService#delete(java.lang.String, long, java.lang.String)}:
     * <ol>
     * <li>the {@link Card} with {@link Classe} the <i>dms model>, containing
     * all the document metadata (that may contain custom attributes defined in
     * the <i>dms model</i>) is deleted; see
     * {@link DmsCategoryHelper#clearDocumentMetadata()};
     * <li>uploads the document to the <i>dms</i>;
     * <li>the document in the <i>dms</i> is deleted, see {@link DmsProviderService#delete(java.lang.String);
     * <li>the <code>FILE</code> attribute in given <code>relatedCard</code> is
     * emptied, see {DmsServiceImpl#autoLink()} with <code>null</code>.
     * </ol>
     *
     * @param classe
     * @param relatedCard
     * @param filename
     */
    @Override
    public void delete(Classe classe, Card relatedCard, String filename) {
        dmsService.delete(classe.getName(), relatedCard.getId(), filename);
    }

    private DocumentDataImplBuilder buildDocumentData(Classe relatedClasse, DocumentHandle documentHandle) {
        DocumentDataImplBuilder docBuilder = DocumentDataImpl.builder()
                .withFilename(documentHandle.getFilename())
                .withCategory(String.valueOf(documentHandle.getCategoryId())) // the choosen id of LookupValue related to dms category
                .withData(documentHandle.getDataHandler()) // handles null, if this is an update (for attachment metadata properties) but attachment file is unchanged
                .withMetadata(dropDuplicatedSerializationKeys(documentHandle.getMetadata()));
        // Parameter that may be not sent on Card metadata update
        if (documentHandle.hasDescription()) {
            docBuilder.withDescription(documentHandle.getDescription());
        }
        if (documentHandle.hasAuthor()) {
            docBuilder.withAuthor(documentHandle.getAuthor());
        }

        if (documentHandle.hasMajorVersionFlag()) {
            docBuilder.withMajorVersion(true);
        }

        return docBuilder;
    }

    public static Map<String, Object> dropDuplicatedSerializationKeys(Map<String, Object> origMetadata) {
        return map(origMetadata).withoutKeys(KNOWN_SERIALIZATION_KEYS);
    }
}
