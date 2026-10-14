Ext.define('CMDBuildUI.view.dashboards.common.components.basechart.BaseChart', {
    extend: 'CMDBuildUI.view.dashboards.common.components.Component',

    requires: [
        'CMDBuildUI.view.dashboards.common.components.basechart.BaseChartController',
        'CMDBuildUI.view.dashboards.common.components.basechart.BaseChartModel'
    ],

    controller: 'dashboards-components-basechart-basechart',
    viewModel: {
        type: 'dashboards-components-basechart-basechart'
    },

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    customAttributes: [],

    customActions: [
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
            text: CMDBuildUI.locales.Locales.dashboards.tools.openinpopup,
            handler: 'actionOpenInPopupHandler',
            scope: 'controller',
            localized: {
                text: 'CMDBuildUI.locales.Locales.dashboards.tools.openinpopup'
            }
        },
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            text: CMDBuildUI.locales.Locales.dashboards.tools.reload,
            handler: 'actionReloadHandler',
            scope: 'controller',
            localized: {
                text: 'CMDBuildUI.locales.Locales.dashboards.tools.reload'
            }
        },
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
            text: CMDBuildUI.locales.Locales.dashboards.tools.gridshow,
            handler: 'actionShowHideTableHandler',
            scope: 'controller',
            localized: {
                text: 'CMDBuildUI.locales.Locales.dashboards.tools.gridshow'
            }
        },
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('file-download', 'solid'),
            text: CMDBuildUI.locales.Locales.dashboards.tools.downloaddata,
            handler: 'actionDownloadDataHandler',
            scope: 'controller',
            localized: {
                text: 'CMDBuildUI.locales.Locales.dashboards.tools.downloaddata'
            }
        },
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
            text: CMDBuildUI.locales.Locales.dashboards.tools.download,
            handler: 'actionDownloadChartHandler',
            scope: 'controller',
            localized: {
                text: 'CMDBuildUI.locales.Locales.dashboards.tools.download'
            }
        }
    ],

    items: [
        {
            xtype: 'fieldset',
            ui: 'managementdashboard',
            collapsible: true,
            collapsed: true,
            title: CMDBuildUI.locales.Locales.dashboards.filters,
            cls: Ext.baseCSSPrefix + 'dashboard-params-fieldset',
            listeners: {
                expand: function (fieldset) {
                    const component = fieldset.up('panel');
                    if (component) {
                        const formHeight = fieldset.getHeight();
                        component.setHeight(component.getHeight() + formHeight);
                    }
                },
                collapse: function (fieldset) {
                    const component = fieldset.up('panel');
                    if (component) {
                        component.setHeight(component.getComponentHeight());
                    }
                }
            },
            localized: {
                title: 'CMDBuildUI.locales.Locales.dashboards.filters'
            },
            bind: {
                hidden: '{form.hidden}'
            },
            items: [
                {
                    xtype: 'form',
                    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
                    items: [
                        {
                            xtype: 'container',
                            margin: '14 0 14 0',
                            layout: {
                                type: 'hbox',
                                pack: 'end'
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    formBind: true,
                                    ui: 'management-primary-outline-small',
                                    text: CMDBuildUI.locales.Locales.common.actions.apply,
                                    itemId: 'applyBtn',
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.common.actions.apply'
                                    }
                                }
                            ]
                        }
                    ],
                    hidden: false,
                    itemId: 'paramsForm',
                    cls: Ext.baseCSSPrefix + 'dashboard-params-form',
                    bind: {
                        hidden: '{form.hidden}'
                    }
                }
            ]
        },
        {
            xtype: 'displayfield',
            cls: Ext.baseCSSPrefix + 'dashboard-applied-params',
            htmlEncode: false,
            bind: {
                value: '{parametersAsText}',
                hidden: '{!haveParameters}'
            }
        },
        { xtype: 'container', itemId: 'chartContainer', layout: 'fit', flex: 1 },
        {
            xtype: 'grid',
            columns: [],
            hidden: true,
            itemId: 'dataTable',
            maxHeight: 200,
            viewConfig: {
                enableTextSelection: true,
                markDirty: false
            },
            bind: {
                store: '{records}',
                hidden: '{grid.hidden}'
            }
        }
    ],

    /**
     * Initialize chart
     */
    initChart: function () {
        Ext.raise('Initialize chart function not implemented.');
    },

    /**
     * Add chart into container
     *
     * @param {Ext.Component} config
     */
    addChart: function (config) {
        const chartcontainer = this.down('#chartContainer');
        chartcontainer.add(config);
    },

    /**
     * Returns array of CMDBuild attributes definition
     *
     * @returns Object[]
     */
    getCustomAttributes: function () {
        return [];
    },

    /**
     * Returns custom attributes
     *
     * @returns Ext.data.Model[]
     */
    getGridCustomAttributes: function () {
        let attributes = [];
        if (this.getCustomAttributes()) {
            attributes = this.getCustomAttributes().map(function (attribute) {
                return Ext.create('Ext.data.Model', attribute);
            });
        }
        return attributes;
    }
});
