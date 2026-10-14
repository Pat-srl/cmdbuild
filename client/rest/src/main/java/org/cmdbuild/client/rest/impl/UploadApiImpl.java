/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.ByteArrayInputStream;
import java.io.File;
import static java.lang.String.format;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.client.rest.api.UploadApi;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class UploadApiImpl extends AbstractServiceClientImpl implements UploadApi {

    public UploadApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public UploadApi upload(String path, byte[] data) {
        checkNotBlank(path);
        checkNotNull(data);
        HttpEntity multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", listenUpload("file upload", new ByteArrayInputStream(data)), ContentType.APPLICATION_OCTET_STREAM, new File(path).getName())
                .addTextBody("path", new File(path).getParentFile().getPath())
                .build();
        post("administration/uploads?overwrite_existing=true", multipart);
        return this;
    }

    @Override
    public UploadApi uploadMany(byte[] zipData) {
        checkNotNull(zipData);
        HttpEntity multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", listenUpload("file upload", new ByteArrayInputStream(zipData)), ContentType.APPLICATION_OCTET_STREAM, "file.zip")
                .build();
        post("administration/uploads/_MANY", multipart);
        return this;
    }

    @Override
    public UploadData download(String fileId) {
        byte[] data = getBytes(format("administration/uploads/%s/file", encodeUrlPath(fileId)));
        return () -> data;
    }

    @Override
    public UploadData downloadAll() {
        byte[] data = getBytes("administration/uploads/_ALL/file.zip");
        return () -> data;
    }

}
