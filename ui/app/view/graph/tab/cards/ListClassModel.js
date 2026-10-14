Ext.define('CMDBuildUI.view.graph.tab.cards.ListClassModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-cards-listclass',

    stores: {
        listClassStore: {
            model: 'CMDBuildUI.model.graph.ListClass',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            sorters: {
                property: 'destTypeDescription',
                direction: 'ASC'
            }
        }
    }
});