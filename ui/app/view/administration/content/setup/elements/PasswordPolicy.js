Ext.define('CMDBuildUI.view.administration.content.setup.elements.PasswordPolicy', {
    extend: 'Ext.form.Panel',

    alias: 'widget.administration-content-setup-elements-passwordpolicy',
    controller: 'administration-content-setup-elements-passwordpolicy',
    viewModel: {
        type: 'administration-content-setup-elements-passwordpolicy'
    },

    scrollable: 'y',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        borderBottom: 1,
        items: [{
            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            itemId: 'editBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },
            cls: 'administration-tool',
            bind: {
                hidden: "{!actions.view}"

            },
            autoEl: {
                'data-testid': 'administration-setup-view-editBtn'
            }
        }]
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        //margin: '5 5 5 5',
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
    }],

    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        padding: 0,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },
        defaults: {
            xtype: 'fieldcontainer',
            layout: 'column',
            defaults: {
                columnWidth: 0.5,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding
            }
        },
        items: [{
            // row 1
            items: [{
                // left column
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'enabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__enabled}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-enablepasswordchangemanagement_input'
                    }
                }]
            }]
        }, {
            // row 2
            items: [{
                // left column
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwddifferentusername,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwddifferentusername'
                    },
                    name: 'enabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__differentFromUsername}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwddifferentusername_input'
                    }
                }]
            }]
        }, {
            // row 2
            items: [{
                // left column
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwddifferentprevious,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwddifferentprevious'
                    },
                    name: 'enabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__differentFromPrevious}',
                        readOnly: '{actions.view}'
                    },
                    listeners: {
                        change: function (check, newValue, oldValue) {
                            try {
                                if (newValue) {
                                    this.up('form').down('#numberpreviouspasswordcannotreused').show();
                                } else {
                                    this.up('form').down('#numberpreviouspasswordcannotreused').hide();
                                }
                            } catch (error) { }
                        }
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwddifferentusername_input'
                    }
                }]
            }, {
                // right column
                items: [{
                    xtype: 'fieldcontainer',
                    itemId: 'numberpreviouspasswordcannotreused',
                    hidden: true,
                    bind: {
                        hidden: '{!theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__differentFromPrevious}'
                    },
                    items: [{
                        xtype: 'numberfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.numberpreviouspasswordcannotreused,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.numberpreviouspasswordcannotreused'
                        },
                        minValue: 0,
                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__differentFromPreviousCount}',
                            hidden: '{actions.view}'
                        },
                        autoEl: {
                            'data-testid': 'administration-systemconfig-passwordpolicies-numberpreviouspasswordcannotreused_input'
                        }
                    },
                    {
                        xtype: 'displayfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.numberpreviouspasswordcannotreused,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.numberpreviouspasswordcannotreused'
                        },
                        minValue: 0,
                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__differentFromPreviousCount}',
                            hidden: '{!actions.view}'
                        },
                        autoEl: {
                            'data-testid': 'administration-systemconfig-passwordpolicies-numberpreviouspasswordcannotreused_display'
                        }
                    }
                    ]
                }]
            }]
        }, {
            // row 3
            items: [{
                // left column
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwdminimumlength,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwdminimumlength'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__minLength}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwdminimumlength_display'
                    }
                }, {
                    xtype: 'numberfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwdminimumlength,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwdminimumlength'
                    },
                    minValue: 0,
                    maxValue: 20,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__minLength}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwdminimumlength_input'
                    }
                }]
            }]
        }, {
            // row 4
            items: [{
                // left column
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwdrequirelowercase,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwdrequirelowercase'
                    },
                    name: 'enabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__requireLowercase}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwdrequirelowercase_input'
                    }
                }]
            }, {
                // right column
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwdrequireuppercase,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwdrequireuppercase'
                    },
                    name: 'enabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__requireUppercase}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwdrequireuppercase_input'
                    }
                }]
            }]
        }, {
            // row 5
            items: [{
                // left column
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwdrequiredigit,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwdrequiredigit'
                    },
                    name: 'enabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__validation__DOT__requireDigit}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwdrequiredigit_input'
                    }
                }]
            }]
        }, {
            // row 6
            items: [{
                // left column
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwdmaxage,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwdmaxage'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__maxAgeDays}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwdmaxage_display'
                    }
                }, {
                    xtype: 'numberfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwdmaxage,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwdmaxage'
                    },
                    minValue: 0,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__maxAgeDays}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwdmaxage_input'
                    }
                }]
            }, {
                // right column
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwdforewarding,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwdforewarding'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__forewarningDays}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwdforewarding_display'
                    }
                }, {
                    xtype: 'numberfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.pwdforewarding,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.pwdforewarding'
                    },
                    minValue: 0,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__forewarningDays}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-pwdforewarding_input'
                    }
                }]
            }]
        }, {
            // row 6
            items: [{
                // left column
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxloginattempts,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxloginattempts'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__maxLoginAttempts__DOT__count}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-maxloginattempts_display'
                    }
                }, {
                    xtype: 'numberfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxloginattempts,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxloginattempts'
                    },
                    minValue: 0,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__maxLoginAttempts__DOT__count}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-maxloginattempts_input'
                    }
                }]
            }, {
                // right column
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxloginattemptswindow,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxloginattemptswindow'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__maxLoginAttempts__DOT__window}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-maxloginattemptswindow_display'
                    }
                }, {
                    xtype: 'numberfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxloginattemptswindow,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxloginattemptswindow'
                    },
                    minValue: 0,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__auth__DOT__maxLoginAttempts__DOT__window}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-maxloginattemptswindow_input'
                    }
                }]
            }]
        }, {
            // row 6
            items: [{
                // left column
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.defaultchangepasswordfirstlogin,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.defaultchangepasswordfirstlogin'
                    },
                    name: 'changePasswordRequiredForNewUser',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__core__DOT__user__DOT__changePasswordRequiredForNewUser}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-defaultchangepasswordfirstlogin_input'
                    }
                }]
            }]
        }]
    }, {
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        padding: 0,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.passwordmanagement,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.passwordmanagement'
        },
        defaults: {
            xtype: 'fieldcontainer',
            layout: 'column',
            defaults: {
                columnWidth: 0.5,
                padding: CMDBuildUI.util.helper.FormHelper.properties.padding
            }
        },
        items: [{
            // row 1
            items: [{
                // left column
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.allowpasswordchange,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.allowpasswordchange'
                    },
                    name: 'allowpasswordchange',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__password__DOT__change__DOT__enabled}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-passwordpolicies-allowpasswordchange_input'
                    }
                }]
            }]
        }]
    }]
});