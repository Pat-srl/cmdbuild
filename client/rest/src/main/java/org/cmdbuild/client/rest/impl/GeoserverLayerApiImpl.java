package org.cmdbuild.client.rest.impl;

import java.io.InputStream;
import static java.lang.String.format;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.cmdbuild.client.rest.api.GeoserverLayerApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;

public class GeoserverLayerApiImpl extends AbstractServiceClientImpl implements GeoserverLayerApi {

    public GeoserverLayerApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public void uploadGeoserverLayer(String classId, Long cardId, String layerId, InputStream data) {
        HttpEntity multipart = MultipartEntityBuilder.create()
                .addBinaryBody("file", listenUpload("custom page upload", data), ContentType.APPLICATION_OCTET_STREAM, "file.zip")
                .build();
        put(format("classes/%s/cards/%s/geolayers/%s", classId, cardId, layerId), multipart);
    }

}
