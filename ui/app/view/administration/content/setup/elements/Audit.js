Ext.define('CMDBuildUI.view.administration.content.setup.elements.Audit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.AuditController',
        'CMDBuildUI.view.administration.content.setup.elements.AuditModel'
    ],

    alias: 'widget.administration-content-setup-elements-audit',
    controller: 'administration-content-setup-elements-audit',
    viewModel: {
        type: 'administration-content-setup-elements-audit'
    },

    layout: 'fit',
    scrollable: 'y',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        xtype: "fieldset",
        ui: 'administration-formpagination',
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
        },
        layout: 'column',
        columnWidth: 1,
        items: [{
            layout: 'column',
            columnWidth: 1,
            items: [{
                /********************* org.cmdbuild.audit.enabled **********************/
                xtype: 'checkbox',
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                },
                name: 'isEnabled',
                bind: {
                    value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__enabled}',
                    readOnly: '{actions.view}'
                },
                autoEl: {
                    'data-testid': 'administration-systemconfig-audit-active_input'
                }
            },
            /********************* org.cmdbuild.audit.logTrackingMode *******************/
            CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('logTrackingMode', {
                logTrackingMode: {
                    fieldcontainer: {
                        layout: 'column',
                        columnWidth: 0.5,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.logtrackingmode,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.logtrackingmode'
                        }
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__logTrackingMode}',
                        readOnly: '{actions.view}',
                        store: '{trackingModesStore}'
                    }
                }
            })
            ]
        }, {
            columnWidth: 1,
            layout: 'column',
            items: [
                /******************************* org.cmdbuild.audit.exclude *****************************/
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('exclude', {
                    exclude: {
                        fieldcontainer: {
                            layout: 'column',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.gates.exclude,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.gates.exclude'
                            }
                        },

                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__exclude}',
                            readOnly: '{actions.view}'
                        }
                    }
                }),

                /******************************* org.cmdbuild.audit.include *****************************/
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('include', {
                    include: {
                        fieldcontainer: {
                            layout: 'column',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.include,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.include'
                            }
                        },

                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__include}',
                            readOnly: '{actions.view}'
                        }
                    }
                })
            ]
        }, {
            layout: 'column',
            columnWidth: 1,
            items: [{
                layout: 'column',
                columnWidth: 0.5,
                items: [{
                    /********************* org.cmdbuild.audit.includePayload **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.includepayload,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.includepayload'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__includepayload}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-audit-includePayload_input'
                    }

                }]
            }, {
                layout: 'column',
                columnWidth: 0.5,
                items: [{
                    /********************* org.cmdbuild.audit.filterPayload **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.filterpayload,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.filterpayload'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__filterPayload}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-audit-filterPayload_input'
                    }
                }]
            }]

        }, {
            layout: 'column',
            columnWidth: 1,
            items: [{
                layout: 'column',
                columnWidth: 0.5,
                items: [{
                    /****************** org.cmdbuild.audit.includeHeaders **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.includeheaders,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.includeheaders'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__includeHeaders}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-audit-includeHeaders_input'
                    }

                }]
            }, {
                layout: 'column',
                columnWidth: 0.5,
                items: [{
                    /****************** org.cmdbuild.audit.includeResponse **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.includeresponse,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.includeresponse'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__includeResponse}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-audit-includeResponse_input'
                    }

                }]
            }]

        }, {

            layout: 'column',
            columnWidth: 1,
            items: [{
                layout: 'column',
                columnWidth: 0.5,
                items: [{
                    /********************* org.cmdbuild.audit.includeTcpDump **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.includetcpdump,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.includetcpdump'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__includeTcpDump}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-audit-includeTcpDump_input'
                    }
                }]
            },
            /*********************** org.cmdbuild.audit.maxPayloadSize ***************************/
            CMDBuildUI.util.administration.helper.FieldsHelper.getCommonNumberfieldInput('maxPayloadSize', {
                maxPayloadSize: {
                    fieldcontainer: {
                        layout: 'column',
                        columnWidth: 0.5,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxpayloadsize,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxpayloadsize'
                        }
                    },
                    step: 0,
                    minValue: 0,
                    hideTrigger: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__audit__DOT__maxPayloadSize}'
                    }
                }
            })
            ]

        }]
    }],
    dockedItems: [{
        dock: 'top',
        xtype: 'components-administration-toolbars-formtoolbar',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: {
                disabled: true,
                bind: {
                    disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
                }
            }
        }, 'audit', 'theSetup',
            [],
            [])
    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true, {
            testid: 'logretention'
        }, {
            testid: 'logretention'
        })
    }]
});