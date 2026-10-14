Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.builder.Row', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.card.builder.RowController',
        'CMDBuildUI.view.administration.content.dashboards.common.card.builder.RowModel'
    ],
    alias: 'widget.administration-content-dashboards-card-builder-row',
    controller: 'administration-content-dashboards-card-builder-row',
    viewModel: {
        type: 'administration-content-dashboards-card-builder-row'
    },
    config: {
        columns: []
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    userCls: 'cmdbuild-fieldsmanagements-row',
    items: [],

    addColumn: function (column, colIndex, rowIndex) {
        return {
            xtype: 'administration-content-dashboards-card-builder-column',
            flex: column.width || 1,
            colIndex: colIndex,
            rowIndex: rowIndex,
            margin: colIndex === 0 ? 0 : '0 0 0 6',
            autoEl: {
                'data-testid': Ext.String.format('administration-dashboards-row-{0}-column-{1}', rowIndex, colIndex)
            },
            store: {
                data: column && column.components ? column.components : []
            }
        };
    }
});
