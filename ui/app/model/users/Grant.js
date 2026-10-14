Ext.define('CMDBuildUI.model.users.Grant', {
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        permissions: {
            attachment_read: '_attachment_access_read',
            attachment_write: '_attachment_access_write',
            detail_read: '_detail_access_read',
            detail_write: '_detail_access_write',
            email_read: '_email_access_read',
            email_write: '_email_access_write',
            history_read: '_history_access_read',
            history_write: '_history_access_write',
            note_read: '_note_access_read',
            note_write: '_note_access_write',
            relation_read: '_relation_access_read',
            relation_write: '_relation_access_write',
            schedule_read: '_schedule_access_read',
            schedule_write: '_schedule_access_write'
        },
        grantType: {
            read: 'read',
            write: 'write',
            none: 'none'
        },
        grantTypeWorkflow: {
            default: 'wf_default',
            plus: 'wf_plus',
            basic: 'wf_basic'
        }
    },
    fields: [{
        name: 'role',
        type: 'number',
        critical: true
    }, {
        name: 'mode',
        type: 'string',
        critical: true,
        defaultValue: 'none'
    }, {
        name: '_on_filter_mismatch',
        type: 'string',
        critical: true
    }, {
        name: '_on_filter_mismatch_calculated',
        calculate: function (data) {
            switch (data._on_filter_mismatch) {
                case 'read':
                    return true;
                default:
                    return false;
            }
        }
    }, {
        name: 'modeTypeNone',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantType.none; /*'-'*/
        },
        depends: ['mode']
    }, {
        name: 'modeTypeAllow',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantType.read; /*'r'*/
        },
        depends: ['mode']
    }, {
        name: 'modeTypeRead',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantType.read; /*'r'*/
        },
        depends: ['mode']
    }, {
        name: 'modeTypeWrite',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantType.write; /*'w'*/
        },
        depends: ['mode']
    }, {
        name: 'modeTypeWFBasic',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantTypeWorkflow.basic; /*'new alue for workflow'*/
        },
        depends: ['mode']
    }, {
        name: 'modeTypeWFPlus',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantTypeWorkflow.plus; /*'w'*/
        },
        depends: ['mode']
    }, {
        name: 'modeTypeWFDefault',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantTypeWorkflow.default; /*'r'*/
        },
        depends: ['mode']
    }, {
        name: 'objectType',
        type: 'string',
        critical: true
    }, {
        name: 'objectTypeName',
        type: 'string',
        critical: true
    }, {
        name: '_object_description',
        type: 'string',
        critical: false
    }, {
        name: 'filter',
        type: 'string',
        critical: true,
        defaultValue: ''
    }, {
        name: 'attributePrivileges',
        type: 'auto',
        critical: true,
        defaultValue: {}
    }, {
        name: 'gisPrivileges',
        type: 'auto',
        critical: true,
        defaultValue: {}
    }, {
        name: '_attributePrivilegesEmpty',
        type: 'boolean',
        critical: false,
        calculate: function (data) {
            var empty = true;
            if (!Ext.Object.isEmpty(data.attributePrivileges)) {
                Ext.Object.eachValue(data.attributePrivileges, function (value) {
                    if (value !== 'write') {
                        empty = false;
                    }
                });
            }

            if (!Ext.Object.isEmpty(data.gisPrivileges)) {
                Ext.Object.eachValue(data.gisPrivileges, function (value) {
                    if (value !== 'default') {
                        empty = false;
                    }
                });
            }
            return empty;
        }
    }, {
        name: '_can_clone',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: '_can_create',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: '_can_delete',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: '_can_update',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: '_relationgraph_access',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: '_can_print',
        type: 'boolean',
        critical: true,
        defaultValue: false
    }, {
        name: '_can_fc_attachment',
        type: 'auto',
        critical: true,
        defaultValue: null
    }, {
        name: '_can_bulk_update',
        type: 'auto',
        critical: true,
        defaultValue: null
    }, {
        name: '_can_bulk_delete',
        type: 'auto',
        critical: true,
        defaultValue: null
    }, {
        name: '_can_bulk_abort',
        type: 'auto',
        critical: true,
        defaultValue: null
    }, {
        name: '_can_search',
        type: 'auto',
        critical: true,
        defaultValue: null
    }, {
        name: 'modeTypeNoneOther',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantType.none; /*'-'*/
        },
        depends: ['mode']
    }, {
        name: 'modeTypeWriteAllow',
        type: 'boolean'
    }, {
        name: 'modeTypeWriteOther',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantType.write; /*'w'*/
        },
        depends: ['mode']
    }, {
        name: 'modeTypeReadOther',
        type: 'boolean',
        critical: true,
        convert: function (value, rec) {
            return rec.get('mode') === CMDBuildUI.model.users.Grant.grantType.read; /*'r'*/
        },
        depends: ['mode']
    }, {
        name: '_attachment_access_read', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_attachment_access_write', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_detail_access_read', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_detail_access_write', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_email_access_read', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_email_access_write', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_history_access_read', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_history_access_write', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_note_access_read', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_note_access_write', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_relation_access_read', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_relation_access_write', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_schedule_access_read', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: '_schedule_access_write', // true/false/null
        type: 'string',
        critical: true
    }, {
        name: 'dmsPrivileges',
        type: 'auto',
        critical: true,
        defaultValue: {}
    }, {
        name: 'lastupdate',
        type: 'auto',
        critical: false,
        persist: false,
        defaultValue: '0'
    }],

    convertOnSet: true,

    changeMode: function (mode) {
        this.set('mode', mode);
    },

    proxy: {
        url: '/roles/',
        type: 'baseproxy',
        extraParams: {
            includeObjectDescription: true,
            includeRecordsWithoutGrant: true,
            ext: true
        },
        writer: {
            type: 'json',
            writeAllFields: true
        }
    }
});