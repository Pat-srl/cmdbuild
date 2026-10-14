/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.etl.webhook.WebhookConfig;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmMapUtils;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;
import static org.cmdbuild.dao.utils.CmFilterProcessingUtils.mapFilter;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_STRINGS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;

/**
 * @author ldare
 */
public class EtlWebhookSerializationHelper {


    public static List<Map<String, Object>> filterAndApplySerialization(CmCollectionUtils.FluentList<WebhookConfig> webhooks, WsQueryOptions wsQueryOptions, ClasseRepository classeRepository) {
        return webhooks
                .map(wsQueryOptions.isDetailed() ? wh -> serializeDetailedWebhook(wh, classeRepository.getClasse(wh.getTarget())) : wh -> serializeBasicWebhook(wh))
                .withOnly(mapFilter(wsQueryOptions.getQuery().getFilter()));
    }

    public static CmMapUtils.FluentMap serializeBasicWebhook(WebhookConfig webhook) {
        return map(
                "_id", webhook.getCode(),
                "code", webhook.getCode(),
                "description", webhook.getDescription(),
                "event", webhook.getEvents().stream().collect(joining(", ")),
                "target", webhook.getTarget(),
                "method", serializeEnum(webhook.getMethod()),
                "url", webhook.getUrl(),
                "active", webhook.isActive()
        );
    }

    public static CmMapUtils.FluentMap serializeDetailedWebhook(WebhookConfig webhook, Classe target) {
        return serializeBasicWebhook(webhook).with(map(
                "_target_type", getTargetType(target),
                "_target_description", target.getDescription(),
                "headers", applyOrNull(webhook.getHeaders(), headers -> unflattenMaps(fromJson(headers, MAP_OF_STRINGS))),
                "body", applyOrNull(webhook.getBody(), body -> unflattenMaps(fromJson(body, MAP_OF_STRINGS))),
                "language", webhook.getLanguage()
        ));
    }

    private static String getTargetType(Classe target) {
        return switch (target.getEtType()) {
            case ET_CLASS -> target.isProcess() ? "process" : "class";
            case ET_DOMAIN -> "domain";
            default -> throw new UnsupportedOperationException("target type not supported");
        };
    }
}
