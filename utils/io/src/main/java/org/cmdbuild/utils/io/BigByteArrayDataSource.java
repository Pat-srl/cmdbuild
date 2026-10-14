/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import jakarta.annotation.Nullable;

public class BigByteArrayDataSource implements DataSource {

    private final String contentType, name;
    private final BigByteArray bigByteArray;

    public BigByteArrayDataSource(BigByteArray bigByteArray, @Nullable String contentType, @Nullable String name) {
        this.bigByteArray = checkNotNull(bigByteArray);
        this.contentType = contentType;
        this.name = name;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new BigByteArrayInputStream(bigByteArray);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getName() {
        return name;
    }

}
