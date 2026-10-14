/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.io.InputStream;
import java.util.Queue;
import static org.cmdbuild.utils.lang.CmCollectionUtils.queue;

public class BigByteArrayInputStream extends InputStream implements BigInputStream {

    private final long total;
    private long count = 0;
    private int index = 0;
    private final Queue<byte[]> queue;

    public BigByteArrayInputStream(BigByteArray byteArray) {
        checkNotNull(byteArray);
        queue = queue();
        byteArray.forEach(queue::add);
        total = byteArray.length();
    }

    @Override
    public int read() throws IOException {
        byte[] buffer = new byte[1];
        int read = read(buffer);
        if (read < 0) {
            return -1;
        } else {
            return buffer[0];
        }
    }

    @Override
    public int read(byte[] buffer, int off, int len) throws IOException {
        if (len == 0) {
            return 0;
        } else if (queue.isEmpty()) {
            return -1;
        } else {
            byte[] peek = queue.peek();
            for (int i = 0; i < len; i++, index++) {
                if (index >= peek.length) {
                    queue.poll();
                    if (queue.isEmpty()) {
                        count += i;
                        return i == 0 ? -1 : i;
                    } else {
                        peek = queue.peek();
                        index = 0;
                    }
                }
                buffer[i + off] = peek[index];
            }
            count += len;
            return len == 0 ? -1 : len;
        }
    }

    @Override
    public long availableLong() throws IOException {
        return total - count;
    }

}
