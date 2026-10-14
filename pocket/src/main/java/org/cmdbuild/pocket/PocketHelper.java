/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.pocket;

import java.util.Map;
import java.util.function.Consumer;
import org.cmdbuild.client.rest.RestClient;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfig;
import org.cmdbuild.dao.config.inner.DatabaseCreatorConfigImpl.DatabaseCreatorConfigImplBuilder;
import static org.cmdbuild.utils.lang.CmExecutorUtils.waitUntil;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface PocketHelper {

    PocketHelper withConfig(Map<String, String> config);

    PocketHelper withDbConfig(Consumer<DatabaseCreatorConfigImplBuilder> config);

    PocketHelper withDbConfig(DatabaseCreatorConfig config);

    PocketHelper withDbConfig(Map<String, String> dbconfig);

    PocketHelper start();

    PocketHelper stop();

    PocketHelper stopSafe();

    boolean isRunning();

    boolean isReady();

    RestClient getRestClient();

    String getBaseUrl();

    PocketHelper cleanup();

    Map<String, String> getConfig();

    int getTomcatPort();

    DatabaseCreatorConfig getDbConfig();

    void reconfigureDatabase(DatabaseCreatorConfig dbConfig);

    default PocketHelper waitUntilReady() {
        waitUntil(this::isReady, 10);
        return this;
    }

    default PocketHelper withConfig(Object... config) {
        return withConfig((Map) map(config).mapValues((k, v) -> toStringOrNull(v)));
    }

    default PocketHelper withDbSource(String source) {
        return this.withDbConfig(b -> b.withConfig(getConfig()).withSource(source));//TODO improve this
    }

}
