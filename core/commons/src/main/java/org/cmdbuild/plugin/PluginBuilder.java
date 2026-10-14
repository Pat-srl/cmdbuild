/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package org.cmdbuild.plugin;

import java.util.Map;

/**
 * Placeholder for Plugin builder.
 *
 * @author afelice
 */
public interface PluginBuilder {

    PluginService buildWithConfiguration(Map<String, String> configs);

}
