/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.utils;

import com.google.common.collect.ImmutableSet;
import java.util.Set;

public class WidgetConst {

    public static final String WIDGET_TABLE = "_Widget",
            ATTR_OWNER = "Owner",
            ATTR_IS_ACTIVE = "Active",
            ATTR_TYPE = "Type",
            ATTR_DATA = "Data";

    public static final String WIDGET_BUTTON_LABEL_KEY = "ButtonLabel",
            WIDGET_OUTPUT_KEY = "Output",
            WIDGET_REQUIRED_KEY = "Required",
            WIDGET_ALWAYS_ENABLED_KEY = "AlwaysEnabled",
            WIDGET_FILTER_KEY = "Filter",
            WIDGET_DEFAULT_SELECTION_KEY = "DefaultSelection",
            WIDGET_CLASS_NAME = "ClassName",
            WIDGET_OUTPUT_TYPE = "OutputType",
            WIDGET_ID = "WidgetId",
            WIDGET_INDEX = "Index",
            WIDGET_INLINE = "Inline",
            WIDGET_INLINE_CLOSED = "InlineClosed",
            WIDGET_INLINE_BEFORE = "InlineBefore",
            WIDGET_INLINE_AFTER = "InlineAfter",
            WIDGET_HIDE_IN_CREATION = "HideInCreation",
            WIDGET_HIDE_IN_EDIT = "HideInEdit";

    public static final String WIDGET_ACTION_SUBMIT = "submit";

    public static final String WIDGET_TYPE_CREATE_MODIFY_CARD = "createModifyCard",
            WIDGET_TYPE_CALENDAR = "calendar",
            WIDGET_TYPE_MANAGE_EMAIL = "manageEmail",
            WIDGET_TYPE_LINK_CARDS = "linkCards",
            WIDGET_TYPE_CUSTOM_FORM = "customForm",
            WIDGET_TYPE_OPEN_ATTACHMENT = "openAttachment",
            WIDGET_TYPE_CREATE_REPORT = "createReport",
            WIDGET_TYPE_PRESET_FROM_CARD = "presetFromCard",
            WIDGET_TYPE_START_WORKFLOW = "startWorkflow",
            WIDGET_TYPE_OPEN_NOTE = "openNote";

    public static final Set<String> WIDGETS_FOR_WORKFLOW = ImmutableSet.of(
            WIDGET_TYPE_CREATE_MODIFY_CARD,
            WIDGET_TYPE_CALENDAR,
            WIDGET_TYPE_MANAGE_EMAIL,
            WIDGET_TYPE_LINK_CARDS,
            WIDGET_TYPE_CUSTOM_FORM,
            WIDGET_TYPE_OPEN_ATTACHMENT,
            WIDGET_TYPE_CREATE_REPORT,
            WIDGET_TYPE_PRESET_FROM_CARD,
            WIDGET_TYPE_START_WORKFLOW,
            WIDGET_TYPE_OPEN_NOTE);

    public static final Set<String> WIDGET_ATTR_KEYS_FOR_CQL_PROCESSING = ImmutableSet.of(WIDGET_FILTER_KEY, WIDGET_DEFAULT_SELECTION_KEY);
}
