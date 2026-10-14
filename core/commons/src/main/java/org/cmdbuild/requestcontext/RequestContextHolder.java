/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.requestcontext;

import jakarta.annotation.Nullable;

public interface RequestContextHolder<T> {

    T get();

    @Nullable
    T getOrNull();

    void set(T value);

    default boolean hasContent() {
        return getOrNull() != null;
    }

}
