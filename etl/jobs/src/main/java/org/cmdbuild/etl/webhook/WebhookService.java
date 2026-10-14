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
public interface WebhookService extends WebhookRepository {

    @Override
    List<WebhookConfig> getAll();

    @Override
    WebhookConfig getByName(String name);

}
