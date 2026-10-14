/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.zip.GZIPInputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.IOUtils.closeQuietly;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.io.CmIoUtils.TIKA;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.isContentType;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CmTarUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static BigByteArray createTarArchive(Collection<Pair<BigByteArray, String>> files) {
        BigByteArrayOutputStream out = new BigByteArrayOutputStream();
        try (TarArchiveOutputStream tar = new TarArchiveOutputStream(out)) {
            files.forEach(rethrowConsumer(r -> {
                BigByteArray data = checkNotNull(r.getLeft());
                String fileName = checkNotBlank(r.getRight());
                tar.putArchiveEntry(new TarArchiveEntry(fileName) {
                    {
                        setSize(data.length());
                    }
                });
                data.writeTo(tar);
                tar.closeArchiveEntry();
            }));
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return out.toBigByteArray();
    }

    public static void untarToDir(byte[] data, File target) {
        untarToDir(new ByteArrayInputStream(data), target);
    }

    public static void untarToDir(File source, File target) {
        untarToDir(toByteArray(source), target);
    }

    public static void untarToDir(InputStream in, File target) {
        try {
            if (!in.markSupported()) {
                in = new BufferedInputStream(in);
            }
            String mime = TIKA.detect(in);
            TarArchiveInputStream tar;
            if (isContentType(mime, "application/gzip")) {
                LOGGER.debug("detected gzip stream, enable gunzip");
                tar = new TarArchiveInputStream(new GZIPInputStream(in));
            } else {
                tar = new TarArchiveInputStream(in);
            }
            ArchiveEntry entry;
            while ((entry = tar.getNextEntry()) != null) {
                File file = new File(target, entry.getName());
                file.getParentFile().mkdirs();
                checkArgument(file.getParentFile().isDirectory());
                if (entry.isDirectory()) {
                    file.mkdir();
                    checkArgument(file.isDirectory());
                    LOGGER.debug("create dir = {}", file.getAbsolutePath());
                } else {
                    LOGGER.debug("unpack file = {} {}", file.getAbsolutePath(), byteCountToDisplaySize(entry.getSize()));
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        copy(tar, out);
                    }
                    int mode = tar.getCurrentEntry().getMode();
                    if ((mode & 1) != 0) {
                        file.setExecutable(true);//TODO add test, handle exceptions
                    }
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        } finally {
            closeQuietly(in);
        }
    }
}
