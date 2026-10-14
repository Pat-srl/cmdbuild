Ext.define('CMDBuildUI.view.administration.content.setup.elements.Gis', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.GisController',
        'CMDBuildUI.view.administration.content.setup.elements.GisModel'
    ],
    alias: 'widget.administration-content-setup-elements-gis',
    controller: 'administration-content-setup-elements-gis',
    viewModel: {
        type: 'administration-content-setup-elements-gis'
    },

    items: [
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    /********************* org.cmdbuild.gis.enabled **********************/
                                    xtype: 'checkbox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                    },
                                    name: 'isEnabled',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__enabled}',
                                        readOnly: '{actions.view}'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    /********************* org.cmdbuild.gis.initialLatitude **********************/
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.initiallatitude,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.initiallatitude'
                                    },
                                    name: 'gisInitialLatitude',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__center__DOT__lat}',
                                        hidden: '{!actions.view}'
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.initiallatitude,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.initiallatitude'
                                    },
                                    name: 'gisInitialLatitude',
                                    hidden: true,
                                    allowExponential: false,
                                    allowDecimal: true,
                                    decimalPrecision: 8,
                                    decimalSeparator: '.',
                                    hideTrigger: true,
                                    minValue: -90,
                                    maxValue: 90,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__center__DOT__lat}',
                                        hidden: '{actions.view}'
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            style: {
                                paddingLeft: '15px'
                            },
                            items: [
                                {
                                    /********************* org.cmdbuild.gis.initialLongitude **********************/
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.initialongitude,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.initialongitude'
                                    },
                                    name: 'gisInitialLongitude',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__center__DOT__lon}',
                                        hidden: '{!actions.view}'
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.initialongitude,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.initialongitude'
                                    },
                                    name: 'gisInitialLongitude',
                                    hideTrigger: true,
                                    hidden: true,
                                    allowExponential: false,
                                    allowDecimal: true,
                                    decimalPrecision: 8,
                                    decimalSeparator: '.',
                                    minValue: -180,
                                    maxValue: 180,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__center__DOT__lon}',
                                        hidden: '{actions.view}'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    /********************* org.cmdbuild.gis.initialZoom **********************/
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.initialzoom,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.initialzoom'
                                    },
                                    name: 'gisInitialZoom',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__initialZoomLevel}',
                                        hidden: '{!actions.view}'
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    allowExponential: false,
                                    allowDecimal: false,
                                    minValue: 0,
                                    maxValue: 25,
                                    step: 1,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.initialzoom,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.initialzoom'
                                    },
                                    name: 'gisInitialZoom',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__initialZoomLevel}',
                                        hidden: '{actions.view}'
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            style: {
                                paddingLeft: '15px'
                            },
                            items: [
                                {
                                    /********************* org.cmdbuild.gis.keepZoomAndPosition.enabled **********************/
                                    xtype: 'checkbox',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.systemconfig.keepzoomandposition,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.systemconfig.keepzoomandposition'
                                    },
                                    name: 'keepZoomAndPosition',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__keepZoomAndPosition__DOT__enabled}',
                                        readOnly: '{actions.view}'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.converters,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.systemconfig.converters'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    /********************* org.cmdbuild.gis.dwg2dxf.url **********************/
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                                    },
                                    name: 'gisUrl',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__dwg2dxf__DOT__url}',
                                        hidden: '{!actions.view}'
                                    }
                                },
                                {
                                    xtype: 'textfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                                    },
                                    name: 'gisUrl',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__dwg2dxf__DOT__url}',
                                        hidden: '{actions.view}'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            items: [
                                {
                                    /********************* org.cmdbuild.gis.dwg2dxf.username **********************/
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.username,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.username'
                                    },
                                    name: 'gisUsername',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__dwg2dxf__DOT__username}',
                                        hidden: '{!actions.view}'
                                    }
                                },
                                {
                                    xtype: 'textfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.username,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.username'
                                    },
                                    name: 'gisUsername',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__dwg2dxf__DOT__username}',
                                        hidden: '{actions.view}'
                                    }
                                }
                            ]
                        },
                        {
                            columnWidth: 0.5,
                            style: {
                                paddingLeft: '15px'
                            },
                            items: [
                                {
                                    /********************* org.cmdbuild.gis.dwg2dxf.password **********************/
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.password,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.password'
                                    },
                                    name: 'gisPassword',
                                    readOnly: true,
                                    value: '********',
                                    hidden: true,
                                    bind: {
                                        hidden: '{!actions.view}'
                                    }
                                },
                                {
                                    xtype: 'passwordfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.password,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.password'
                                    },
                                    name: 'gisPassword',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__gis__DOT__dwg2dxf__DOT__password}',
                                        hidden: '{actions.view}'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
