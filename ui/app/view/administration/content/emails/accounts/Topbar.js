Ext.define('CMDBuildUI.view.administration.content.emails.accounts.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.accounts.TopbarController'
    ],

    alias: 'widget.administration-content-emails-accounts-topbar',
    controller: 'administration-content-emails-accounts-topbar',
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
                text: CMDBuildUI.locales.Locales.administration.emails.addaccount,
                localized:{
                    text: 'CMDBuildUI.locales.Locales.administration.emails.addaccount'
                },
                ui: 'administration-action-small',
                reference: 'addaccount',
                itemId: 'addaccount',
                autoEl: {
                    'data-testid': 'administration-email-account-addAccountBtn'
                },
                bind: {
                    disabled: '{!toolAction._canAdd}'
                }
            },
            {
                xtype: 'localsearchfield',
                gridItemId: '#emailAccountsGrid'
            }
        ]
    }]
});