/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import java.util.function.BiConsumer;

public class CmBiConsumerUtils {

    private final static BiConsumer NOOP_CONSUMER = (x, y) -> {
    };

    public static <T, U> BiConsumer<T, U> noop() {
        return NOOP_CONSUMER;
    }
}
