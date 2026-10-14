/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static org.cmdbuild.config.api.ConfigUpdate.ConfigUpdateMode.UM_UPDATE;

public interface ConfigEntry extends ConfigUpdate {

    String getValue();

    String getStoredValue();

    boolean isEncrypted();

    ConfigEntry withKey(String key);

    @Override
    default ConfigUpdateMode getMode() {
        return UM_UPDATE;
    }

}
