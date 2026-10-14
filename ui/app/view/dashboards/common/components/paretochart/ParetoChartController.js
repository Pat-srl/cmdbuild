Ext.define('CMDBuildUI.view.dashboards.common.components.paretochart.ParetoChartController', {
    extend: 'CMDBuildUI.view.dashboards.common.components.basechart.BaseChartController',
    alias: 'controller.dashboards-components-paretochart-paretochart',

    listen: {
        store: {
            '#recordsStore': {
                load: 'onRecordsStoreLoad'
            }
        }
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model[]} records
     * @param {Boolean} successful
     * @param {Ext.data.operation.Read} operation
     * @param {Object} eOpts
     */
    onRecordsStoreLoad: function (store, records, successful, operation, eOpts) {
        const view = this.getView();
        const component = view.getItemDefinition();
        const valuefield = component.get('valueAxisFields')[0];
        const sum = store.sum(valuefield);
        let cumulative = 0;
        store.each(function (record) {
            cumulative += record.get(valuefield);
            record.set({
                Cumulative: cumulative,
                CumulativePerc: Ext.Number.parseInt((cumulative / sum) * 100)
            });
        });
    }
});
