Ext.define('CMDBuildUI.view.login.FormPanel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.login.FormPanelController',
        'CMDBuildUI.view.login.FormPanelModel',
        'Ext.ux.form.MultiSelect'
    ],

    xtype: 'login-formpanel',
    controller: 'login-formpanel',
    viewModel: {
        type: 'login-formpanel'
    },

    layout: 'anchor',

    autoEl: 'form',

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    modelValidation: true,

    items: [
        {
            html: CMDBuildUI.locales.Locales.main.pleasecorrecterrors,
            localized: {
                html: 'CMDBuildUI.locales.Locales.main.pleasecorrecterrors'
            },
            cls: 'error',
            hidden: true,
            autoEl: {
                'data-testid': 'login-errormessage'
            },
            bind: {
                hidden: '{!showErrorMessage}'
            }
        },
        {
            xtype: 'textfield',
            reference: 'usernameField',
            itemId: 'usernameField',
            fieldLabel: CMDBuildUI.locales.Locales.login.fields.username,
            allowBlank: false,
            autoEl: {
                'data-testid': 'login-inputusername'
            },
            bind: {
                value: '{theSession.username}',
                disabled: '{disabledfields.username}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.username'
            },
            listeners: {
                afterrender: function (field) {
                    field.inputEl.set({
                        autocomplete: CMDBuildUI.view.fields.password.Password.username
                    });
                    field.focus(false, 1000);
                }
            }
        },
        {
            xtype: 'passwordfield',
            reference: 'passwordField',
            itemId: 'passwordField',
            fieldLabel: CMDBuildUI.locales.Locales.login.fields.password,
            allowBlank: false,
            hidden: false,
            autocomplete: CMDBuildUI.view.fields.password.Password.currentPassword,
            autoEl: {
                'data-testid': 'login-inputpassword'
            },
            bind: {
                value: '{password}',
                disabled: '{disabledfields.password}',
                hidden: '{hiddenfields.password}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.password'
            }
        },
        {
            xtype: 'combobox',
            itemId: 'roleField',
            fieldLabel: CMDBuildUI.locales.Locales.login.fields.group,
            displayField: 'label',
            valueField: 'value',
            queryMode: 'local',
            forceSelection: true,
            editable: false,
            hidden: true,
            autoEl: {
                'data-testid': 'login-inputrole'
            },
            bind: {
                hidden: '{hiddenfields.role}',
                disabled: '{hasRole}',
                value: '{theSession.role}',
                store: '{groups}'
            },
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.login.fields.group'
            }
        },
        {
            xtype: 'combobox',
            reference: 'activeTenantsFieldone',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.tenant,
            displayField: 'label',
            valueField: 'value',
            queryMode: 'local',
            editable: false,
            hidden: true,
            autoEl: {
                'data-testid': 'login-inputtenant-one'
            },
            bind: {
                hidden: '{!tenantsone}',
                store: '{tenants}',
                fieldLabel: '{tenantLabel}'
                //NOTE: the value bind is set in the onBeforeRender
            }
        },
        {
            xtype: 'panel',
            maxHeight: 300,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'multiselectfield',
                    reference: 'activeTenantsField',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.tenant,
                    displayField: 'label',
                    valueField: 'value',
                    hidden: true,
                    height: '100%',
                    autoEl: {
                        'data-testid': 'login-inputtenant'
                    },
                    bind: {
                        hidden: '{!tenantsany}',
                        store: '{tenants}',
                        fieldLabel: '{tenantLabel}'
                        //NOTE: the value bind is set in the onBeforeRender
                    }
                }
            ]
        },
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.login.buttons.login,
            formBind: true, //only enabled once the form is valid
            disabled: true,
            reference: 'loginbtn',
            itemId: 'loginbtn',
            ui: 'management-primary',
            cls: Ext.baseCSSPrefix + 'mt-2',
            autoEl: {
                'data-testid': 'login-btnlogin'
            },
            bind: {
                text: '{locales.buttons.login}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.login.buttons.login'
            },
            width: '100%'
        },
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.login.buttons.logout,
            reference: 'logoutbtn',
            itemId: 'logoutbtn',
            ui: 'secondary-action',
            cls: Ext.baseCSSPrefix + 'mt-1',
            autoEl: {
                'data-testid': 'login-btnlogout'
            },
            bind: {
                text: '{locales.buttons.logout}',
                hidden: '{hiddenfields.cancelbtn}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.login.buttons.logout'
            },
            width: '100%'
        }
    ]
});
