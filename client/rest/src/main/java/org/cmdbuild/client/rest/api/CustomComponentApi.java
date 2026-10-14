/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import java.io.InputStream;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.client.rest.model.CustomComponentInfo;

public interface CustomComponentApi {

    CustomComponentApiResponse uploadCustomPage(InputStream data, String description, @Nullable String targetDevice);

    CustomComponentApiResponse uploadCustomWidget(InputStream data, String description, @Nullable String targetDevice);

    CustomComponentApiResponse uploadCustomContextMenu(InputStream data, String description, @Nullable String targetDevice);

    CustomComponentApiResponse createCustomScript(String code, String description, String data, boolean active);

    CustomComponentApiResponse getCustomScript(String code);

    List<CustomComponentApiResponse> getCustomScripts();

    CustomComponentApiResponse updateCustomScript(String code, String newDescription, String newData, boolean active);

    boolean deleteCustomScript(String scriptCode);

    interface CustomComponentApiResponse {

        CustomComponentInfo getCustomComponentInfo();

        CustomComponentApi then();
    }

}
