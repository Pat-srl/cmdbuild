/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.beans;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.BigByteArrayOutputStream;
import org.springframework.web.util.WebUtils;

public class MyContentCachingResponseWrapper extends HttpServletResponseWrapper {

    private final BigByteArrayOutputStream content = new BigByteArrayOutputStream();

    private ServletOutputStream outputStream;

    private PrintWriter writer;

    private int statusCode = HttpServletResponse.SC_OK;

    /**
     * Create a new ContentCachingResponseWrapper for the given servlet
     * response.
     *
     * @param response the original servlet response
     */
    public MyContentCachingResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void setStatus(int sc) {
        super.setStatus(sc);
        this.statusCode = sc;
    }

    @Override
    public void sendError(int sc) throws IOException {
        try {
            super.sendError(sc);
        } catch (IllegalStateException ex) {
            // Possibly on Tomcat when called too late: fall back to silent setStatus
            super.setStatus(sc);
        }
        this.statusCode = sc;
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        super.sendRedirect(location);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (outputStream == null) {
            outputStream = new ResponseServletOutputStream();
        }
        return outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            String characterEncoding = getCharacterEncoding();
            writer = (characterEncoding != null ? new ResponsePrintWriter(characterEncoding)
                    : new ResponsePrintWriter(WebUtils.DEFAULT_CHARACTER_ENCODING));
        }
        return writer;
    }

    @Override
    public void resetBuffer() {
        super.resetBuffer();
        this.content.reset();
    }

    @Override
    public void reset() {
        super.reset();
        this.content.reset();
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public BigByteArray getContentBytes() {
        return this.content.toBigByteArray();
    }

    public InputStream getContentInputStream() {
        return this.content.toBigByteArray().toInputStream();
    }

    public long getContentSize() {
        return this.content.size();
    }

    public boolean isEmpty() {
        return getContentSize() == 0;
    }

    private class ResponseServletOutputStream extends ServletOutputStream {

        private final ServletOutputStream inner;

        public ResponseServletOutputStream() throws IOException {
            inner = getResponse().getOutputStream();
        }

        @Override
        public void write(int b) throws IOException {
            inner.write(b);
            content.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            inner.write(b, off, len);
            content.write(b, off, len);
        }

        @Override
        public boolean isReady() {
            return inner.isReady();
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            inner.setWriteListener(writeListener);
        }
    }

    private class ResponsePrintWriter extends PrintWriter {

        private final PrintWriter inner;

        public ResponsePrintWriter(String characterEncoding) throws UnsupportedEncodingException, IOException {
            super(new OutputStreamWriter(content, characterEncoding));
            inner = getResponse().getWriter();
        }

        @Override
        public void write(char buf[], int off, int len) {
            inner.write(buf, off, len);
            super.write(buf, off, len);
            super.flush();
        }

        @Override
        public void write(String s, int off, int len) {
            inner.write(s, off, len);
            super.write(s, off, len);
            super.flush();
        }

        @Override
        public void write(int c) {
            inner.write(c);
            super.write(c);
            super.flush();
        }
    }

}
