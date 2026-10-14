Ext.define('CMDBuildUI.view.administration.home.widgets.spaceusage.TablesGrid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.home.widgets.spaceusage.TablesGridController',
        'CMDBuildUI.view.administration.home.widgets.spaceusage.TablesGridModel'
    ],
    alias: 'widget.administration-home-widgets-spaceusage-tablesgrid',
    controller: 'administration-home-widgets-spaceusage-tablesgrid',
    viewModel: {
        type: 'administration-home-widgets-spaceusage-tablesgrid'
    },
    title: CMDBuildUI.locales.Locales.administration.home.tables,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.home.tables'
    },
    style: {
        marginBottom: '30px'
    },
    disableSelection: true,
    ui: 'admindashboard',
    forceFit: 1,
    bind: {
        store: '{tablesStatsLight}'
    },
    height: '480',
    minHeight: '480',
    maxHeight: '480',
    tbar: [],
    columns: [
        {
            dataIndex: 'table',
            text: CMDBuildUI.locales.Locales.administration.home.table,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.home.table'
            },
            flex: 1
        },
        {
            dataIndex: '_type_description',
            text: CMDBuildUI.locales.Locales.administration.common.labels.type,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
            },
            hidden: true,
            width: 120
        },
        {
            text: CMDBuildUI.locales.Locales.administration.home.items,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.home.items'
            },
            columns: [
                {
                    dataIndex: 'active',
                    text: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    width: 90,
                    align: 'right'
                },
                {
                    dataIndex: 'active_size',
                    text: CMDBuildUI.locales.Locales.administration.home.activesize,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.home.activesize'
                    },
                    width: 90,
                    align: 'right',
                    hidden: true
                },
                {
                    dataIndex: 'updated',
                    text: CMDBuildUI.locales.Locales.administration.home.updated,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.home.updated'
                    },
                    width: 90,
                    align: 'right'
                },
                {
                    dataIndex: 'updated_size',
                    text: CMDBuildUI.locales.Locales.administration.home.updatedsize,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.home.updatedsize'
                    },
                    width: 90,
                    align: 'right',
                    hidden: true
                },
                {
                    dataIndex: 'deleted',
                    text: CMDBuildUI.locales.Locales.administration.home.deleted,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.home.deleted'
                    },
                    width: 90,
                    align: 'right'
                },
                {
                    dataIndex: 'deleted_size',
                    text: CMDBuildUI.locales.Locales.administration.home.deletedsize,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.home.deletedsize'
                    },
                    width: 90,
                    align: 'right',
                    hidden: true
                },
                {
                    dataIndex: 'total',
                    text: CMDBuildUI.locales.Locales.administration.home.total,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.home.total'
                    },
                    width: 90,
                    align: 'right'
                }
            ]
        },
        {
            dataIndex: 'total_size',
            text: CMDBuildUI.locales.Locales.administration.home.totalsize,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.home.totalsize'
            },
            width: 90,
            align: 'right',
            renderer: function (value) {
                return Ext.util.Format.fileSize(value);
            }
        }
    ],
    initComponent: function () {
        // add localsearchfield grid
        this.itemId = this.itemId || Ext.String.format('spaceusage-tablesgrid-{0}', new Date().getTime());
        this.tbar = Ext.Array.merge(
            [
                {
                    xtype: 'localsearchfield',
                    gridItemId: '#' + this.itemId
                },
                {
                    xtype: 'combo',
                    emptyText: CMDBuildUI.locales.Locales.administration.home.alltypes,
                    cls: 'administration-input',
                    displayField: 'label',
                    valueField: 'value',
                    bind: {
                        store: '{typeFilterStore}',
                        value: '{typeFilterValue}'
                    },
                    triggers: {
                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger(function (combo) {
                            combo.setValue('');
                        }, false)
                    }
                }
            ],
            this.tbar
        );
        this.callParent(arguments);
    }
});
