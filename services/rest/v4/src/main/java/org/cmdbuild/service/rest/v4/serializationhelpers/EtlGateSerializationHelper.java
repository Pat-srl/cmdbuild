/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.serializationhelpers;

import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.loader.EtlTemplateService;
import org.cmdbuild.service.rest.common.serializationhelpers.EtlTemplateSerializationHelper;
import org.cmdbuild.utils.lang.CmMapUtils;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class EtlGateSerializationHelper {

    public static CmMapUtils.FluentMap<String, Object> serializeGate(EtlGate gate, boolean detailed, boolean includeEtlTemplates, EtlTemplateService templateService) {
        return (detailed ? serializeDetailedGate(gate) : serializeBasicGate(gate)).accept(m -> {
            if (includeEtlTemplates) {
                m.put("_templates", gate.getAllTemplates().stream().map(templateService::getTemplateByName).filter(t -> t.isActive()).map(EtlTemplateSerializationHelper::serializeDetailedTemplate).collect(toImmutableList()));
            }
        });
    }

    public static CmMapUtils.FluentMap<String, Object> serializeBasicGate(EtlGate gate) {
        return (CmMapUtils.FluentMap) map(
                "_id", gate.getCode(),
                "code", gate.getCode(),
                "description", gate.getDescription(),
                "allowPublicAccess", gate.getAllowPublicAccess(),
                "processingMode", serializeEnum(gate.getProcessingMode()),
                "enabled", gate.isEnabled(),
                "_has_single_handler", gate.hasSingleHandler()
        ).accept(m -> {
            if (gate.hasSingleHandler()) {
                m.put("_handler_type", gate.getSingleHandlerType());
            }
        });
    }

    public static CmMapUtils.FluentMap<String, Object> serializeDetailedGate(EtlGate gate) {
        return serializeBasicGate(gate).with(
                "config", map(gate.getConfig()),
                "handlers", list(gate.getHandlers()).map(h -> map(h.getConfig())
                        .withoutKeys(k -> gate.getConfig().containsKey(k) && equal(gate.getConfig(k), h.getConfig(k))).with("type", h.getType()))
        ).accept(m -> {
            if (gate.hasSingleHandler()) {
                m.put("_handler_config", gate.getSingleHandler().getConfig());
            }
        });
    }
}
