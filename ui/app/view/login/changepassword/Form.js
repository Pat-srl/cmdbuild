Ext.define('CMDBuildUI.view.login.changepassword.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.login.changepassword.FormController',
        'CMDBuildUI.view.login.changepassword.FormModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    alias: 'widget.login-changepassword-form',
    controller: 'login-changepassword-form',
    viewModel: {
        type: 'login-changepassword-form'
    },

    /**
     * @event passwordchange
     * Fires when the Password has been changed succesfully.
     * @param {CMDBuildUI.view.login.changepassword.Form} this This change password form
     */

    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    autoEl: {
        tag: 'form',
        'data-testid': 'login-changepassword-form'
    },
    items: [],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        itemId: 'cancelBtn',
        ui: 'secondary-action',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        autoEl: {
            'data-testid': 'login-changepassword-form-cancelBtn'
        }
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, // enable once the form is valid
        disabled: true,
        itemId: 'saveBtn',
        ui: 'management-primary',
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        autoEl: {
            'data-testid': 'login-changepassword-form-saveBtn'
        }
    }]
});