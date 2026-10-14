/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Strings.nullToEmpty;
import jakarta.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.io.CmIoUtils.getCharsetFromContentTypeOrUtf8;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

public class StringDatasource implements DataSource {

    private final String content, contentType, name;

    public StringDatasource(@Nullable String content) {
        this(content, null, null);
    }

    public StringDatasource(@Nullable String content, @Nullable String contentType) {
        this(content, contentType, null);
    }

    public StringDatasource(@Nullable String content, @Nullable String contentType, @Nullable String name) {
        this.content = nullToEmpty(content);
        this.contentType = contentType;
        this.name = name;
    }

    @Override
    public InputStream getInputStream() {
        try {
            return new ByteArrayInputStream(content.getBytes(getCharsetFromContentTypeOrUtf8(contentType)));
        } catch (UnsupportedEncodingException ex) {
            throw runtime(ex);
        }
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException("read only data source");
    }

    @Override
    @Nullable
    public String getContentType() {
        return contentType;
    }

    @Override
    @Nullable
    public String getName() {
        return name;
    }

}
