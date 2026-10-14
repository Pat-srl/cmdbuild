
Ext.define('CMDBuildUI.view.emails.DMSAttachments.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.emails.DMSAttachments.PanelController',
        'CMDBuildUI.view.emails.DMSAttachments.PanelModel'
    ],

    alias: 'widget.emails-dmsattachments-panel',
    controller: 'emails-dmsattachments-panel',
    viewModel: {
        type: 'emails-dmsattachments-panel'
    },

    layout: 'vbox',

    tbar: [{
        xtype: 'combobox',
        emptyText: CMDBuildUI.locales.Locales.emails.selectaclass,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.emails.selectaclass'
        },
        displayField: 'label',
        valueField: ['value'],
        queryMode: 'local',
        forceSelection: true,
        width: 500,
        itemId: 'comboclass',
        bind: {
            store: '{attributeslist}'
        }
    }, {
        xtype: 'textfield',
        itemId: 'searchcardemail',
        emptyText: CMDBuildUI.locales.Locales.emails.searchcard,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.emails.searchcard'
        },
        padding: '0 0 0 30',
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

    items: [{
        flex: 1,
        layout: 'fit',
        xtype: 'container',
        itemId: 'classcontainer',
        width: '100%'
    }, {
        flex: 1,
        layout: 'fit',
        xtype: 'container',
        itemId: 'attachmentcontainer',
        width: '100%'
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelBtn',
        ui: 'secondary-action',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.save,
        itemId: 'saveBtn',
        ui: 'management-primary',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        }
    }]
});
