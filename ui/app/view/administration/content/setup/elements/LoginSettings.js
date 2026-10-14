Ext.define('CMDBuildUI.view.administration.content.setup.elements.LoginSettings', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.LoginSettingsController',
        'CMDBuildUI.view.administration.content.setup.elements.LoginSettingsModel'
    ],

    alias: 'widget.administration-content-setup-elements-loginsettings',
    controller: 'administration-content-setup-elements-loginsettings',
    viewModel: {
        type: 'administration-content-setup-elements-loginsettings'
    },

    scrollable: 'y',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    dockedItems: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'north',
            borderBottom: 1,
            items: [
                {
                    xtype: 'button',
                    itemId: 'spacer',
                    style: {
                        visibility: 'hidden'
                    }
                },
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'tool',
                    itemId: 'editBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                    localized: {
                        tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
                    },
                    cls: 'administration-tool',
                    bind: {
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-setup-view-editBtn'
                    }
                }
            ]
        },
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
        }
    ],
    items: [
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            padding: 0,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
            },
            defaults: {
                xtype: 'fieldcontainer',
                layout: 'column',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                defaults: {
                    columnWidth: 0.5
                }
            },
            items: [
                {
                    /********************* org.cmdbuild.auth.helpText **********************/
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.loginpagetext,
                    items: [
                        CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                            columnWidth: 1,
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__helpText}',
                                hidden: '{actions.view}'
                            }
                        }),
                        {
                            // view
                            xtype: 'displayfield',
                            columnWidth: 1,
                            hidden: true,
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__helpText}',
                                hidden: '{!actions.view}'
                            }
                        }
                    ]
                },
                {
                    /********************* org.cmdbuild.auth.sso.redirect.enabled **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.disableautologinsso,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.disableautologinsso'
                    },
                    name: 'isDisabled',
                    bind: {
                        value: '{ssoautoLogin}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-login-disable-ssoautologin'
                    }
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            padding: 0,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.operatorinfo,
            localized: {
                title: ' CMDBuildUI.locales.Locales.administration.systemconfig.operatorinfo,'
            },
            defaults: {
                xtype: 'fieldcontainer',
                layout: 'column',
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                defaults: {
                    columnWidth: 0.5
                }
            },
            items: [
                {
                    items: [
                        {
                            xtype: 'combo',
                            itemId: 'modeConfig',
                            allowBlank: false,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.mode,
                            displayField: 'label',
                            valueField: 'value',
                            queryMode: 'local',
                            hidden: true,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.mode'
                            },
                            bind: {
                                store: '{modeConfigs}',
                                value: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__mode}',
                                hidden: '{actions.view}'
                            },
                            autoEl: {
                                'data-testid': 'administration-systemconfig-setup-modeConfig_input'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.mode,
                            hidden: true,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.mode'
                            },
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__mode}',
                                hidden: '{!actions.view}'
                            },
                            renderer: function (value) {
                                const me = this;
                                const vm = me.lookupViewModel();
                                const store = vm.getStore('modeConfigs');
                                if (store) {
                                    const record = store.findRecord('value', value);
                                    if (record) {
                                        return record.get('label');
                                    }
                                }
                                return value;
                            },
                            autoEl: {
                                'data-testid': 'administration-systemconfig-setup-modeConfig_display'
                            }
                        }
                    ]
                },
                {
                    items: [
                        {
                            xtype: 'combo',
                            itemId: 'operatorClass',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.operatorclass,
                            displayField: 'label',
                            valueField: 'value',
                            queryMode: 'local',
                            hidden: true,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.operatorclass'
                            },
                            bind: {
                                store: '{operatorClasses}',
                                value: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__class}',
                                hidden: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__mode !== "CMDBUILD_CLASS" || actions.view}'
                            },
                            autoEl: {
                                'data-testid': 'administration-systemconfig-setup-operatorClass_input'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            itemId: 'operatorClassDisplay',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.operatorclass,
                            hidden: true,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.operatorclass'
                            },
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__class}',
                                hidden: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__mode !== "CMDBUILD_CLASS" || !actions.view}'
                            },
                            renderer: function (value) {
                                const me = this;
                                const vm = me.lookupViewModel();
                                const store = vm.getStore('operatorClasses');
                                if (store) {
                                    const record = store.findRecord('value', value);
                                    if (record) {
                                        return record.get('label');
                                    }
                                }
                                return value;
                            },
                            autoEl: {
                                'data-testid': 'administration-systemconfig-setup-operatorClass_display'
                            }
                        },
                        {
                            xtype: 'combo',
                            itemId: 'matchingAttribute',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.matchingattribute,
                            displayField: 'label',
                            valueField: 'value',
                            queryMode: 'local',
                            hidden: true,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.matchingattribute'
                            },
                            bind: {
                                store: '{matchingAttributes}',
                                value: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__attribute}',
                                hidden: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__mode !== "CMDBUILD_CLASS" || actions.view}'
                            },
                            autoEl: {
                                'data-testid': 'administration-systemconfig-setup-matchingAttribute_input'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            itemId: 'matchingAttributeDisplay',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.matchingattribute,
                            hidden: true,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.matchingattribute'
                            },
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__attribute}',
                                hidden: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__mode !== "CMDBUILD_CLASS" || !actions.view}'
                            },
                            renderer: function (value) {
                                const me = this;
                                const vm = me.lookupViewModel();
                                const store = vm.getStore('matchingAttributes');
                                if (store) {
                                    const record = store.findRecord('value', value);
                                    if (record) {
                                        return record.get('label');
                                    }
                                }
                                return value;
                            },
                            autoEl: {
                                'data-testid': 'administration-systemconfig-setup-matchingAttribute_display'
                            }
                        }
                    ]
                },
                {
                    items: [
                        {
                            xtype: 'combo',
                            itemId: 'matchingUserProperty',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.matchinguserproperty,
                            hidden: true,
                            displayField: 'label',
                            valueField: 'value',
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.matchinguserproperty'
                            },
                            bind: {
                                store: '{matchingUserProperties}',
                                value: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__userProperty}',
                                hidden: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__mode !== "CMDBUILD_CLASS" || actions.view}'
                            },
                            autoEl: {
                                'data-testid': 'administration-systemconfig-setup-matchingUserProperty_input'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            itemId: 'matchingUserPropertyDisplay',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.matchinguserproperty,
                            hidden: true,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.systemconfig.matchinguserproperty'
                            },
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__userProperty}',
                                hidden: '{theSetup.org__DOT__cmdbuild__DOT__session__DOT__userMapping__DOT__mode !== "CMDBUILD_CLASS" || !actions.view}'
                            },
                            renderer: function (value) {
                                const me = this;
                                const vm = me.lookupViewModel();
                                const store = vm.getStore('matchingUserProperties');
                                if (store) {
                                    const record = store.findRecord('value', value);
                                    if (record) {
                                        return record.get('label');
                                    }
                                }
                                return value;
                            },
                            autoEl: {
                                'data-testid': 'administration-systemconfig-setup-matchingUserProperty_display'
                            }
                        }
                    ]
                }
            ]
        }
    ]
});
