
Ext.define('CMDBuildUI.view.login.passwordforgotten.Panel',{
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.login.passwordforgotten.PanelController'
    ],

    alias: 'widget.login-passwordforgotten-panel',
    controller: 'login-passwordforgotten-panel',

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,

    items:[{
        xtype: 'textfield',
        fieldLabel: CMDBuildUI.locales.Locales.login.fields.username,
        allowBlank: false,
        autoEl: {
            'data-testid': 'login-passwordforgotten-panel-username'
        },
        bind: {
            value: '{username}'
        },
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.username'
        }
    }, {
        xtype: 'textfield',
        fieldLabel: CMDBuildUI.locales.Locales.main.password.email,
        allowBlank: false,
        autoEl: {
            'data-testid': 'login-passwordforgotten-panel-email'
        },
        bind: {
            value: '{email}'
        },
        localized: {
            fieldLabel: 'CMDBuildUI.locales.Locales.main.password.email'
        }
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.main.password.reset,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        itemId: 'resetbtn',
        ui: 'management-primary',
        autoEl: {
            'data-testid': 'login-passwordforgotten-panel-resetbtn'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.main.password.reset'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelbtn',
        ui: 'secondary-action',
        autoEl: {
            'data-testid': 'login-passwordforgotten-panel-cancelbtn'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        }
    }]
});
