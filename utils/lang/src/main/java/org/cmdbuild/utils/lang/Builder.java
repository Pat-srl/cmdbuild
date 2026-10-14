/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import java.util.function.Consumer;

public interface Builder<T, B extends Builder<T, B>> {

    T build();

    default B accept(Consumer<B> visitor) {
        visitor.accept((B) this);
        return (B) this;
    }

}
