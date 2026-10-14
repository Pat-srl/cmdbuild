Ext.define('CMDBuildUI.view.main.header.tenants.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.main.header.tenants.ContainerController',
        'CMDBuildUI.view.main.header.tenants.ContainerModel'
    ],

    alias: 'widget.tenants-menu',
    controller: 'main-header-tenants-container',
    viewModel: {
        type: 'main-header-tenants-container'
    },

    layout: 'fit',

    fbar: [{
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        ui: 'secondary-action',
        itemId: 'cancelbutton',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.save,
        itemId: 'savebutton',
        ui: 'management-primary',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        disabled: true,
        bind: {
            disabled: '{fields.buttonSaveDisabled}'
        }
    }],

    items: {
        xtype: 'tenants-grid',
        itemId: 'tenantsGrid'
    }

});