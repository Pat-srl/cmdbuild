/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.model;

import java.io.Serializable;
import java.util.Map;
import jakarta.annotation.Nullable;

public interface SessionData extends Serializable {

    Map<String, Object> getSessionData();

    default @Nullable
    <E> E get(String key) {
        return (E) getSessionData().get(key);
    }

}
