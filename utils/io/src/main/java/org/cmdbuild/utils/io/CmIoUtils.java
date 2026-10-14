/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Duration;
import java.time.Instant;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.NullOutputStream;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.tika.Tika;
import org.apache.tika.config.TikaConfig;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDuration;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDuration;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.getCharsetFromContentType;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.unsafe;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.utils.lang.LambdaExceptionUtils.Supplier_WithExceptions;
import org.cmdbuild.utils.url.CmUrlUtils;
import static org.cmdbuild.utils.url.CmUrlUtils.isDataUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class CmIoUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public final static Tika TIKA = new Tika();

    private final static Supplier<File> CM_SLOW_CACHE_DIR = Suppliers.memoize(CmIoUtils::configureCmSlowCacheDir);

    @Nullable
    public static byte[] emptyToNull(@Nullable byte[] data) {
        return data == null || data.length == 0 ? null : data;
    }

    public static void writeToFile(File file, String content) {
        writeToFile(file, content.getBytes(StandardCharsets.UTF_8));
    }

    public static void writeToFile(File file, byte[] data) {
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            FileUtils.writeByteArrayToFile(file, data);
        } catch (IOException ex) {
            throw runtime(ex, "error writitng to file = %s", file);
        }
    }

    public static void writeToFile(File file, BigByteArray data) {
        try {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }
            FileUtils.copyInputStreamToFile(data.toInputStream(), file);
        } catch (IOException ex) {
            throw runtime(ex, "error writitng to file = %s", file);
        }
    }

    public static String readToString(File file) {
        return readToString(file, StandardCharsets.UTF_8);
    }

    public static String readToString(File file, Charset charset) {
        try {
            return FileUtils.readFileToString(file, charset);
        } catch (IOException ex) {
            throw runtime(ex, "error reading file = %s", file);
        }
    }

    public static List<String> readLines(String string) {
        return IOUtils.readLines(new StringReader(string));
    }

    public static byte[] toByteArray(File file) {
        try {
            return FileUtils.readFileToByteArray(file);
        } catch (IOException ex) {
            throw runtime(ex, "error reading file = %s", file);
        }
    }

    public static BigByteArray toBigByteArray(File file) {
        try (FileInputStream in = new FileInputStream(file)) {
            BigByteArrayOutputStream out = new BigByteArrayOutputStream();
            IOUtils.copyLarge(in, out);
            return out.toBigByteArray();
        } catch (IOException ex) {
            throw runtime(ex, "error reading file = %s", file);
        }
    }

    public static BigByteArray toBigByteArray(InputStream in) {
        try {
            BigByteArrayOutputStream out = new BigByteArrayOutputStream();
            IOUtils.copyLarge(in, out);
            return out.toBigByteArray();
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static BigByteArray toBigByteArray(DataSource data) {
        try (InputStream in = data.getInputStream()) {
            return toBigByteArray(in);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static boolean isUrl(@Nullable String mayBeUrl) {
        if (isBlank(mayBeUrl)) {
            return false;
        } else if (isDataUrl(mayBeUrl)) {
            return true;
        } else {
            try {
                URL url = new URL(mayBeUrl);
                return true;
            } catch (Exception ex) {
                return false;
            }
        }
    }

    /**
     * return temp file; will set deleteOnExit().
     *
     * @return
     */
    public static File tempFile() {
        return tempFile(null, null);
    }

    /**
     * return temp file with given prefix and optional suffix; will set
     * deleteOnExit().
     *
     * @param prefix optional file prefix, trailing [-_]+ will be stripped and
     * replaced with single _
     * @param suffix optional file suffix, leading [.]+ will be stripped and
     * replaced with single .
     * @param deleteOnExit delete file on exit
     * @return
     */
    public static File tempFile(@Nullable String prefix, @Nullable String suffix, boolean deleteOnExit) {
        return customTempFile(cmTmpDir(), prefix, suffix, deleteOnExit);
    }

    public static File customTempFile(File customDir, String prefix, String suffix, boolean deleteOnExit) {
        File tempFile = new File(customDir, (firstNotNull(prefix, "cmdbuild_temp_").replaceAll("[_-]+$", "") + "_" + tempId()) + "." + firstNotNull(suffix, "file").replaceAll("^[.]+", ""));
        checkArgument(tempFile.getParentFile().isDirectory(), "java temp dir error, tmpdir = (%s)", tempFile.getParent());
        if (deleteOnExit) {
            tempFile.deleteOnExit();
        }
        return tempFile;
    }

    public static File tempFile(@Nullable String prefix, @Nullable String suffix) {
        return tempFile(prefix, suffix, true);
    }

    public static File tempFile(@Nullable String prefix, @Nullable String suffix, byte[] data, boolean deleteOnExit) {
        File file = tempFile(prefix, suffix, deleteOnExit);
        writeToFile(data, file);
        return file;

    }

    public static File tempFile(@Nullable String prefix, @Nullable String suffix, byte[] data) {
        return tempFile(prefix, suffix, data, true);
    }

    /**
     * Store a file in a current user temporary dir: at runtime, when running
     * CMDBuild, it is the
     * <i>tomcat user</i>, with optional prefix; will set
     * <code>deleteOnExit()</code> (delete directory when Tomcat is stopped).
     *
     * @param prefix
     * @param suffix
     * @param data
     * @return
     */
    public static File currentUserTempFile(@Nullable String prefix, @Nullable String suffix, byte[] data) {
        File file = customTempFile(currentUserTempDir(null), prefix, suffix, true);
        writeToFile(data, file);
        return file;
    }

    /**
     * return temp dir; will set deleteOnExit().
     *
     * @return
     */
    public static File tempDir() {
        return tempDir((String) null);
    }

    /**
     * return temp dir with optional prefix; will set deleteOnExit().
     *
     * @param prefix dir prefix, trailing [-_]+ will be stripped and replaced
     * with single _
     * @return
     */
    public static File tempDir(@Nullable String prefix) {
        return tempDir(prefix, true, true);
    }

    public static File tempDir(Duration ttl) {
        return tempDir(format("cmdbuild_tempttl_%s_", Hex.encodeHexString(toIsoDuration(ttl).getBytes(UTF_8))), true, true);
    }

    public static void tempFilesTtlCleanup() {
        LOGGER.debug("execute temp files ttl-based cleanup on folder =< {} >", cmTmpDir().getAbsolutePath());
        asList(cmTmpDir().listFiles((d, n) -> n.startsWith("cmdbuild_tempttl_"))).forEach(unsafe(f -> {
            LOGGER.trace("check file =< {} >", f.getAbsolutePath());
            Matcher matcher = Pattern.compile("cmdbuild_tempttl_([^_]+)_.*").matcher(f.getName());
            if (matcher.matches()) {
                Duration ttl = toDuration(new String(Hex.decodeHex(checkNotBlank(matcher.group(1))), UTF_8));
                Instant lastModified = Files.readAttributes(f.toPath(), BasicFileAttributes.class).lastModifiedTime().toInstant();
                if (lastModified.plus(ttl).isBefore(now().toInstant())) {
                    LOGGER.debug("delete file=< {} > ( last mod =< {} > ttl =< {} > )", f.getAbsolutePath(), toIsoDateTime(lastModified), toIsoDuration(ttl));
                    deleteQuietly(f);
                }
            }
        }));
    }

    public static File tempDir(@Nullable String prefix, boolean deleteOnExit, boolean create) {
        return customTempDir(cmTmpDir(), prefix, create, deleteOnExit);
    }

    public static File customTempDir(File customTmpDir, String prefix, boolean create, boolean deleteOnExit) {
        File tempDir = new File(customTmpDir, (firstNotNull(prefix, "cmdbuild_temp_").replaceAll("[_-]+$", "") + "_" + tempId()));
        if (create) {
            checkArgument(tempDir.mkdirs(), "unable to create temp dir %s", tempDir);
            if (deleteOnExit) {
                tempDir.deleteOnExit();
            }
        }
        return tempDir;
    }

    public static File javaTmpDir() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    public static File cmTmpDir() {
        for (String name : list(System.getProperty("cmdbuild.tmpdir"), System.getenv("CMDBUILD_TMP"), System.getProperty("java.io.tmpdir"))) {
            if (isNotBlank(name)) {
                File file = new File(name);
                file.mkdirs();
                if (file.isDirectory()) {
                    return file;
                }
            }
        }
        throw runtime("error: unable to find temp dir for cmdbuild");
    }

    /**
     * Current user temporary dir: at runtime, when running CMDBuild, it is the
     * <i>tomcat user</i>, with optional prefix; will set
     * <code>deleteOnExit()</code> (delete directory when Tomcat is stopped).
     *
     * @param prefix dir prefix, trailing [-_]+ will be stripped and replaced
     * with single _
     * @return
     */
    public static File currentUserTempDir(@Nullable String prefix) {
        return currentUserTempDir(prefix, true, true);
    }

    /**
     * Current user temporary dir: at runtime, when running CMDBuild, it is the
     * <i>tomcat user</i>
     *
     * @param prefix dir prefix, trailing [-_]+ will be stripped and replaced
     * with single _
     * @return
     */
    public static File currentUserTempDir(@Nullable String prefix, boolean deleteOnExit, boolean create) {
        return customTempDir(new File(System.getProperty("user.home"), "temp"), prefix, create, deleteOnExit);
    }

    public static File tempDirNoCreate() {
        return tempDir(null, false, false);
    }

    public static File cmSlowCacheDir() {
        return CM_SLOW_CACHE_DIR.get();
    }

    private static File configureCmSlowCacheDir() {
        try {
            File cacheDir = new File(System.getProperty("user.home"), ".cache/cmdbuild");
            cacheDir.mkdirs();
            checkArgument(cacheDir.exists(), "unable to create cache file =< {} >", cacheDir.getAbsolutePath());
            return cacheDir;
        } catch (Exception ex) {
            LOGGER.warn("error configuring user cache file, will use java tmp", ex);
            return javaTmpDir();
        }
    }

    /**
     *
     * @return a temp id build with date and random uuid, to be used as part of
     * a temporary file name
     */
    public static String tempId() {
        return dateTimeFileSuffix() + "_" + UUID.randomUUID().toString().substring(0, 6);
    }

    public static File fetchFileWithCache(String sha1checksum, String url) {
        return fetchFileWithCache(sha1checksum, () -> {
            try {
                LOGGER.info("fetch resource from url = {}", url);
                if (url.startsWith("classpath:")) {
                    return CmIoUtils.class.getClassLoader().getResourceAsStream(url.replace("classpath:", ""));
                } else {
                    return new URI(url).toURL().openStream();
                }
            } catch (Exception ex) {
                throw runtime(ex);
            }
        });
    }

    public static File fetchFileWithCache(String sha1checksum, Supplier<InputStream> fetcher) {
        checkNotBlank(sha1checksum);
        return fetchFileWithCache(new File(cmSlowCacheDir(), "cm_" + DigestUtils.md5Hex(sha1checksum + nullToEmpty(System.getProperty("user.name"))) + ".cache"), (f) -> {
            try {
                String actual = DigestUtils.sha1Hex(new FileInputStream(f));
                if (actual.equalsIgnoreCase(sha1checksum)) {
                    return true;
                } else {
                    LOGGER.warn("mismatching checksum for file, expected =< {} > but found =< {} >", sha1checksum, actual);
                    return false;
                }
            } catch (IOException ex) {
                throw runtime(ex);
            }
        }, fetcher);
    }

    public static File fetchFileWithCache(File cacheFile, Predicate<File> fileChecker, Supplier<InputStream> fetcher) {
        if (!(cacheFile.exists() && fileChecker.test(cacheFile))) {
            cacheFile.getParentFile().mkdirs();
            copy(fetcher.get(), cacheFile);
        }
        return cacheFile;
    }

    public static void copy(InputStream in, OutputStream out) {
        try {
            IOUtils.copy(in, out);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void copy(InputStream in, File out) {
        try {
            FileUtils.copyInputStreamToFile(in, out);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void writeToFile(String data, File out) {
        try {
            FileUtils.write(out, data, Charset.defaultCharset());
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void writeToFile(BigByteArray data, File out) {
        copy(data.toInputStream(), out);
    }

    public static void writeToFile(byte[] data, File out) {
        try {
            FileUtils.writeByteArrayToFile(out, data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void writeToFile(DataSource data, File out) {
        try {
            FileUtils.copyInputStreamToFile(data.getInputStream(), out);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static void copy(DataHandler in, File out) {
        try {
            FileUtils.copyInputStreamToFile(in.getInputStream(), out);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static byte[] serializeObject(Object object) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(out)) {
            objectOutputStream.writeObject(object);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return out.toByteArray();
    }

    public static <T> T deserializeObject(byte[] data) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            throw runtime(ex);
        }
    }

    public static Properties loadProperties(byte[] data) {
        try {
            Properties properties = new Properties();
            properties.load(new ByteArrayInputStream(data));
            return properties;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] toByteArray(DataHandler dataHandler) {
        try (InputStream in = dataHandler.getInputStream()) {
            return IOUtils.toByteArray(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static BigByteArray toBigByteArray(DataHandler dataHandler) {
        try (InputStream in = dataHandler.getInputStream()) {
            return toBigByteArray(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] toByteArray(DataSource dataSource) {
        try (InputStream in = dataSource.getInputStream()) {
            return toByteArray(in);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] toByteArray(InputStream inputStream) {
        try {
            return inputStream instanceof ByteArrayInputStream byteArrayInputStream ? byteArrayInputStream.readAllBytes() : IOUtils.toByteArray(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static byte[] readBytes(InputStream inputStream, int count) {
        try {
            byte[] buffer = new byte[count];
            int res = inputStream.read(buffer);
            checkArgument(res == count, "try to read %s bytes, but only %s available", count, res);
            return buffer;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static DataSource urlToDataSource(String url) {
        return CmUrlUtils.urlToDataSource(url);
    }

    public static byte[] urlToByteArray(String url) {
        return CmUrlUtils.urlToByteArray(url);
    }

    public static String readToString(DataHandler dataHandler) {
        try {
            return IOUtils.toString(dataHandler.getInputStream(), getCharsetFromContentTypeOrDefault(dataHandler.getContentType()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String readToString(DataSource dataSource) {
        try {
            return IOUtils.toString(dataSource.getInputStream(), getCharsetFromContentTypeOrDefault(dataSource.getContentType()));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String readToString(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String readToString(URI uri) {
        try (InputStream in = uri.toURL().openStream()) {
            return IOUtils.toString(in, Charset.defaultCharset());//TODO handle content type
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String readToString(InputStream in, @Nullable String contentType) {
        return readToString(toByteArray(in), contentType);
    }

    public static String readToString(byte[] data, @Nullable String contentType) {
        try {
            return new String(data, getCharsetFromContentTypeOrDefault(contentType));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static DataHandler toDataHandler(InputStream data) {
        return toDataHandler(toByteArray(data));
    }

    /**
     * Build a {@link DataHandler} for given byte content, with:
     * <ul>
     * <li>auto-detected content type;
     * <li>auto-built file name, with file extension based on auto-detected
     * content type.
     * </ul>
     *
     * @param data
     * @return
     */
    public static DataHandler toDataHandler(byte[] data) {
        try {
            String contentType = TIKA.detect(data);
            String filename = getFilenameFromContentType(contentType);
            return newDataHandler(data, contentType, filename);
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    /**
     * Build a {@link DataHandler} for given byte content and filename, with:
     * <ul>
     * <li>auto-detected content type.
     * </ul>
     *
     * @param data
     * @param filename
     * @return
     */
    public static DataHandler toDataHandler(byte[] data, String filename) {
        try {
            String contentType = TIKA.detect(data);
            return newDataHandler(data, contentType, filename);
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static String getFilenameFromContentType(String contentType) {
        return "file_" + UUID.randomUUID().toString().substring(0, 4).toLowerCase() + getExtFromContentType(contentType);
    }

    public static String getExtFromContentType(String contentType) {
        try {
            return TikaConfig.getDefaultConfig().getMimeRepository().forName(contentType).getExtension();
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static String getContentType(DataSource data) {
        if (isNotBlank(data.getContentType()) && !isContentType(data.getContentType(), "application/octet-stream")) {
            return data.getContentType();
        } else {
            return detectContentType(data);
        }
    }

    public static String detectContentType(DataSource data) {
        try (InputStream in = data.getInputStream()) {
            return getContentType(in);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static String getContentType(BigByteArray data) {
        return getContentType(data.toInputStream());
    }

    public static String getContentType(byte[] data) {
        String contentType = TIKA.detect(data);
        if (isContentType(contentType, "text/plain") && isHtml(new String(data))) {
            contentType = "text/html";
        } else if (isContentType(contentType, "text/plain") && isJson(new String(data))) {
            contentType = "application/json";
        }
        return contentType;
    }

    public static String getContentType(File file) {
        try {
            return TIKA.detect(file);
        } catch (IOException ex) {
            LOGGER.warn("tika detect error", ex);
            return "application/octet-stream";
        }
    }

    public static String getContentType(InputStream in) {
        try {
            return TIKA.detect(in);
        } catch (IOException ex) {
            LOGGER.warn("tika detect error", ex);
            return "application/octet-stream";
        }
    }

    public static boolean isPlaintext(byte[] data) {
        return getContentType(data).matches("text/.*");
    }

    public static boolean isPlaintext(DataSource data) {
        return getContentType(data).matches("text/.*");
    }

    public static boolean isZip(DataSource data) {
        return nullToEmpty(data.getName()).toLowerCase().endsWith(".zip") || isContentType(getContentType(data), "application/zip");
    }

    public static boolean isDataCompressed(DataSource data) {
        return isZip(data) || isContentType(getContentType(data), "application/gzip", "application/zlib");
    }

    public static boolean isDataCompressed(byte[] data) {
        return isContentType(getContentType(data), "application/zip", "application/gzip", "application/zlib");
    }

    public static boolean isCompressedWithXzip(File file) {
        try {
            return TIKA.detect(file).contains("application/x-xz");
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static boolean isJson(DataSource data) {
        return isContentType(data.getContentType(), "application/json");
    }

    public static boolean isHtml(String mayBeHtml) {
        Matcher matcher = Pattern.compile("</?\\s*(div|p|br|span|b|i|a)[^a-z]", Pattern.CASE_INSENSITIVE).matcher(mayBeHtml);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        int eolnCount = mayBeHtml.replaceAll("[^\n]", "").length();
        return (count > 0 && count > mayBeHtml.length() / 500) || (count > 0 && eolnCount >= 5 && count > eolnCount / 3) || (count > 0 && eolnCount < 5);
    }

    public static boolean isJson(String mayBeJson) {
        if (isNotBlank(mayBeJson) && mayBeJson.matches("(?s)[\\[{].+[\\]}]")) {
            try {
                fromJson(mayBeJson, JsonNode.class);
                return true;
            } catch (Exception ex) {
                LOGGER.debug("not json: {}", ex.toString());
            }
        }
        return false;
    }

    private final static Pattern CONTENT_TYPE_CHARSET_PATTERN = Pattern.compile("charset *= *\"?([a-z0-9_-]+)\"?", Pattern.CASE_INSENSITIVE);

    @Nullable
    public static String getCharsetFromContentType(@Nullable String contentType) {
        Matcher matcher = CONTENT_TYPE_CHARSET_PATTERN.matcher(nullToEmpty(contentType));
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    public static String getCharsetFromContentTypeOrDefault(@Nullable String contentType) {
        return firstNotBlank(getCharsetFromContentType(contentType), Charset.defaultCharset().name());
    }

    public static String getCharsetFromContentTypeOrUtf8(@Nullable String contentType) {
        return firstNotBlank(getCharsetFromContentType(contentType), StandardCharsets.UTF_8.name());
    }

    public static String setCharsetInContentType(String contentType, String charset) {
        checkNotBlank(contentType);
        Matcher matcher = CONTENT_TYPE_CHARSET_PATTERN.matcher(contentType);
        if (matcher.find()) {
            return matcher.replaceFirst(Matcher.quoteReplacement(format("charset=%s", charset)));
        } else {
            return format("%s; charset=%s", contentType, charset);
        }
    }

    public static String setCharsetInContentType(String contentType, Charset charset) {
        return setCharsetInContentType(contentType, charset.name());
    }

    public static boolean hasContentType(DataSource data, String... contentTypeExprPatterns) {
        return isContentType(getContentType(data), contentTypeExprPatterns);
    }

    public static boolean hasContentTypeActualOrDetected(DataSource data, String... contentTypeExprPatterns) {
        return isContentType(data.getContentType(), contentTypeExprPatterns) || isContentType(detectContentType(data), contentTypeExprPatterns);
    }

    public static boolean isContentType(@Nullable String contentTypeString, String... contentTypeExprPatterns) {
        if (contentTypeString == null) {
            return false;
        }

        String toCheckContentType = contentTypeString.toLowerCase();

        return asList(contentTypeExprPatterns).stream().anyMatch(contentTypeExprPattern -> toCheckContentType.startsWith(contentTypeExprPattern.replace("*", "").toLowerCase()));
    }

    @Nullable
    public static String getExt(byte[] data) {
        return getExtFromContentType(TIKA.detect(data));
    }

    public static DataHandler newDataHandler(File file) {
        return toDataHandler(newDataSource(file));
    }

    public static DataHandler newDataHandler(byte[] data, String contentType, @Nullable String fileName) {
        return new DataHandler(newDataSource(data, contentType, fileName));
    }

    public static DataHandler newDataHandler(byte[] data, String contentType) {
        return newDataHandler(data, contentType, null);
    }

    public static DataHandler newDataHandler(DataHandler data) {
        return newDataHandler(toBigByteArray(data), data.getContentType(), data.getName());
    }

    public static DataHandler newDataHandler(BigByteArray data, @Nullable String contentType, @Nullable String fileName) {
        return new DataHandler(newDataSource(data, contentType, fileName));
    }

    public static DataHandler newDataHandler(BigByteArray data) {
        return new DataHandler(newDataSource(data, getContentType(data), null));
    }

    public static DataHandler newDataHandler(String data, String contentType) {
        return newDataHandler(data.getBytes(), contentType, null);//TODO check charset
    }

    public static DataHandler newDataHandler(InputStream in) {
        return toDataHandler(newDataSource(in));
    }

    public static DataHandler newDataHandler(byte[] data) {
        return newDataHandler(data, TIKA.detect(data), null);
    }

    public static DataHandler toDataHandler(DataSource data) {
        return new DataHandler(data);
    }

    public static DataSource urlToDataSource(URL url) {
        return urlToDataSource(url.toString());
    }

    public static DataSource toDataSource(DataHandler dataHandler) {
        return new DataHandlerAsDataSource(dataHandler);
    }

    public static DataSource newDataSourceFromUrl(String url) {
        try {
            return newDataSource(new URL(checkNotBlank(url)));
        } catch (MalformedURLException ex) {
            throw runtime(ex);
        }
    }

    public static DataSource newDataSource(URL url) {
        checkNotNull(url);
        if (url.getProtocol().equalsIgnoreCase("file")) {
            return newDataSource(url::openStream, null, FilenameUtils.getName(url.getPath()));
        } else {
            try {
                return newDataSource(toBigByteArray(url.openStream()));
            } catch (IOException ex) {
                throw runtime(ex, "error reading from url =< %s >", url);
            }
        }
    }

    public static DataSource newDataSource(String data, @Nullable String contentType) {
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        if (isBlank(contentType)) {
            contentType = getContentType(bytes);
        }
        contentType = setCharsetInContentType(contentType, StandardCharsets.UTF_8);
        return newDataSource(bytes, contentType);
    }

    public static DataSource newDataSource(File file) {
        checkNotNull(file);
        checkArgument(file.isFile() && file.canRead(), "invalid file =< %s >", file.getAbsolutePath());
        return new MyDataSource(() -> new FileInputStream(file), getContentType(file), file.getName());
    }

    public static DataSource toDataSource(File file) {
        return newDataSource(toBigByteArray(file), getContentType(file), file.getName());
    }

    public static DataSource emptyDataSource() {
        return newDataSource(new byte[]{});
    }

    public static DataSource newDataSource(String data) {
        return newDataSource(data.getBytes(StandardCharsets.UTF_8));
    }

    public static DataSource newDataSource(byte[] data) {
        return newDataSource(data, getContentType(data));
    }

    public static DataSource newDataSource(byte[] data, @Nullable String contentType) {
        return newDataSource(data, contentType, null);
    }

    public static DataSource newDataSource(InputStream in) {
        return newDataSource(in, null);
    }

    public static DataSource newDataSource(InputStream in, @Nullable String contentType) {
        return newDataSource(in, contentType, null);
    }

    public static DataSource newDataSource(InputStream in, @Nullable String contentType, @Nullable String fileName) {
        return newDataSource(toByteArray(in), contentType, fileName);
    }

    public static DataSource newDataSource(Supplier_WithExceptions<InputStream, IOException> in, @Nullable String contentType, @Nullable String fileName) {
        checkNotNull(in, "input stream supplier is null");
        return new MyDataSource(in, contentType, fileName);
    }

    public static DataSource newDataSource(String data, @Nullable String contentType, @Nullable String charset, @Nullable String fileName) {
        try {
            if (isBlank(charset)) {
                charset = getCharsetFromContentTypeOrDefault(contentType);
            }
            byte[] bytes = data.getBytes(charset);
            if (isBlank(contentType)) {
                contentType = getContentType(bytes);//TODO also use file name
            }
            contentType = setCharsetInContentType(contentType, charset);
            return newDataSource(bytes, contentType, fileName);
        } catch (UnsupportedEncodingException ex) {
            throw runtime(ex);
        }
    }

    public static DataSource newDataSource(String data, @Nullable String contentType, @Nullable String fileName) {
        return newDataSource(data, contentType, null, fileName);
    }

    public static DataSource newDataSource(byte[] data, @Nullable String contentType, @Nullable String charset, @Nullable String fileName) {
        checkNotNull(data);
        if (isBlank(contentType)) {
            contentType = getContentType(data);//TODO also use file name
        }
        if (isNotBlank(charset)) {
            contentType = setCharsetInContentType(contentType, charset);
        }
        return new MyDataSource(() -> new ByteArrayInputStream(data), contentType, fileName);
    }

    public static DataSource newDataSource(byte[] data, @Nullable String contentType, @Nullable String fileName) {
        return newDataSource(data, contentType, null, fileName);
    }

    public static DataSource newDataSource(BigByteArray data) {
        return newDataSource(data, null, null);
    }

    public static DataSource newDataSource(BigByteArray data, @Nullable String contentType, @Nullable String fileName) {
        checkNotNull(data);
        return new MyDataSource(data::toInputStream, isBlank(contentType) ? getContentType(data) : contentType, fileName);
    }

    public static long countBytes(DataSource dataSource) {
        try {
            return IOUtils.copyLarge(dataSource.getInputStream(), NullOutputStream.INSTANCE);
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static long countBytes(DataHandler dataHandler) {
        try {
            return IOUtils.copyLarge(dataHandler.getInputStream(), NullOutputStream.INSTANCE);
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static String dataSourceInfoSafe(DataHandler dataHandler) {
        return dataSourceInfoSafe(toDataSource(dataHandler));
    }

    public static String dataSourceInfoSafe(DataSource dataSource) {
        try {
            String info = format("(%s bytes, %s)", countBytes(dataSource), getContentType(dataSource));
            if (isNotBlank(dataSource.getName())) {
                info = dataSource.getName() + " " + info;
            }
            return info;
        } catch (Exception ex) {
            LOGGER.debug("error retrieving data source info", ex);
            return "<error retrieving data source info>";
        }
    }

    public static long getAvailableLong(InputStream in) throws IOException {
        if (in instanceof BigInputStream bigInputStream) {
            return bigInputStream.availableLong();
        } else if (in instanceof FileInputStream fileInputStream) {
            return fileInputStream.available() == 0 ? 0l : fileInputStream.getChannel().size() - fileInputStream.getChannel().position();
        } else {
            return in.available();
        }
    }

    private static class DataHandlerAsDataSource implements DataSource {

        private final DataHandler inner;

        public DataHandlerAsDataSource(DataHandler dataHandler) {
            this.inner = checkNotNull(dataHandler);
        }

        @Override
        public String getContentType() {
            return inner.getContentType();
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return inner.getInputStream();
        }

        @Override
        public String getName() {
            return inner.getName();
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            return inner.getOutputStream();
        }
    }

    private static class MyDataSource implements DataSource {

        private final Supplier_WithExceptions<InputStream, IOException> is;
        private final String contentType, fileName;

        public MyDataSource(Supplier_WithExceptions<InputStream, IOException> is, @Nullable String contentType) {
            this(is, contentType, null);
        }

        public MyDataSource(Supplier_WithExceptions<InputStream, IOException> is, @Nullable String contentType, @Nullable String fileName) {
            this.is = checkNotNull(is);
            this.contentType = firstNotBlank(contentType, "application/octet-stream");
            this.fileName = nullToEmpty(fileName);
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return checkNotNull(is.get());
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContentType() {
            return contentType;
        }

        @Override
        public String getName() {
            return fileName;
        }

        @Override
        public String toString() {
            return "DataSource{" + "contentType=" + contentType + ", fileName=" + fileName + '}';
        }

    }
}
