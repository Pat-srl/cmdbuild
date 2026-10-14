Ext.define('CMDBuildUI.view.emails.DMSAttachments.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.emails.DMSAttachments.GridController'
    ],

    alias: 'widget.attachments-grid',
    controller: 'attachments-grid',

    ui: 'cmdbuildgrouping',

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'textfield',
        itemId: 'searchattachmentemail',
        emptyText: CMDBuildUI.locales.Locales.emails.searchattachment,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.emails.searchattachment'
        },
        width: 250,
        listeners: {
            specialkey: 'onSearchSpecialKey'
        },
        triggers: {
            search: {
                cls: Ext.baseCSSPrefix + 'form-search-trigger',
                handler: 'onSearchSubmit'
            },
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear'
            }
        }
    }],

    columns: [{
        text: CMDBuildUI.locales.Locales.attachments.filename,
        dataIndex: 'name',
        align: 'left',
        hidden: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.filename'
        }
    }, {
        text: CMDBuildUI.locales.Locales.attachments.description,
        dataIndex: 'description',
        align: 'left',
        hidden: false,
        localized: {
            text: 'CMDBuildUI.locales.Locales.attachments.description'
        }
    }],

    scrollable: true,
    selModel: {
        type: 'checkboxmodel'
    },

    features: [{
        ftype: 'grouping',
        groupHeaderTpl: '{name}',
        depthToIndent: 50
    }],

    bind: {
        store: '{attachments}'
    }
});