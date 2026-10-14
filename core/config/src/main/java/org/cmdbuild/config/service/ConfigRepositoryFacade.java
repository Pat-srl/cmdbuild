/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import org.cmdbuild.config.api.ConfigEntryImpl;
import org.cmdbuild.config.api.ConfigUpdate;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;

public interface ConfigRepositoryFacade {

    void loadConfigFromFiles();

    void loadConfigFromFilesAndDb();

    void updateConfig(List<? extends ConfigUpdate> configs);

    Map<String, String> getAllConfig();

    Map<String, String> getAllStoredConfig();

    Map<String, String> getConfigFromFile();

    Map<String, String> getConfigFromDb();

    default void updateConfig(Map<String, String> configs) {
        updateConfig(configs.entrySet().stream().map(e -> new ConfigEntryImpl(e.getKey(), e.getValue())).collect(toImmutableList()));
    }

    default void reloadAllConfig() {
        loadConfigFromFilesAndDb();
    }
}
