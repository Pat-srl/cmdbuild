/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.tika.Tika;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.dms.DocumentData;
import org.cmdbuild.dms.inner.DmsProviderService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import static org.cmdbuild.dms.inner.DocumentPathUtils.nextVersion;
import org.cmdbuild.dms.thumbnailer.GenericThumbnailer;
import org.cmdbuild.dms.thumbnailer.Thumbnailer;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerExt;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.BigByteArrayOutputStream;
import static org.cmdbuild.utils.io.CmIoUtils.getAvailableLong;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class PgDmsProviderServiceImpl implements DmsProviderService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Tika tika = new Tika();

    private final DocumentInfoRepository repository;
    private final DocumentDataRepository dataRepository;
    private final DmsConfiguration configuration;
    private final Map<String, Thumbnailer> thumbnailer;

    private final MinionHandlerExt minionHandler;

    public PgDmsProviderServiceImpl(DocumentInfoRepository repository, DocumentDataRepository dataRepository, DmsConfiguration dmsConfiguration, List<Thumbnailer> thumbnailer) {
        this.repository = checkNotNull(repository);
        this.dataRepository = checkNotNull(dataRepository);
        this.configuration = checkNotNull(dmsConfiguration);
        this.thumbnailer = mapOf(String.class, Thumbnailer.class).accept(m -> thumbnailer.forEach(tmblr -> tmblr.getAcceptedMIMETypes().forEach(mimeType -> m.put(mimeType, tmblr))));
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("DMS_ Postgres Service")
                .withDescription("DMS_ Postgres Service")
                .withEnabledChecker(() -> configuration.isEnabled(getDmsProviderServiceName()))
                .reloadOnConfigs(DmsConfiguration.class)
                .withHidden(true)
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
    public String getDmsProviderServiceName() {
        return DMS_PROVIDER_POSTGRES;
    }

    @Override
    public boolean isServiceOk() {
        return getMinionHandler().isReady();
    }

    @Override
    public BigByteArray exportAllDocumentsAsZipFile() {
        BigByteArrayOutputStream out = new BigByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            repository.getAll().stream().filter(dataRepository::hasDocumentData).forEach(rethrowConsumer(d -> {
                ZipEntry entry = new ZipEntry(format("Class/%s/%s", d.getCardId(), d.getFileName()));
                zip.putNextEntry(entry);
                zip.write(dataRepository.getDocumentData(d));
                zip.closeEntry();
            }));
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return out.toBigByteArray();
    }

    @Override
    public List<DocumentInfoAndDetail> getDocuments(String classId, long cardId) {
        return repository.getAllByCardId(cardId).stream().filter(dataRepository::hasDocumentData).collect(toList());
    }

    @Override
    public DocumentInfoAndDetail getDocument(String documentId) {
        return repository.getById(documentId);
    }

    @Override
    public List<DocumentInfoAndDetail> getDocumentVersions(String documentId) {
        return repository.getAllVersions(documentId).stream().filter(dataRepository::hasDocumentData).collect(toList());
    }

    @Override
    @Transactional
    public DocumentInfoAndDetail create(String className, long cardId, DocumentData document) {
        byte[] data = checkNotNull(document.getData(), "cannot create document with null data");
        DmsModelDocument documentInfo = DmsModelDocumentImpl.builder()
                .withAuthor(document.getAuthor())
                .withCardId(cardId)
                .withCategory(document.getCategory())
                .withCreated(now())
                .withDescription(document.getDescription())
                .withFileName(document.getFilename())
                .withFileSize(data.length)
                .withHash(hash(data))
                .withMimeType(tika.detect(data))
                .withModified(now())
                .withVersion(nextVersion(null, true))
                .withDocumentId(randomId())
                .build();
        dataRepository.createDocumentData(documentInfo, data);
        return documentInfo;
    }

    @Override
    @Transactional
    public DocumentInfoAndDetail update(String documentId, DocumentData data) {
        DmsModelDocument currentDocument = repository.getById(documentId);
        DmsModelDocument documentInfo = DmsModelDocumentImpl.copyOf(currentDocument)
                .withAuthor(data.getAuthor())
                .withCategory(data.getCategory())
                .withDescription(data.getDescription())
                .accept((b) -> {
                    if (data.hasData()) {
                        b.withFileSize(data.getData().length)
                                .withDocumentId(randomId())
                                .withHash(hash(data.getData()))
                                .withMimeType(tika.detect(data.getData()))
                                .withModified(now())
                                .withVersion(nextVersion(currentDocument.getVersion(), data.isMajorVersion()));
                    }
                })
                .build();
        if (data.hasData()) {
            dataRepository.createDocumentData(documentInfo, data.getData());
        }
        return documentInfo;
    }

    @Override
    public DataHandler download(String documentId, @Nullable String version) {
        DmsModelDocument document;
        if (isBlank(version)) {
            document = repository.getById(documentId);
        } else {
            document = repository.getByIdAndVersion(documentId, version);
        }
        byte[] data = dataRepository.getDocumentData(document);
        return newDataHandler(data, document.getMimeType(), document.getFileName());
    }

    @Override
    public Optional<DataHandler> preview(String documentId) {
        DataHandler attachment = download(documentId);
        if (configuration.isPgPreviewEnabled()) {
            try {
                logger.debug("trying to generate preview for {} - {}", attachment.getName(), attachment.getContentType());
                InputStream in = attachment.getInputStream();
                if (getAvailableLong(in) < configuration.getPgPreviewMaxFileSize() * 1024 * 1024) {
                    Thumbnailer thumbnailerGenerator = thumbnailer.getOrDefault(attachment.getContentType(), new GenericThumbnailer());
                    return thumbnailerGenerator.generateThumbnail(in);
                }
            } catch (Exception ex) {
                logger.warn(marker(), "error retrieving preview for documentId =< {} >", documentId, ex);
            }
        }
        return Optional.empty();
    }

    @Override
    public void delete(String documentId) {
        DmsModelDocument document = repository.getByIdOrNull(documentId);
        if (document != null) { //Made this to avoid error in deletion when using postgres dms service due to double query deletion
            repository.delete(document);
        }
    }

    @Override
    public List<String> queryDocuments(String fulltextQuery, String classId, Long cardId) {
        return emptyList(); //returning empty list, no content search for pgDms
    }

}
