Ext.define('CMDBuildUI.view.graph.tab.cards.ListCardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-cards-listcard',

    stores: {
        listCardStore: {
            model: 'CMDBuildUI.model.domains.Relation',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            sorters: [{
                property: 'destTypeDescription',
                direction: 'ASC'
            }, {
                property: '_destinationCode',
                direction: 'ASC'
            }, {
                property: '_destinationDescription',
                direction: 'ASC'
            }]
        }
    }
});