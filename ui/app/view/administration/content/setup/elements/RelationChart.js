
Ext.define('CMDBuildUI.view.administration.content.setup.elements.RelationChart', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.RelationChartController',
        'CMDBuildUI.view.administration.content.setup.elements.RelationChartModel'
    ],
    alias: 'widget.administration-content-setup-elements-relationchart',
    controller: 'administration-content-setup-elements-relationchart',
    viewModel: {
        type: 'administration-content-setup-elements-relationchart'
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
                    /********************* org.cmdbuild.bim.enabled **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__graph__DOT__enabled}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-relationchart-active_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    /********************* org.cmdbuild.bim.enabled **********************/
                    xtype: 'checkbox',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.enablenodetooltip,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.enablenodetooltip'
                    },
                    name: 'isEnabled',
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__graph__DOT__enableNodeTooltip}',
                        readOnly: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-relationchart-enablenodetooltip_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                items: [{
                    columnWidth: 0.5,
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
                    },
                    items: [CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                        columnWidth: 0.8,
                        alt: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor,
                        localized: {
                            alt: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.fillcolor'
                        },
                        bind: {
                            value: '{theSetup.org__DOT__cmdbuild__DOT__graph__DOT__edgeColor}',
                            hidden: '{actions.view}'
                        }
                    })]
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.clusteringthreshold,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.clusteringthreshold'
                    },
                    bind: {
                        hidden: '{!actions.view}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__graph__DOT__clusteringThreshold}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-relationchart-clusteringthreshold_display'
                    }
                }, {
                    xtype: 'numberfield',
                    name: 'clusteringThreshold',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.clusteringthreshold,
                    minValue: 2,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.clusteringthreshold'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__graph__DOT__clusteringThreshold}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-relationchart-clusteringthreshold_input'
                    }
                }]
            }]
        }, {
            layout: 'column',
            items: [{
                columnWidth: 0.5,
                style: {
                    paddingRight: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.baselevel,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.baselevel'
                    },
                    bind: {
                        hidden: '{!actions.view}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__graph__DOT__baseLevel}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-relationchart-baselevel_display'
                    }
                }, {
                    xtype: 'numberfield',
                    name: 'baseLevel',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.baselevel,
                    minValue: 1,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.baselevel'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__graph__DOT__baseLevel}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-relationchart-baselevel_input'
                    }
                }]
            }, {
                columnWidth: 0.5,
                style: {
                    paddingLeft: '15px'
                },
                items: [{
                    xtype: 'displayfield',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.stepradius,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.stepradius'
                    },
                    bind: {
                        hidden: '{!actions.view}',
                        value: '{theSetup.org__DOT__cmdbuild__DOT__graph__DOT__stepRadius}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-relationchart-stepradius_display'
                    }
                }, {
                    xtype: 'numberfield',
                    name: 'stepRadius',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.stepradius,
                    minValue: 1,
                    maxValue: 1000,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.stepradius'
                    },
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__graph__DOT__stepRadius}',
                        hidden: '{actions.view}'
                    },
                    autoEl: {
                        'data-testid': 'administration-systemconfig-relationchart-stepradius_input'
                    }
                }]
            }]
        }]
    }]
});
