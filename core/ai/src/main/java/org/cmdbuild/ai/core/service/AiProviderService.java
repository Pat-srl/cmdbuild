/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.ai.core.service;

import org.cmdbuild.ai.core.api.AiRequest;
import org.cmdbuild.ai.core.api.AiResponse;
import org.cmdbuild.plugin.PluginService;

/**
 *
 * @author ataboga
 */
public interface AiProviderService extends PluginService {

    @Override
    default String getName() {
        return getAiProviderServiceName();
    }

    boolean isServiceOk();

    String getAiProviderServiceName();

    AiResponse query(AiRequest request);
}
