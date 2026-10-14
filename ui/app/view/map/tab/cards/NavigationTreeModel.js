Ext.define('CMDBuildUI.view.map.tab.cards.NavigationTreeModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-cards-navigationtree',

    data: {
        initialized: false,
        forceFirstCheck: true,
        extraCheckOnType: false
    },

    stores: {
        navigationTreeStore: {
            type: 'tree',
            id: 'navigationtreestore',
            model: 'CMDBuildUI.model.gis.GeoValueTree',
            autoLoad: true,
            root: {
                checked: true,
                expanded: true,
                description: CMDBuildUI.locales.Locales.gis.root,
                _id_composed: "root_id_composed",
                _id: "root_id"
            },
            sorters: [{
                property: 'text'
            }]
        }
    }

});
