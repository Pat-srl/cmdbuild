package org.cmdbuild.auth.grant;

public interface GrantConstants {

    static final String GRANT_CLASS_NAME = "_Grant";
    static final String GRANT_ATTR_ROLE_ID = "IdRole";
    static final String GRANT_ATTR_PRIVILEGED_CLASS_ID = "ObjectClass";
    static final String GRANT_ATTR_MODE = "Mode";
    static final String GRANT_ATTR_TYPE = "Type";
    static final String GRANT_ATTR_PRIVILEGED_OBJECT_ID = "ObjectId", GRANT_ATTR_PRIVILEGED_OBJECT_CODE = "ObjectCode";
    static final String GRANT_ATTR_PRIVILEGE_FILTER = "Filter";
    static final String GRANT_ATTR_ATTRIBUTES_PRIVILEGES = "AttributePrivileges";
    static final String GRANT_ATTR_DMS_PRIVILEGES = "DmsPrivileges";
    static final String GRANT_ATTR_GIS_PRIVILEGES = "GisPrivileges";
    static final String GRANT_ATTR_UI_CARD_EDIT_MODE = "OtherPrivileges";

    static final String GDCP_FLOW_START = "flow_start",
            GDCP_FLOW_CLOSED_MODIFY_ATTACHMENT = "fc_attachment",
            GDCP_BULK_UPDATE = "bulk_update",
            GDCP_BULK_DELETE = "bulk_delete",
            GDCP_BULK_ABORT = "bulk_abort",
            GDCP_ON_FILTER_MISMATCH = "on_filter_mismatch",
            GDCP_RELGRAPH = "relgraph",
            GDCP_ATTACHMENT = "attachment",
            GDCP_DETAIL = "detail",
            GDCP_EMAIL = "email",
            GDCP_HISTORY = "history",
            GDCP_NOTE = "note",
            GDCP_RELATION = "relation",
            GDCP_SCHEDULE = "schedule";

}
