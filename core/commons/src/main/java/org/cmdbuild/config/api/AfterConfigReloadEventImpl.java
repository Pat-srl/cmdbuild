/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableSet;
import java.util.Set;

/**
 *
 */
public class AfterConfigReloadEventImpl implements AfterConfigReloadEvent {

    private final Set<String> keys;

    public AfterConfigReloadEventImpl(String... keys) {
        this.keys = ImmutableSet.copyOf(keys);
    }

    public AfterConfigReloadEventImpl(Iterable<String> keys) {
        this.keys = ImmutableSet.copyOf(keys);
    }

    @Override
    public boolean impactNamespace(String namespace) {
        return keys.isEmpty() ? true : keys.stream().anyMatch((key) -> key.startsWith(checkNotNull(namespace)));
    }

    @Override
    public boolean isGlobalConfigEvent() {
        return this.keys.isEmpty();
    }

}
