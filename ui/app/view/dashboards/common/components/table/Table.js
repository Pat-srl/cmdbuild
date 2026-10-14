Ext.define('CMDBuildUI.view.dashboards.common.components.table.Table', {
    extend: 'CMDBuildUI.view.dashboards.common.components.basechart.BaseChart',

    requires: [
        'CMDBuildUI.view.dashboards.common.components.table.TableController',
        'CMDBuildUI.view.dashboards.common.components.table.TableModel'
    ],

    alias: 'widget.dashboards-components-table-table',
    controller: 'dashboards-components-table-table',
    viewModel: {
        type: 'dashboards-components-table-table'
    },

    customActions: [
        {
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            text: CMDBuildUI.locales.Locales.dashboards.tools.reload,
            handler: 'actionReloadHandler',
            scope: 'controller',
            localized: {
                text: 'CMDBuildUI.locales.Locales.dashboards.tools.reload'
            }
        }
    ],

    initComponent: function () {
        this.items = Ext.Array.filter(this.items, function (item) {
            return item.itemId !== 'dataTable';
        });
        this.callParent(arguments);
    },

    /**
     * Initialize table
     */
    initChart: function () {
        this.getController().initTableConfig(this, this.getItemDefinition());
    }
});
