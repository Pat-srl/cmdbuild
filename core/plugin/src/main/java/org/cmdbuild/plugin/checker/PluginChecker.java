/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.checker;

import java.util.Date;

/**
 *
 * @author ataboga
 */
public interface PluginChecker {

    boolean checkPlugin(String pluginName, String path);

    Date getExpirationDate(String pluginName, String filename);
}
