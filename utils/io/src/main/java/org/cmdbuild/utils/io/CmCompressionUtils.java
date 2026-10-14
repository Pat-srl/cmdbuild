/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Stopwatch;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorOutputStream;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.io.IOUtils.copyLarge;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmPlatformUtils.isLinux;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmCompressionUtils {

    public static int SYSTEM_XZ_THRESHOLD = 10000000;// 10 MiB

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static byte[] deflate(byte[] data) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (OutputStream out = new DeflaterOutputStream(byteArrayOutputStream)) {
            out.write(data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        LOGGER.debug("compressed from {} to {}, elapsed: {}", byteCountToDisplaySize(data.length), byteCountToDisplaySize(byteArrayOutputStream.size()), stopwatch);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] inflate(byte[] data) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (OutputStream out = new InflaterOutputStream(byteArrayOutputStream)) {
            out.write(data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        LOGGER.debug("decompressed from {} to {}, elapsed: {}", byteCountToDisplaySize(data.length), byteCountToDisplaySize(byteArrayOutputStream.size()), stopwatch);
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] xzip(byte[] data) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        try (CompressorOutputStream compressorOutputStream = data.length < SYSTEM_XZ_THRESHOLD ? new XZCompressorOutputStream(out, 9) : buildXzCompressorOutputStream(out, 9)) {
        try (CompressorOutputStream compressorOutputStream = new XZCompressorOutputStream(out, 9)) { //safer
            compressorOutputStream.write(data);
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return out.toByteArray();
    }

    public static byte[] xunzip(byte[] data) {
        try (XZCompressorInputStream in = new XZCompressorInputStream(new ByteArrayInputStream(data))) {
            return toByteArray(in);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    public static CompressorOutputStream buildXzCompressorOutputStream(OutputStream out, int level) {
        if (hasSystemXz()) {
            LOGGER.debug("enabled system xz compressor output stream");
            return buildSystemXzCompressorOutputStream(out, level);
        } else {
            try {
                return new XZCompressorOutputStream(out, level);
            } catch (IOException ex) {
                throw runtime(ex);
            }
        }
    }

    private static boolean hasSystemXz() {
        if (isLinux()) {
            try {
                LOGGER.debug("test system xz");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                try (CompressorOutputStream compressor = buildSystemXzCompressorOutputStream(out, 9)) {
                    compressor.write("ciao come va".getBytes(StandardCharsets.UTF_8));
                }
                byte[] data = xunzip(out.toByteArray());
                checkArgument(new String(data, StandardCharsets.UTF_8).equals("ciao come va"));
                return true;
            } catch (Exception ex) {
                LOGGER.debug("system xz test failed", ex);
                return false;
            }
        }
        return false;
    }

    private static CompressorOutputStream buildSystemXzCompressorOutputStream(OutputStream out, int level) {
        checkNotNull(out);
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"xz", "-" + level, "-T", Integer.toString(Runtime.getRuntime().availableProcessors() / 2)});
            CompletableFuture future = new CompletableFuture();
            new Thread() {

                private final InputStream processInputStream = checkNotNull(process.getInputStream()),
                        processErrorStream = checkNotNull(process.getErrorStream());

                @Override
                public void run() {
                    try {
                        LOGGER.debug("reading xz process output");
                        long count = copyLarge(processInputStream, out);
                        LOGGER.debug("received {} bytes from xz process", count);
                        String errors = readToString(processErrorStream);
                        if (!isBlank(errors)) {
                            LOGGER.error("xz process stderr:\n\n{}", errors);
                        }
                        LOGGER.debug("waiting for xz process shutdown");
                        checkArgument(process.waitFor() == 0, "process exit error code = %s: %s", process.exitValue(), abbreviate(errors));
                        future.complete(null);
                    } catch (Throwable ex) {
                        future.completeExceptionally(ex);
                    }
                }
            }.start();
            return new CompressorOutputStream() {

                private final OutputStream processOutputStream = checkNotNull(process.getOutputStream());

                @Override
                public void write(int b) throws IOException {
                    processOutputStream.write(b);
                }

                @Override
                public void write(byte[] buf, int off, int len) throws IOException {
                    LOGGER.debug("write {} bytes to xz process", len);
                    processOutputStream.write(buf, off, len);
                }

                @Override
                public void flush() throws IOException {
                    processOutputStream.flush();
                }

                @Override
                public void close() throws IOException {
                    LOGGER.debug("close xz output stream");
                    processOutputStream.close();
                    try {
                        future.get();
                    } catch (InterruptedException | ExecutionException ex) {
                        throw runtime(ex);
                    }
                    out.close();
                }
            };
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }
}
