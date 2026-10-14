/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.ai.core.api.AiRequest;
import org.cmdbuild.ai.core.api.AiResponse;
import org.cmdbuild.ai.core.service.AiService;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.springframework.stereotype.Component;

/**
 * @author ldare
 */
@Component
public class AiWsCommand {

    private final AiService aiService;
    private final OperationUserSupplier operationUserSupplier;

    public AiWsCommand(AiService aiService, OperationUserSupplier operationUserSupplier) {
        this.aiService = aiService;
        this.operationUserSupplier = operationUserSupplier;
    }

    public AiResponse query(String query, String context) {
        //TODO check if this userId is correct
        String userId = operationUserSupplier.getUser().getId().toString();
        //TODO requestId?
        AiRequest request = new AiRequest(query, userId, context);
        return aiService.query(request);
    }
}
