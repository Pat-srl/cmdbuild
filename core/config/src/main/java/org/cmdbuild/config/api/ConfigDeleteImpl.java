/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.config.api.ConfigUpdate.ConfigUpdateMode.UM_DELETE;

public class ConfigDeleteImpl implements ConfigUpdate {

    private final String key;

    public ConfigDeleteImpl(String key) {
        this.key = checkNotNull(key);
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ConfigUpdateMode getMode() {
        return UM_DELETE;
    }

    @Override
    public String toString() {
        return "ConfigDelete{" + "key=" + key + '}';
    }

}
