Ext.define('CMDBuildUI.view.administration.home.widgets.activerecords.ActiveRecords', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.home.widgets.activerecords.ActiveRecordsController',
        'CMDBuildUI.view.administration.home.widgets.activerecords.ActiveRecordsModel'
    ],

    alias: 'widget.administration-home-widgets-activerecords-activerecords',
    controller: 'administration-home-widgets-activerecords-activerecords',
    viewModel: {
        type: 'administration-home-widgets-activerecords-activerecords'
    },

    title: CMDBuildUI.locales.Locales.administration.home.datastatistics,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.home.datastatistics'
    },

    ui: 'admindashboard',
    style: {
        marginBottom: '15px'
    },

    items: [
        {
            xtype: 'cartesian',
            reference: 'chart',
            width: '100%',
            height: 300,
            legend: {
                docked: 'top',
                padding: 5,
                type: 'dom'
            },
            bind: {
                store: '{dataStats}'
            },
            insetPadding: '10 40 10 20',
            innerPadding: 10,
            axes: [
                {
                    type: 'numeric',
                    fields: ['cards', 'procInstances', 'attachments', 'relations'],
                    position: 'left',
                    grid: true,
                    minimum: 0,
                    renderer: 'onDataStatsLabelRender'
                },
                {
                    type: 'category',
                    fields: 'year',
                    position: 'bottom',
                    grid: true
                }
            ],
            series: [
                {
                    type: 'line',
                    bind: {
                        title: '{seriesTitles.cards}'
                    },
                    xField: 'year',
                    yField: 'cards',
                    marker: true,
                    highlightCfg: {
                        scaling: 1.5
                    },
                    tooltip: {
                        trackMouse: true,
                        renderer: 'onDataStatsTooltipRender'
                    }
                },
                {
                    type: 'line',
                    bind: {
                        title: '{seriesTitles.precessinstances}'
                    },
                    xField: 'year',
                    yField: 'procInstances',
                    marker: true,
                    highlightCfg: {
                        scaling: 1.5
                    },
                    tooltip: {
                        trackMouse: true,
                        renderer: 'onDataStatsTooltipRender'
                    }
                },
                {
                    type: 'line',
                    bind: {
                        title: '{seriesTitles.attachments}'
                    },
                    xField: 'year',
                    yField: 'attachments',
                    marker: true,
                    highlightCfg: {
                        scaling: 1.5
                    },
                    tooltip: {
                        trackMouse: true,
                        renderer: 'onDataStatsTooltipRender'
                    }
                },
                {
                    type: 'line',
                    bind: {
                        title: '{seriesTitles.relations}'
                    },
                    xField: 'year',
                    yField: 'relations',
                    marker: true,
                    highlightCfg: {
                        scaling: 1.5
                    },
                    tooltip: {
                        trackMouse: true,
                        renderer: 'onDataStatsTooltipRender'
                    }
                }
            ]
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            padding: '25 0',
            items: [
                {
                    xtype: 'tbfill'
                },
                {
                    xtype: 'tbitem',
                    bind: {
                        html: '{lockedItemsMessage}'
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.home.unlockall,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.home.unlockall'
                    },
                    handler: function (button) {
                        CMDBuildUI.util.administration.helper.AjaxHelper.unlockAllCards(button).then(function () {
                            if (!button.destroyed) {
                                button.lookupViewModel().getLockedCounter();
                            }
                        });
                    }
                }
            ],
            hidden: true,
            bind: {
                hidden: '{!theSession.rolePrivileges.admin_all}'
            }
        }
    ]
});
