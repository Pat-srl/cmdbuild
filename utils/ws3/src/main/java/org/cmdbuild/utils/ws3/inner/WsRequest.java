/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Optional;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface WsRequest {

    @Nullable
    String getPayload();

    @Nullable
    Object getInner();

    Map<String, String> getParams();

    Map<String, WsPart> getParts();

    Map<String, String> getHeaders();

    @Nullable
    default String getParam(String key) {
        return getParams().get(key);
    }

    @Nullable
    default String getHeader(String key) {
        return getHeaders().get(checkNotBlank(key).toLowerCase());
    }

    @Nullable
    default WsPart getPart(String key) {
        return getParts().get(key);
    }

    @Nullable
    default DataSource getPartData(String key) {
        return Optional.ofNullable(getPart(key)).map(WsPart::getDataSource).orElse(null);
    }

    default boolean hasPayload() {
        return isNotBlank(getPayload());
    }

    default boolean hasPart(String key) {
        return getParts().containsKey(key);
    }

    default boolean hasParam(String key) {
        return getParams().containsKey(key);
    }

}
