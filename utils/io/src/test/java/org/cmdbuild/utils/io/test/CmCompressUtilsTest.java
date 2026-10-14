/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import static org.cmdbuild.utils.io.CmCompressionUtils.xunzip;
import static org.cmdbuild.utils.io.CmCompressionUtils.xzip;
import org.cmdbuild.utils.testutils.Slow;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmCompressUtilsTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testXzUtils1() {
        String orig = "ciao come va?";
        byte[] data = orig.getBytes(StandardCharsets.UTF_8),
                compressed = xzip(data),
                decompressed = xunzip(compressed);
        assertFalse(Arrays.equals(compressed, data));
        assertArrayEquals(data, decompressed);
    }

    @Test
    public void testXzUtils2() {
        byte[] data = new byte[100000],// 100 KiB
                compressed = xzip(data),
                decompressed = xunzip(compressed);
        logger.info("compressed {} bytes to {} bytes", data.length, compressed.length);
        assertTrue(compressed.length < data.length);
        assertFalse(Arrays.equals(compressed, data));
        assertArrayEquals(data, decompressed);
    }

    @Test
    @Slow
    public void testXzUtils3() {
        byte[] data = new byte[100000000],// 100 MiB
                compressed = xzip(data),
                decompressed = xunzip(compressed);
        logger.info("compressed {} bytes to {} bytes", data.length, compressed.length);
        assertTrue(compressed.length < data.length);
        assertFalse(Arrays.equals(compressed, data));
        assertArrayEquals(data, decompressed);
    }

}
