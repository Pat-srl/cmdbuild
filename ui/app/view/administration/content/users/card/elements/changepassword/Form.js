Ext.define('CMDBuildUI.view.administration.content.users.elements.changepassword.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.users.elements.changepassword.FormController',
        'CMDBuildUI.view.administration.content.users.elements.changepassword.FormModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    alias: 'widget.administration-users-changepassword-form',
    controller: 'administration-users-changepassword-form',
    viewModel: {
        type: 'administration-users-changepassword-form'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    autoEl: {
        tag: 'form',
        'data-testid': 'administration-users-changepassword-form',
        autocomplete: 'off'
    },
    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.emails.password,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.password'
        },

        items: [{
            xtype: 'passwordfield',
            autoEl: {
                'data-testid': 'administration-user-password'
            },            
            listeners: {
                afterrender: function (cmp) {
                    cmp.inputEl.set({
                        autocomplete: 'new-password'
                    });
                },
                change: function (cmp) {
                    var confirm = this.up().down('[name="new-confirm"]');
                    confirm.allowBlank = (this.getValue() === '');
                    confirm.validate();
                }
            },
            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
            },
            name: 'new-data',
            reference: 'password',
            enforceMaxLength: true,
            allowBlank: false,
            maxLength: 40,
            bind: {
                value: '{theUser.password}'
            }
        }, {
            xtype: 'passwordfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.confirmpassword,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.confirmpassword'
            },
            name: 'new-confirm',
            reference: 'confirmPasswordField',
            vtype: 'passwordMatch',
            enforceMaxLength: true,
            maxLength: 40,
            allowBlank: false,
            autoEl: {
                'data-testid': 'administration-user-confirmpassword'
            },
            bind: {
                value: '{theUser.confirmPassword}'
            }
        }, {

            xtype: 'checkbox',
            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.changepasswordfirstlogin,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.changepasswordfirstlogin'
            },
            name: 'changePasswordRequired',
            bind: {
                value: '{theUser.changePasswordRequired}'
            }

        }]
    }],

    buttons: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)

});