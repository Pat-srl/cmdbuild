/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import jakarta.activation.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Random;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.BigByteArrayInputStream;
import org.cmdbuild.utils.io.BigByteArrayOutputStream;
import static org.cmdbuild.utils.io.CmIoUtils.isUrl;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSourceFromUrl;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.tempFilesTtlCleanup;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmIoUtils.urlToByteArray;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import static org.cmdbuild.utils.url.CmUrlUtils.toDataUrl;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;
import org.junit.Test;

public class CmIoTest {

    private final Random random = new Random(123123123);

    @Test
    public void testTempCleanup() {
        File one = tempDir(Duration.ofMillis(1)), two = tempDir(Duration.ofSeconds(100));
        assertTrue(one.exists());
        assertTrue(two.exists());
        sleepSafe(10);
        tempFilesTtlCleanup();
        assertFalse(one.exists());
        assertTrue(two.exists());
    }

    @Test(expected = IllegalArgumentException.class)
    @Ignore("slow test")
    public void testByteArrayOfThreeGigsThrowError() throws IOException {
        File file = tempFile();
        try {
            try ( FileOutputStream out = new FileOutputStream(file)) {
                for (int i = 0; i < 3000; i++) {
                    byte[] data = new byte[1024 * 1024];//1 M
                    random.nextBytes(data);
                    out.write(data);
                }
            }
            assertEquals(3000 * 1024 * 1024l, file.length());
            byte[] allData = toByteArray(file);
        } finally {
            deleteQuietly(file);
        }
    }

    @Test
    @Ignore("slow test")
    public void testBigByteArrayOfThreeGigsFromFile() throws IOException {
        File file = tempFile();
        try {
            try ( FileOutputStream out = new FileOutputStream(file)) {
                for (int i = 0; i < 3000; i++) {
                    byte[] data = new byte[1024 * 1024];
                    random.nextBytes(data);
                    out.write(data);
                }
            }
            assertEquals(3000 * 1024 * 1024l, file.length());
            BigByteArray bigByteArray = toBigByteArray(file);
            assertEquals(3000 * 1024 * 1024l, bigByteArray.length());
        } finally {
            deleteQuietly(file);
        }
    }

    @Test
    @Ignore("slow test")
    public void testBigByteArrayOfThreeGigs() throws IOException {
        BigByteArrayOutputStream out = new BigByteArrayOutputStream();
        for (int i = 0; i < 3000; i++) {
            byte[] data = new byte[1024 * 1024];
            random.nextBytes(data);
            out.write(data);
        }
        assertEquals(3000 * 1024 * 1024l, out.toBigByteArray().length());
    }

    @Test
    public void testBigByteArray() throws IOException {
        BigByteArrayOutputStream bbaos = new BigByteArrayOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < 100; i++) {
            byte[] data = new byte[i + 100];
            random.nextBytes(data);
            bbaos.write(data);
            baos.write(data);
        }

        assertEquals(baos.size(), bbaos.size());

        byte[] data = baos.toByteArray(),
                bdata = bbaos.toByteArray();

        assertEquals(data.length, bdata.length);
        assertArrayEquals(data, bdata);

        BigByteArrayInputStream bbais = new BigByteArrayInputStream(bbaos.toBigByteArray());
        byte[] dataFromStream = toByteArray(bbais);

        assertEquals(data.length, dataFromStream.length);
        assertArrayEquals(data, dataFromStream);
    }

    @Test
    public void testBigByteArray2() throws IOException {
        BigByteArrayOutputStream bbaos = new BigByteArrayOutputStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (int i = 0; i < 100; i++) {
            byte[] data = new byte[i + 100];
            random.nextBytes(data);
            bbaos.write(data, i / 2, data.length / 2 + 1);
            baos.write(data, i / 2, data.length / 2 + 1);
        }

        assertEquals(baos.size(), bbaos.size());

        byte[] data = baos.toByteArray(),
                bdata = bbaos.toByteArray();

        assertEquals(data.length, bdata.length);
        assertArrayEquals(data, bdata);

        BigByteArrayInputStream bbais = new BigByteArrayInputStream(bbaos.toBigByteArray());
        byte[] dataFromStream = toByteArray(bbais);

        assertEquals(data.length, dataFromStream.length);
        assertArrayEquals(data, dataFromStream);
    }

    @Test
    public void testBigByteArray3() throws IOException {
        BigByteArray bigByteArray = new BigByteArray();

        assertEquals(0, bigByteArray.length());
        assertEquals(0, bigByteArray.toByteArray().length);
        assertEquals("", bigByteArray.toString());

        bigByteArray.append(new byte[]{});

        assertEquals(0, bigByteArray.length());
        assertEquals(0, bigByteArray.toByteArray().length);
        assertEquals("", bigByteArray.toString());

        bigByteArray.append(new BigByteArray());

        assertEquals(0, bigByteArray.length());
        assertEquals(0, bigByteArray.toByteArray().length);
        assertEquals("", bigByteArray.toString());
    }

    @Test
    public void testBigByteArrayToString() throws IOException {
        BigByteArray bigByteArray = new BigByteArray();

        bigByteArray
                .append("hello".getBytes(StandardCharsets.UTF_8))
                .append((byte) ' ')
                .append("world!".getBytes(StandardCharsets.UTF_8));

        assertEquals(12, bigByteArray.length());
        assertEquals("hello world!", bigByteArray.toString());
    }

    @Test
    public void testIsUrl() {
        assertTrue(isUrl("http://some.site"));
        assertTrue(isUrl("file:///local/file.txt"));
        assertFalse(isUrl(""));
        assertFalse(isUrl(null));
        assertFalse(isUrl("something"));
        assertTrue(isUrl(toDataUrl("some data".getBytes(UTF_8))));
    }

    @Test
    public void testUrlRead() throws IOException {
        File file = tempFile();
        try {
            Files.writeString(file.toPath(), "test");

            DataSource dataSource = newDataSourceFromUrl("file://" + file.getAbsolutePath());//TODO improve this
            assertEquals("test", readToString(dataSource));
        } finally {
            deleteQuietly(file);
        }
    }

    @Test
    public void testUrlToByteArray() throws IOException {
        byte[] data = "test".getBytes(StandardCharsets.UTF_8);

        String dataUrl = toDataUrl(data);
        assertTrue(isUrl(dataUrl));

        assertEquals("test", new String(urlToByteArray(dataUrl), StandardCharsets.UTF_8));

        data = new byte[1024];
        new Random().nextBytes(data);

        dataUrl = toDataUrl(data);

        assertArrayEquals(data, urlToByteArray(dataUrl));
    }
}
