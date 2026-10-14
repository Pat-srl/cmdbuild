/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.url;

import java.util.Map;

public interface UrlPathAndParams {

    String getPath();

    Map<String, String> getParams();

}
