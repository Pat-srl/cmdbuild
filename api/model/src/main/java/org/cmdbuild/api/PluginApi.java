/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.api;

import java.util.Map;

/**
 *
 * @author ataboga
 */
public interface PluginApi extends PluginConfigApi {

    Map<String, String> getPlugins();
}
