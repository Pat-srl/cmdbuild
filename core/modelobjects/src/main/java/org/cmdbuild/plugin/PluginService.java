/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.plugin;

import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface used by all plugins (dms, waterway, ecc.)
 *
 * @author afelice
 */
public interface PluginService {

    Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    String getName();

    default Map<String, Object> getConfigs(String language) {
        return emptyMap();
    }

    default Map<String, Object> getConfigs() {
        return getConfigs(null);
    }
}
