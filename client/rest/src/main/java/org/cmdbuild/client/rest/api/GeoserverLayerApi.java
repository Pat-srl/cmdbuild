package org.cmdbuild.client.rest.api;

import java.io.InputStream;

public interface GeoserverLayerApi {

    void uploadGeoserverLayer(String classId, Long cardId, String layerId, InputStream data);

}
