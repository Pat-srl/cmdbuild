
Ext.define('CMDBuildUI.view.thematisms.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.thematisms.PanelController',
        'CMDBuildUI.view.thematisms.PanelModel'
    ],

    alias: 'widget.thematisms-panel',
    controller: 'thematisms-panel',
    viewModel: {
        type: 'thematisms-panel'
    },

    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    ui: 'managementlighttabpanel',

    fbar: [{
        xtype: 'button',
        itemId: 'cancelbutton',
        ui: 'secondary-action-small',
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        autoEl: {
            'data-testid': 'filters-panel-cancelbutton'
        }
    }, {
        xtype: 'button',
        itemId: 'savebutton',
        text: CMDBuildUI.locales.Locales.common.actions.saveandapply,
        ui: 'management-primary-outline-small',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.saveandapply'
        },
        autoEl: {
            'data-testid': 'filters-panel-savebutton'
        },
        disabled: true,
        bind: {
            disabled: '{buttonsDisabled}'
        }
    }, {
        xtype: 'button',
        itemId: 'applybutton',
        text: CMDBuildUI.locales.Locales.common.actions.apply,
        ui: 'management-primary-small',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.apply'
        },
        autoEl: {
            'data-testid': 'filters-panel-applybutton'
        },
        disabled: true,
        bind: {
            disabled: '{buttonsDisabled}'
        }
    }]
});
