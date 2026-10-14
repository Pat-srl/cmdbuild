Ext.define('CMDBuildUI.view.administration.home.widgets.userstats.Stats', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.home.widgets.userstats.StatsController',
        'CMDBuildUI.view.administration.home.widgets.userstats.StatsModel'
    ],

    alias: 'widget.administration-home-widgets-userstats-stats',
    controller: 'administration-home-widgets-userstats-stats',
    viewModel: {
        type: 'administration-home-widgets-userstats-stats'
    },

    title: CMDBuildUI.locales.Locales.administration.home.usergroupstatistic,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.home.usergroupstatistic'
    },

    layout: {
        type: 'hbox'
    },
    style: {
        marginBottom: '30px'
    },
    ui: 'admindashboard',

    tools: [
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
            itemId: 'addUsersTool',
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.add,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
            },
            hidden: true,
            bind: {
                hidden: '{!theSession.rolePrivileges.admin_all}'
            }
        }
    ],

    items: [
        {
            xtype: 'cartesian',
            itemId: 'statsChart',
            flex: 1,
            height: 200,
            insetPadding: 20,
            margin: '25 0 0 0',
            theme: 'admindashboard',
            flipXY: true,
            animation: {
                easing: 'easeOut',
                duration: 500
            },
            bind: {
                store: '{userStats}'
            },
            axes: [
                {
                    type: 'category',
                    position: 'left',
                    fields: 'label',
                    grid: true
                },
                {
                    type: 'numeric',
                    position: 'bottom',
                    fields: 'count',
                    grid: true,
                    renderer: function (axis, label, layoutContext) {
                        return Number.isInteger(label) ? label : '';
                    }
                }
            ],
            series: [
                {
                    type: 'bar',
                    xField: 'label',
                    yField: 'count',
                    style: {
                        opacity: 0.8,
                        minGapWidth: 5
                    },
                    highlightCfg: {
                        opacity: 0.95
                    },
                    renderer: function (sprite, config, rendererData, index) {
                        var record = rendererData.store.getAt(index);
                        if (record.get('type') === 'standarda') {
                            return {
                                fillStyle: '#005CA9',
                                strokeStyle: Ext.util.Color.fromString('#005CA9')
                                    .createDarker(this.darkerStrokeRatio)
                                    .toString()
                            };
                        }
                    },
                    label: {
                        field: 'count',
                        orientation: 'horizontal',
                        display: 'outside',
                        font: '12px'
                    }
                }
            ]
        },
        {
            xtype: 'grid',
            margin: '15 0 0 12',
            disableSelection: true,
            flex: 1,
            forceFit: true,
            bind: {
                store: '{userGroupStats}'
            },
            columns: [
                {
                    hideable: false,
                    menuDisabled: true,
                    dataIndex: 'label'
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    dataIndex: 'countActive',
                    align: 'right'
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.home.nonactive,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.home.nonactive'
                    },
                    dataIndex: 'countInactive',
                    align: 'right'
                }
            ]
        }
    ]
});
