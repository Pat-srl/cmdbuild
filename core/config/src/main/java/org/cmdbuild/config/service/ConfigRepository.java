/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import java.util.List;
import org.cmdbuild.config.api.ConfigEntry;
import org.cmdbuild.config.api.ConfigUpdate;

public interface ConfigRepository {

    public final static String DATABASE_EXT_CONFIG = "org.cmdbuild.database.ext"; // internal use

    List<ConfigEntry> getConfigEntries();

    void updateConfigs(List<? extends ConfigUpdate> configs);
}
