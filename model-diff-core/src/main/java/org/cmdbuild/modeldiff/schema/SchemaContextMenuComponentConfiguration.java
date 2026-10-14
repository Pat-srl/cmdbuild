/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import java.util.stream.Collectors;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_SERIALIZATION;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Represents a <i>context menu item</i> (used by <i>UI</i>) that is a
 * Javascript component.
 *
 * <p>
 * It's not a {@link SchemaItemConfiguration} because it's a bit different: it
 * doesn't work only on map of properties as {@llink ClasseConfiguration}.
 *
 * @author afelice
 */
public class SchemaContextMenuComponentConfiguration {

    private String componentId;
    private boolean active = true;

    /**
     * A <code>byte[]</code> (like the Zip file bytes for a Javascript script)
     * is not storable in a Json. So a <code>Base64</code> encoding is applied.
     */
    private Map<TargetDevice, String> scriptByTargetDevice;

    public SchemaContextMenuComponentConfiguration(String componentId, boolean active) {
        this(componentId, active, map());
    }

    @JsonCreator
    public SchemaContextMenuComponentConfiguration(@JsonProperty("componentId") String componentId, @JsonProperty("active") boolean active, @JsonProperty("scripts") Map<String, String> itemsConf) {
        this.componentId = checkNotNull(componentId);
        this.active = active;

        setScriptByTargetDeviceStr(itemsConf);
    }

    public String getComponentId() {
        return componentId;
    }

    @JsonIgnore
    public Map<TargetDevice, String> getScriptByTargetDevice() {
        return scriptByTargetDevice;
    }

    /**
     * scripts
     * @return 
     */
    @JsonProperty(ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_SERIALIZATION)
    public Map<String, String> getScriptByTargetDeviceStr() {
        return scriptByTargetDevice.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> serializeEnum(e.getKey()), // target device enum as String
                        Map.Entry::getValue // same value
                ));
    }

    @JsonIgnore
    public void setScriptByTargetDevice(Map<TargetDevice, String> scriptByTargetDevice) {
        this.scriptByTargetDevice = scriptByTargetDevice;
    }

    @JsonProperty("scriptByTargetDevice")
    public void setScriptByTargetDeviceStr(Map<String, String> scriptByTargetDeviceStr) {
        this.scriptByTargetDevice = scriptByTargetDeviceStr.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> parseEnumOrDefault(e.getKey(), TargetDevice.TD_DEFAULT), // from String to target device enum
                        Map.Entry::getValue // same value
                ));
    }
}
