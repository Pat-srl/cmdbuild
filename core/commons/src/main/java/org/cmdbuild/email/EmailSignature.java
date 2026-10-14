/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.email;

import jakarta.annotation.Nullable;

public interface EmailSignature {

    @Nullable
    Long getId();

    String getCode();

    String getDescription();

    String getContentHtml();
    
    boolean isActive();
}
