/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils;

public interface UrlHandler {

    boolean handlesUrl(String url);

    UrlHandlerResponse loadFromUrl(String url);

}
