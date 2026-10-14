Ext.define('CMDBuildUI.view.administration.content.gis.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.GridController',
        // plugins
        'Ext.grid.filters.Filters'
    ],

    alias: 'widget.administration-content-gis-grid',
    controller: 'administration-content-gis-grid',
    viewModel: {},

    forceFit: true,
    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.icon,
            dataIndex: 'iconelement',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.icon'
            },
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            dataIndex: 'description',
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            align: 'left'
        },
        {
            xtype: 'actioncolumn',
            minWidth: 104,
            maxWidth: 104,
            items: [
                {
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.administration.gis.editicon;
                    },
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent('actionedit', grid, record, rowIndex, colIndex);
                    }
                },
                {
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.administration.gis.deleteicon;
                    },
                    handler: function (grid, rowIndex, colIndex) {
                        var record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent('actiondelete', grid, record, rowIndex, colIndex);
                    }
                }
            ]
        }
    ],
    bind: {
        store: '{icons}'
    }
});
