/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.etl.webhook;

import java.util.List;

/**
 *
 * @author ataboga
 */
public interface WebhookRepository {

    List<WebhookConfig> getAll();

    WebhookConfig getByName(String webhookName);

    WebhookConfig create(WebhookConfig webhook);

    WebhookConfig update(WebhookConfig webhook);

    void delete(String webhookName);

}
