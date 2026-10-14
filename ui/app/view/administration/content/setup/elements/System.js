Ext.define('CMDBuildUI.view.administration.content.setup.elements.System', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.SystemController'
    ],

    alias: 'widget.administration-content-setup-elements-system',
    controller: 'administration-content-setup-elements-system',
    viewModel: {},

    margin: 10,
    items: [{
        xtype: "fieldset",
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.systemconfig.actions,
        localized: {
            title: "CMDBuildUI.locales.Locales.administration.systemconfig.actions"
        },
        scrollable: true,
        forceFit: true,
        defaults: {
            xtype: 'button',
            margin: 'auto 15 auto auto',
            ui: 'administration-action-small'
        },
        items: [{
            text: CMDBuildUI.locales.Locales.administration.systemconfig.dropcache,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.systemconfig.dropcache'
            },
            handler: 'onDropCacheBtnClick',
            disabled: true,
            bind: {
                disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
            },
            autoEl: {
                'data-testid': 'administration-systemconfig-system-dropcache_button'
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.systemconfig.systempreload,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.systemconfig.systempreload'
            },
            handler: 'onSystemPreloadBtnClick',
            disabled: true,
            bind: {
                disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
            },
            autoEl: {
                'data-testid': 'administration-systemconfig-system-systempreload_button'
            }
        }, {
            text: CMDBuildUI.locales.Locales.administration.systemconfig.unlockallcards,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.systemconfig.unlockallcards'
            },
            handler: 'onUnlockAllCardsBtnClick',
            disabled: true,
            bind: {
                disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
            },
            autoEl: {
                'data-testid': 'administration-systemconfig-system-unlockallcards_button'
            }
        }]
    }, {
        // services grid
        xtype: "fieldset",
        ui: 'administration-formpagination',
        title: CMDBuildUI.locales.Locales.administration.systemconfig.services,
        localized: {
            title: "CMDBuildUI.locales.Locales.administration.systemconfig.services"
        },
        scrollable: true,
        forceFit: true,
        items: [{
            xtype: 'administration-content-setup-elements-statusgrid',
            autoEl: {
                'data-testid': 'administration-systemconfig-system-services_grid'
            }
        }]
    }]
});