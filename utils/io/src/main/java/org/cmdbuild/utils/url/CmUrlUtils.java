/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.url;

import com.google.common.base.Joiner;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getLast;
import com.google.common.net.UrlEscapers;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static java.lang.String.format;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import jakarta.annotation.Nullable;
import org.apache.commons.codec.binary.Base64;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import static org.cmdbuild.utils.io.CmIoUtils.TIKA;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.getContentType;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;

public class CmUrlUtils {

    public static String encodeUrlParams(Map<String, ? extends Object> params) {
        try {
            return params.entrySet().stream().map(rethrowFunction(e -> format("%s=%s", URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8.name()), URLEncoder.encode(toStringOrEmpty(e.getValue()), StandardCharsets.UTF_8.name())))).collect(joining("&"));
        } catch (UnsupportedEncodingException ex) {
            throw runtime(ex);
        }
    }

    public static UrlPathAndParams decodeUrlPathAndParams(@Nullable String pathWithParams) {
        Matcher matcher = Pattern.compile("([^?]*)([?](.*))?").matcher(nullToEmpty(pathWithParams));
        checkArgument(matcher.matches());
        String path = nullToEmpty(matcher.group(1));
        Map<String, String> params = decodeUrlParams(matcher.group(3));
        return new UrlPathAndParams() {
            @Override
            public String getPath() {
                return path;
            }

            @Override
            public Map<String, String> getParams() {
                return params;
            }
        };
    }

    public static Map<String, String> decodeUrlParams(@Nullable String params) {
        if (isBlank(params)) {
            return emptyMap();
        } else {
            return Splitter.on("&").omitEmptyStrings().trimResults().splitToList(params).stream().map((v) -> {
                try {
                    Matcher matcher = Pattern.compile("([^=]+)=(.*)").matcher(v);
                    if (matcher.matches()) {
                        return Pair.of(matcher.group(1), URLDecoder.decode(matcher.group(2), UTF_8.name()));
                    } else {
                        return Pair.of(v, (String) null);
                    }
                } catch (Exception ex) {
                    throw runtime(ex, "error deconding url param token = %s", v);
                }
            }).collect(toImmutableMap(Pair::getKey, Pair::getValue));
        }
    }

    public static boolean isDataUrl(String url) {
        return nullToEmpty(url).matches("data:.+");
    }

    public static DataSource urlToDataSource(String url) {
        checkNotBlank(url);
        Matcher matcher = Pattern.compile("^data:(.*?);base64,(.+)").matcher(url);
        try {
            if (matcher.find()) {
                String contentType = URLDecoder.decode(matcher.group(1), UTF_8.name());
                byte[] data = Base64.decodeBase64(checkNotBlank(matcher.group(2)));
                return newDataSource(data, contentType);
            } else if (nullToEmpty(url).toLowerCase().matches("^https?://.*$")) {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpUriRequest request = new HttpGet(url);
                    try (CloseableHttpResponse response = httpClient.execute(request)) {
                        checkArgument(String.valueOf(response.getStatusLine().getStatusCode()).matches("2.."), "error processing request url =< %s >, server returned status code =< %s >", url, response.getStatusLine());
                        byte[] data = EntityUtils.toByteArray(response.getEntity());
                        return newDataSource(data, Optional.ofNullable(response.getEntity().getContentType()).map(h -> h.getValue()).orElse(TIKA.detect(data)));//TODO filename??
                    }
                } catch (IOException ex) {
                    throw runtime(ex, "error processing url =< %s >", url);
                }
            } else if (url.startsWith("classpath:")) {
                return newDataSource(CmUrlUtils.class.getClassLoader().getResourceAsStream(url.replaceFirst("classpath:", "")));
            } else {
                URLConnection connection = new URL(url).openConnection();
                String contentType = connection.getContentType();
                byte[] data = toByteArray(connection.getInputStream());
                return newDataSource(data, contentType);
            }
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public static byte[] urlToByteArray(String url) {
        return toByteArray(urlToDataSource(url));
    }

    public static String urlToString(String url) {
        return readToString(urlToDataSource(url));
    }

    public static String toDataUrl(byte[] data) {
        return toDataUrl(newDataSource(data));
    }

    public static String toDataUrl(DataHandler data) {
        return toDataUrl(toDataSource(data));
    }

    public static String toFileUrl(DataHandler data) {
        return toFileUrl(toDataSource(data));
    }

    public static String toDataUrl(DataSource data) {
        return format("data:%s;base64,%s", UrlEscapers.urlFragmentEscaper().escape(nullToEmpty(getContentType(data))), Base64.encodeBase64String(toByteArray(data)));
    }

    public static String toFileUrl(DataSource data) {
        try {
            File tempDir = tempDir();
            File tempFile = new File(tempDir, firstNotBlank(data.getName(), "file.raw"));
            tempFile.deleteOnExit();
            copy(data.getInputStream(), tempFile);
            return checkNotBlank(tempFile.getAbsoluteFile().toURI().toASCIIString());
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static String buildUrlPath(String... parts) {
        return stream(parts).collect(joining("/")).replaceAll("[/]+", "/");
    }

    public static String getUrlPathFilename(String path) {
        return checkNotBlank(getLast(list(path.replaceFirst("[/]+$", "").replaceFirst("^[/]+", "").split("/")), null), "unable to get filename from url path =< %s >", path);
    }

    public static String getUrlPathParent(String path) {
        List<String> parts = list(path.replaceFirst("[/]+$", "").replaceFirst("^[/]+", "").split("/"));
        checkArgument(!parts.isEmpty(), "cannot get parent of path =< %s >: path is empty or root", path);
        parts.remove(parts.size() - 1);
        String res = Joiner.on("/").join(parts);
        if (path.matches("^/.*")) {
            res = "/" + res;
        }
        return res;
    }

}
