/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.log;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.function.Consumer;

public class LogUtils {

    public static PrintStream printStreamFromLogger(Consumer<String> logger) {
        return new PrintStream(new LoggerOutputStream(logger), true);
    }

    public static PrintWriter printWriterFromLogger(Consumer<String> logger) {
        return new PrintWriter(new LoggerOutputStream(logger), true);
    }

    private static class LoggerOutputStream extends OutputStream {

        private final Consumer<String> logger;

        public LoggerOutputStream(Consumer<String> logger) {
            this.logger = checkNotNull(logger);
        }

        @Override
        public void write(int b) throws IOException {
            write(new byte[]{(byte) b}, 0, 0);
        }

        @Override
        public void write(byte data[], int off, int len) throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data, off, len)));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.accept(line);
            }
        }

    }

}
