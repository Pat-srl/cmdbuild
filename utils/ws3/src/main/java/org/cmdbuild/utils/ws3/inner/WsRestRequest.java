/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;

public interface WsRestRequest extends WsRequest {

    String getResourceUri();

    WsRestRequest withParams(Map<String, String> otherParams);

    @Nullable
    List<String> getParams(String key);

}
