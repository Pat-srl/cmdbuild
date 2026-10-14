/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.activation.DataSource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.urlToDataSource;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.url.CmUrlUtils.buildUrlPath;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlParams;
import static org.cmdbuild.utils.url.CmUrlUtils.encodeUrlParams;
import static org.cmdbuild.utils.url.CmUrlUtils.getUrlPathFilename;
import static org.cmdbuild.utils.url.CmUrlUtils.getUrlPathParent;
import static org.cmdbuild.utils.url.CmUrlUtils.toDataUrl;
import static org.cmdbuild.utils.url.CmUrlUtils.toFileUrl;
import static org.cmdbuild.utils.url.CmUrlUtils.urlToByteArray;
import static org.cmdbuild.utils.url.CmUrlUtils.urlToString;
import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class UrlUtilsTest {

    @Test
    public void testFileUrl1() {
        String url = toFileUrl(newDataSource("ciao!".getBytes(), null, "file.txt"));
        assertThat(url, matchesPattern("file:/.*/file.txt"));
        assertEquals("file.txt", new File(URI.create(url)).getName());
        assertEquals("ciao!", readToString(new File(URI.create(url))));
    }

    @Test
    public void testFileUrl2() {
        String url = toFileUrl(newDataHandler("ciao!".getBytes(), null, "file.txt"));
        assertThat(url, matchesPattern("file:/.*/file.txt"));
        assertEquals("file.txt", new File(URI.create(url)).getName());
        assertEquals("ciao!", readToString(new File(URI.create(url))));
    }

    @Test
    public void testFileUrl3() {
        String url = toFileUrl(newDataHandler("ciao! \u0200\u0117".getBytes(StandardCharsets.UTF_8), null, "file... \u0200\u0117  something.wathever"));
        assertThat(url, matchesPattern("file:/.*%20%20something.wathever"));
        assertEquals("file... \u0200\u0117  something.wathever", new File(URI.create(url)).getName());
        assertEquals("ciao! \u0200\u0117", readToString(new File(URI.create(url)), StandardCharsets.UTF_8));
    }

    @Test
    public void testUrlParsing1() {
        Map<String, String> map = decodeUrlParams(null);
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    public void testUrlParsing2() {
        Map<String, String> map = decodeUrlParams("");
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    public void testUrlParsing3() {
        Map<String, String> map = decodeUrlParams("  ");
        assertNotNull(map);
        assertEquals(0, map.size());
    }

    @Test
    public void testUrlParsing4() {
        Map<String, String> map = decodeUrlParams("test");
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals("test", getOnlyElement(map.keySet()));
        assertEquals(null, map.get("test"));
    }

    @Test
    public void testUrlParsing5() {
        Map<String, String> map = decodeUrlParams("test=something&other=else");
        assertNotNull(map);
        assertEquals(2, map.size());
        assertEquals("something", map.get("test"));
        assertEquals("else", map.get("other"));
    }

    @Test
    public void testUrlEncoding1() {
        assertEquals("", encodeUrlParams(emptyMap()));
        assertEquals("one=1&two=true&three=", encodeUrlParams(map("one", 1, "two", true, "three", null)));
    }

    @Test
    public void testUrlEncoding2() {
        String value = "sdf23^%$#@*()+!~`'\"?/",
                enc = encodeUrlParams(map("special", value));
        assertEquals("special=sdf23%5E%25%24%23%40*%28%29%2B%21%7E%60%27%22%3F%2F", enc);
        assertEquals(value, decodeUrlParams(enc).get("special"));
    }

    @Test
    public void testDataUrlEncoding1() throws DecoderException, MalformedURLException, IOException {
        byte[] data = Hex.decodeHex("2937489aab9789e0987f987d78");
        String dataUrl = toDataUrl(newDataSource(data, "application/x-something"));
        assertEquals("data:application/x-something;base64,KTdImquXieCYf5h9eA==", dataUrl);
        assertArrayEquals(data, urlToByteArray(dataUrl));
        assertEquals("application/x-something", urlToDataSource(dataUrl).getContentType());
        assertArrayEquals(data, toByteArray(urlToDataSource(dataUrl)));
    }

    @Test
    public void testDataUrlEncoding2() throws DecoderException, MalformedURLException, IOException {
        String dataUrl = toDataUrl(newDataSource("hello", "text/plain"));
        assertEquals("data:text/plain;%20charset=UTF-8;base64,aGVsbG8=", dataUrl);
        assertEquals("hello", urlToString(dataUrl));
        assertEquals("text/plain; charset=UTF-8", urlToDataSource(dataUrl).getContentType());
        assertEquals("hello", readToString(urlToDataSource(dataUrl)));
    }

    @Test
    public void testDataUrlEncoding3() throws DecoderException, MalformedURLException, IOException {
        DataSource data = newDataSource("My File Content".getBytes(), "text/plain;charset=ISO-8859-1");
        assertEquals("text/plain;charset=ISO-8859-1", data.getContentType());
        assertEquals("My File Content", readToString(data));
        String dataUrl = toDataUrl(data);
        data = urlToDataSource(dataUrl);
        assertEquals("text/plain;charset=ISO-8859-1", data.getContentType());
        assertEquals("My File Content", readToString(data));
    }

    @Test
    public void testPathBuild() {
        assertEquals("some/path/and/some/sub/path", buildUrlPath("some/path/", "and/some/sub/path"));
        assertEquals("/some/path/and/some/sub/path", buildUrlPath("/some/path/", "and/some/sub/path"));
        assertEquals("/some/path/and/some/sub/path", buildUrlPath("/some/path/", "/and/some/sub/path"));
        assertEquals("some/path/and/some/sub/path/", buildUrlPath("some/path/", "/and/some/", "///sub///path/"));
    }

    @Test
    public void testUrlPathParent() {
        assertEquals("some/path", getUrlPathParent("some/path/file"));
        assertEquals("some/path", getUrlPathParent("some/path/file/"));
        assertEquals("/some/path", getUrlPathParent("/some/path/file"));
        assertEquals("/some/path", getUrlPathParent("/some/path/file/"));
        assertEquals("", getUrlPathParent("file"));
        assertEquals("", getUrlPathParent("file/"));
        assertEquals("/", getUrlPathParent("/file"));
        assertEquals("/", getUrlPathParent("/file/"));
    }

    @Test
    public void testUrlPathFilename() {
        assertEquals("file", getUrlPathFilename("some/path/file"));
        assertEquals("file", getUrlPathFilename("some/path/file/"));
        assertEquals("file", getUrlPathFilename("/some/path/file"));
        assertEquals("file", getUrlPathFilename("/some/path/file/"));
        assertEquals("file", getUrlPathFilename("file"));
        assertEquals("file", getUrlPathFilename("file/"));
        assertEquals("file", getUrlPathFilename("/file"));
        assertEquals("file", getUrlPathFilename("/file/"));
    }

}
