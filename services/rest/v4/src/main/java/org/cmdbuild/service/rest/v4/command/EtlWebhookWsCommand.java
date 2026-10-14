/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.etl.webhook.WebhookConfig;
import org.cmdbuild.etl.webhook.WebhookService;
import org.cmdbuild.service.rest.v4.model.WsEtlWebhookData;
import org.cmdbuild.utils.lang.CmCollectionUtils.FluentList;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class EtlWebhookWsCommand {

    private final WebhookService webhookService;

    public EtlWebhookWsCommand(WebhookService webhookService) {
        this.webhookService = checkNotNull(webhookService);
    }

    public FluentList<WebhookConfig> doReadAll(Predicate<WebhookConfig> predicate) {
        return list(webhookService.getAll()).withOnly(predicate);
    }

    public WebhookConfig doReadOne(String idOrCode) {
        return webhookService.getByName(idOrCode);
    }

    public WebhookConfig doCreate(WsEtlWebhookData data) {
        return webhookService.create(data.toWebhookTrigger().build());
    }

    public WebhookConfig doUpdate(String webhookId, WsEtlWebhookData data) {
        return webhookService.update(data.toWebhookTrigger().withCode(webhookId).build());
    }

    public void doDelete(String templateName) {
        webhookService.delete(templateName);
    }

}
