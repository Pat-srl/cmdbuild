/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import jakarta.activation.DataSource;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.cmdbuild.utils.io.CmIoUtils;

public interface WsResponsePrinter {

    void printResponse(HttpServletResponse response) throws IOException;

    String getResponseAsString();

    DataSource getResponseAsDataSource();

    Map<String, String> getResponseHeaders();

    default boolean isJson() {
        return CmIoUtils.isJson(getResponseAsDataSource());
    }

}
