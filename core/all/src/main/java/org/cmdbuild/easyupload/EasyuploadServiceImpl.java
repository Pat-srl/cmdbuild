/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easyupload;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import jakarta.mail.util.ByteArrayDataSource;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.http.entity.ContentType;
import org.apache.tika.Tika;
import org.cmdbuild.easyupload.EasyuploadItemImpl.EasyuploadItemImplBuilder;
import static org.cmdbuild.easyupload.EasyuploadUtils.normalizePath;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EasyuploadServiceImpl implements EasyuploadService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EasyuploadRepository repository;
    private final Tika tika = new Tika();

    public EasyuploadServiceImpl(EasyuploadRepository repository) {
        this.repository = checkNotNull(repository);
    }

    @Override
    public EasyuploadItem create(DataHandler dataHandler, @Nullable String fileOrDir, @Nullable String description) {
        return doCreate(dataHandler, fileOrDir, description, false);
    }

    @Override
    public EasyuploadItem createOrUpdate(DataHandler dataHandler, @Nullable String fileOrDir, @Nullable String description) {
        return doCreate(dataHandler, fileOrDir, description, true);
    }

    private EasyuploadItem doCreate(DataHandler dataHandler, @Nullable String fileOrDir, @Nullable String description, boolean updateExisting) {
        checkNotBlank(fileOrDir);
        String path;
        if (isBlank(fileOrDir)) {
            path = normalizePath("other", randomId(), dataHandler.getName());
        } else if (isNotBlank(FilenameUtils.getExtension(fileOrDir))) {
            path = normalizePath(fileOrDir);
        } else {
            path = normalizePath(fileOrDir, dataHandler.getName());
        }
        if (updateExisting) {
            EasyuploadItem current = getByPathOrNull(path);
            if (current != null) {
                return update(current.getId(), toByteArray(dataHandler), description);
            }
        }
        EasyuploadItem item = EasyuploadItemImpl.builder().accept(dataToItem(toByteArray(dataHandler)))
                .withPath(path)
                .withFileName(FilenameUtils.getName(path))
                .withDescription(description)
                .build();
        logger.info("create easyupload item = {}", item);
        return repository.create(item);
    }

    @Override
    public EasyuploadItem update(long fileId, @Nullable byte[] data, @Nullable String description) {
        EasyuploadItem item = getById(fileId);
        item = EasyuploadItemImpl.copyOf(item).accept(b -> {
            if (data != null) {
                b.accept(dataToItem(data));
            }
            if (description != null) {
                b.withDescription(description);
            }
        }).build();
        return repository.update(item);
    }

    @Override
    public void delete(long fileId) {
        repository.delete(fileId);
    }

    @Override
    @Nullable
    public EasyuploadItem getByPathOrNull(String path) {
        return repository.getByPathOrNull(path);
    }

    @Override
    public EasyuploadItem get(String path) {
        return repository.getByPath(path);
    }

    @Override
    public EasyuploadItem getById(long fileId) {
        return repository.getItemById(fileId);
    }

    @Override
    public List<EasyuploadItemInfo> getAll() {
        return repository.getAllInfo();
    }

    @Override
    public List<EasyuploadItemInfo> getByDir(String dir) {
        return repository.getInfoByDir(dir);
    }

    @Override
    public DataSource getUploadsAsZipFile(String dir) {
        return toZip(repository.getAllByDir(dir));
    }

    @Override
    public DataSource getAllUploadsAsZipFile() {
        return toZip(repository.getAll());
    }

    @Override
    public void uploadZip(byte[] toByteArray) {
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(toByteArray))) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                String path = normalizePath(entry.getName());
                byte[] data = toByteArray(in);
                in.closeEntry();
                createOrUpdate(newDataHandler(data), path, null);
            }
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    @Override
    public List<String> getSubdirsForDir(String path) {
        return repository.getSubdirsForDir(path);
    }

    private Consumer<EasyuploadItemImplBuilder> dataToItem(byte[] data) {
        return (b) -> b
                .withContent(data)
                .withHash(hash(data))
                .withMimeType(tika.detect(data))
                .withSize(data.length);
    }

    private DataSource toZip(List<EasyuploadItem> items) {
        File zip = tempFile();

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip))) {
            for (EasyuploadItem item : items) {
                ZipEntry zipEntry = new ZipEntry(item.getPath());
                out.putNextEntry(zipEntry);
                out.write(item.getContent());
                out.closeEntry();
            }
        } catch (IOException ex) {
            throw runtime(ex);
        }

        byte[] zipData = CmIoUtils.toByteArray(zip);
        deleteQuietly(zip);

        return new ByteArrayDataSource(zipData, ContentType.APPLICATION_OCTET_STREAM.getMimeType()) {
            {
                setName("upload.zip");
            }
        };
    }

}
