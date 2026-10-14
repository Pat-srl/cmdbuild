/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.ai.core.impl;

import com.google.common.base.Preconditions;
import org.cmdbuild.ai.AiConfiguration;
import org.cmdbuild.ai.core.api.AiRequest;
import org.cmdbuild.ai.core.api.AiResponse;
import org.cmdbuild.ai.core.service.AiProviderService;
import org.cmdbuild.ai.core.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class AiServiceImpl implements AiService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AiConfiguration aiConfiguration;
    private final Map<String, AiProviderService> services;

    public AiServiceImpl(AiConfiguration aiConfiguration, List<AiProviderService> services) {
        this.aiConfiguration = checkNotNull(aiConfiguration);
        this.services = map(Preconditions.checkNotNull(services), AiProviderService::getAiProviderServiceName);
    }

    @Override
    public boolean isEnabled() {
        return aiConfiguration.isEnabled();
    }

    @Override
    public AiProviderService getService() {
        return checkNotNull(services.get(aiConfiguration.getService()), "ai service not found for name =< %s >", aiConfiguration.getService());
    }

    @Override
    public AiResponse query(AiRequest request) {
        return getService().query(request);
    }
}
