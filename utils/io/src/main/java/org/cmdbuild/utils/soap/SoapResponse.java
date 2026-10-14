/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.soap;

import jakarta.annotation.Nullable;
import org.w3c.dom.Document;

public interface SoapResponse {

    String asString();

    Document asDocument();

    @Nullable
    String evalXpath(String expr);

    @Nullable
    String evalXpathNode(String expr);

    @Nullable
    default String getBodyAsString() {
        return evalXpathNode("//soap:Body");
    }

}
