/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ui;

import jakarta.annotation.Nullable;

public interface UiBaseUrlService {

    String getBaseUrl(@Nullable Object request);

    String getBaseUrl();

}
