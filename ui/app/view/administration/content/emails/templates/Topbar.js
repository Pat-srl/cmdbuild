Ext.define('CMDBuildUI.view.administration.content.emails.templates.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.TopbarController'
    ],

    alias: 'widget.administration-content-emails-templates-topbar',
    controller: 'administration-content-emails-templates-topbar',
    viewModel: {},
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',

        items: [{
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.emails.addtemplate,
            ui: 'administration-action-small',
            reference: 'addtemplate',
            itemId: 'addtemplate',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.emails.addtemplate'
            },
            autoEl: {
                'data-testid': 'administration-template-toolbar-addTemplateBtn'
            },
            menu: [],
            bind: {
                disabled: '{!toolAction._canAdd}'
            }
        }, {
            xtype: 'localsearchfield',
            gridItemId: '#emailTemplatesGrid'
        }]
    }]

});