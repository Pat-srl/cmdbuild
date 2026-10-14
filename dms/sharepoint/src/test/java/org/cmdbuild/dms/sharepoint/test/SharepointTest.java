/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.sharepoint.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;
import jakarta.activation.DataHandler;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.ClasseImpl;
import org.cmdbuild.dms.DmsConfiguration;
import org.cmdbuild.dms.DocumentDataImpl;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import static org.cmdbuild.dms.sharepoint.SharepointDmsAuthProtocol.MSAZUREOAUTH2_PASSWORD;
import org.cmdbuild.dms.sharepoint.SharepointDmsProviderService;
import org.cmdbuild.dms.sharepoint.config.SharepointDmsConfiguration;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmExecutorUtils.waitFor;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharepointTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private DmsConfiguration dmsConfig;
    private SharepointDmsConfiguration config;
    private SharepointDmsProviderService service;

    @Before
    public void init() {
        DaoService dao = mock(DaoService.class);
        dmsConfig = mock(DmsConfiguration.class);
        config = mock(SharepointDmsConfiguration.class);

        service = new SharepointDmsProviderService(dao, dmsConfig, config);

        when(dao.getClasse("MyClass")).thenReturn(ClasseImpl.builder().withName("MyClass").build());
        when(dao.getClasse("MyClass1")).thenReturn(ClasseImpl.builder().withName("MyClass1").build());
        when(dao.getClasse("MyClass2")).thenReturn(ClasseImpl.builder().withName("MyClass2").build());
        when(dao.getClasse("MyClass3")).thenReturn(ClasseImpl.builder().withName("MyClass3").build());
        when(dao.getClasse("MyClass4")).thenReturn(ClasseImpl.builder().withName("MyClass4").build());

        when(config.getSharepointGraphApiBaseUrl()).thenReturn("https://graph.microsoft.com/v1.0/");

        when(config.getSharepointCustomDescriptionColumn()).thenReturn("Label");
        when(config.hasSharepointCustomDescriptionColumn()).thenReturn(true);
        when(config.getSharepointCustomAuthorColumn()).thenReturn("OpenMAINT_Autore");
        when(config.hasSharepointCustomAuthorColumn()).thenReturn(true);
        when(config.getSharepointCustomCategoryColumn()).thenReturn("Categoria");
        when(config.hasSharepointCustomCategoryColumn()).thenReturn(true);

        when(config.getSharepointUrl()).thenReturn("----");
        when(config.getSharepointUser()).thenReturn("----");
        when(config.getSharepointPassword()).thenReturn("----");
        when(config.getSharepointPath()).thenReturn("/cmdbuild_home");

        when(config.getSharepointAuthClientId()).thenReturn("----");
        when(config.getSharepointAuthClientSecret()).thenReturn("----");
        when(config.getSharepointAuthResourceId()).thenReturn("----");
        when(config.getSharepointAuthServiceUrl()).thenReturn("https://login.microsoftonline.com");
        when(config.getSharepointAuthTenantId()).thenReturn("----");
        when(config.getSharepointAuthProtocol()).thenReturn(MSAZUREOAUTH2_PASSWORD);
    }

    @Test
    @Ignore
    public void testSharepointClientRead1() {
        DocumentInfoAndDetail document1 = getOnlyElement(service.getDocuments("MyClass", 123));
        logger.info("found document1 = {}", document1);
        logger.info("hash = {}", document1.getHash());
        logger.info("author = {}", document1.getAuthor());
        logger.info("description = {}", document1.getDescription());
        logger.info("category = {}", document1.getCategory());
    }

    @Test
    @Ignore
    public void testSharepointClientRead2() {
        DocumentInfoAndDetail document1 = getOnlyElement(service.getDocuments("MyClass", 123));
        logger.info("found document1 = {}", document1);
        DocumentInfoAndDetail document2 = service.getDocument(document1.getDocumentId());
        logger.info("found document2 = {}", document2);
        assertEquals(document1.getDocumentId(), document2.getDocumentId());
        assertEquals(document1.getFileName(), document2.getFileName());
        DataHandler data = service.download(document2.getDocumentId());
        logger.info("content = \n\n{}\n", readToString(data));

        List<DocumentInfoAndDetail> versions = service.getDocumentVersions(document1.getDocumentId());
        assertFalse(versions.isEmpty());
        versions.forEach(v -> {
            logger.info("found version =< {} > author =< {} > date =< {} > descr =< {} > content = \n\n{}\n", v.getVersion(), v.getAuthor(), toIsoDateTime(v.getModified()), abbreviate(v.getDescription()), readToString(service.download(v.getDocumentId(), v.getVersion())));
        });

        DataHandler preview = service.preview(document2.getDocumentId()).get();
        logger.info("preview = {} ({} bytes {})", preview, toByteArray(preview).length, preview.getContentType());
    }

    @Test
    @Ignore
    public void testSharepointClientWrite() {
        DocumentInfoAndDetail document3 = service.create("MyClass", 789, DocumentDataImpl.builder()
                .withAuthor("user3")
                .withCategory("cat3")
                .withDescription("descr3")
                .withData("ciao!".getBytes())
                .withFilename("test.txt").build());
        logger.info("created document3 = {}", document3);
        DocumentInfoAndDetail document4 = getOnlyElement(service.getDocuments("MyClass", 789));
        logger.info("found document4 = {}", document4);
        assertEquals(document3.getDocumentId(), document4.getDocumentId());
        assertEquals("ciao!", readToString(service.download(document4.getDocumentId())));
    }

    @Test
    @Ignore
    public void testSharepointClientWriteRead() {
        list(1, 2).forEach(i -> {
            testWriteRead("MyClass", 456, format("test%s_", i));
        });
    }

    @Test
    @Ignore
    public void testSharepointClientWriteRead2() {
        list(1, 2).forEach(i -> {
            testWriteRead("MyClass2", 456 + i, format("test2%s_", i));
        });
    }

    @Test
    @Ignore
    public void testSharepointClientWriteReadMultithread() throws Exception {
        int nthread = 4;
        ExecutorService executor = Executors.newFixedThreadPool(nthread);
        try {
            IntStream.range(0, nthread).mapToObj(t -> executor.submit(() -> {
                list(1, 2).forEach(i -> {
                    testWriteRead("MyClass3", 300 + t, format("testp%s_%s_", t, i));
                });
            })).forEach(rethrowConsumer(Future::get));
            logger.info("test OK");
        } finally {
            while (!executor.isTerminated()) {
                shutdownQuietly(executor);
            }
        }
    }

    @Test
    @Ignore
    public void testSharepointClientBigFileWriteRead1() {
        byte[] data = new byte[5000000];
        new Random().nextBytes(data);
        logger.info("upload file with size = {}", byteCountToDisplaySize(data.length));
        DocumentInfoAndDetail document = service.create("MyClass4", 400, DocumentDataImpl.builder().withData(data).withFilename("file.raw").withDescription("big random file").build());
        assertEquals(data.length, document.getFileSize());
        logger.info("uploaded file with size = {}; download file", byteCountToDisplaySize(document.getFileSize()));
        byte[] data2 = toByteArray(service.download(document.getDocumentId()));
        assertArrayEquals(data, data2);
        service.delete(document.getDocumentId());
        logger.info("file with size = {} is OK", byteCountToDisplaySize(document.getFileSize()));
    }

    @Test
    @Ignore
    public void testSharepointClientBigFileWriteRead2() {
        list(100, 100000, 1000000, 5000000, 30000000).forEach(size -> {
            byte[] data = new byte[size];
            new Random().nextBytes(data);
            logger.info("upload file with size = {}", byteCountToDisplaySize(data.length));
            DocumentInfoAndDetail document = service.create("MyClass4", 400, DocumentDataImpl.builder().withData(data).withFilename("file.raw").withDescription("big random file").build());
            assertEquals(data.length, document.getFileSize());
            logger.info("uploaded file with size = {}; download file", byteCountToDisplaySize(document.getFileSize()));
            byte[] data2 = toByteArray(service.download(document.getDocumentId()));
            assertArrayEquals(data, data2);
            service.delete(document.getDocumentId());
            logger.info("file with size = {} is OK", byteCountToDisplaySize(document.getFileSize()));
        });
    }

    public void testWriteRead(String classId, long cardId, String token) {
        assertEquals(0, service.getDocuments(classId, cardId).size());
        DocumentInfoAndDetail document3 = service.create(classId, cardId, DocumentDataImpl.builder()
                .withAuthor(token + "user1")
                .withCategory(token + "cat1")
                .withDescription(token + "descr1")
                .withData((token + " ciao").getBytes())
                .withFilename(token + "test.txt").build());
        logger.info("created document3 = {}", document3);
        if (config.hasSharepointCustomAuthorColumn()) {
            assertEquals(token + "user1", document3.getAuthor());
        }
        if (config.hasSharepointCustomCategoryColumn()) {
            assertEquals(token + "cat1", document3.getCategory());
        }
        if (config.hasSharepointCustomDescriptionColumn()) {
            assertEquals(token + "descr1", document3.getDescription());
        }
        DocumentInfoAndDetail document4 = getOnlyElement(service.getDocuments(classId, cardId));
        logger.info("found document4 = {}", document4);
        if (config.hasSharepointCustomAuthorColumn()) {
            assertEquals(token + "user1", document4.getAuthor());
        }
        if (config.hasSharepointCustomCategoryColumn()) {
            assertEquals(token + "cat1", document4.getCategory());
        }
        if (config.hasSharepointCustomDescriptionColumn()) {
            assertEquals(token + "descr1", document4.getDescription());
        }
        assertEquals(document3.getDocumentId(), document4.getDocumentId());
        assertEquals(token + " ciao", readToString(service.download(document4.getDocumentId())));
        DocumentInfoAndDetail document6 = service.update(document4.getDocumentId(), DocumentDataImpl.builder()
                .withAuthor(token + "user2")
                .withCategory(token + "cat2")
                .withDescription(token + "descr2")
                .withData((token + " ciao2").getBytes())
                .withFilename(token + "test.txt").build());
        if (config.hasSharepointCustomAuthorColumn()) {
            assertEquals(token + "user2", document6.getAuthor());
        }
        if (config.hasSharepointCustomCategoryColumn()) {
            assertEquals(token + "cat2", document6.getCategory());
        }
        if (config.hasSharepointCustomDescriptionColumn()) {
            assertEquals(token + "descr2", document6.getDescription());
        }
        assertEquals(document3.getDocumentId(), document6.getDocumentId());
        assertEquals(token + " ciao2", readToString(service.download(document6.getDocumentId())));
        DocumentInfoAndDetail document5 = getOnlyElement(service.getDocuments(classId, cardId));
        if (config.hasSharepointCustomAuthorColumn()) {
            assertEquals(token + "user2", document5.getAuthor());
        }
        if (config.hasSharepointCustomCategoryColumn()) {
            assertEquals(token + "cat2", document5.getCategory());
        }
        if (config.hasSharepointCustomDescriptionColumn()) {
            assertEquals(token + "descr2", document5.getDescription());
        }
        assertEquals(document3.getDocumentId(), document5.getDocumentId());
        assertEquals(token + " ciao2", readToString(service.download(document5.getDocumentId())));
        DocumentInfoAndDetail document7 = service.update(document4.getDocumentId(), DocumentDataImpl.builder()
                .withAuthor(token + "user2b")
                .withCategory(token + "cat2b")
                .withDescription(token + "descr2b").build());
        if (config.hasSharepointCustomAuthorColumn()) {
            assertEquals(token + "user2b", document7.getAuthor());
        }
        if (config.hasSharepointCustomCategoryColumn()) {
            assertEquals(token + "cat2b", document7.getCategory());
        }
        if (config.hasSharepointCustomDescriptionColumn()) {
            assertEquals(token + "descr2b", document7.getDescription());
        }
        DataHandler preview = waitFor(() -> service.preview(document5.getDocumentId()), Optional::isPresent, Optional::get);
        logger.info("found preview = {}", preview);
        service.delete(document5.getDocumentId());
        assertEquals(0, service.getDocuments(classId, cardId).size());
    }

    @Test
    @Ignore
    public void testSharepointClientWriteReadListMore200() {
        IntStream.rangeClosed(1, 250).forEach(i -> {
            testWriteReadMore200("MyClass", 456, format("test%s_", i), i);
        });
        service.getDocuments("MyClass", 456).forEach(d -> {
            logger.info("delete: {}", d.getDocumentId());
            service.delete(d.getDocumentId());
        });
    }

    public void testWriteReadMore200(String classId, long cardId, String token, int size) {
        DocumentInfoAndDetail document = service.create(classId, cardId, DocumentDataImpl.builder()
                .withAuthor(token + "user1")
                .withCategory(token + "cat1")
                .withDescription(token + "descr1")
                .withData((token + " ciao").getBytes())
                .withFilename(token + "test.txt").build());
        logger.info("created document = {}", document);
        logger.info("size = {}", service.getDocuments(classId, cardId).size());
        assertEquals(service.getDocuments(classId, cardId).size(), size);
    }

}
