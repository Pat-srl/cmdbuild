/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.email.beans.EmailSignatureImpl;

/**
 *
 * @author schursin
 */
public class WsEmailSignatureData {

    private final String code, description, contentHtml;
    private final Boolean active;

    public WsEmailSignatureData(
            @JsonProperty("active") Boolean active,
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("content_html") String contentHtml) {
        this.code = code;
        this.active = active;
        this.description = description;
        this.contentHtml = contentHtml;
    }

    public EmailSignatureImpl.EmailSignatureImplBuilder toEmailSignature() {
        return EmailSignatureImpl.builder()
                .withDescription(description)
                .withCode(code)
                .withContentHtml(contentHtml)
                .withActive(active);
    }
}
