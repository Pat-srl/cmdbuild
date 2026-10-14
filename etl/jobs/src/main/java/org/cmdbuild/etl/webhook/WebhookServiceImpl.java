/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.etl.webhook;

import java.util.List;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
@Primary
public class WebhookServiceImpl implements WebhookService {

    private final WebhookRepository repository;

    public WebhookServiceImpl(WebhookRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<WebhookConfig> getAll() {
        return repository.getAll();
    }

    @Override
    public WebhookConfig getByName(String name) {
        return repository.getByName(name);
    }

    @Override
    public WebhookConfig create(WebhookConfig webhook) {
        return repository.create(webhook);
    }

    @Override
    public WebhookConfig update(WebhookConfig webhook) {
        return repository.update(webhook);
    }

    @Override
    public void delete(String webhookName) {
        repository.delete(webhookName);
    }

}
