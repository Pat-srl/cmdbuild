package org.cmdbuild.service.rest.common.serializationhelpers;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import org.cmdbuild.contextmenu.ContextMenuItem;
import static org.cmdbuild.contextmenu.ContextMenuType.COMPONENT;
import static org.cmdbuild.widget.utils.WidgetUtils.serializeWidgetDataToString;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.springframework.stereotype.Component;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.contextmenu.ContextMenuItemImpl;
import org.cmdbuild.contextmenu.ContextMenuType;
import org.cmdbuild.contextmenu.ContextMenuVisibility;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.uicomponents.contextmenu.ContextMenuComponentService;
import static org.cmdbuild.utils.lang.CmConvertUtils.isBoolean;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.isBetweenQuotes;
import static org.cmdbuild.utils.lang.CmStringUtils.removeQuotes;
import static org.cmdbuild.widget.utils.WidgetUtils.parseSerializedWidgetData;

@Component
public class ContextMenuSerializationHelper {

    private final ContextMenuComponentService contextMenuComponentService;
    private final ObjectTranslationService translationService;

    public ContextMenuSerializationHelper(ContextMenuComponentService service, ObjectTranslationService translationService) {
        this.contextMenuComponentService = checkNotNull(service);
        this.translationService = checkNotNull(translationService);
    }

    public Object contextMenuItemsToResponse(Collection<ContextMenuItem> contextMenuItems, String ownerName) {
        return contextMenuItems.stream().map((item) -> {
            return map(
                    "label", item.getLabel(),
                    "type", item.getType().name().toLowerCase(),
                    "active", item.isActive(),
                    "visibility", item.getVisibility().name().toLowerCase())
                    .skipNullValues()
                    .with(
                            "componentId", item.getComponentId(),
                            "script", item.getJsScript(),
                            "config", serializeWidgetDataToString(item.getConfig())
                    ).accept((t) -> {
                        switch (item.getType()) {
                            case COMPONENT:
                                // Added for testability reasons, when simulating that the JS component is not found
                                String componentAlias = ""; 
                                String componentJsComponentId = ""; 
                                final UiComponentInfo componentInfo = (UiComponentInfo) contextMenuComponentService.getByCode(item.getComponentId());
                                if (componentInfo != null) {
                                    componentAlias = componentInfo.getExtjsAlias();
                                    componentJsComponentId = componentInfo.getExtjsComponentId();
                                }
                                
                                t.put("alias", componentAlias);
                                t.put("jscomponent", componentJsComponentId);
                            case CUSTOM:
                                t.put("_label_translation", translationService.translateContextMenuLabel(ownerName, item.getLabel(), item.getLabel()));
                                break;
                        }
                        if (item.getConfig() != null && !item.getConfig().isEmpty()) {
                            Map<String, Object> mappedConfig = map();
                            item.getConfig().forEach((k, v) -> {
                                mappedConfig.put(k, processValue(v.toString()));
                            });
                            t.putAll(mappedConfig);
                        }
                    });
        }).collect(toList());
    }

    public List<ContextMenuItem> toContextMenuItems(List<WsClassDataContextMenuItem> contextMenuItems) {
        return contextMenuItems.stream().map((i) -> ContextMenuItemImpl.builder()
                .withActive(i.active)
                .withComponentId(i.componentId)
                .withConfig(parseSerializedWidgetData(i.config == null ? "" : i.config))
                .withJsScript(i.script)
                .withLabel(i.label)
                .withType(ContextMenuType.valueOf(i.type.toUpperCase()))
                .withVisibility(ContextMenuVisibility.valueOf(i.visibility.toUpperCase()))
                .build()).collect(toList());
    }

    private Object processValue(String valueAsString) {
        if (isBetweenQuotes(valueAsString)) {
            return removeQuotes(valueAsString);
        } else if (NumberUtils.isCreatable(valueAsString)) {
            return Integer.valueOf(valueAsString);
        } else if (isBoolean(valueAsString)) {
            return toBoolean(valueAsString);
        } else {
            return valueAsString;
        }
    }

    public static class WsClassDataContextMenuItem {

        private final boolean active;
        private final String label, type, componentId, script, config, visibility;

        public WsClassDataContextMenuItem(
                @JsonProperty("active") Boolean active,
                @JsonProperty("label") String label,
                @JsonProperty("type") String type,
                @JsonProperty("componentId") String componentId,
                @JsonProperty("script") String script,
                @JsonProperty("config") String config,
                @JsonProperty("visibility") String visibility) {
            this.active = active;
            this.label = label;
            this.type = type;
            this.componentId = componentId;
            this.script = script;
            this.config = config;
            this.visibility = visibility;
        }

    }
}
