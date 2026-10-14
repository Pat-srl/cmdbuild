/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Predicates;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableMap;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.tika.Tika;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowBiConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CmZipUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final static Tika TIKA = new Tika();

    /**
     * unzip source zip file into target directory; will create target dir if
     * not existing.
     *
     * @param in source zip file
     * @param target target directory
     */
    public static void unzipToDir(InputStream in, File target) {
        checkNotNull(in);
        try {
            File zipFile = tempFile("cmdbuild_unzip_source_file", "zip");
            zipFile.deleteOnExit();
            FileUtils.copyInputStreamToFile(in, zipFile);
            unzipToDir(zipFile, target);
            FileUtils.deleteQuietly(zipFile);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void unzipToDir(byte[] data, File target) {
        unzipToDir(new ByteArrayInputStream(data), target);
    }

    public static Collection<Pair<String, byte[]>> unzipData(byte[] zipData) {
        Collection<Pair<String, byte[]>> list = list();
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String name = FilenameUtils.getName(entry.getName());
                    byte[] data = CmIoUtils.toByteArray(in);
                    list.add(Pair.of(name, data));
                }
            }
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return list;
    }

    public static BigByteArray buildZipFile(Map<String, BigByteArray> map) {
        BigByteArrayOutputStream byteArrayOutputStream = new BigByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(byteArrayOutputStream)) {
            map.forEach(rethrowBiConsumer((path, data) -> {
                ZipEntry entry = new ZipEntry(path);
                zip.putNextEntry(entry);
                data.writeTo(zip);
                zip.closeEntry();
            }));
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return byteArrayOutputStream.toBigByteArray();
    }

    public static Map<String, BigByteArray> unzipDataAsMap(BigByteArray zipData) {
        return map(unzipDataAsMap(zipData.toByteArray())).mapValues(BigByteArray::new);
    }

    public static Map<String, byte[]> unzipDataAsMap(byte[] zipData) {
        return ImmutableMap.copyOf(transformValues(uniqueIndex(unzipData(zipData), Pair::getKey), Pair::getValue));
    }

    public static byte[] getZipFileContentByPath(byte[] zipData, String path) {
        return getZipFileContentByPath(new ByteArrayInputStream(zipData), path);
    }

    public static byte[] getZipFileContentByPath(InputStream zipData, String path) {
        checkNotBlank(path);
        try {
            path = new File(path).getCanonicalPath();
            try (ZipInputStream in = new ZipInputStream(zipData)) {
                ZipEntry entry;
                while ((entry = in.getNextEntry()) != null) {
                    if (!entry.isDirectory()) {
                        String thisPath = new File(entry.getName()).getCanonicalPath();
                        if (equal(thisPath, path)) {
                            return CmIoUtils.toByteArray(in);
                        }
                    }
                }
            }
        } catch (IOException ex) {
            throw runtime(ex);
        }
        throw new NullPointerException("zip file entry not found for path = " + path);
    }

    public static void validateZipFileContent(byte[] zipData) {
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(zipData))) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    byte[] data = toByteArray(in);
                    checkArgument(data.length == entry.getSize(), "invalid entry size");
                    in.closeEntry();
                }
            }
        } catch (IOException ex) {
            throw runtime(ex, "error reading zip file content");
        }
    }

    public static byte[] getZipFileContentByName(byte[] zipData, String fileName) {
        return getZipFileContentByName(new ByteArrayInputStream(zipData), fileName);
    }

    public static byte[] getZipFileContentByName(InputStream zipData, String fileName) {
        checkNotBlank(fileName);
        try (ZipInputStream in = new ZipInputStream(zipData)) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String name = FilenameUtils.getName(entry.getName());
                    if (equal(name, fileName)) {
                        return CmIoUtils.toByteArray(in);
                    }
                }
            }
        } catch (IOException ex) {
            throw runtime(ex);
        }
        throw new NullPointerException("zip file entry not found for name = " + fileName);
    }

    public static void unzipToDir(File zipFile, File target) {
        unzipToDir(zipFile, target, Predicates.alwaysTrue());
    }

    public static void unzipToDir(File zipFile, File target, Predicate<String> filter) {//TODO use filter !!!
        checkNotNull(zipFile);
        checkNotNull(target);
        try {
            target.mkdirs();
            checkArgument(target.isDirectory(), "target file %s is not a directory", target.getAbsolutePath());
            new ZipFile(zipFile).extractAll(target.getAbsolutePath());//TODO use filter !!!
        } catch (ZipException ex) {
            throw new RuntimeException("error unpacking zip file = " + zipFile.getAbsolutePath() + " to dir = " + target.getAbsolutePath(), ex);
        }
    }

    public static byte[] dirToZip(File dir) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(byteArrayOutputStream)) {
            for (File file : FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
                String path = dir.toPath().relativize(file.toPath()).toString();
                ZipEntry entry = new ZipEntry(path);
                zip.putNextEntry(entry);
                zip.write(toByteArray(file));
                zip.closeEntry();
            }
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static boolean isZipFile(String filename) {
        return isNotBlank(filename) && equal("zip", nullToEmpty(FilenameUtils.getExtension(filename)).toLowerCase());
    }

}
