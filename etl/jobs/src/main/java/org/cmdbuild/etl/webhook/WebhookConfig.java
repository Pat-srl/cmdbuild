/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.etl.webhook;

import java.util.Set;
import org.cmdbuild.data.filter.CmdbFilter;

/**
 *
 * @author ataboga
 */
public interface WebhookConfig {

    String getCode();

    String getDescription();

    String getTarget();

    WebhookMethod getMethod();

    String getUrl();

    String getHeaders();

    String getBody();

    Set<String> getEvents();

    String getLanguage();

    Boolean isActive();

    CmdbFilter getFilter();

}
