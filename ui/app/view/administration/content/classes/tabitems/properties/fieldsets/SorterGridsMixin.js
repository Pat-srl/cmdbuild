Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.SorterGridsMixin', {

    mixinId: 'administrationroutes-sortergrids',

    moveUp: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        rowIndex--;
        if (!record || rowIndex < 0) {
            return;
        }

        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    },
    moveDown: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        rowIndex++;
        if (!record || rowIndex >= store.getCount()) {
            return;
        }
        store.remove(record);
        store.insert(rowIndex, record);

        grid.refresh();
        Ext.resumeLayouts();
    },
    deleteRow: function (grid, rowIndex, colIndex, item, event, record, row) {
        Ext.suspendLayouts();
        var mainView = grid.up('administration-content-classes-view') || grid.up('administration-content-processes-view') || grid.up('administration-content-dms-models-view') || grid.up('joinviews-configuration-items-contextmenusfieldset');
        var vm = mainView.getViewModel();
        var store = grid.getStore();
        var counter = store.getData().length - 1;
        store.remove(record);
        var counterKey;
        switch (record.$className) {
            case 'CMDBuildUI.model.FormTrigger':
                counterKey = 'formTriggerCount';
                break;
            case 'CMDBuildUI.model.ContextMenuItem':
                counterKey = 'contextMenuCount';
                break;
            case 'CMDBuildUI.model.WidgetDefinition':
                counterKey = 'formWidgetCount';
                break;
            case 'CMDBuildUI.model.AttributeGrouping':
                counterKey = 'attributeGroupingCount';
                break;
        }
        if (counterKey) {
            vm.set(counterKey, counter);
        }
        grid.grid.reconfigure(store);
        grid.refresh();
        Ext.resumeLayouts();
    }
});