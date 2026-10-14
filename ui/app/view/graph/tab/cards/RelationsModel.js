Ext.define('CMDBuildUI.view.graph.tab.cards.RelationsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-cards-relations',

    stores: {
        edgesRelationStore: {
            model: 'CMDBuildUI.model.graph.EdgesRelation',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            grouper: {
                groupFn: function (item) {
                    return Ext.String.format('{0}_{1}', item.get('_type'), item.get('_direction'));
                }
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