/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.BigByteArrayOutputStream;
import org.springframework.http.HttpMethod;

/**
 * adapted from spring
 * org.springframework.web.util.ContentCachingRequestWrapper, in order to fix
 * the default char encoding issue
 */
public class MyContentCachingRequestWrapper extends HttpServletRequestWrapper {

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    private final BigByteArrayOutputStream cachedContent;

    private ServletInputStream inputStream;

    private BufferedReader reader;

    /**
     * Create a new ContentCachingRequestWrapper for the given servlet request.
     *
     * @param request the original servlet request
     */
    public MyContentCachingRequestWrapper(HttpServletRequest request) {
        super(request);
        this.cachedContent = new BigByteArrayOutputStream();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (this.inputStream == null) {
            this.inputStream = new ContentCachingInputStream(getRequest().getInputStream());
        }
        return this.inputStream;
    }

    @Override
    public String getCharacterEncoding() {
        String enc = super.getCharacterEncoding();
        //BEGIN original code
//		return (enc != null ? enc : WebUtils.DEFAULT_CHARACTER_ENCODING);
        //END original code
        //BEGIN fixed code
        return enc;
        //END fixed code
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (this.reader == null) {
            String encoding = getCharacterEncoding();
            InputStreamReader inputStreamReader;
            if (encoding == null) {
                inputStreamReader = new InputStreamReader(getInputStream());
            } else {
                inputStreamReader = new InputStreamReader(getInputStream(), encoding);
            }
            this.reader = new BufferedReader(inputStreamReader);
        }
        return this.reader;
    }

    @Override
    public String getParameter(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameter(name);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterMap();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterNames();
    }

    @Override
    public String[] getParameterValues(String name) {
        if (this.cachedContent.size() == 0 && isFormPost()) {
            writeRequestParametersToCachedContent();
        }
        return super.getParameterValues(name);
    }

    private boolean isFormPost() {
        String contentType = getContentType();
        return (contentType != null && contentType.contains(FORM_CONTENT_TYPE)
                && HttpMethod.POST.matches(getMethod()));
    }

    private void writeRequestParametersToCachedContent() {
        try {
            if (this.cachedContent.size() == 0) {
                String requestEncoding = getCharacterEncoding();
                Map<String, String[]> form = super.getParameterMap();
                for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
                    String name = nameIterator.next();
                    List<String> values = Arrays.asList(form.get(name));
                    for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext();) {
                        String value = valueIterator.next();
                        this.cachedContent.write((requestEncoding == null ? URLEncoder.encode(name) : URLEncoder.encode(name, requestEncoding)).getBytes());
                        if (value != null) {
                            this.cachedContent.write('=');
                            this.cachedContent.write((requestEncoding == null ? URLEncoder.encode(name) : URLEncoder.encode(value, requestEncoding)).getBytes());
                            if (valueIterator.hasNext()) {
                                this.cachedContent.write('&');
                            }
                        }
                    }
                    if (nameIterator.hasNext()) {
                        this.cachedContent.write('&');
                    }
                }
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to write request parameters to cached content", ex);
        }
    }

    public BigByteArray getContentAsBigByteArray() {
        return this.cachedContent.toBigByteArray();
    }

    private class ContentCachingInputStream extends ServletInputStream {

        private final ServletInputStream delegate;

        public ContentCachingInputStream(ServletInputStream is) {
            this.delegate = checkNotNull(is);
        }

        @Override
        public int read() throws IOException {
            int ch = delegate.read();
            if (ch != -1) {
                cachedContent.write(ch);
            }
            return ch;
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            int actual = delegate.read(b, off, len);
            if (actual > 0) {
                cachedContent.write(b, off, actual);
            }
            return actual;
        }

        @Override
        public boolean isFinished() {
            return delegate.isFinished();
        }

        @Override
        public boolean isReady() {
            return delegate.isReady();
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            delegate.setReadListener(readListener);
        }
    }

}
