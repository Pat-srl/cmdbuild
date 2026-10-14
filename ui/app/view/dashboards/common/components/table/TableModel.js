Ext.define('CMDBuildUI.view.dashboards.common.components.table.TableModel', {
    extend: 'CMDBuildUI.view.dashboards.common.components.basechart.BaseChartModel',
    alias: 'viewmodel.dashboards-components-table-table',

    formulas: {
        applyFilterManager: {
            bind: { bindTo: { records: '{records}', filter: '{storedata.filter}' } },
            get: function (data) {
                if (data.records && data.filter) {
                    data.records.getAdvancedFilter().addCustomFilter(data.filter);
                }
            }
        }
    },

    stores: {
        records: {
            type: '{storedata.type}',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.proxyurl}',
                extraParams: '{storedata.extraparams}'
            },
            pageSize: '{storedata.pagesize}',
            autoLoad: '{storedata.autoload}',
            autoDestroy: true
        }
    }
});
