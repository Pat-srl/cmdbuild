Ext.define('CMDBuildUI.view.administration.content.setup.elements.Bim', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.BimController',
        'CMDBuildUI.view.administration.content.setup.elements.BimModel'
    ],

    alias: 'widget.administration-content-setup-elements-bim',
    controller: 'administration-content-setup-elements-bim',
    viewModel: {
        type: 'administration-content-setup-elements-bim'
    },
    items: [
        {
            xtype: 'fieldset',
            ui: 'administration-formpagination',
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
                                    /********************* org.cmdbuild.bim.enabled **********************/
                                    xtype: 'checkbox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                    },
                                    name: 'isEnabled',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__enabled}',
                                        readOnly: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-systemconfig-bim-active_input'
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
                                    /********************* org.cmdbuild.bim.ifc2xkt.url **********************/
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                                    },
                                    name: 'bimUrl',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__ifc2xkt__DOT__url}',
                                        hidden: '{!actions.view}'
                                    }
                                },
                                {
                                    xtype: 'textfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.url,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.url'
                                    },
                                    name: 'bimUrl',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__ifc2xkt__DOT__url}',
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
                                    /********************* org.cmdbuild.bim.ifc2xkt.username **********************/
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.username,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.username'
                                    },
                                    name: 'bimUsername',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__ifc2xkt__DOT__username}',
                                        hidden: '{!actions.view}'
                                    }
                                },
                                {
                                    xtype: 'textfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.username,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.username'
                                    },
                                    name: 'bimUsername',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__ifc2xkt__DOT__username}',
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
                                    /********************* org.cmdbuild.bim.ifc2xkt.password **********************/
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.password,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.password'
                                    },
                                    name: 'bimPassword',
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
                                    name: 'bimPassword',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__bim__DOT__ifc2xkt__DOT__password}',
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
