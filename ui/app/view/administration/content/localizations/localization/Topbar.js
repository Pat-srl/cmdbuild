Ext.define('CMDBuildUI.view.administration.content.localizations.localization.Topbar', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.localization.TopbarController',
        'CMDBuildUI.view.administration.content.localizations.localization.TopbarModel'
    ],

    alias: 'widget.administration-content-localizations-localization-topbar',
    controller: 'administration-content-localizations-localization-topbar',
    viewModel: {
        type: 'administration-content-localizations-localization-topbar'
    },

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
            text: CMDBuildUI.locales.Locales.administration.localizations.import,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.localizations.import'
            },
            ui: 'administration-action-small',
            reference: 'importLocalizationBtn',
            itemId: 'importLocalizationBtn',
            disabled: true,
            bind: {
                disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
            },
            autoEl: {
                'data-testid': 'administration-email-account-importLocalizationBtn'
            }
        }, {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.administration.localizations.export,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.localizations.export'
            },
            ui: 'administration-action-small',
            reference: 'exportLocalizationBtn',
            itemId: 'exportLocalizationBtn',
            autoEl: {
                'data-testid': 'administration-email-account-exportLocalizationBtn'
            }
        }]
    }]
});