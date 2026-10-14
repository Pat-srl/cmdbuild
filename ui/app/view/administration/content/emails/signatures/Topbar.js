Ext.define('CMDBuildUI.view.administration.content.emails.signatures.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.signatures.TopbarController'
    ],

    alias: 'widget.administration-content-emails-signatures-topbar',
    controller: 'administration-content-emails-signatures-topbar',
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
                text: CMDBuildUI.locales.Locales.administration.emails.addsignature,
                localized:{
                    text: 'CMDBuildUI.locales.Locales.administration.emails.addsignature'
                },
                ui: 'administration-action-small',
                reference: 'addsignature',
                itemId: 'addsignature',
                autoEl: {
                    'data-testid': 'administration-email-signature-addSignatureBtn'
                },
                bind: {
                    disabled: '{!toolAction._canAdd}'
                }
            },
            {
                xtype: 'localsearchfield',
                gridItemId: '#emailSignaturesGrid'
            }
        ]
    }]
});