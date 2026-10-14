Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.chat', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-chat',
    ui: 'administration-formpagination',
    viewModel: {},
    config: {
        theGroup: {}
    },
    bind: {
        theGroup: '{theGroup}'
    },
    items: [{
        xtype: 'fieldset',
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.systemconfig.chat,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.chat'
        },
        collapsible: true,
        items: [{
            xtype: 'checkboxgroup',
            columns: 1,
            vertical: true,
            bind: {
                readOnly: '{actions.view}'
            },
            items: [{
                boxLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.allowchat,
                localized: {
                    boxLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.allowchat'
                },
                name: '_rp_chat_access',
                bind: {
                    value: '{theGroup._rp_chat_access}'
                }
            }]
        }]
    }]
});