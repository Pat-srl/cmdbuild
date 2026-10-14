/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import jakarta.annotation.Nullable;

public interface LookupApi {

    boolean exists(String lookupType, String lookupCode);

    void create(String lookupType, String code, @Nullable String description);

    default void create(String lookupType, String code) {
        create(lookupType, code, null);
    }

}
