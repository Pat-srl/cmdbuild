Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.builder.RowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dashboards-card-builder-row',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        }
    },

    onAfterRender: function (view) {
        const vm = this.getViewModel();
        const row = view.getWidgetRecord();
        const columns = row.get('columns');
        const rowIndex = row.store.findExact('id', row.get('id'));

        vm.set('row', row);
        vm.set('columnsLength', columns.length);

        Ext.Array.forEach(columns, function (col, colIndex) {
            view.add(view.addColumn(col, colIndex, rowIndex));
        });
    }
});
