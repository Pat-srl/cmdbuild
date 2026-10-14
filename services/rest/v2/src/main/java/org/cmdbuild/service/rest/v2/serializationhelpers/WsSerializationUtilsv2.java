package org.cmdbuild.service.rest.v2.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.email.EmailAccountService;
import org.cmdbuild.email.template.EmailTemplate;
import org.cmdbuild.email.template.EmailTemplateService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.widget.model.WidgetData;
import org.springframework.stereotype.Component;

@Component
public class WsSerializationUtilsv2 {

    private final EmailTemplateService emailTemplateService;
    private final EmailAccountService emailAccountService;

    private final List<String> DEFAULT_EXTENDED_DATA = ImmutableList.of("ReportType", "ReportCode", "ForcePDF", "ReadOnlyAttributes", "WidgetId");

    public WsSerializationUtilsv2(EmailTemplateService emailTemplateService, EmailAccountService emailAccountService) {
        this.emailTemplateService = checkNotNull(emailTemplateService);
        this.emailAccountService = checkNotNull(emailAccountService);
    }

    public Object serializeWidget(WidgetData widgetData) {
        return map("_id", widgetData.getId(),
                "label", widgetData.getLabel(),
                "type", "." + capitalize(widgetData.getType()),
                "output", widgetData.getOutputParameterOrNull(),
                "active", widgetData.isActive(),
                "alwaysenabled", widgetData.isAlwaysEnabled(),
                "required", widgetData.isRequired())
                .accept((b) -> {
                    switch (widgetData.getType()) {
                        case "manageEmail":
                            b.putAll(serializeManageEmailWidget(widgetData));
                            break;
                        case "createReport":
                            b.put("data", serializeBasicWidgetData(widgetData)
                                    .with(getExtendedDataWithLowercaseKeys(widgetData))
                                    .with(map(
                                            "forceFormat", getReportType(widgetData.getExtendedData()),
                                            "readOnlyAttributes", widgetData.getExtendedData().get("ReadOnlyAttributes") != null ? Splitter.on(",").splitToList(widgetData.getExtendedData().get("ReadOnlyAttributes").toString()) : list(),
                                            "preset", serializeReportPreset(widgetData.getExtendedData()))
                                    ));
                            break;
                        case "linkCards":
                            b.put("data", serializeBasicWidgetData(widgetData)
                                    .with(getExtendedDataWithLowercaseKeys(widgetData))
                                    .with(map(
                                            "className", ((String) widgetData.getExtendedData().get("ClassName")).replaceAll("\"", ""))
                                    ));
                            break;
                        case "createModifyCard":
                            b.put("data", serializeBasicWidgetData(widgetData)
                                    .with(getExtendedDataWithLowercaseKeys(widgetData))
                                    .accept(p -> {
                                        String className = ((String) widgetData.getExtendedData().get("ClassName"));
                                        if (className != null) {
                                            className = className.replaceAll("\"", "");
                                        }
                                        p.put("targetClass", className);
                                        p.put("idcardcqlselector", toStringOrNull(widgetData.getExtendedData().get("ObjId")));
                                        if (widgetData.getExtendedData().get("ReadOnly") != null) {
                                            p.put("readonly", widgetData.getExtendedData().get("ReadOnly").equals(1));
                                        }
                                    }));
                            break;
                        default:
                            b.put("data", serializeBasicWidgetData(widgetData)
                                    .with(getExtendedDataWithLowercaseKeys(widgetData)));
                            break;
                    }
                });
    }

    private FluentMap<String, Object> serializeReportPreset(Map<String, Object> widgetExtendedData) {
        return widgetExtendedData.entrySet().stream().filter(e -> !DEFAULT_EXTENDED_DATA.contains(e.getKey())).collect(toMap(map -> map.getKey(), map -> map.getValue()));
    }

    private FluentMap<String, Object> serializeBasicWidgetData(WidgetData widgetData) {
        return map(
                "id", widgetData.getId(),
                "label", widgetData.getLabel(),
                "type", "." + capitalize(widgetData.getType()),
                "active", widgetData.isActive(),
                "readOnly", false,
                "alwaysenabled", widgetData.isAlwaysEnabled(),
                "required", widgetData.isRequired()
        );
    }

    private String getReportType(Map<String, Object> extendedData) {
        if (extendedData.get("ForcePDF") != null) {
            if (extendedData.get("ForcePDF").equals(1)) {
                return "PDF";
            }
        } else if (extendedData.get("ForceCSV") != null) {
            if (extendedData.get("ForceCSV").equals(1)) {
                return "CSV";
            }
        }
        return "PDF";
    }

    private Map<String, Object> serializeManageEmailWidget(WidgetData widgetData) {
        String templateCode = toStringOrNull(widgetData.getExtendedData().get("Template"));
        List templates = list();
        if (isNotBlank(templateCode)) {
            EmailTemplate template = emailTemplateService.getByName(templateCode);
            templates.add(map(
                    "account", template.getAccount() == null ? null : emailAccountService.getAccount(template.getAccount()).getName(),
                    "bccAddresses", template.getBcc(),
                    "ccAddresses", template.getCc(),
                    "condition", widgetData.getExtendedData().get("Condition"),
                    "content", template.getContent(),
                    "delay", template.getDelay(),
                    "fromAddress", template.getFrom(),
                    "keepSynchronization", template.getKeepSynchronization(),
                    "promptSynchronization", template.getPromptSynchronization(),
                    "notifyWith", widgetData.getExtendedData().get("NotifyWith"),
                    "toAddresses", template.getTo(),
                    "subject", template.getSubject(),
                    "noSubjectPrefix", false,
                    "variables", template.getMeta(),
                    "key", "implicitTemplateName"
            ));
        }
        Map<String, Object> manageEmailWidgetData = map();
        manageEmailWidgetData.put("data", serializeBasicWidgetData(widgetData)
                .with(map("templates", templates,
                        "noSubjectPrefix", false
                ))
        );
        return manageEmailWidgetData;
    }

    private Map<String, Object> getExtendedDataWithLowercaseKeys(WidgetData data) {
        Map<String, Object> extendedData = map();
        data.getExtendedData().keySet().stream().forEach(k -> {
            if (Character.isUpperCase(k.charAt(0))) {
                Object prevVal = data.getExtendedData().get(k);
                k = k.substring(0, 1).toLowerCase() + k.substring(1);
                extendedData.put(k, prevVal);
            } else {
                extendedData.put(k, data.getExtendedData().get(k));
            }
        });
        return extendedData;
    }
}
