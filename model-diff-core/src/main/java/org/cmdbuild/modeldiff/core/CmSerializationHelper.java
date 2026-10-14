/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

import java.util.Map;
import java.util.UUID;
import org.cmdbuild.contextmenu.ContextMenuType;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

/**
 * Names used in CMDBuild serialization.
 *
 * @author afelice
 */
public interface CmSerializationHelper {

    // Data
    String ATTR_ID_SERIALIZATION = "_id";
    String ATTR_IDCLASS_SERIALIZATION = "_type";

    // Schema
    String ATTR_ACTIVE_SERIALIZATION = "active"; // to handle deactivation of Attribute/Classe/Process/Domain
    String ATTR_CODE_SERIALIZATION = "code";
    String ATTR_DESCRIPTION_SERIALIZATION = "description";
    String ATTR_INHERITED_SERIALIZATION = "inherited"; // to handle attributes inherited from a super Classe
    String ATTR_LOOKUP_TYPE_REFERENCE_SERIALIZATION = "lookupValues";
    String ATTR_LOOKUP_VALUES_SERIALIZATION = "values";
    String ATTR_LOOKUP_TYPE_SPECIALITY_SERIALIZATION = "speciality";
    String ATTR_VALUE_PARENT_ID_SERIALIZATION = "parent_id";
    String ATTR_METADATA_SERIALIZATION = "metadata"; // to handle metadata modified in attributes inherited from a super Classe
    String ATTR_METADATA_SUPERCLASS_SERIALIZATION = "";
    String ATTR_NAME_SERIALIZATION = "name";
    String ATTR_PARENT_SERIALIZATION = "parent"; // to handle topological sorting of Classe and LookupType
    String ATTR_REFERENCE_DIRECTION_SERIALIZATION = "direction"; // to handle topological sorting of Classe
    String ATTR_REFERENCE_DIRECTION_DIRECT_SERIALIZATION = "direct"; // to handle topological sorting of Classe
    String ATTR_REFERENCE_DIRECTION_INVERSE_SERIALIZATION = "inverse"; // to handle topological sorting of Classe
    String ATTR_REFERENCE_DOMAIN_SERIALIZATION = "domain"; // to handle deactivation of reference Attributes
    String ATTR_REFERENCED_CLASSE_SERIALIZATION = "targetClass"; // to handle deactivation of reference/foreign key Attributes
    String ATTR_LOOKUP_ATTRIBUTE_SERIALIZATION = "lookupType"; // to handle deactivation of Lookup/LookupArray Attributes
    String ATTR_LOOKUP_VALUE_DEFAULT_ATTRIBUTE_SERIALIZATION = "default"; // to handle deactivation of LookupValue
    String ATTR_LOOKUP_VALUE_PARENT_TYPE_ATTRIBUTE_SERIALIZATION = "default"; // to handle insert/update of inherited LookupValue
    String ATTR_TYPE_SERIALIZATION = "type"; // to handle deactivation of reference/foreign key Attributes
    String ATTR_ATTRIBUTE_MODE_SERIALIZATION = "mode"; // to handle skip of unmodificable Attribute
    String ATTR_ATTRIBUTE_VIRTUAL_SERIALIZATION = "virtual"; // to handle FORMLA Attribute workaround
    String VALUE_ATTRIBUTE_TYPE_FORMULA_SERIALIZATION = "formula"; // to handle FORMLA Attribute
    String ATTR_ATTRIBUTE_TYPE_FORMULA_TYPE_SERIALIZATION = "formulaType"; // to handle FORMLA Attribute
    String ATTR_ATTRIBUTE_TYPE_FORMULA_CODE_SERIALIZATION = "formulaCode"; // to handle FORMLA Attribute
    String VALUE_ATTRIBUTE_TYPE_FORMULA_SCRIPT_SERIALIZATION = "script"; // to handle FORMLA Attribute
    String ATTR_CLASSE_ICON_SERIALIZATION = "_icon"; // to handle Icons for Classe, stored as base 64 in CMDBuild DB
    String ATTR_CLASSE_TYPE_SPECIALITY_SERIALIZATION = "speciality"; // to handle Dms Models
    String ATTR_DMS_MODEL_TYPE_SPECIALITY_VALUE = "dmsmodel"; // to handle Dms Models
    String ATTR_CLASSE_DMS_CATEGORY_SERIALIZATION = "dmsCategory"; // to handle DMS Category for Classe
    String ATTR_CLASSE_CONTEXT_MENU_ITEMS_SERIALIZATION = "contextMenuItems"; // to handle context menu items
    String ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_TYPE_SERIALIZATION = serializeEnum(ContextMenuType.COMPONENT); // to handle context menu items
    String ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_ID_SERIALIZATION = "componentId"; // to handle context menu items
    String ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_LABEL_SERIALIZATION = "label"; // to handle context menu items
    String ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_NAME_SERIALIZATION = "jscomponent"; // to handle context menu items
    String ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_COMPONENT_SERIALIZATION = "scripts"; // to handle context menu items
    String ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_DESKTOP_COMPONENT_SERIALIZATION = serializeEnum(TargetDevice.TD_DEFAULT); // to handle context menu items
    String ATTR_CLASSE_CONTEXT_MENU_ITEMS_JS_MOBILE_COMPONENT_SERIALIZATION = serializeEnum(TargetDevice.TD_MOBILE); // to handle context menu items
    String ATTR_CLASSE_DEFAULT_ORDER_SERIALIZATION = "defaultOrder"; // to handle defaultOrder
    String ATTR_CLASSE_WIDGETS_SERIALIZATION = "widgets"; // to fix widgets serialization for Classe
    String ATTR_CLASSE_WIDGET_WIDGET_ID_SERIALIZATION = "WidgetId"; // to fix widgets serialization for Classe
    String ATTR_CLASSE_WIDGET_CONFIG_SERIALIZATION = "_config"; // to fix widgets serialization for Classe
    String ATTR_PROCESS_ENABLE_SAVE_BUTTON_SERIALIZATION = "enableSaveButton"; // to handle Process
    String ATTR_PROCESS_FLOW_STATUS_ATTR_SERIALIZATION = "flowStatusAttr"; // to handle Process
    String ATTR_PROCESS_MESSAGE_ATTR_SERIALIZATION = "messageAttr"; // to handle Process
    String ATTR_PROCESS_WORKFLOW_STOPPABLE_BY_USER_SERIALIZATION = "stoppableByUser"; // to handle Process
    String ATTR_PROCESS_WORKFLOW_ENGINE_SERIALIZATION = "engine"; // to handle Process
    String ATTR_PROCESS_PLAN_ID_SERIALIZATION = "planId"; // to handle XPDL for Process, stored as separated testual documents in a zip file
    String ATTR_PROCESS_XPDL_MD5_SERIALIZATION = "_xpdl_md5"; // to handle diff of XPDL for Process
    String ATTR_PROCESS_XPDL_ZIP_FILENAME_SERIALIZATION = "xpdlZipFile"; // to handle XPDL for Process, stored as separated testual documents in a zip file

    String ATTR_DMS_DOCUMENT_ID_SERIALIZATION = "documentId"; // to handle Card Attachments 
    String ATTR_DMS_DOCUMENT_FILE_NAME_SERIALIZATION = "FileName"; // to handle Card Attachments (legacy input serialization in WsAttributeData); normal output serializatoin uses standard "name" property
    String ATTR_DMS_DOCUMENT_AUTHOR_SERIALIZATION = "author"; // to handle Card Attachments 
    String ATTR_DMS_DOCUMENT_CATEGORY_NAME_SERIALIZATION = "_category_name"; // to handle Card Attachments     
    String ATTR_DMS_DOCUMENT_CATEGORY_ID_SERIALIZATION = "_category"; // to handle Card Attachments     
    String ATTR_DMS_DOCUMEMT_MAJOR_VERSION_FLAG_SERIALIZATION = "majorVersion"; // to handle Card Attachments 
    String ATTR_DMS_DOCUMENT_VERSION_SERIALIZATION = "version"; // to handle Card Attachments 
    String ATTR_CARD_ATTACHMENT_CARD_ID_SERIALIZATION = "_card"; // to handle Card Attachments 
    String ATTR_NEW_CARD_ATTACHMENT_UUID = "_card_uuid"; // to handle attachments to a newly added Card (card metadata expects a long value for id, so this synthesized property is used)
    
    public static boolean isUUID(String id) {
        try {
            UUID uuid = UUID.fromString(id);
            return uuid.toString().equals(id);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }    
    
    /**
     * As in {@link Card#get(java.lang.String, java.lang.Class) .
     *
     * @return
     * @param serialization
     * @param name
     * @param clazz
     * @param <T>
     */
    static public <T> T get(Map<String, Object> serialization, Class<T> clazz, String key) {
        return convert(serialization.get(key), clazz);
    }    
    
    /**
     * 
     * @param <T>
     * @param serialization
     * @param clazz
     * @param keyOne
     * @param keyTwo
     * @return 
     */
    static public <T> T get(Map<String, Object> serialization, Class<T> clazz, String keyOne, String keyTwo) {
        Object value = serialization.containsKey(keyOne) ? serialization.get(keyOne) : serialization.get(keyTwo);
        
        return convert(value, clazz);
    }            

    /**
     * Gets, converting to given class, trying in order:
     * <ol>
     * <li>key as capitalized, for things concerted to <i>data</i> <b>system</b> {@link Attribute} stuff: <code>Code</code>, <code>Description</code>, <code>Name</code>, <code>Version</code>;
     * <li>key as is, typically lowerized (as returned by most serialization for <i>model</i> stuff).
     * </ol>
     * 
     * @param <T>
     * @param serialization
     * @param clazz
     * @param key
     * @return 
     */
    static public <T> T getBoth(Map<String, Object> serialization, Class<T> clazz, String key) {
        return get(serialization, clazz, capitalize(key), key);
    }
    
    static public String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }    
    
    static public String lowerize(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        return s.substring(0, 1).toLowerCase() + s.substring(1);
    }
    
    /**
     * Handles duplicated capitalized names produced by <i>card attachments</i> serialization
     * and used by UI to modify the properties.
     */
    class CardAttachmentSerializationHelper {
        static public String getDocumentId(Map<String, Object> cardAttachmentSerialization) {
            return get(cardAttachmentSerialization, String.class, ATTR_ID_SERIALIZATION);
        }
        
        static public String getFileName(Map<String, Object> cardAttachmentSerialization) {
            return get(cardAttachmentSerialization, String.class, ATTR_DMS_DOCUMENT_FILE_NAME_SERIALIZATION, ATTR_NAME_SERIALIZATION);
        }
        
        static public Long getMetadataCardId(Map<String, Object> cardAttachmentSerialization) {
            return get(cardAttachmentSerialization, Long.class, ATTR_CARD_ATTACHMENT_CARD_ID_SERIALIZATION);
        }
        
        static public String getDescription(Map<String, Object> cardAttachmentSerialization) {
            return getBoth(cardAttachmentSerialization, String.class, ATTR_DESCRIPTION_SERIALIZATION);
        }
        
        static public String getAuthor(Map<String, Object> cardAttachmentSerialization) {
            return getBoth(cardAttachmentSerialization, String.class, ATTR_DMS_DOCUMENT_AUTHOR_SERIALIZATION);
        }        
        
        /**
         * Ignore <code>"Version"</code> sent by UI.
         * 
         * @param cardAttachmentSerialization
         * @return 
         */
        static public String getVersion(Map<String, Object> cardAttachmentSerialization) {
            return get(cardAttachmentSerialization, String.class, ATTR_DMS_DOCUMENT_VERSION_SERIALIZATION);
        }
        
    } // end CardAttachmentSerializationHelper class
}
