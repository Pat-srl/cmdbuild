/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.Math.max;
import static java.lang.Math.toIntExact;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;

public final class BigByteArray {

    private final static int BUFFER_SIZE_MIN = 1024 * 16; //16KiB;
    private final static int NO_BUFFER_BLOCK_SIZE_THRESHOLD = 16 * 1024; //16KiB;
    private final static int BUFFER_SIZE_MAX = 128 * 1024; //128KiB

    private final List<byte[]> bytes = list();
    private long length = 0;
    private ByteArrayOutputStream buffer;

    public static BigByteArray copyOf(BigByteArray source) {
        return new BigByteArray().append(source);
    }

    public BigByteArray() {
    }

    public BigByteArray(byte[] data) {
        this();
        append(data);
    }

    public BigByteArray append(byte b) {
        prepareBuffer(1);
        buffer.write(b);
        length++;
        return this;
    }

    public BigByteArray append(byte[] data) {
        if (data.length == 0) {
            return this;
        } else if (data.length > NO_BUFFER_BLOCK_SIZE_THRESHOLD) {
            bytes.add(data);
            length += data.length;
            return this;
        } else {
            return append(data, 0, data.length);
        }
    }

    public BigByteArray append(byte[] data, int off, int len) {
        if (len > 0) {
            checkNotNull(data);
            prepareBuffer(len);
            buffer.write(data, off, len);
            length += len;
        }
        return this;
    }

    public BigByteArray append(BigByteArray data) {
        data.forEach(this::append);
        return this;
    }

    public void forEach(Consumer<byte[]> action) {
        flushBuffer();
        bytes.forEach(action);
    }

    public byte[] toByteArray() {
        if (length == 0) {
            return new byte[]{};
        } else {
            flushBuffer();
            checkArgument(length <= Integer.MAX_VALUE, "unable to convert this BigByteArray to byte[], length = %s (more than maxint)", length);
            ByteBuffer byteBuffer = ByteBuffer.allocate(toIntExact(length));
            forEach(byteBuffer::put);
            return byteBuffer.array();
        }
    }

    @Override
    public String toString() {
        return new String(toByteArray(), StandardCharsets.UTF_8);
    }

    public Stream<byte[]> stream() {
        flushBuffer();
        return bytes.stream();
    }

    public long length() {
        return length;
    }

    public void clear() {
        buffer = null;
        length = 0;
        bytes.clear();
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        forEach(rethrowConsumer(outputStream::write));
    }

    public InputStream toInputStream() {
        return new BigByteArrayInputStream(this);
    }

    private void prepareBuffer(int expected) {
        if (buffer != null && (long) buffer.size() + (long) expected > (long) BUFFER_SIZE_MAX) {
            flushBuffer();
        }
        if (buffer == null) {
            buffer = new ByteArrayOutputStream(max(BUFFER_SIZE_MIN, expected));
        }
    }

    /**
     * this is synchronized to avoid concurrent read issues
     */
    private synchronized void flushBuffer() {
        if (buffer != null) {
            if (buffer.size() > 0) {
                bytes.add(buffer.toByteArray());
            }
            buffer = null;
        }
    }

}
