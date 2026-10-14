/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import jakarta.annotation.Nullable;

public interface ClassData {

    String getName();

    String getDescription();

    String getType();

    @Nullable
    String getParentId();

    boolean isActive();

    boolean isSuperclass();

    default String getId() {
        return getName();
    }
}
