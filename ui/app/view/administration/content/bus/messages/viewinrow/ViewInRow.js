Ext.define('CMDBuildUI.view.administration.content.bus.messages.viewinrow.viewinrow.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    alias: 'widget.administration-content-bus-messages-viewinrow-viewinrow',
    requires: [],
    controller: 'administration-content-bus-messages-viewinrow-viewinrow',
    viewModel: {
        type: 'administration-content-bus-messages-viewinrow-viewinrow'
    },
    config: {},

    cls: 'administration',
    ui: 'administration-tabandtools',
    items: [
        {
            title: CMDBuildUI.locales.Locales.administration.busmessages.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.busmessages.generalproperties'
            },
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            autoEl: {
                'data-testid': 'administration-components-attributes-fieldscontainers-generalproperties'
            },
            items: [
                {
                    xtype: 'administration-content-bus-messages-viewinrow-tabitems-generalproperties'
                }
            ]
        },
        {
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            title: CMDBuildUI.locales.Locales.administration.busmessages.meta,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.busmessages.meta'
            },
            autoEl: {
                'data-testid': 'administration-components-attributes-fieldscontainers-typeproperties'
            },
            items: [
                {
                    xtype: 'administration-content-bus-messages-viewinrow-tabitems-meta'
                }
            ]
        },
        {
            xtype: 'fieldset',
            ui: 'administration-formpagination',

            title: CMDBuildUI.locales.Locales.administration.busmessages.attachments,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.busmessages.attachments'
            },
            autoEl: {
                'data-testid': 'administration-components-attributes-fieldscontainers-otherproperties'
            },
            items: [
                {
                    xtype: 'administration-content-bus-messages-viewinrow-tabitems-attachments'
                }
            ]
        },
        {
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            title: CMDBuildUI.locales.Locales.administration.busmessages.history,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.busmessages.history'
            },
            autoEl: {
                'data-testid': 'administration-components-attributes-fieldscontainers-otherproperties'
            },
            items: [
                {
                    xtype: 'administration-content-bus-messages-viewinrow-tabitems-history'
                }
            ]
        }
    ]
});
