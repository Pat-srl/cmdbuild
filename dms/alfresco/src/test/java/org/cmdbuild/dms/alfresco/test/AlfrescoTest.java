package org.cmdbuild.dms.alfresco.test;

import jakarta.activation.DataHandler;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.alfresco.AlfrescoDmsProviderService;
import org.cmdbuild.dms.alfresco.config.AlfrescoDmsConfiguration;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlfrescoTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DmsConfiguration dmsConfig;
    private AlfrescoDmsConfiguration config;
    private AlfrescoDmsProviderService service;

    private final String className = "TestClass";
    private static long id = 1234;

    private final String DEFAULT_AUTHOR = "admin";
    private final String DEFAULT_CATEGORY = "Document";
    private final String DEFAULT_DESCRIPTION = "Description";
    private final String DEFAULT_DATA = "Lorem ipsum";
    private final String DEFAULT_FILENAME = "text.txt";

    private final DocumentDataImpl DEFAULT_ATTACHMENT = DocumentDataImpl.builder()
            .withAuthor(DEFAULT_AUTHOR)
            .withCategory(DEFAULT_CATEGORY)
            .withDescription(DEFAULT_DESCRIPTION)
            .withData(DEFAULT_DATA.getBytes())
            .withFilename(DEFAULT_FILENAME)
            .build();

    @Before
    public void init() throws InterruptedException {
        DaoService dao = mock(DaoService.class);
        dmsConfig = mock(DmsConfiguration.class);
        config = mock(AlfrescoDmsConfiguration.class);

        doAnswer((invocation)
                -> ClasseImpl.builder()
                        .withName(invocation.getArgumentAt(0, String.class))
                        .withAncestors(list("ParentClass"))
                        .build())
                .when(dao)
                .getClasse(anyString());

        when(dmsConfig.isAdvancedSearchEnabled()).thenReturn(true);
        when(config.getAlfrescoUser()).thenReturn("admin");
        when(config.getAlfrescoPassword()).thenReturn("admin");
        when(config.getAlfrescoApiBaseUrl()).thenReturn("http://127.0.0.1:10080/alfresco/api/-default-/public/alfresco/versions/1/");
        when(config.getAlfrescoPageSize()).thenReturn("5");

        service = new AlfrescoDmsProviderService(dao, dmsConfig, config);
    }

    @Test
    @Ignore
    public void testAlfrescoCreateDocument() throws IOException {
        final long id = getNextId();

        DocumentInfoAndDetail newDocument = service.create(className, id, DEFAULT_ATTACHMENT);
        logger.info("Created file: {}", newDocument);

        DocumentInfoAndDetail readDocument = service.getDocument(newDocument.getDocumentId());
        assertEquals(DEFAULT_AUTHOR, readDocument.getAuthor());
        assertEquals(DEFAULT_CATEGORY, readDocument.getCategory());
        assertEquals(DEFAULT_DESCRIPTION, readDocument.getDescription());
        assertEquals(DEFAULT_FILENAME, readDocument.getFileName());
        assertEquals(DEFAULT_DATA, readToString(service.download(newDocument.getDocumentId())));

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Optional<DataHandler> optionalPreview = service.preview(newDocument.getDocumentId());
        if (optionalPreview.isPresent()) {
            DataHandler previewData = service.preview(newDocument.getDocumentId()).get();
            logger.info("preview = {} ({} bytes {})", previewData, previewData.getInputStream().available(), previewData.getContentType());
        }

        assertTrue(optionalPreview.isPresent());
        deleteDocumentsAndAssertEmptyFolder(className, id, list(newDocument.getDocumentId()));
    }

    @Test
    @Ignore
    public void testAlfrescoNonExistingFolder() {
        List<DocumentInfoAndDetail> docs = service.getDocuments("NonExistingClass", 1);

        assertThat(docs, is(empty()));
    }

    @Test
    @Ignore
    public void testAlfrescoUpdateAndVersions() {
        final long id = getNextId();

        // Create file
        DocumentInfoAndDetail newDocument = service.create(className, id, DEFAULT_ATTACHMENT);
        logger.info("Created file: {}", newDocument);

        // Update file
        DocumentInfoAndDetail updatedDocument = service.update(newDocument.getDocumentId(), DocumentDataImpl.builder()
                .withAuthor(DEFAULT_AUTHOR)
                .withCategory(DEFAULT_CATEGORY)
                .withDescription("Updated description")
                .withData("Updated content".getBytes())
                .withFilename(DEFAULT_FILENAME)
                .build());

        // Update file
        DocumentInfoAndDetail updatedDocument2 = service.update(newDocument.getDocumentId(), DocumentDataImpl.builder()
                .withAuthor("different_author")
                .withCategory(DEFAULT_CATEGORY)
                .withDescription("Updated description (again)")
                .withData("Updated content (again)".getBytes())
                .withFilename(DEFAULT_FILENAME)
                .build());

        // Check data
        List<DocumentInfoAndDetail> documentVersions = service.getDocumentVersions(newDocument.getDocumentId());

        assertEquals(3, documentVersions.size());
        DocumentInfoAndDetail secondVersion = documentVersions.get(1);
        assertEquals(DEFAULT_AUTHOR, secondVersion.getAuthor());
        assertEquals(DEFAULT_CATEGORY, secondVersion.getCategory());
        assertEquals("Updated description", secondVersion.getDescription());
        DocumentInfoAndDetail thirdVersion = documentVersions.get(0);
        assertEquals("different_author", thirdVersion.getAuthor());
        assertEquals(DEFAULT_CATEGORY, thirdVersion.getCategory());
        assertEquals("Updated description (again)", thirdVersion.getDescription());
        assertEquals("Updated content", readToString(service.download(updatedDocument.getDocumentId())));
        assertEquals("Updated content (again)", readToString(service.download(updatedDocument2.getDocumentId())));

        deleteDocumentsAndAssertEmptyFolder(className, id, list(newDocument.getDocumentId()));
    }

    @Test
    @Ignore
    public void testAlfrescoUpdateMajorVersion() {
        final long id = getNextId();

        // Create file
        DocumentInfoAndDetail newDocument = service.create(className, id, DEFAULT_ATTACHMENT);
        logger.info("Created file: {}", newDocument);

        // Update file with major version
        DocumentInfoAndDetail majorVersionDocument = service.update(newDocument.getDocumentId(), DocumentDataImpl.builder()
                .withAuthor(DEFAULT_AUTHOR)
                .withCategory(DEFAULT_CATEGORY)
                .withDescription("Updated description")
                .withData("Updated content".getBytes())
                .withFilename(DEFAULT_FILENAME)
                .withMajorVersion(true)
                .build());

        // Update file without major version
        DocumentInfoAndDetail normalVersionDocument = service.update(newDocument.getDocumentId(), DocumentDataImpl.builder()
                .withAuthor(DEFAULT_AUTHOR)
                .withCategory(DEFAULT_CATEGORY)
                .withDescription("Updated description")
                .withData("Updated content".getBytes())
                .withFilename(DEFAULT_FILENAME)
                .withMajorVersion(false)
                .build());

        // Check data
        List<DocumentInfoAndDetail> documentVersions = service.getDocumentVersions(newDocument.getDocumentId());

        assertEquals(3, documentVersions.size());
        assertEquals("2.1", documentVersions.get(0).getVersion());
        assertEquals("2.0", documentVersions.get(1).getVersion());
        assertEquals("1.0", documentVersions.get(2).getVersion());

        deleteDocumentsAndAssertEmptyFolder(className, id, list(newDocument.getDocumentId()));
    }

    @Test
    @Ignore
    public void testAlfrescoPagination() {
        final long id = getNextId();
        List<String> ids = list();

        // Create files
        for (int i = 1; i <= 15; i++) {
            ids.add(service.create(className, id, DocumentDataImpl.builder()
                    .withAuthor(DEFAULT_AUTHOR)
                    .withCategory(DEFAULT_CATEGORY)
                    .withDescription(DEFAULT_DESCRIPTION)
                    .withData(DEFAULT_DATA.getBytes())
                    .withFilename("file_%s.txt".formatted(i))
                    .build()).getDocumentId());
        }

        // Check data
        List<DocumentInfoAndDetail> documents = service.getDocuments(className, id);

        assertEquals(15, documents.size());

        deleteDocumentsAndAssertEmptyFolder(className, id, ids);
    }

    private void deleteDocumentsAndAssertEmptyFolder(String classId, long cardId, List<String> documentIds) {
        // Delete all documents
        documentIds.forEach(service::delete);

        // Assert that the containing folder is null
        assertThat(service.getDocuments(classId, cardId), is(empty()));
    }

    private synchronized long getNextId() {
        return id++;
    }
}
