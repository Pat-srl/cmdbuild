/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils;

import java.util.Map;
import jakarta.activation.DataSource;

public interface UrlHandlerResponse {

    DataSource getDataSource();

    Map<String, String> getMeta();

}
