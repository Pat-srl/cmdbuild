Ext.define('CMDBuildUI.view.administration.content.setup.elements.Workflow', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.WorkflowController'        
    ],
    alias: 'widget.administration-content-setup-elements-workflow',
    controller: 'administration-content-setup-elements-workflow',
    viewModel: {       
    },

    items: [{
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
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__enabled}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-workflow-active_input'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: []
            }]

        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.enableattachmenttoclosedactivities,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.enableattachmenttoclosedactivities'
                    },
                    name: 'enableAddAttachmentOnClosedActivities',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__enableAddAttachmentOnClosedActivities}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-workflow-enableattachmenttoclosedactivities_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.usercandisable,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.usercandisable'
                    },
                    name: 'userCanDisable',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__userCanDisable}',
                        readOnly: '{actions.view}'
                    },                    
                    autoEl: {
                        'data-testid': 'administration-systemconfig-workflow-usercandisable_input'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.hidesavebutton,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.hidesavebutton'
                    },
                    name: 'hideSaveButton',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__hideSaveButton}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-workflow-hidesavebutton_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.dafaultjobusername,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.dafaultjobusername'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__jobs__DOT__defaultUser}',
                        hidden: '{!actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-workflow-dafaultjobusername_display'
                    }
                }, {
                    xtype: 'textfield',
                    name: 'serviceUrl',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.dafaultjobusername,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.dafaultjobusername'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__workflow__DOT__jobs__DOT__defaultUser}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-workflow-dafaultjobusername_input'
                    }
                }]
            }]
        }]
    }]
});