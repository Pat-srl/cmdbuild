/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.cmdbuild.dao.beans.ClassMetadataImpl;
import org.cmdbuild.dao.entrytype.ClassPermissionMode;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_PROTECTED;
import static org.cmdbuild.dao.entrytype.ClassPermissionMode.CPM_RESERVED;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.EntryTypeMetadata;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_METADATA_SERIALIZATION;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ContextMenuSerializationHelper;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrDefault;

/**
 * Workaround to serialize {@link ClassPermissionMode} without modifying the
 * other CMDBuild code.
 *
 *
 * @author afelice
 */
public class WsClassData_WithPermissionMode extends ClassSerializationHelper.WsClassData {

    public final static String CLASS_PERMISSION_MODE_METADATA_INTERNAL_KEY = "cm_class_permission_mode";
    private final static EnumSet<ClassPermissionMode> RESERVED_MODE = EnumSet.of(CPM_PROTECTED, CPM_RESERVED);

    // See default enum value used in EntryTypeMetadataImpl constructor
    private static final ClassPermissionMode DEFAULT_CLASSE_PERMISSION_MODE = ClassPermissionMode.CPM_ALL;

    // Workaround to serialize ClassPermissionMode without modifying the
    // other CMDBuild code.
    public static final String ATTR_METADATA_DEFAULT_CLASSE_PERMISSION_MODE = serializeEnum(DEFAULT_CLASSE_PERMISSION_MODE);

    private final ClassPermissionMode permissionMode;

    @JsonCreator
    public WsClassData_WithPermissionMode(@JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("defaultFilter") Long defaultFilter,
            @JsonProperty("defaultImportTemplate") String defaultImportTemplate,
            @JsonProperty("defaultExportTemplate") String defaultExportTemplate,
            @JsonProperty("_icon") Long iconId,
            @JsonProperty("validationRule") String validationRule,
            @JsonProperty("type") String type,
            @JsonProperty("allowedExtensions") String allowedExtensions,
            @JsonProperty("checkCount") String checkCount,
            @JsonProperty("checkCountNumber") Integer checkCountNumber,
            @JsonProperty("maxFileSize") Integer maxFileSize,
            @JsonProperty("messageAttr") String messageAttr,
            @JsonProperty("flowStatusAttr") String flowStatusAttr,
            @JsonProperty("engine") String engine,
            @JsonProperty("parent") String parentId,
            @JsonProperty("active") Boolean isActive,
            @JsonProperty("prototype") Boolean isSuperclass,
            @JsonProperty("noteInline") Boolean noteInline,
            @JsonProperty("noteInlineClosed") Boolean noteInlineClosed,
            @JsonProperty("noteInGrid") Boolean noteInGrid,
            @JsonProperty("attachmentsInline") Boolean attachmentsInline,
            @JsonProperty("attachmentsInlineClosed") Boolean attachmentsInlineClosed,
            @JsonProperty("enableSaveButton") Boolean enableSaveButton,
            @JsonProperty("dmsCategory") String dmsCategory,
            @JsonProperty("multitenantMode") String multitenantMode,
            @JsonProperty("stoppableByUser") Boolean stoppableByUser,
            @JsonProperty("defaultOrder") List<WsClassDataDefaultOrder> defaultOrder,
            @JsonProperty("formTriggers") List<WsClassDataFormTrigger> formTriggers,
            @JsonProperty("contextMenuItems") List<ContextMenuSerializationHelper.WsClassDataContextMenuItem> contextMenuItems,
            @JsonProperty("widgets") List<WsClassDataWidget> widgets,
            @JsonProperty("attributeGroups") List<WsClassDataAttributeGroup> attributeGroups,
            @JsonProperty("domainOrder") List<String> domainOrder,
            @JsonProperty("help") String help,
            @JsonProperty("autoValue") String autoValue,
            @JsonProperty("uiRouting_mode") String uiRoutingMode,
            @JsonProperty("uiRouting_target") String uiRoutingTarget,
            @JsonProperty("uiRouting_custom") JsonNode uiRoutingCustom,
            @JsonProperty("barcodeSearchAttr") String barcodeSearchAttr,
            @JsonProperty("barcodeSearchRegex") String barcodeSearchRegex,
            @JsonProperty("formStructure") JsonNode formStructure,
            @JsonProperty(CLASS_PERMISSION_MODE_METADATA_INTERNAL_KEY) String permissionMode,
            @JsonProperty("icon_font") String iconFont
    ) {
        super(name, description, defaultFilter, defaultImportTemplate, defaultExportTemplate, iconId, validationRule, type, allowedExtensions, checkCount, checkCountNumber, maxFileSize, messageAttr, flowStatusAttr, engine, parentId, isActive, isSuperclass, noteInline, noteInlineClosed, noteInGrid, attachmentsInline, attachmentsInlineClosed, enableSaveButton, dmsCategory, multitenantMode, stoppableByUser, defaultOrder, formTriggers, contextMenuItems, widgets, attributeGroups, domainOrder, help, autoValue, uiRoutingMode, uiRoutingTarget, uiRoutingCustom, barcodeSearchAttr, barcodeSearchRegex, formStructure, iconFont);

        this.permissionMode = parseEnumOrDefault(permissionMode, DEFAULT_CLASSE_PERMISSION_MODE);
    }

    @Override
    public Consumer<ClassMetadataImpl.ClassMetadataImplBuilder> metadataFillerForClassDataUpdate(boolean sanitizeHtml) {
        return (b) -> b.accept(super.metadataFillerForClassDataUpdate(sanitizeHtml))
                .withMode(permissionMode);
    }

    /**
     * Add {@link ClassPermissionMode} serialization to <i>metadata</i>.
     *
     * <p>
     * Workaround to serialize {@link ClassPermissionMode} without modifying the
     * other CMDBuild code.
     *
     * @param cmdbMetadataSerialization <b>note</b>: modified by current method.
     * @param classPermissionMode
     */
    static public void addPermissionModeMetadata(Map<String, Object> cmdbMetadataSerialization, ClassPermissionMode classPermissionMode) {
        Object metadataCmdbSerialization = cmdbMetadataSerialization.get(ATTR_METADATA_SERIALIZATION);
        Map<String, String> newMap = mapOf(String.class, String.class);
        if (metadataCmdbSerialization != null) {
            newMap.putAll((Map<String, String>) metadataCmdbSerialization); // to prevent trying to put something in an unmodifiable map, that leads to a UnsupportedOperationException
        }
        cmdbMetadataSerialization.put(ATTR_METADATA_SERIALIZATION, newMap);
        newMap.put(CLASS_PERMISSION_MODE_METADATA_INTERNAL_KEY, serializeEnum(classPermissionMode));
    }

    /**
     * Extract {@link Classe} <i>permission mode</i> from {@link Classe}
     * <i>metadata</i>.
     *
     * <p>
     * {@link ClassPermissionMode#CPM_ALL} if a wrong serialization value found.
     *
     * @param cmdbMetadataSerialization
     * @return
     */
    static public ClassPermissionMode fetchPermissionMode(Map<String, Object> cmdbMetadataSerialization) {
        Object metadataCmdbSerializationObj = cmdbMetadataSerialization.get(ATTR_METADATA_SERIALIZATION);
        if (metadataCmdbSerializationObj != null && metadataCmdbSerializationObj instanceof Map metadataCmdbSerialization) {
            String modeStr = toStringOrDefault(metadataCmdbSerialization.get(CLASS_PERMISSION_MODE_METADATA_INTERNAL_KEY), serializeEnum(DEFAULT_CLASSE_PERMISSION_MODE));
            return parseEnum(modeStr, ClassPermissionMode.class);
        }

        return DEFAULT_CLASSE_PERMISSION_MODE;
    }

    static public boolean isReserved(Map<String, Object> classeCmdbSerialization) {
        ClassPermissionMode foundMode = fetchPermissionMode(classeCmdbSerialization);
        return RESERVED_MODE.contains(foundMode);
    }

    static public boolean isReserved(EntryType entryType) {
        return isReserved(entryType.getMetadata());
    }

    static public boolean isReserved(EntryTypeMetadata entryTypeMetadata) {
        ClassPermissionMode foundMode = entryTypeMetadata.getMode();
        return RESERVED_MODE.contains(foundMode);
    }
} // end WsClassData_WithClassPermissionMode class
