Ext.define('CMDBuildUI.view.administration.content.setup.MultiTenant', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.MultiTenantController',
        'CMDBuildUI.view.administration.content.setup.elements.MultiTenantModel'
    ],
    alias: 'widget.administration-content-setup-elements-multitenant',
    controller: 'administration-content-setup-elements-multitenant',
    viewModel: {
        type: 'administration-content-setup-elements-multitenant'
    },

    items: [{
        xtype: 'container',
        hidden: true,
        bind: {
            hidden: '{actions.view}',
            html: '{messageinfo}'
        },
        autoEl: {
            'data-testid': 'administration-systemconfig-multitenant-messageinfo'
        },
        margin: 10,
        ui: 'messageinfo'
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },
        items: [{
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'enabled',
                    bind: {
                        value: '{multiTenantEnabled}',
                        readOnly: '{actions.view}',
                        disabled: '{multitenantFieldsDisabled}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-multitenant-active_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            hidden: true,
            bind: {
                hidden: '{!multiTenantEnabled}'
            },
            items: [{
                columnWidth: 0.5,
                items: [{
                        xtype: 'textfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.multitenantname,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.multitenantname'
                        },
                        name: 'multitenant_name',
                        hidden: true,
                        bind: {
                            hidden: '{actions.view || !multiTenantEnabled}',
                            value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__name}'
                        },
                        autoEl: {
                            'data-testid': 'administration-systemconfig-multitenant-multitenantname_input'
                        }
                    },
                    {
                        xtype: 'displayfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.multitenantname,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.multitenantname'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{actions.edit || !multiTenantEnabled}',
                            value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__name}'
                        },
                        autoEl: {
                            'data-testid': 'administration-systemconfig-multitenant-multitenantname_display'
                        }
                    }
                ]
            }]
        }, {
            layout: 'column',
            hidden: true,
            bind: {
                hidden: '{!multiTenantEnabled}'
            },
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'combo',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.configurationmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.configurationmode'
                    },

                    queryMode: 'local',
                    allowBlank: false,
                    displayField: 'label',
                    valueField: 'value',
                    hidden: true,
                    bind: {
                        store: '{getConfigurationModeStore}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}',
                        hidden: '{multitenantConfigurationModeComboHidden}',
                        disabled: '{multitenantFieldsDisabled}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-multitenant-configurationmode_input'
                    }
                }, {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.configurationmode,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.configurationmode'
                    },
                    allowBlank: false,
                    displayField: 'label',
                    valueField: 'value',
                    hidden: true,
                    bind: {
                        hidden: '{multitenantConfigurationModeDisplayHidden}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__mode}'
                    },
                    renderer: function (value) {
                        switch (value) {
                            case 'CMDBUILD_CLASS':
                                return CMDBuildUI.locales.Locales.administration.localizations.class;
                            case 'DB_FUNCTION':
                                return CMDBuildUI.locales.Locales.administration.common.labels.funktion;
                            default:
                                return '';
                        }
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-multitenant-configurationmode_display'
                    }
                }]
            }]
        }, {
            layout: 'column',
            hidden: true,
            bind: {
                hidden: '{isFunctionMode}'
            },
            listeners: {
                show: function () {
                    var tenantClassInput = this.down('#tenantClass_input');
                    tenantClassInput.allowBlank = false;
                    this.up('form').isValid();
                },
                hide: function () {
                    var tenantClassInput = this.down('#tenantClass_input');
                    tenantClassInput.allowBlank = true;
                    this.up('form').isValid();
                }
            },
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'combo',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.class,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.class'
                    },
                    itemId: 'tenantClass_input',
                    clearFilterOnBlur: true,
                    queryMode: 'local',
                    forceSelection: true,
                    displayField: 'description',
                    valueField: 'name',
                    bind: {
                        store: '{getFilteredClasses}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__tenantClass}',
                        hidden: '{multitenantConfigurationClassComboHidden}',
                        readOnly: '{actions.view}',
                        disabled: '{multitenantFieldsDisabled}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-multitenant-tenantclass_input'
                    }
                }, {
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.localizations.class,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.localizations.class'
                    },
                    bind: {
                        hidden: '{multitenantConfigurationClassDisplayHidden}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__multitenant__DOT__tenantClass}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-multitenant-tenantclass_display'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                    },
                    value: '_cm3_multitenant_get',
                    hidden: true,
                    bind: {
                        hidden: '{!isFunctionMode}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-multitenant-tenantfunction_display'
                    }
                }]
            }]
        }]
    }]
});