Ext.define(
    'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.DisabledAllElements',
    {
        extend: 'Ext.panel.Panel',
        alias: 'widget.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledallelements',
        ui: 'administration-formpagination',
        viewModel: {},
        config: {
            theGroup: {}
        },
        bind: {
            theGroup: '{theGroup}'
        },
        items: [
            {
                xtype: 'fieldset',
                ui: 'administration-formpagination',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.enabledallitems,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.enabledallitems'
                },
                collapsible: true,
                items: [
                    {
                        xtype: 'checkboxgroup',
                        columns: 1,
                        vertical: true,
                        bind: {
                            readOnly: '{actions.view}'
                        },
                        items: [
                            {
                                boxLabel: CMDBuildUI.locales.Locales.administration.navigation.classes,
                                localized: {
                                    boxLabel: 'CMDBuildUI.locales.Locales.administration.navigation.classes'
                                },
                                name: '_rp_class_access',
                                bind: {
                                    value: '{theGroup._rp_class_access}'
                                }
                            },
                            {
                                boxLabel: CMDBuildUI.locales.Locales.administration.navigation.processes,
                                localized: {
                                    boxLabel: 'CMDBuildUI.locales.Locales.administration.navigation.processes'
                                },
                                name: '_rp_process_access',
                                bind: {
                                    value: '{theGroup._rp_process_access}'
                                }
                            },
                            {
                                boxLabel: CMDBuildUI.locales.Locales.administration.navigation.views,
                                localized: {
                                    boxLabel: 'CMDBuildUI.locales.Locales.administration.navigation.views'
                                },
                                name: '_rp_dataview_access',
                                bind: {
                                    value: '{theGroup._rp_dataview_access}'
                                }
                            },
                            {
                                boxLabel: CMDBuildUI.locales.Locales.administration.navigation.dashboards,
                                localized: {
                                    boxLabel: 'CMDBuildUI.locales.Locales.administration.navigation.dashboards'
                                },
                                name: '_rp_dashboard_access',
                                bind: {
                                    value: '{theGroup._rp_dashboard_access}'
                                }
                            },
                            {
                                boxLabel: CMDBuildUI.locales.Locales.administration.navigation.reports,
                                localized: {
                                    boxLabel: 'CMDBuildUI.locales.Locales.administration.navigation.reports'
                                },
                                name: '_rp_report_access',
                                bind: {
                                    value: '{theGroup._rp_report_access}'
                                }
                            },
                            {
                                boxLabel: CMDBuildUI.locales.Locales.administration.navigation.custompages,
                                localized: {
                                    boxLabel: 'CMDBuildUI.locales.Locales.administration.navigation.custompages'
                                },
                                name: '_rp_custompages_access',
                                bind: {
                                    value: '{theGroup._rp_custompages_access}'
                                }
                            },
                            {
                                boxLabel: CMDBuildUI.locales.Locales.administration.navigation.dependencytrees,
                                localized: {
                                    boxLabel: 'CMDBuildUI.locales.Locales.administration.navigation.dependencytrees'
                                },
                                name: '_rp_dependencytree_access',
                                bind: {
                                    value: '{theGroup._rp_dependencytree_access}'
                                }
                            }
                        ]
                    }
                ]
            }
        ]
    }
);
