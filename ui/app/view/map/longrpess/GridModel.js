Ext.define('CMDBuildUI.view.map.longpress.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-longpress-grid',

    data: {
        idElements: []
    },

    formulas: {
        ready: {
            bind: {
                idElements: '{idElements}'
            },
            get: function (data) {
                var store = this.get("gridStore");

                //set the filter
                var advancedFilter = store.getAdvancedFilter();
                advancedFilter.addAttributeFilter('_id', 'in', data.idElements);

                //load the store
                store.load();
            }
        }
    },

    stores: {
        gridStore: {
            model: 'CMDBuildUI.model.classes.Card',
            proxy: {
                type: 'baseproxy',
                url: '/classes/Class/cards'
            }
        }
    }

});
