/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.ai.core.service;

import org.cmdbuild.ai.core.api.AiRequest;
import org.cmdbuild.ai.core.api.AiResponse;

/**
 * @author ldare
 */
public interface AiService {

    public boolean isEnabled();

    public AiProviderService getService();

    public AiResponse query(AiRequest request);
}
