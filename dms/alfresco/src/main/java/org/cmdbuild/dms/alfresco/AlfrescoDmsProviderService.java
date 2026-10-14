package org.cmdbuild.dms.alfresco;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import java.nio.file.Path;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dms.DmsConfiguration;
import static org.cmdbuild.dms.DmsConfiguration.DMS_CONFIG_PROVIDER;
import org.cmdbuild.dms.DocumentData;
import static org.cmdbuild.dms.alfresco.AlfrescoRestClient.ALFRESCO_PROPERTY_AUTHOR;
import static org.cmdbuild.dms.alfresco.AlfrescoRestClient.ALFRESCO_PROPERTY_CATEGORY;
import static org.cmdbuild.dms.alfresco.AlfrescoRestClient.ALFRESCO_PROPERTY_DESCRIPTION;
import static org.cmdbuild.dms.alfresco.AlfrescoRestClient.ALFRESCO_PROPERTY_VERSION;
import org.cmdbuild.dms.alfresco.config.AlfrescoDmsConfiguration;
import static org.cmdbuild.dms.alfresco.utils.AlfrescoDmsUtils.decodeDocumentId;
import static org.cmdbuild.dms.alfresco.utils.AlfrescoDmsUtils.decodeDocumentVersion;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetailImpl.DocumentInfoAndDetailImplBuilder;
import static org.cmdbuild.dms.inner.DocumentPathUtils.buildDocumentPath;
import org.cmdbuild.exception.DmsException;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionUtils.buildMinionRuntimeStatusChecker;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.encode.CmEncodeUtils.encodeString;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmConvertUtils.toInt;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AlfrescoDmsProviderService implements DmsProviderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final DmsConfiguration dmsConfig;
    private final AlfrescoDmsConfiguration alfrescoConfig;
    private final AlfrescoRestClient client;
    private final MinionHandler minionHandler;

    private final String DMS_PROVIDER_ALFRESCO = "alfresco";

    public AlfrescoDmsProviderService(DaoService dao, DmsConfiguration dmsConfig, AlfrescoDmsConfiguration config) {
        this.dao = checkNotNull(dao);
        this.dmsConfig = checkNotNull(dmsConfig);
        this.alfrescoConfig = checkNotNull(config);
        this.client = new AlfrescoRestClient(config);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("DMS_ Alfresco")
                .withEnabledChecker(() -> this.dmsConfig.isEnabled(getDmsProviderServiceName()))
                .withStatusChecker(buildMinionRuntimeStatusChecker(this::isEnabled, this::isServiceOk))
                .reloadOnConfigs(AlfrescoDmsConfiguration.class, DMS_CONFIG_PROVIDER)
                .build();
    }

    @Override
    public boolean isServiceOk() {
        return client.isReady();
    }

    @Override
    public String getDmsProviderServiceName() {
        return DMS_PROVIDER_ALFRESCO;
    }

    @Override
    public DocumentInfoAndDetail getDocument(String documentId) {
        return parseMap(client.getResourceInformation(documentId));
    }

    @Override
    public List<DocumentInfoAndDetail> getDocuments(String classId, long cardId) {
        return client.listResourceChildren(buildAlfrescoDocumentPath(classId, cardId))
                .stream()
                .map(this::parseMap)
                .toList();
    }

    @Override
    public List<DocumentInfoAndDetail> getDocumentVersions(String documentId) {
        DocumentInfoAndDetail documentData = getDocument(documentId);
        return client.listResourceVersions(documentId)
                .stream()
                .map(data -> parseMap(data, documentData))
                .toList();
    }

    @Override
    public DocumentInfoAndDetail create(String classId, long cardId, DocumentData document) {
        return parseMap(client.createResource(buildAlfrescoDocumentPath(classId, cardId), document));
    }

    @Override
    public DocumentInfoAndDetail update(String documentId, DocumentData document) {
        client.updateResourceInformation(documentId, document);
        if (!document.hasData()) {
            return getDocument(documentId);
        }
        return parseMap(client.updateResourceData(documentId, document));
    }

    @Override
    public DataHandler download(String documentId, @Nullable String version) {
        DocumentInfoAndDetail document = getDocument(documentId);
        DataHandler data = client.getResourceContent(documentId, version == null ? decodeDocumentVersion(documentId) : version);
        return newDataHandler(toByteArray(data), document.getMimeType(), document.getFileName());
    }

    @Override
    public Optional<DataHandler> preview(String documentId) {
        DocumentInfoAndDetail document = getDocument(documentId);
        DataHandler data = client.getResourcePreview(documentId);
        if (data == null) {
            return Optional.empty();
        }
        return Optional.of(newDataHandler(toByteArray(data), document.getMimeType(), document.getFileName()));
    }

    @Override
    public List<String> queryDocuments(String fulltextQuery, String classId, @Nullable Long cardId) {
        if (!dmsConfig.isAdvancedSearchEnabled()) {
            return emptyList();
        }

        // Check that at least three alphanumeric characters are present in the query term
        if (fulltextQuery.chars().filter(Character::isLetterOrDigit).count() < 3) {
            throw new DmsException("an attachment query must have at least three alphanumeric characters. Found =< %s >", fulltextQuery);
        }

        return client.queryResources(fulltextQuery, client.getResourceIdByPath(buildAlfrescoDocumentPath(classId, cardId)))
                .stream()
                .map(resource -> toStringNotBlank(resource.get("id")))
                .toList();
    }

    @Override
    public void delete(String documentId) {
        logger.debug("deleting document with id = {}", documentId);
        client.deleteResource(documentId);
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    private String buildAlfrescoDocumentPath(String classId, @Nullable Long cardId) {
        return Path.of(alfrescoConfig.getAlfrescoPath(), buildDocumentPath(dao.getClasse(classId), cardId)).toString();
    }

    private DocumentInfoAndDetail parseMap(Map<String, Object> map) {
        logger.debug("parsing map = \n\n{}\n", mapToLoggableStringLazy(map));
        return getBasicDocumentInfoAndDetail(map, DocumentInfoAndDetailImpl.builder())
                .withDocumentId(encodeString("%s;%s".formatted(map.get("id"), getProperty(map, ALFRESCO_PROPERTY_VERSION, "1.0"))))
                .withCreated(toDateTime(map.get("createdAt")))
                .withFileName(toStringNotBlank(map.get("name"), "missing required attribute =< name >"))
                .withHash("unknown")
                .withMimeType(applyOrDefault(map.get("content"), el -> toStringOrEmpty(((Map<String, ?>) el).get("mimeType")), "application/octet-stream"))
                .build();
    }

    private DocumentInfoAndDetail parseMap(Map<String, Object> map, DocumentInfoAndDetail reference) {
        logger.debug("parsing map from reference = \n\n{}\n", mapToLoggableStringLazy(map));
        return getBasicDocumentInfoAndDetail(map, DocumentInfoAndDetailImpl.copyOf(reference))
                .withDocumentId(encodeString("%s;%s".formatted(decodeDocumentId(reference.getDocumentId()), map.get("id")))) // Only used when requesting versions of the attachment
                .build();
    }

    private DocumentInfoAndDetailImplBuilder getBasicDocumentInfoAndDetail(Map<String, Object> map, DocumentInfoAndDetailImplBuilder builder) {
        return builder
                .withAuthor(getProperty(map, ALFRESCO_PROPERTY_AUTHOR, "unknown"))
                .withCategory(getProperty(map, ALFRESCO_PROPERTY_CATEGORY, ""))
                .withDescription(getProperty(map, ALFRESCO_PROPERTY_DESCRIPTION, ""))
                .withFileSize(applyOrNull(map.get("content"), el -> toInt(((Map<String, ?>) el).get("sizeInBytes"))))
                .withModified(toDateTime(map.get("modifiedAt")))
                .withVersion(getProperty(map, ALFRESCO_PROPERTY_VERSION, "1.0"));
    }

    private String getProperty(Map<String, Object> map, String property, String defaultValue) {
        Map<String, String> properties = (Map<String, String>) map.get("properties");
        return firstNotNull(properties.get(property), defaultValue);
    }
}
