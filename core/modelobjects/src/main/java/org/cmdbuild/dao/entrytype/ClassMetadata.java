/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.common.beans.TypeAndCode;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_DEFAULT;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_DMSMODEL;
import static org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality.CS_PROCESS;
import org.cmdbuild.lookup.DmsCategoryConfig;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface ClassMetadata extends EntryTypeMetadata, DmsCategoryConfig {

    static final String SUPERCLASS = "cm_superclass",
            CLASS_TYPE = "cm_class_type",
            CLASS_TYPE_SIMPLE = "simpleclass",
            CLASS_TYPE_STANDARD = "class",
            USER_STOPPABLE = "cm_stoppable",
            WORKFLOW_STATUS_ATTR = "cm_workflow_status_attr",
            WORKFLOW_MESSAGE_ATTR = "cm_workflow_message_attr",
            WORKFLOW_ENABLE_SAVE_BUTTON = "cm_workflow_enable_save_button",
            WORKFLOW_PROVIDER = "cm_workflow_provider",
            DMS_CATEGORY = "cm_dms_category",
            DEFAULT_FILTER = "cm_default_filter",
            DEFAULT_IMPORT_TEMPLATE = "cm_default_import_template",
            DEFAULT_EXPORT_TEMPLATE = "cm_default_export_template",
            NOTE_INLINE = "cm_note_inline",
            NOTE_INLINE_CLOSED = "cm_note_inline_closed",
            NOTE_INGRID = "cm_note_ingrid",
            ATTACHMENTS_INLINE = "cm_attachments_inline",
            ATTACHMENTS_INLINE_CLOSED = "cm_attachments_inline_closed",
            VALIDATION_RULE = "cm_validation_rule",
            CLASS_ICON = "cm_class_icon",
            PROCESS_ENGINE = "cm_process_engine",
            MULTITENANT_MODE = "cm_multitenant_mode",
            DOMAIN_ORDER = "cm_domain_order",
            CLASS_SPECIALITY = "cm_class_speciality",
            HELP_MESSAGE = "cm_help",
            AUTO_VALUE = "cm_auto_value",
            UI_ROUTING_MODE = "cm_ui_routing_mode",
            UI_ROUTING_TARGET = "cm_ui_routing_target",
            UI_ROUTING_CUSTOM = "cm_ui_routing_custom",
            BARCODE_SEARCH_ATTR = "cm_barcode_search_attr",
            BARCODE_SEARCH_REGEX = "cm_barcode_search_regex",
            TEMPLATE_TYPE_TEMPLATE = "template",
            TEMPLATE_TYPE_GATE = "gate",
            ICON_FONT = "cm_icon_font";

    boolean isSuperclass();

    ClassType getClassType();

    boolean isWfUserStoppable();

    ClassSpeciality getClassSpeciality();

    @Nullable
    String getFlowStatusAttr();

    @Nullable
    String getFlowProviderOrNull();

    @Nullable
    String getMessageAttr();

    @Nullable
    Boolean isFlowSaveButtonEnabled();

    @Nullable
    String getDmsCategoryOrNull();

    @Nullable
    TypeAndCode getDefaultImportTemplateOrNull();

    @Nullable
    Long getDefaultFilterOrNull();

    @Nullable
    TypeAndCode getDefaultExportTemplateOrNull();

    @Nullable
    String getValidationRuleOrNull();

    boolean getNoteInline();

    boolean getNoteInlineClosed();

    boolean getNoteInGrid();

    boolean getAttachmentsInline();

    boolean getAttachmentsInlineClosed();

    ClassMultitenantMode getMultitenantMode();

    List<String> getDomainOrder();

    @Nullable
    String getHelpMessage();

    @Nullable
    String getAutoValue();

    ClassUiRoutingMode getUiRoutingMode();

    @Nullable
    String getUiRoutingTarget();

    @Nullable
    String getUiRoutingCustom();

    @Nullable
    String getBarcodeSearchAttr();

    @Nullable
    String getBarcodeSearchRegex();

    @Nullable
    String getIconFont();

    default TypeAndCode getDefaultImportTemplate() {
        return checkNotNull(getDefaultImportTemplateOrNull());
    }

    default TypeAndCode getDefaultExportTemplate() {
        return checkNotNull(getDefaultExportTemplateOrNull());
    }

    default boolean hasDefaultExportTemplate() {
        return getDefaultExportTemplateOrNull() != null;
    }

    default boolean hasDefaultImportTemplate() {
        return getDefaultImportTemplateOrNull() != null;
    }

    default boolean isDmsModel() {
        return equal(getClassSpeciality(), CS_DMSMODEL);
    }

    default boolean isProcess() {
        return equal(getClassSpeciality(), CS_PROCESS);
    }

    default boolean isDefaultSpeciality() {
        return equal(getClassSpeciality(), CS_DEFAULT);
    }

    default boolean holdsHistory() {
        return switch (getClassType()) {
            case CT_STANDARD ->
                true;
            case CT_SIMPLE ->
                false;
            default ->
                throw illegalArgument("unsupported class type = %s", getClassType());
        };
    }

    default boolean hasIcon() {
        return isNotBlank(getAll().get(CLASS_ICON));
    }

    default String getIcon() {
        return checkNotBlank(getAll().get(CLASS_ICON));
    }

    default boolean isSimpleClass() {
        return equal(getClassType(), ClassType.CT_SIMPLE);
    }

    default boolean isStandardClass() {
        return equal(getClassType(), ClassType.CT_STANDARD);
    }

    enum ClassSpeciality {
        CS_DEFAULT, CS_DMSMODEL, CS_PROCESS
    }

}
